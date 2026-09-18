package Rendering;

import Debugging.DebugLogger;
import FileStorage.ReadableBuffer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * parses a snapshot of the file to a write-only buffer and answers queries based on a write-only buffer. Buffers are
 * swapped after a complete render pass
 */
public class DoubleBufferRenderer implements Renderer {
    private boolean isRunning = true;
    private Thread renderThread;

    private RenderingBuffer front;
    private RenderingBuffer back;
    private final ReentrantLock lock = new ReentrantLock();

    private boolean refreshRequested = true;
    private final Condition needsRefresh = lock.newCondition();

    private final ReadableBuffer reference;

    private final RendererStateManager stateManager = new RendererStateManager();

    private String fileExtension;

    public DoubleBufferRenderer(ReadableBuffer reference, String fileExtension) {
        front = new RenderingBuffer();
        back = new RenderingBuffer();

        this.reference = reference;
        this.fileExtension = fileExtension;
    }

    @Override
    public void registerFileTypeChange(String newFileExtension) {
        fileExtension = newFileExtension;
        requestRefresh();
    }

    @Override
    public void requestRefresh() {
        lock.lock();
        refreshRequested = true;
        needsRefresh.signal();
        lock.unlock();
    }

    private void swapBuffers() throws InterruptedException {
        lock.lock();

        RenderingBuffer temp = front;
        front = back;
        back = temp;

        refreshRequested = false;
        while (!refreshRequested) {
            needsRefresh.await();
        }

        lock.unlock();
    }

    @Override
    public void start() {
        renderThread = new Thread(this::render);
        renderThread.start();
    }

    @Override
    public void stop() {
        isRunning = false;
        renderThread.interrupt();
    }

    @Override
    public StyleData[] getStyle(int paragraph) {
        try {
            lock.lock();
            if (paragraph >= front.size()) {
                return new StyleData[0];
            }
            
            return front.getParagraph(paragraph);
        }
        finally {
            lock.unlock();
        }
    }

    private void render() {
        while (isRunning) {
            ReadableBuffer snapshot = reference.snapshot();
            back.clear();

            stateManager.reset(fileExtension);

            for (int paragraph = 0; paragraph < snapshot.length(); paragraph++) {
                String text = snapshot.readParagraph(paragraph);
                StyleData[] data = stateManager.parse(text);
                back.addParagraph(data);
            }

            System.gc();
            try {
                swapBuffers();
            }
            catch (InterruptedException ignored) { }
        }
    }
}

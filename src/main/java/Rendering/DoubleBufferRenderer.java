package Rendering;

import FileStorage.ReadableBuffer;

import java.util.concurrent.locks.ReentrantLock;

public class DoubleBufferRenderer implements Renderer {
    private boolean isRunning = true;
    private Thread renderThread;

    private RenderingBuffer front;
    private RenderingBuffer back;
    private final ReentrantLock lock = new ReentrantLock();

    private final ReadableBuffer reference;

    private final RendererStateManager stateManager = new RendererStateManager();

    public DoubleBufferRenderer(ReadableBuffer reference) {
        front = new RenderingBuffer();
        back = new RenderingBuffer();

        this.reference = reference;
    }

    private void swapBuffers() {
        lock.lock();

        RenderingBuffer temp = front;
        front = back;
        back = temp;

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
            stateManager.reset();

            for (int paragraph = 0; paragraph < snapshot.length(); paragraph++) {
                String text = snapshot.readParagraph(paragraph);
                StyleData[] data = stateManager.parse(text);
                back.addParagraph(data);
            }

            swapBuffers();
            try {
                Thread.sleep(1000); //TEMP -> replace with a better mechanic to limit re-runs on short files
            }
            catch (InterruptedException ignored) { }
        }
    }
}

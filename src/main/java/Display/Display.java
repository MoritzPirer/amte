package Display;

import Controller.Controller;
import Controller.View.View;
import Controller.Input;
import Controller.SizeConfiguration;

import java.io.IOException;

public abstract class Display {
    protected Controller controller;

    public Display(Controller controller) {
        this.controller = controller;
    }

    public final void run() {
        try {
            init();
            controller.updateSizeConfiguration(initialSizeConfiguration());
            while (controller.isRunning()) {
                updateSize();
                redraw();
                parseInput();
                System.out.println("ping");
                Thread.sleep(50);
                //Delay here?
            }

            destroy();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException ignored) {}
    }

    private void updateSize() {
        SizeConfiguration sizeConfiguration = checkSizeConfiguration();
        if (sizeConfiguration != null) {
            controller.updateSizeConfiguration(sizeConfiguration);
        }
    }

    private void redraw() throws IOException {
        View view = controller.getView();
        render(view);
    }

    private void parseInput() throws IOException {
        Input input = checkInput();
        if (input != null) {
            controller.applyInput(input);
        }
    }

    abstract protected void init() throws IOException;
    abstract protected void destroy() throws IOException;


    abstract protected SizeConfiguration initialSizeConfiguration();
    /**
     * checks if the size of the window has changed and returns the new division of the window if so.
     * @return the new SizeConfiguration, or null if the Size has not changed
     */
    abstract protected SizeConfiguration checkSizeConfiguration();

    /**
     * check if the user has pressed a key. Should not block to wait for input
     * @return the key that was pressed, or null if no key was pressed
     */
    abstract protected Input checkInput() throws IOException;

    /**
     * Draw the view to the screen
     * @param view the view to draw
     */
    abstract protected void render(View view) throws IOException;
}

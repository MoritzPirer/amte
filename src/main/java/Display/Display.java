package Display;

import Controller.Controller.Controller;
import Controller.Controller.ModalController;
import Controller.View.View;
import Controller.Input;
import Controller.SizeConfiguration;

import java.io.IOException;

public abstract class Display {
    protected final Controller controller;

    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println("Start without arguments or with a single file path!");
            return;
        }
        Controller controller;
        if (args.length == 1) {
            controller = new ModalController(args[0]);
        }
        else {
            controller = new ModalController();
        }
        Display display = new LanternaDisplay(controller);
        display.run();
    }

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
                if (parseInput()) {
                    Thread.sleep(50);
                }
            }

            destroy();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException ignored) {}
    }

    private void updateSize() {
        SizeConfiguration sizeConfiguration = checkSizeConfiguration(controller.getRecommendedNumberWidth());
        if (sizeConfiguration != null) {
            controller.updateSizeConfiguration(sizeConfiguration);
        }
    }

    private void redraw() throws IOException {
        View view = controller.getView();
        render(view);
    }

    private boolean parseInput() throws IOException {
        Input input = checkInput();
        if (input != null) {
            controller.applyInput(input);
            return false;
        }

        return true;
    }

    /**
     * the start method of the implementation that will be called. Should do any setup needed for the display
     * to be usable
     * @throws IOException if setup went wrong in a way that makes it impossible to start the editor
     */
    abstract protected void init() throws IOException;

    /**
     * the end method of the implementation that will be called. Should release any resources that need manual release.
     * @throws IOException if cleanup went wrong.
     */
    abstract protected void destroy() throws IOException;

    /**
     * finds the size of the components at the time of creation
     * @return the SizeConfiguration with the initial size of components
     */
    abstract protected SizeConfiguration initialSizeConfiguration();

    /**
     * checks if the size of the window has changed and returns the new division of the window if so.
     * @return the new SizeConfiguration, or null if the Size has not changed
     */
    abstract protected SizeConfiguration checkSizeConfiguration(int recommendedNumberWidth);

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

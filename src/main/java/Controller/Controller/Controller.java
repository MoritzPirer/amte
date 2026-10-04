package Controller.Controller;

import Controller.View.View;
import Controller.Input;
import Controller.SizeConfiguration;

/**
 * applies Input from the display to the internal file representation and retrieves the elements to be rendered
 */
public interface Controller {
    /**
     * determines if the program is running. Will stop running when user inputs a quit command
     * @return true until the user inputs a quit command, then false
     */
    boolean isRunning();

    /**
     * applies the input to the internal file representation. Depends on the context established by previous inputs
     * @param input the input to process
     */
    void applyInput(Input input);

    /**
     * assembles the contents of the editor to be shown on the screen.
     * @return the contents of the individual components
     */
    View getView();

    /**
     * @return how wide the number panel should be based on the length of the file. Does not account for padding or
     * disabling the number panel in the setting
     */
    int getRecommendedNumberWidth();

    /**
     * updates the stored size of individual components to account for window resizing
     * @param sizeConfiguration the sizes of the ui components
     */
    void updateSizeConfiguration(SizeConfiguration sizeConfiguration);

    /**
     * perform any calculations or checks needed to be done every frame, independently of rending tasks
     */
    void update();
}

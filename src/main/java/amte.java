import Controller.Controller.Controller;
import Controller.Controller.ModalController;
import Debugging.DebugLogger;
import Display.Display;
import Display.LanternaDisplay;

import java.util.Arrays;

public class amte {

    public static void main(String[] args) {
        if (args.length > 1) {
            DebugLogger.info("STARTUP", "Invalid startup: " + Arrays.toString(args));
            return;
        }
        Controller controller;
        if (args.length == 1) {
            DebugLogger.info("STARTUP", "Starting with filepath: " + args[0]);
            controller = new ModalController(args[0]);
        }
        else {
            DebugLogger.info("STARTUP", "Starting without file");
            controller = new ModalController();
        }

        Display display = new LanternaDisplay(controller);
        display.run();
    }
}

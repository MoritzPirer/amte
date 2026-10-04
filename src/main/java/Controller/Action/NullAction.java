package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;

/**
 * Null Object Pattern to be used for incomplete commands, frames without user input, mode switching commands, etc
 */
public class NullAction extends Action {
    @Override
    public void apply(ViewPortManager viewPortManager) { }
}

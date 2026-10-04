package Controller.Action;

import Controller.ViewPortManager.ViewPortManager;

/**
 * Outlines an action to be applied to a viewport
 */
public abstract class Action {

    /**
     * executes the action by modifying the current viewport or changing what the current viewport is.
     */
    public abstract void apply(ViewPortManager viewPortManager);

    /**
     * decides if the action is undoable. Should always be called before calling undo()
     * Subclasses should only override this if they also Override undo()
     * @return true if the action can be undone, false if not
     */
    public boolean canBeUndone() {
        return false;
    }

    /**
     * undoes the given action, such that calling apply(x) and undo(x) without any modifications to x in between
     * returns x to its original state. Some implementations are not undoable. calling undo() before apply(), or calling undo()
     * on a viewport that is not in the state it was immediately after apply() can have unpredictable effects or throw exceptions
     * @throws UnsupportedOperationException if the implementation cannot be undone. Such implementations will have canBeUndone() returning false
     */
    public void undo(ViewPortManager viewPortManager) {
        throw new UnsupportedOperationException("Not undoable!");
    }

    /**
     * check if it is possible for this action to be modified in such a way that calling apply() on the
     * modified action is equivalent to calling apply() for this action, then the given action
     * @param action the candidate for merging with this action
     * @return true if the given action can be merged into this action
     */
    public boolean canAbsorb(Action action) {
        return false;
    }

    /**
     * modify this action in such a way that calling apply() on the modified action is equivalent to calling
     * apply() on this action, then on the given action.
     * @param action the action to be merged into action
     * @throws UnsupportedOperationException if the implementation cannot absorb the given action. Implementations
     * will guarantee that the exception is thrown if and only if canAbsorb() called on the same two actions returned false,
     * i.e. if action1.canAbsorb(action2) returns true, action1.absorb(action2) will never throw.
     */
    public void absorb(Action action) {
        throw new UnsupportedOperationException("Not absorbable!");
    }
}

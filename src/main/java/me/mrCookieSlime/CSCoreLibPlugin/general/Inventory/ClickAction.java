package me.mrCookieSlime.CSCoreLibPlugin.general.Inventory;

/**
 * An old remnant of CS-CoreLib.
 * This will be removed once we updated everything.
 * Don't look at the code, it will be gone soon, don't worry.
 *
 */
@Deprecated
public class ClickAction {

    private boolean right;
    private boolean shift;

    public ClickAction(boolean rightClicked, boolean shiftClicked) {
        this.right = rightClicked;
        this.shift = shiftClicked;
    }

    public boolean isRightClicked() {
        return right;
    }

    public boolean isShiftClicked() {
        return shift;
    }

}

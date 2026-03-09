package logicsim.controllers;

public class KeyCombination {
    public final int keycode;
    public final int modifiers;

    KeyCombination(int keycode, int modifiers) {
        this.keycode = keycode;
        this.modifiers = modifiers;
    }
}

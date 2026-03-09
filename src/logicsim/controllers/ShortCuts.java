package logicsim.controllers;

import logicsim.App;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class ShortCuts {
    public static void keyPressed(KeyEvent keyEvent) {
        for (KeyCombination kc : shortcuts.keySet()) {
            if (kc.keycode == keyEvent.getKeyCode() && kc.modifiers == keyEvent.getModifiersEx()) {
                shortcuts.get(kc).run();
            }
        }
    }

    public static Map<KeyCombination, Runnable> shortcuts = new HashMap<>();

    public static void addShortcut(KeyCombination keys, Runnable action) {
        shortcuts.put(keys, action);
    }

    public static void init() {
        addShortcut(
            new KeyCombination(
                KeyEvent.VK_R,
                KeyEvent.CTRL_DOWN_MASK
            ),
            () -> {
                if (App.instance.lsframe.getSelectedCount() == 0) return;
                App.instance.lsframe.rotateSelected();
            }
        );
        addShortcut(
            new KeyCombination(
                KeyEvent.VK_M,
                KeyEvent.CTRL_DOWN_MASK
            ),
            () -> {
                if (App.instance.lsframe.getSelectedCount() == 0) return;
                App.instance.lsframe.mirrorSelected();
            }
        );
        addShortcut(
            new KeyCombination(
                KeyEvent.VK_I,
                KeyEvent.CTRL_DOWN_MASK
            ),
            () -> {
                if (App.instance.lsframe.getSelectedCount() == 0) return;
                App.instance.lsframe.increaseInputsForSelected();
            }
        );
        addShortcut(
            new KeyCombination(
                KeyEvent.VK_D,
                KeyEvent.CTRL_DOWN_MASK
            ),
            () -> {
                if (App.instance.lsframe.getSelectedCount() == 0) return;
                App.instance.lsframe.decreaseInputsForSelected();
            }
        );
    }
}

package logicsim.ui;

import logicsim.localization.Lang;

import javax.swing.*;

public class LSButton extends JButton {

    public LSButton(String iconName, Lang toolTip) {
        ButtonHelper.initButton(this, iconName, toolTip);
    }
}

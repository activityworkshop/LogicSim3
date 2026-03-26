package logicsim.ui;

import logicsim.localization.Lang;

import javax.swing.*;

public class LSToggleButton extends JToggleButton {

	public LSToggleButton(String iconName, Lang toolTip) {
		ButtonHelper.initButton(this, iconName, toolTip);
	}
}

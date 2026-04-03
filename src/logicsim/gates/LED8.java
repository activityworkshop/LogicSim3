package logicsim.gates;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;

import javax.swing.JColorChooser;

import logicsim.ColorFactory;
import logicsim.Gate;
import logicsim.localization.I18N;
import logicsim.LSLevelEvent;
import logicsim.localization.Lang;

/**
 * 8-fold LED
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class LED8 extends Gate {
	public static final String GATE_TYPE = "led8";

	private static final String COLOR = "color";
	private static final int OVAL_RADIUS = 9;

	private Color color = null;

	public LED8() {
		super("outputs", GATE_TYPE);
		setWidth(20);
		setHeight(90);
		createInputs(8);
		loadProperties();
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		fireRepaint();
	}

	@Override
	public void loadProperties() {
		color = ColorFactory.makeColor(getProperty(COLOR), Color.RED);
	}

	@Override
	public void drawRotated(Graphics2D g2) {
		for (int i = 0; i < 8; i++) {
			Color c = getPin(i).getLevel() ? color : Color.LIGHT_GRAY;
			g2.setPaint(c);
			g2.fillOval(origX + CONN_SIZE - 1, origY + i * 10 + 6, OVAL_RADIUS, OVAL_RADIUS);
			g2.setPaint(Color.BLACK);
			g2.drawOval(origX + CONN_SIZE - 1, origY + i * 10 + 6, OVAL_RADIUS, OVAL_RADIUS);
		}
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		super.showPropertiesUI(frame);
		Color newColor = JColorChooser.showDialog(null, I18N.getString(type, I18N.TITLE) + " " + I18N.tr(Lang.SETTINGS),
				color);
		if (newColor != null) {
			color = newColor;
		}
		setProperty(COLOR, ColorFactory.getString(color));
		return true;
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
	}
}

package logicsim.gates;

import java.awt.*;
import java.awt.geom.AffineTransform;

import javax.swing.*;

import logicsim.ColorFactory;
import logicsim.Gate;
import logicsim.localization.I18N;
import logicsim.LSLevelEvent;
import logicsim.localization.Lang;

/**
 * LED for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class LED extends Gate {
	public static final String GATE_TYPE = "led";

	private static final String COLOR = "color";

	private Color color = null;
	private final Image glowImage;

	public LED() {
		super("outputs", GATE_TYPE);
		width = 40;
		height = 40;
		createInputs(1);
		variableInputCountSupported = false;
		loadProperties();
		reset();
		glowImage = new ImageIcon(getClass().getResource("/images/ledglow.png")).getImage();
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
	public void draw(Graphics2D g2) {
		super.draw(g2);
		int x = getX();
		int y = getY();

		final int ovalCenterY = y + getHeight() / 2;
		final int ovalRadius = 14;

		int y1 = ovalCenterY - ovalRadius;

		Color c = getPin(0).getLevel() ? color : Color.LIGHT_GRAY;
		g2.setPaint(c);

		AffineTransform old = null;
		if (rotate90 != 0) {
			old = g2.getTransform();
			g2.rotate(Math.toRadians(rotate90 * 90), xc, yc);
		}
		g2.fillOval(x + CONN_SIZE - 1, y1, ovalRadius * 2, ovalRadius * 2);
		g2.setPaint(Color.BLACK);
		g2.drawOval(x + CONN_SIZE - 1, y1, ovalRadius * 2, ovalRadius * 2);
		if (getPin(0).getLevel()) {
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f)); // Set transparency
			g2.drawImage(glowImage, x, y, null);
		}

		if (rotate90 != 0) {
			g2.setTransform(old);
		}
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		super.showPropertiesUI(frame);
		Color newColor = JColorChooser.showDialog(null,
				I18N.getString(type, I18N.TITLE) + " " + I18N.tr(Lang.SETTINGS),
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

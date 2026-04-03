package logicsim.gates;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import logicsim.Gate;
import logicsim.WidgetHelper;

/**
 * Text Label component for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class TextLabel extends Gate {
	public static final String GATE_TYPE = "label";

	private static final String TEXTLABEL_DEFAULT = "Text";

	public TextLabel() {
		super("outputs", GATE_TYPE); // not really an output, not really a Gate either!
		width = 60;
		height = 20;
		loadProperties();
	}

	@Override
	public void loadProperties() {
		setProperty(TEXT, getPropertyWithDefault(TEXT, TEXTLABEL_DEFAULT));
	}

	@Override
	public boolean insideFrame(int mx, int my) {
		return getBoundingBox().contains(mx, my);
	}

	@Override
	protected void drawActiveFrame(Graphics2D g2) {
		g2.setFont(bigFont);
		if (text != null) {
			Rectangle r = WidgetHelper.textDimensions(g2, text);
			width = r.width + 10;
			height = r.height + 10;
			super.drawActiveFrame(g2);
		}
	}

	/** Prevent rotation, causes coordinate confusion */
	@Override
	public void rotate() {
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
	}

	@Override
	public void drawText(Graphics2D g2) {
		drawLabelWithOffset(g2, getProperty(TEXT), bigFont, 0, 0);
	}
}

package logicsim.gates;

import java.awt.Color;
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
	private static final String TEXTLABEL_DEFAULT = "Text";

	public TextLabel() {
		super("outputs", "label"); // not really an output, not really a Gate either!
		width = 60;
		height = 20;
		loadProperties();
	}

	@Override
	public void loadProperties() {
		text = getPropertyWithDefault(TEXT, TEXTLABEL_DEFAULT);
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
	public void drawRotated(Graphics2D g2) {
		g2.setFont(bigFont);
		g2.setColor(Color.black);
		if (text != null) {
			Rectangle r = WidgetHelper.textDimensions(g2, text);
			width = r.width + 10;
			height = r.height + 10;
            xc = getX() + width / 2;
            yc = getY() + height / 2;
			WidgetHelper.drawString(g2, text, xc, yc, WidgetHelper.ALIGN_CENTER);
		}
	}
}

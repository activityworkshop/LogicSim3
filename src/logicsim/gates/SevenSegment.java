package logicsim.gates;

import java.awt.*;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.localization.I18N;
import logicsim.localization.Lang;

import javax.swing.*;

/**
 * Seven Segment Display for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class SevenSegment extends Gate {
	public static final String GATE_TYPE = "sevenseg";
	private static final Color OFF_COLOR = new Color(0xE8, 0xE8, 0xE8);
	private static final Color DEFAULT_ON_COLOR = Color.RED;
    private static final String PROPERTY_COLOR = "color";

	private Color color = DEFAULT_ON_COLOR;

	public SevenSegment() {
		super("outputs", GATE_TYPE);
		height = 80;
		width = 80;
		createInputs(7);
		for (int i = 0; i < 7; i++) {
			getPin(i).setProperty(TEXT, String.valueOf((char) (((int) 'a') + i)));
		}
		reset();
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		final int xOffset = getX() + 29;
		final int yOffset = getY() + 16;
		g.setStroke(new BasicStroke(1));
		for (int i = 0; i < getNumInputs(); i++) {
			g.setColor(getPin(i).getLevel() ? color : OFF_COLOR);
			switch (i) {
			case 0:
				drawHorizontalSegment(g, xOffset + 1, yOffset + 1);
				break;
			case 1:
				drawVerticalSegment(g, xOffset + 23, yOffset + 2);
				break;
			case 2:
				drawVerticalSegment(g, xOffset + 23, yOffset + 25);
				break;
			case 3:
				drawHorizontalSegment(g, xOffset + 1, yOffset + 47);
				break;
			case 4:
				drawVerticalSegment(g, xOffset, yOffset + 25);
				break;
			case 5:
				drawVerticalSegment(g, xOffset, yOffset + 2);
				break;
			case 6:
				drawHorizontalSegment(g, xOffset + 1, yOffset + 24);
				break;
			}
		}
	}

	private void drawHorizontalSegment(Graphics2D g2, int x, int y) {
		final Path2D path = new Path2D.Double();
		path.moveTo(x, y);
		path.lineTo(x + 2, y - 2);
		path.lineTo(x + 19, y - 2);
		path.lineTo(x + 21, y);
		path.lineTo(x + 19, y + 2);
		path.lineTo(x + 2, y + 2);
		g2.fill(path);
	}

	private void drawVerticalSegment(Graphics2D g2, int x, int y) {
		final Path2D path = new Path2D.Double();
		path.moveTo(x, y);
		path.lineTo(x + 2, y + 2);
		path.lineTo(x + 2, y + 19);
		path.lineTo(x, y + 21);
		path.lineTo(x - 2, y + 19);
		path.lineTo(x - 2, y + 2);
		g2.fill(path);
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
        setProperty(PROPERTY_COLOR, "#" + Integer.toHexString(color.getRGB()).substring(2));
        return true;
    }
}

package logicsim.gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import logicsim.Gate;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * Binary Display for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class Display4d extends Gate {
	public static final String GATE_TYPE = "disp4d";

	private static final int BLANK = 17;
	private static final int MINUS = 16;

	private static final Color OFF_COLOR = new Color(0xE0, 0xE0, 0xE0);
	private static final Color ON_COLOR = Color.RED;

	private static final int WE = 8;

	// signed - active low - when low then value is signed
	private static final int SI = 9;
	private static final int CLK = 10;
	private static final int CLR = 11;

	private final int[] out = new int[] { 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1,
			0, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1,
			1, 1, 1, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 };

	private int value;
	private final int[] digit = new int[] { BLANK, BLANK, BLANK, BLANK };

	public Display4d() {
		super("cpu", GATE_TYPE);
		height = 90;
		width = 150;
		createInputs(12);
		
		for (int i = 0; i < 8; i++) {
			getPin(i).setProperty(TEXT, String.valueOf(i));
		}
		
		getPin(WE).setProperty(TEXT, "WE");
		getPin(WE).paintDirection = Pin.LEFT;
		getPin(WE).setX(getX() + width);
		getPin(WE).setY(getY() + 30);
		getPin(SI).setProperty(TEXT, "/SI");
		getPin(SI).paintDirection = Pin.LEFT;
		getPin(SI).setX(getX() + width);
		getPin(SI).setY(getY() + 50);
		getPin(CLK).setProperty(TEXT, Pin.POS_EDGE_TRIG);
		getPin(CLK).paintDirection = Pin.LEFT;
		getPin(CLK).setX(getX() + width);
		getPin(CLK).setY(getY() + 70);
		getPin(CLR).setProperty(TEXT, "/CL");
		getPin(CLR).paintDirection = Pin.LEFT;
		getPin(CLR).setX(getX() + width);
		getPin(CLR).setY(getY() + 10);
		value = BLANK;
	}

	public void computeValue() {
		// compute value
		value = 0;
		for (int i = 0; i < 8; i++) {
			if (getPin(i).getLevel()) {
				int pow = (int) Math.pow(2, i);
				value += pow;
			}
		}
		if (getPin(SI).getLevel() == LOW && value > 127) {
			value -= 256;
		}
	}

	private void display() {
		for (int i = 0; i < 4; i++) {
			digit[i] = BLANK;
		}
		int v = value;
		if (v < -99)
			digit[0] = MINUS;
		else if (v < -9)
			digit[1] = MINUS;
		else if (v < 0)
			digit[2] = MINUS;
		v = Math.abs(v);
		if (v > 99) {
			digit[1] = v / 100;
			digit[2] = (v % 100) / 10;
		} else if (v > 9) {
			digit[2] = v / 10;
		}
		digit[3] = v % 10;
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// edge detection
		if (e.source.equals(getPin(CLK)) && e.level == HIGH) {
			if (getPin(WE).getLevel() == HIGH) {
				computeValue();
				display();
			}
		}
		if (e.source.equals(getPin(CLR)) && e.level == LOW) {
            Arrays.fill(digit, BLANK);
		}
	}

	public void drawDigit(Graphics2D g, int num) {
		final int xOffset = xc + ((num - 2) * 30);
		final int yOffset = yc - 23;
		g.setStroke(new BasicStroke(1));

		for (int i = 0; i < 7; i++) {
			g.setColor(out[digit[num] * 7 + i] == 1 ? ON_COLOR : OFF_COLOR);
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
	protected void drawRotated(Graphics2D g2) {
		super.drawRotated(g2);
		for (int i = 0; i < 4; i++) {
			drawDigit(g2, i);
		}
	}
}

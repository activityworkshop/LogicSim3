package logicsim.gates;

import java.awt.Color;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * D-Flipflop with Set and Reset
 * 
 * @see <a href="http://cedmagic.com/tech-info/data/cd4013.pdf">http://cedmagic.com/tech-info/data/cd4013.pdf</a>
 * 
 * @author Peter Gabriel
 * @version 2.0
 */
public class DRFlipFlop extends Gate {

	boolean clk = false;
	boolean out0 = false;
	boolean out1 = true;

	public DRFlipFlop() {
		super("flipflops", "drff");
		createInputs(3);
		createOutputs(2);

		getPin(0).setProperty(TEXT, "D");
		getPin(1).setProperty(TEXT, "R");
		getPin(2).setProperty(TEXT, Pin.POS_EDGE_TRIG);

		getPin(3).setProperty(TEXT, "Q");
		getPin(4).setProperty(TEXT, "/Q");
		getPin(4).setLevelType(Pin.INVERTED);

		getPin(3).moveBy(0, 10);
		getPin(4).moveBy(0, -10);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// clock: pin2
		// d: pin0
		// r: pin1
		if (e.source.equals(getPin(2)) && e.level == HIGH) {
			// clock rising edge detection
			boolean d = getPin(0).getLevel();
			boolean r = getPin(1).getLevel();
			if (!r) {
				LSLevelEvent evt = new LSLevelEvent(this, d);
				getPin(3).changedLevel(evt);
				getPin(4).changedLevel(evt);
			}
		} else if (e.source.equals(getPin(1)) && e.level == HIGH) {
			// reset pressed
			LSLevelEvent evt = new LSLevelEvent(this, LOW);
			getPin(3).changedLevel(evt);
			getPin(4).changedLevel(evt);
		}
	}

	@Override
	public void reset() {
		super.reset();
		LSLevelEvent evt = new LSLevelEvent(this, LOW, true);
		getPin(3).changedLevel(evt);
		getPin(4).changedLevel(evt);
	}
	
	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		g2.setColor(Color.black);
		drawLabel(g2, "DR", Pin.smallFont);
	}
}

package gates;

import java.awt.Color;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * D-Flipflop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class DFlipFlop extends Gate {

	public DFlipFlop() {
		super("flipflops", "dff");
		createInputs(2);
		createOutputs(2);

		getPin(0).setProperty(TEXT, "D");
		getPin(1).setProperty(TEXT, Pin.POS_EDGE_TRIG);

		getPin(2).setProperty(TEXT, "Q");
		getPin(3).setProperty(TEXT, "/Q");
		getPin(3).setLevelType(Pin.INVERTED);

		getPin(0).moveBy(0, 10);
		getPin(1).moveBy(0, -10);
		getPin(2).moveBy(0, 10);
		getPin(3).moveBy(0, -10);
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		g2.setColor(Color.black);
		drawLabel(g2, "D-FF", Pin.smallFont);
	}

	/**
     * <a href="https://www.electronicsforu.com/resources/learn-electronics/flip-flop-rs-jk-t-d">https://www.electronicsforu.com/resources/learn-electronics/flip-flop-rs-jk-t-d</a>
     */
	@Override
	public void changedLevel(LSLevelEvent e) {
		if (e.source.equals(getPin(1)) && e.level == HIGH) {
			// rising edge detection
			boolean d = getPin(0).getLevel();
			LSLevelEvent evt = new LSLevelEvent(this, d, true);
			getPin(2).changedLevel(evt);
			getPin(3).changedLevel(evt);
		}
	}
}

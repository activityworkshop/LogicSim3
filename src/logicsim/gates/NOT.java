package logicsim.gates;

import logicsim.Pin;

/**
 * NOT Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NOT extends Buffer {
	public static final String GATE_TYPE = "not";

	public NOT() {
		super(GATE_TYPE);
		getPin(1).setLevelType(Pin.INVERTED);
	}
}

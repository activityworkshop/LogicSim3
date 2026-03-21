package logicsim.gates;

import logicsim.Pin;

/**
 * NOR Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NOR extends OR {
	public static final String GATE_TYPE = "nor";

	public NOR() {
		super(GATE_TYPE);
		getPin(0).setLevelType(Pin.INVERTED);
	}
}

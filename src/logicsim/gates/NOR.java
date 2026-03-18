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
	public NOR() {
		super("nor");
		getPin(0).setLevelType(Pin.INVERTED);
	}
}

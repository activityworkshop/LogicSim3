package logicsim.gates;

import logicsim.Pin;

/**
 * NAND Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NAND extends AND {
	public static final String GATE_TYPE = "nand";

	public NAND() {
		super(GATE_TYPE);
		getPin(0).setLevelType(Pin.INVERTED);
	}
}

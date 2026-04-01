package logicsim.gates;

import logicsim.Pin;

/**
 * Equivalence Gate for LogicSim (XNOR)
 */
public class EQU extends XOR {
	public static final String GATE_TYPE = "equ";

	public EQU() {
		super(GATE_TYPE);
		getPin(0).setLevelType(Pin.INVERTED);
	}
}

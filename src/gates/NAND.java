package gates;

import logicsim.Pin;

/**
 * NAND Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NAND extends AND {
	public NAND() {
		super("nand");
		getPin(0).setLevelType(Pin.INVERTED);
	}
}

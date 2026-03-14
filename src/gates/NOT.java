package gates;

import logicsim.Pin;

/**
 * NOT Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NOT extends Buffer {

	public NOT() {
		super("not");
		getPin(1).setLevelType(Pin.INVERTED);
	}
}

package logicsim.gates;

/**
 * Off-Delay component for LogicSim
 */
public class OffDelay extends Delay {
	public static final String GATE_TYPE = "offdelay";

	public OffDelay() {
		super(GATE_TYPE, false);
	}
}

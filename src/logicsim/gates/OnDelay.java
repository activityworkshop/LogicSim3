package logicsim.gates;

/**
 * ON-Delay component for LogicSim
 */
public class OnDelay extends Delay {
	public static final String GATE_TYPE = "ondelay";

	public OnDelay() {
		super(GATE_TYPE, true);
	}
}

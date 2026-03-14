package gates;

import logicsim.Gate;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * Equivalence Gate for LogicSim (XNOR)
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class EQU extends Gate {
	public EQU() {
		super("basic", "equ");
		label = "=";
		createOutputs(1);
		createInputs(2);
		getPin(0).setLevel(true);
		variableInputCountSupported = true;
	}

	public void simulate() {
		int n = 0;
		for (Pin p : getInputs()) {
			if (p.getLevel()) {
				n++;
			}
		}
		// if n is even, set true
		LSLevelEvent evt = new LSLevelEvent(this, n % 2 == 0, force);
		getPin(0).changedLevel(evt);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		if (busted) {
			return;
		}
		simulate();
	}
}

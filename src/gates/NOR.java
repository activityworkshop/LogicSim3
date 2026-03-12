package gates;

import logicsim.localization.I18N;
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
		type = "nor";
		getPin(0).setLevelType(Pin.INVERTED);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "NOR");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "NOR Gate (variable Inputcount)");
		I18N.addGate("de", type, I18N.DESCRIPTION, "NOR Gatter mit einstellbarer Eingangsanzahl");
		I18N.addGate("es", type, I18N.TITLE, "NOR (NO-O)");
		I18N.addGate("fr", type, I18N.TITLE, "Non Ou (NOR)");
	}
}
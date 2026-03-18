package logicsim.gates;

import logicsim.Gate;

abstract class BinaryOutput extends Gate {
	protected enum DisplayType {DECIMAL, HEXADECIMAL}

	protected DisplayType displayType = DisplayType.HEXADECIMAL;

	protected BinaryOutput(String type) {
		super("outputs", type);
	}

	protected String getDisplayType() {
		return switch(displayType) {
			case DECIMAL -> "DEC";
			case HEXADECIMAL -> "HEX";
		};
	}

	protected void setDisplayType(DisplayType type) {
		displayType = type;
	}

	/** @return two or three character value */
	protected String getValueAsString(int value) {
		String sval = switch(displayType) {
			case DECIMAL -> Integer.toString(value);
			case HEXADECIMAL -> Integer.toHexString(value);
		};
		if (sval.length() == 1) {
			sval = "0" + sval;
		}
		return sval.toUpperCase();
	}
}

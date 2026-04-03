package logicsim;

import java.awt.Color;

public final class ColorFactory {
	/** @return a String describing the given color */
	public static String getString(Color color) {
		return "#" + Integer.toHexString(color.getRGB()).substring(2);
	}

	/** @return a Color constructed from the given string value */
	public static Color makeColor(String value, Color defaultColor) {
        if (value != null && value.length() == 7 && value.charAt(0) == '#') {
			try {
				final int r = Integer.parseInt(value.substring(1, 3), 16);
				final int g = Integer.parseInt(value.substring(3, 5), 16);
				final int b = Integer.parseInt(value.substring(5, 7), 16);
				return new Color(r, g, b, 255);
			}
			catch (IllegalArgumentException ignored) {}
        }
		return defaultColor;
    }
}

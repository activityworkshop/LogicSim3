package logicsim.localization;

import logicsim.App;
import logicsim.Dialogs;
import logicsim.LSProperties;

import java.util.*;

/**
 * @author atetzl
 */
public class I18N {

	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String ALL = "ALL";

	public static String lang = "en";
	public static Properties prop = null;

	/** Creates a new instance of I18N */
	public I18N() {
		if (prop != null)
			return;

		lang = LSProperties.getInstance().getProperty(LSProperties.LANGUAGE, "en");
		prop = load(lang);
		if (prop.isEmpty() && !"en".equals(lang)) {
			prop = load("en");
			if (prop == null) {
				Dialogs.messageDialog(null,
						"Language file languages/en.properties not found.\nPlease run the program from its directory.");
				System.exit(5);
			}
		}
	}

	public static Properties load(String lang) {
		Properties properties = new Properties();

        String path = "/languages/" + lang + ".properties";
        try {
            properties.load(App.class.getResourceAsStream(path));
        } catch (Exception e) {
            e.printStackTrace();
        }

		return properties;
	}

	public static String tr(Lang langkey) {
		if (prop == null) {
			return "- I18N not initialized -";
		}
		return tr(langkey.getKey());
	}

	public static String tr(String key) {
		if (prop == null)
			return "- I18N not initialized -";
		if (prop.containsKey(key)) {
			String item = prop.getProperty(key);
			if (item != null)
				return item;
		}
		System.err.println("I18N: translation of '" + key + "' is missing");
		return "-" + key + "-";
	}

	public static String getString(String id, String key) {
		return tr("gate." + id + "." + key);
	}

	public static boolean hasString(String key) {
		String item = prop.getProperty(key);
		return (item != null);
	}

	public static boolean hasString(String id, String key) {
		return hasString("gate." + id + "." + key);
	}

	public static String tr(Lang key, String value) {
		String s = tr(key);
		return String.format(s, value);
	}

	public static List<String> getLanguages() {
		List<String> langs = new ArrayList<>();
        langs.add("de");
		langs.add("en");
        langs.add("es");
        langs.add("fr");
        langs.add("it");
        langs.add("nl");
        langs.add("pl");
        langs.add("sv");
        langs.add("tr");
		return langs;
	}

	public static void addGate(String langGate, String type, String key, String value) {
		if (!langGate.equals(lang) && !langGate.equals(ALL))
			return;
		prop.setProperty("gate." + type + "." + key, value);
	}

	public static void add(String slang, String key, String value) {
		if (!slang.equals(lang) && !slang.equals(ALL))
			return;
		prop.setProperty(key, value);
	}
}

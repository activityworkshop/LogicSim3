package logicsim;

import logicsim.controllers.ShortCuts;
import logicsim.localization.I18N;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.util.Enumeration;

public class App {

	public static final String CIRCUIT_FILE_SUFFIX = "lsc";
	public static final String MODULE_FILE_SUFFIX = "lsm";

	public static App instance;

	public LSFrame lsframe;


	public App() {
		new I18N();

		setUIFont(new FontUIResource("Roboto", Font.PLAIN, 12));

		// center the window and adjust dimensions
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(1024, 768);
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		lsframe = new LSFrame();
		lsframe.setIconImage(new ImageIcon(getClass().getResource("/images/icon.png")).getImage());
		lsframe.setSize(frameSize);
		lsframe.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		lsframe.setVisible(true);

		Wire.setColorMode();
	}

	private static void setUIFont(FontUIResource f) {
		Enumeration<Object> keys = UIManager.getDefaults().keys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			Object value = UIManager.get(key);
			if (value instanceof FontUIResource) {
				UIManager.put(key, f);
			}
		}
	}

	public static String getModulePath() {
		String fName = new File("").getAbsolutePath() + "/modules/";
		final File f = new File(fName);
		if (f.exists() && f.isDirectory()) {
			return f.getAbsolutePath() + "/";
		} else {
			Dialogs.errorDialog(null, "Directory 'modules' not found.\nPlease run the program from its directory");
			System.exit(0);
		}

		return "";
	}

	public static String getCircuitPath() {
		String fName = new File("").getAbsolutePath() + "/circuits/";
		final File f = new File(fName);
		if (f.exists() && f.isDirectory()) {
			return f.getAbsolutePath() + "/";
		} else {
			Dialogs.errorDialog(null, "Directory 'circuits' not found.\nPlease run the program from its directory");
			System.exit(0);
		}
		return "";
	}

	/**
	 * Main method
	 */
	public static void main(String[] args) {
		instance = new App();
		ShortCuts.init();
	}
}

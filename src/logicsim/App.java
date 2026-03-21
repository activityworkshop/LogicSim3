package logicsim;

import logicsim.controllers.ShortCuts;
import logicsim.localization.I18N;
import logicsim.xml.XMLLoader;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.List;

public class App {

	public static final String APP_TITLE = "LogicSim";
	public static final String CIRCUIT_FILE_SUFFIX = "lsc";
	public static final String MODULE_FILE_SUFFIX = "lsm";

    public static App instance;

	public LSFrame lsframe;

	static ArrayList<Category> cats = new ArrayList<>();

	public App() {
		new I18N();
		initializeGateCategories();

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
		lsframe = new LSFrame(APP_TITLE);
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
            if (value instanceof FontUIResource)
                UIManager.put(key, f);
        }
    }

    private static void addToCategory(Gate g) {
		String catTitle = g.category;
		if (g.category == null)
			catTitle = "hidden";

		Category cat = null;
		for (Category c : cats) {
			if (c.getTitle().equals(catTitle)) {
				cat = c;
				break;
			}
		}
		if (cat == null) {
			cat = new Category(catTitle);
			cats.add(cat);
		}
		cat.addGate(g);
	}

	private static void initializeGateCategories() {
		Category cat = new Category("hidden");
		Gate g = new MODIN();
		cat.addGate(g);
		g = new MODOUT();
		cat.addGate(g);
		cats.add(cat);

		cats.add(new Category("category.basic"));
		cats.add(new Category("category.inputs"));
		cats.add(new Category("category.outputs"));
		cats.add(new Category("category.flipflops"));

		List<Class<?>> classes;
		try {
			classes = GateLoaderHelper.getClasses();
			for (Class<?> c : classes) {
				Gate gate = (Gate) c.getDeclaredConstructor().newInstance();
				addToCategory(gate);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IOException |
                 IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}

        loadModules();
	}

	private static void loadModules() {
		/*
		 * module part
		 */
		File mods = new File(App.getModulePath());
		// list of filenames in modules dir
		String[] list = mods.list();
        assert list != null;
        Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);
		// prepare list for all loaded modules
		ArrayList<String> loadedModules = new ArrayList<>();
		// prepare list of modules with sublist of needed modules
		Map<String, ArrayList<String>> modules = new HashMap<>();

		// now collect all modules with their needed modules
        for (String filename : list) {
            if (filename.endsWith(App.MODULE_FILE_SUFFIX)) {
                String type = new File(filename).getName();
                type = type.substring(0, type.lastIndexOf("."));
                modules.put(type, XMLLoader.getModuleListFromFile(App.getModulePath() + "/" + filename));
            }
        }
		int maxTries = modules.size();
		int tries = 0;
		while (tries < maxTries && maxTries != loadedModules.size()) {
			for (String modname : modules.keySet()) {
				boolean load = true;
				for (String neededModuleName : modules.get(modname)) {
					if (!loadedModules.contains(neededModuleName.toLowerCase())) {
						load = false;
						break;
					}
				}
				if (load && !loadedModules.contains(modname.toLowerCase())) {
					Module mod = new Module(modname);
					addToCategory(mod);
					loadedModules.add(modname.toLowerCase());
				}
			}
			tries++;
		}
	}

	public static String getModulePath() {
		String fName = new File("").getAbsolutePath() + "/modules/";
		final File f = new File(fName);
		if (f.exists() && f.isDirectory()) {
			return f.getAbsolutePath() + "/";
		} else {
			Dialogs.messageDialog(null, "Directory 'modules' not found.\nPlease run the program from its directory");
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
			Dialogs.messageDialog(null, "Directory 'circuits' not found.\nPlease run the program from its directory");
			System.exit(0);
		}
		return "";
	}

	public static Gate getGate(String type) {
		for (Category cat : cats) {
			for (Gate g : cat.getGates()) {
				if (g.type.toLowerCase().equals(type)) {
					return g;
				}
			}
		}
		return null;
	}

	/**
	 * Main method
	 */
	public static void main(String[] args) {
		instance = new App();
        ShortCuts.init();
	}
}

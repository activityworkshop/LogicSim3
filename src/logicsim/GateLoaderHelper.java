package logicsim;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class GateLoaderHelper {

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 *
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	static List<Class<?>> getClasses() throws ClassNotFoundException, IOException {
		List<Class<?>> classes = getClassesOutsideJar();
        classes.add(gates.ALU8.class);
		classes.add(gates.AND.class);
        classes.add(gates.BinDisp.class);
        classes.add(gates.BinIn.class);
        classes.add(gates.Buffer.class);
        classes.add(gates.CLK.class);
        classes.add(gates.Counter.class);
        classes.add(gates.CtrlLogic.class);
        classes.add(gates.DFlipFlop.class);
        classes.add(gates.Display4d.class);
        classes.add(gates.DRFlipFlop.class);
        classes.add(gates.DSRFlipFlop.class);
        classes.add(gates.EQU.class);
        classes.add(gates.JKFlipFlop.class);
        classes.add(gates.LED.class);
        classes.add(gates.LED8.class);
        classes.add(gates.Memory128.class);
        classes.add(gates.MonoFlop.class);
        classes.add(gates.NAND.class);
        classes.add(gates.NOR.class);
        classes.add(gates.NOT.class);
        classes.add(gates.OffDelay.class);
        classes.add(gates.OnDelay.class);
        classes.add(gates.OR.class);
        classes.add(gates.ProgramCounter4.class);
        classes.add(gates.Register4.class);
        classes.add(gates.Register8.class);
        classes.add(gates.SevenSegment.class);
        classes.add(gates.SevenSegmentDriver.class);
        classes.add(gates.SRFlipFlop.class);
        classes.add(gates.SRLatch.class);
        classes.add(gates.SRRFlipFlop.class);
        classes.add(gates.Switch.class);
        classes.add(gates.Switch4.class);
        classes.add(gates.Switch8.class);
        classes.add(gates.TEST.class);
        classes.add(gates.TextLabel.class);
        classes.add(gates.TriStateOutput.class);
        classes.add(gates.XOR.class);
		return classes;
	}

    public static List<Class<?>> getClassesOutsideJar() {
        List<String> classNames = new ArrayList<>();

        File directory = new File("gates");
        if (!directory.exists()) {
            return new ArrayList<>();
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (!file.getName().endsWith(".class"))
                continue;
            classNames.add(file.getName().substring(0, file.getName().length() - 6));
        }

        ArrayList<Class<?>> classes = new ArrayList<>();

        File file = new File(".");
        try {
            // Convert File to URL
            URL url = file.toURI().toURL();
            URL[] urls = new URL[] { url };

            // Create a new class loader with the directory
            URLClassLoader cl = new URLClassLoader(urls);
            for (String className : classNames) {
                Class<?> cls = cl.loadClass("gates." + className);
                classes.add(cls);
            }
            cl.close();
        } catch (MalformedURLException e) {
        } catch (ClassNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

	/**
	 * 
	 * @param g
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Gate create(Gate g) {
		Gate gate = null;
		try {
			Class<? extends Gate> c = g.getClass();
			Object obj;
			if (g instanceof Module) {
				Class[] cArg = new Class[] { String.class, Boolean.TYPE };
				obj = c.getDeclaredConstructor(cArg).newInstance(g.type, true);
				gate = (Module) obj;
			} else {
				obj = c.getDeclaredConstructor().newInstance();
				gate = (Gate) obj;
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e.getMessage());
		} catch (SecurityException e) {
			throw new RuntimeException(e.getMessage());
		}
	
		return gate;
	}

}

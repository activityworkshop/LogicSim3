package logicsim;

import logicsim.gates.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class GateLoaderHelper {

    /**
     * Scans all classes accessible from the context class loader which belong to
     * the given package and subpackages.
     */
    static List<Class<?>> getClasses() throws ClassNotFoundException, IOException {
        List<Class<?>> classes = getClassesOutsideJar();
        classes.add(ALU8.class);
        classes.add(AND.class);
        classes.add(BinDisp.class);
        classes.add(BinIn.class);
        classes.add(Buffer.class);
        classes.add(CLK.class);
        classes.add(Counter.class);
        classes.add(CtrlLogic.class);
        classes.add(DFlipFlop.class);
        classes.add(Display4d.class);
        classes.add(DRFlipFlop.class);
        classes.add(DSRFlipFlop.class);
        classes.add(EQU.class);
        classes.add(JKFlipFlop.class);
        classes.add(LED.class);
        classes.add(LED8.class);
        classes.add(Memory128.class);
        classes.add(MonoFlop.class);
        classes.add(NAND.class);
        classes.add(NOR.class);
        classes.add(NOT.class);
        classes.add(OffDelay.class);
        classes.add(OnDelay.class);
        classes.add(OR.class);
        classes.add(ProgramCounter4.class);
        classes.add(Register4.class);
        classes.add(Register8.class);
        classes.add(SevenSegment.class);
        classes.add(SevenSegmentDriver.class);
        classes.add(SRFlipFlop.class);
        classes.add(SRLatch.class);
        classes.add(SRRFlipFlop.class);
        classes.add(Switch.class);
        classes.add(Switch4.class);
        classes.add(Switch8.class);
        classes.add(TextLabel.class);
        classes.add(TriStateOutput.class);
        classes.add(XOR.class);
        return classes;
    }

    public static List<Class<?>> getClassesOutsideJar() {
        List<String> classNames = new ArrayList<>();

        File directory = new File("logicsim/gates");
        if (!directory.exists()) {
            return new ArrayList<>();
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (!file.getName().endsWith(".class")) {
                continue;
            }
            classNames.add(file.getName().substring(0, file.getName().length() - 6));
        }

        ArrayList<Class<?>> classes = new ArrayList<>();

        File file = new File(".");
        try {
            // Convert File to URL
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};

            // Create a new class loader with the directory
            URLClassLoader cl = new URLClassLoader(urls);
            for (String className : classNames) {
                Class<?> cls = cl.loadClass("gates." + className);
                classes.add(cls);
            }
            cl.close();
        } catch (MalformedURLException | ClassNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return classes;
    }

    public static Gate create(Gate g) {
        try {
            Class<? extends Gate> c = g.getClass();
            if (g instanceof Module) {
                Class<?>[] cArg = new Class<?>[]{String.class, Boolean.TYPE};
                return c.getDeclaredConstructor(cArg).newInstance(g.type, true);
            } else {
                return c.getDeclaredConstructor().newInstance();
            }
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

package logicsim.module;

import logicsim.Dialogs;
import logicsim.LogicSimFile;
import logicsim.gatelist.GateDefinition;
import logicsim.localization.I18N;
import logicsim.localization.Lang;
import logicsim.xml.XMLLoader;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ModuleLoader {

    public static Collection<GateDefinition> loadModule(File moduleFile) {
        final HashMap<String, GateDefinition> modules = new HashMap<>();
        final String requestedFilename = moduleFile.getName();
        modules.put(requestedFilename, null);
        while (isIncomplete(modules)) {
            HashSet<String> missingModules = new HashSet<>();
            boolean stillFinding = false;
            for (String filename : modules.keySet()) {
                if (modules.get(filename) != null) {
                    continue;
                }
                GateDefinition loadedDef = loadModule(moduleFile.getParentFile(), filename,
                        getFoundModules(modules), missingModules);
                if (loadedDef != null) {
                    stillFinding = true;
                    modules.put(filename, loadedDef); // TODO: Maybe this causes an exception?
                }
            }
            if (!stillFinding && missingModules.isEmpty()) {
                return null;
            }
            for (String missingModule : missingModules) {
                if (!modules.containsKey(missingModule)) {
                    modules.put(missingModule, null);
                }
            }
        }
        return modules.values();
    }

    private static GateDefinition loadModule(File moduleFolder, String filename, Set<String> foundModules,
                                             Set<String> missingModules) {
        File fileToLoad = new File(moduleFolder, filename);
        try {
            LogicSimFile lsFile = XMLLoader.verifyXmlFile(fileToLoad, foundModules, missingModules);
            if (lsFile == null) {
                return null;
            }
            foundModules.add(filename);
            return makeGateDefinition(fileToLoad, lsFile);
        } catch (RuntimeException | IOException x) {
            // TODO: Show an error if the main module failed to load
//            JOptionPane.showMessageDialog(null, I18N.tr(Lang.READERROR) + ": " + x.getMessage());
            return null;
        }
    }

    private static GateDefinition makeGateDefinition(File file, LogicSimFile lsFile) {
        if (lsFile == null || lsFile.circuit == null) {
            return null;
        }
        if (lsFile.getErrorString() != null) {
            Dialogs.messageDialog(null, lsFile.getErrorString());
        }

        String label = lsFile.getLabel();
        if (label == null || label.isEmpty()) {
            label = lsFile.extractFileName();
        }
        if (label.length() > 15) {
            label = label.substring(0, 12) + "...";
        }
        final String description = lsFile.getDescription();

        MODIN moduleIn = lsFile.circuit.getModIn();
        MODOUT moduleOut = lsFile.circuit.getModOut();
        if (moduleIn == null || moduleOut == null) {
            JOptionPane.showMessageDialog(null, I18N.tr(Lang.NOMODULE));
            throw new RuntimeException("no module - does not contain both module i/o components: " + lsFile.fileName);
        }
        final String moduleType = label;
        return new GateDefinition("modules", moduleType.toLowerCase(), 0,
                () -> new Module(file, moduleType), label, description);
    }

    private static Set<String> getFoundModules(HashMap<String, GateDefinition> modules) {
        return modules.keySet().stream()
                .filter(m -> modules.get(m) != null)
                .collect(Collectors.toSet());
    }

    private static boolean isIncomplete(HashMap<String, GateDefinition> modules) {
        for (String key : modules.keySet()) {
            if (modules.get(key) == null) {
                return true;
            }
        }
        return modules.isEmpty();
    }
}

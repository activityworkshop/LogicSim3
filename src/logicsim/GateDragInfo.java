package logicsim;

import logicsim.gatelist.GateDefinition;

import java.awt.datatransfer.DataFlavor;

/**
 * Datenobjekt für Drag-and-Drop aus der Gate-Liste ins Canvas.
 * Enthält den Gate-Prototyp und die gewünschte Eingangsanzahl.
 */
public class GateDragInfo {
    public static final DataFlavor FLAVOR = new DataFlavor(
            DataFlavor.javaJVMLocalObjectMimeType + ";class=" + GateDragInfo.class.getName(),
            "GateDragInfo");

    private final GateDefinition definition;
    private final int numInputs;

    public GateDragInfo(GateDefinition definition, int numInputs) {
        this.definition = definition;
        this.numInputs = numInputs;
    }

    public GateDefinition getDefinition() {
        return definition;
    }

    public int getNumInputs() {
        return numInputs;
    }
}

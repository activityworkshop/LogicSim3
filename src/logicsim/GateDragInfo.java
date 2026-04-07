package logicsim;

import logicsim.gatelist.GateDefinition;

import java.awt.datatransfer.DataFlavor;

/**
 * Datenobjekt für Drag-and-Drop aus der Gate-Liste ins Canvas.
 * Enthält die Gate-Definition und die gewünschte Eingangsanzahl.
 */
public record GateDragInfo(GateDefinition definition, int numInputs) {
    public static final DataFlavor FLAVOR = new DataFlavor(
            DataFlavor.javaJVMLocalObjectMimeType + ";class=" + GateDragInfo.class.getName(),
            "GateDragInfo");

}

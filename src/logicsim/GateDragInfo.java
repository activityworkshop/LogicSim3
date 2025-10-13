package logicsim;

import java.awt.datatransfer.DataFlavor;

/**
 * Datenobjekt für Drag-and-Drop aus der Gate-Liste ins Canvas.
 * Enthält den Gate-Prototyp und die gewünschte Eingangsanzahl.
 */
public class GateDragInfo {
    public static final DataFlavor FLAVOR = new DataFlavor(
            DataFlavor.javaJVMLocalObjectMimeType + ";class=" + GateDragInfo.class.getName(),
            "GateDragInfo");

    private final Gate prototype;
    private final int numInputs;

    public GateDragInfo(Gate prototype, int numInputs) {
        this.prototype = prototype;
        this.numInputs = numInputs;
    }

    public Gate getPrototype() {
        return prototype;
    }

    public int getNumInputs() {
        return numInputs;
    }
}

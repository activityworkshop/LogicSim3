package logicsim;

import java.awt.datatransfer.DataFlavor;

/**
 * Payload-Objekt für Drag & Drop eines Gates aus der Liste ins Canvas.
 * Enthält das Gate-Prototype-Objekt und die gewünschte Eingangsanzahl.
 */
public class GateDragInfo {
    public static final String MIME = DataFlavor.javaJVMLocalObjectMimeType + ";class=logicsim.GateDragInfo";
    public static final DataFlavor FLAVOR = createFlavor();

    private static DataFlavor createFlavor() {
        try {
            return new DataFlavor(MIME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

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


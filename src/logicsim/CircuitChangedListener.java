package logicsim;

import logicsim.ui.ClickPoint;

public interface CircuitChangedListener {
	public void changedCircuit();

	public void changedStatusText(String text);

	public void changedZoomPos(double zoom, ClickPoint pos);

	public void setAction(int action);

	public void needsRepaint(CircuitPart circuitPart);
}

package logicsim;

import logicsim.controllers.Action;
import logicsim.ui.ClickPoint;

public interface CircuitChangedListener {

	public void changedCircuit();

	public void changedStatusText(String text);

	public void changedZoomPos(double zoom, ClickPoint pos);

	public void setAction(Action action);

	public void needsRepaint(CircuitPart circuitPart);
}

package logicsim;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.Serial;

public class LSMouseEvent extends MouseEvent {

	public final int lsAction;
	public final CircuitPart[] activeParts;

	public LSMouseEvent(MouseEvent e, int lsAction, CircuitPart[] currentParts) {
		super((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getX(), e.getY(),
				e.getClickCount(), e.isPopupTrigger());
		this.lsAction = lsAction;
		this.activeParts = currentParts;
	}
}

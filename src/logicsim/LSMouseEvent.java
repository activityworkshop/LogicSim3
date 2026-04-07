package logicsim;

import logicsim.controllers.Action;

import java.awt.Component;
import java.awt.event.MouseEvent;

public class LSMouseEvent extends MouseEvent {

	public final Action lsAction;
	public final CircuitPart[] activeParts;

	public LSMouseEvent(MouseEvent e, Action lsAction, CircuitPart[] currentParts) {
		super((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getX(), e.getY(),
				e.getClickCount(), e.isPopupTrigger());
		this.lsAction = lsAction;
		this.activeParts = currentParts;
	}
}

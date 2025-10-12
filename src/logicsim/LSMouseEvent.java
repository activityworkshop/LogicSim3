package logicsim;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.io.Serial;

public class LSMouseEvent extends MouseEvent {

	public int lsAction;
	public CircuitPart[] activeParts;

	@Serial
    private static final long serialVersionUID = -5200375901758197955L;

	public LSMouseEvent(MouseEvent e, int lsAction, CircuitPart[] currentParts) {
		super((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiersEx(), e.getX(), e.getY(),
				e.getClickCount(), e.isPopupTrigger());
		this.lsAction = lsAction;
		this.activeParts = currentParts;
	}

}

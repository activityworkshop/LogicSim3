package logicsim;

import logicsim.controllers.Action;
import logicsim.controllers.ShortCuts;
import logicsim.localization.I18N;
import logicsim.localization.Lang;
import logicsim.module.Module;
import logicsim.ui.ClickPoint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;

import javax.swing.event.MouseInputAdapter;

public class LSPanel extends Viewer implements CircuitChangedListener, LSRepaintListener {

	private final ClickPoint dragStartPoint = new ClickPoint();
	private final ClickPoint currentMousePoint = new ClickPoint();

	public class LogicSimPainterGraphics implements Painter {
		@Override
		public void paint(Graphics2D g2, AffineTransform at, int w, int h) {
			// set anti-aliasing
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g2.transform(at);
			if (LSProperties.getInstance().getPropertyBoolean(LSProperties.PAINTGRID, true) && scaleX > 0.7f) {
				int startX = CircuitPart.round((int) Math.round(getTransformer().screenToWorldX(0)));
				int startY = CircuitPart.round((int) Math.round(getTransformer().screenToWorldY(0)));
				int endX = (int) getTransformer().screenToWorldX(w + 9);
				int endY = (int) getTransformer().screenToWorldY(h + 9);
				g2.setColor(gridColor);
				Path2D grid = new Path2D.Double();
				g2.setStroke(new BasicStroke(1));
				for (int x = startX; x < endX; x += 10) {
					for (int y = startY; y < endY; y += 10) {
						grid.moveTo(x, y);
						grid.lineTo(x, y);
					}
				}
				g2.draw(grid);
			}

			draw(g2);

			// redraw selected parts so that they are in the foreground
			for (CircuitPart part : circuit.getSelected()) {
				part.draw(g2);
			}

			if (currentAction == Action.SELECT && selectRect != null) {
				g2.setStroke(new BasicStroke());
				g2.setColor(new Color(0, 115, 255));
				g2.drawRect(selectRect.getMinX(), selectRect.getMinY(), selectRect.getWidth(), selectRect.getHeight());
				g2.setColor(new Color(0, 115, 255, 115));
				g2.fillRect(selectRect.getMinX(), selectRect.getMinY(), selectRect.getWidth(), selectRect.getHeight());
			}
		}
	}

	/**
	 * A class summarizing the mouse interaction for this viewer.
	 */
	private class MouseControl extends MouseInputAdapter
			implements MouseListener, MouseMotionListener, MouseWheelListener {

		@Override
		public void mouseDragged(MouseEvent e) {
			e = convertToWorld(e);
			if (!dragStartPoint.isSet()) {
				dragStartPoint.set(e.getX(), e.getY());
				selectRect = new Rectangle();
			}
			currentMousePoint.set(e.getX(), e.getY());
			if (currentAction == Action.SELECT) {
				notifyZoomPos(scaleX, currentMousePoint);
				selectRect.clear();
				selectRect.addPoint(currentMousePoint.getX(), currentMousePoint.getY());
				selectRect.addPoint(dragStartPoint.getX(), dragStartPoint.getY());
				repaint();
				return;
			}

			CircuitPart[] parts = circuit.getSelected();
			if (parts.length == 0) {
				// drag world
				int dx = currentMousePoint.getX() - dragStartPoint.getX();
				int dy = currentMousePoint.getY() - dragStartPoint.getY();
				translate(dx, dy);
				return;
			}

			// don't drag elements in simulation mode
			if (Simulation.getInstance().isRunning()) {
				return;
			}

			// If shift is pressed, restrict coordinates to orthogonal movements
			if (e.isShiftDown()) {
				currentMousePoint.setOrthogonal(dragStartPoint);
			}
			// drag parts
			notifyZoomPos(scaleX, currentMousePoint);
			for (CircuitPart part : parts) {
				part.mouseDragged(dragStartPoint, currentMousePoint);

				if (LSProperties.getInstance().getPropertyBoolean(LSProperties.AUTOWIRE, true)) {
					// check if currentpart is a gate and if any output touches another part's input pin
					if (part instanceof Gate gate) {
						checkAutowiring(gate);
					}
				}
			}
			fireCircuitChanged();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			e = convertToWorld(e);

			currentMousePoint.set(CircuitPart.round(e.getX()), CircuitPart.round(e.getY()));
			notifyZoomPos(scaleX, currentMousePoint);

			CircuitPart[] parts = circuit.getSelected();
			if (parts.length == 1 && parts[0] instanceof Wire wire) {
				if (wire.isNotFinished()) {
					if (e.isShiftDown()) {
						// pressed SHIFT while moving and drawing wire
						WirePoint wp = wire.getLastPoint();
						currentMousePoint.setOrthogonal(wp.getX(), wp.getY());
					}
					// the selected wire is unfinished - force draw
					wire.setTempPoint(currentMousePoint.getX(), currentMousePoint.getY());
					repaint();
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			e = convertToWorld(e);
			ClickPoint clickPoint = new ClickPoint();
			clickPoint.set(CircuitPart.round(e.getX()), CircuitPart.round(e.getY()));

			boolean simRunning = Simulation.getInstance().isRunning();
			if (simRunning) {
				currentAction = Action.NONE;
			} else if (currentAction == Action.NONE && e.getButton() == MouseEvent.BUTTON3) {
				currentAction = Action.SELECT;
				selectRect = new Rectangle();
			}

			if (currentAction == Action.SELECT) {
				selectRect = new Rectangle(e.getX(), e.getY(), 0, 0);
			}

			CircuitPart[] parts = circuit.getSelected();
			CircuitPart clickedPart = circuit.findPartAt(e.getX(), e.getY());

			if (currentAction == Action.DELPOINT && clickedPart instanceof WirePoint && clickedPart.parent instanceof Wire) {
				clickedPart.parent.mousePressed(new LSMouseEvent(e, Action.DELPOINT, null));
			}
			if (!simRunning && clickedPart instanceof Pin && !e.isAltDown() && currentAction == Action.NONE) {
				// we start a new wire
				currentAction = Action.ADDWIRE;
			}

			if (currentAction == Action.ADDWIRE) {
				WirePoint wp = null;
				Wire newWire = null;
				switch (clickedPart) {
					case null:
						// empty space
						wp = new WirePoint(clickPoint.getX(), clickPoint.getY());
						break;
					case Wire clickedWire: {
						// put a wirepoint at this position
						int pt = clickedWire.isAt(e.getX(), e.getY());
						clickedWire.insertPointAfter(pt, clickPoint.getX(), clickPoint.getY());
						clickedPart = clickedWire.findPartAt(clickPoint.getX(), clickPoint.getY());
						wp = (WirePoint) clickedPart;
						break;
					}
					case WirePoint wirePoint:
						wp = wirePoint;
						break;
					case Pin p: {
						newWire = new Wire(p, null);
						if (circuit.addWire(newWire)) {
							p.connect(newWire);
						}
						break;
					}
					default: break;
				}
				if (newWire == null) {
					newWire = new Wire(wp, null);
					if (circuit.addWire(newWire)) {
						assert wp != null;
						wp.connect(newWire);
					}
				}
				fireStatusText(Lang.WIREEDIT);
				circuit.deselectAll();
				newWire.select();
				fireCircuitChanged();
				currentAction = Action.EDITWIRE;
				return;
			}

			if (currentAction == Action.EDITWIRE) {
				Wire wire = circuit.getUnfinishedWire();
				if (e.isShiftDown()) {
					// pressed SHIFT while clicking to edit a wire
					WirePoint wp = wire.getLastPoint();
					clickPoint.setOrthogonal(wp.getX(), wp.getY());
				}
				switch (clickedPart) {
					case null:
						// empty space clicked
						wire.addPoint(clickPoint.getX(), clickPoint.getY());
						break;
					case Pin pin: {
						wire.setTo(pin);
						wire.getTo().connect(wire);
						wire.finish();
						currentAction = Action.NONE;
						fireStatusText("");
						fireCircuitChanged();
						break;
					}
					case Wire clickedWire: {
						if (clickedWire.equals(wire)) {
							return;
						}
						int pt = clickedWire.isAt(e.getX(), e.getY());
						clickedWire.insertPointAfter(pt, clickPoint.getX(), clickPoint.getY());
						clickedPart = clickedWire.findPartAt(clickPoint.getX(), clickPoint.getY());
						wire.setTo(clickedPart);
						wire.getTo().connect(wire);
						wire.finish();
						currentAction = Action.NONE;
						fireStatusText("");
						fireCircuitChanged();
						break;
					}
					case WirePoint clickedWP: {
						// check if the clicked point belongs to another wire
						if (clickedWP.parent != null && clickedWP.parent.equals(wire)) {
							// the clicked wirepoint belongs to the editing wire...
							// so check if we clicked the last point of the wire to finish it
							WirePoint lp = wire.getLastPoint();
							if (lp.getX() == clickPoint.getX() && lp.getY() == clickPoint.getY()) {
								// it is the same point as the last one
								wire.removeLastPoint();
								wire.setTo(new WirePoint(clickPoint.getX(), clickPoint.getY()));
								wire.getTo().connect(wire);
								wire.finish();
								currentAction = Action.NONE;
								fireStatusText("");
								fireCircuitChanged();
							} else {
								// shorten the wire and delete circles
								wire.addPoint(clickPoint.getX(), clickPoint.getY());
							}
						} else {
							// wirepoint belongs to another wire
							wire.setTo(clickedWP);
							wire.getTo().connect(wire);
							wire.finish();
							currentAction = Action.NONE;
							fireStatusText("");
							fireCircuitChanged();
						}
						break;
					}
					default: break;
				}
				repaint();
				return;
			}

			if (clickedPart == null) {
				// empty space has been clicked
				circuit.deselectAll();
				repaint();
				fireStatusText("");
				return;
			}
			// check if the part is a connector
			if (clickedPart instanceof Pin pin && !e.isAltDown() && !simRunning) {
				fireStatusText(I18N.tr(Lang.PIN) + " (" + clickedPart.getId() + ")");
				// modify input (inverted or high or low or revert to normal type)
				if (pin.isInput()) {
					// 1. if we clicked on an input modificator
					final int pinLevel = switch (currentAction) {
						case Action.PINHIGH -> Pin.HIGH;
						case Action.PINLOW -> Pin.LOW;
						case Action.PININVERTED -> Pin.INVERTED;
						case Action.PINNORMAL -> Pin.NORMAL;
						default -> -1;
					};
					if (pinLevel != -1) {
						pin.setLevelType(pinLevel);
						pin.changedLevel(new LSLevelEvent(new Wire(null, null), pin.level, true));
						currentAction = Action.NONE;
						fireStatusText("");
						fireCircuitChanged();
						return;
					}
				}
			}
			if (clickedPart instanceof Gate) {
				String type = ((Gate) clickedPart).type;
				if (clickedPart instanceof Module)
					fireStatusText(I18N.tr(Lang.MODULE) + " (" + clickedPart.getId() + ")");
				else
					fireStatusText(I18N.getString(type, I18N.DESCRIPTION) + " (" + clickedPart.getId() + ")");

				if (parts.length > 0 && !simRunning) {
					// check if we clicked a new gate
					if (!clickedPart.isSelected()) {
						clickedPart.select();
						if (!e.isShiftDown()) {
							circuit.deselectAll();
						}
						parts = circuit.getSelected();
					}
				}
			} else if (clickedPart instanceof Wire && !simRunning) {
				String s = clickedPart.getProperty(CircuitPart.TEXT);
				String desc = I18N.tr(Lang.WIRE);
				if (s != null)
					desc += ": " + s;
				desc += " (" + clickedPart.getId() + ")";
				fireStatusText(desc);
				circuit.deselectAll();
				clickedPart.select();
			} else if (clickedPart instanceof WirePoint) {
				fireStatusText(I18N.tr(Lang.WIREPOINT) + " (" + clickedPart.getId() + ")");
				circuit.deselectAll();
				clickedPart.select();
			}

			clickedPart.mousePressed(new LSMouseEvent(e, currentAction, parts));
			currentAction = Action.NONE;
			fireStatusText("");
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			e = convertToWorld(e);
			int x = e.getPoint().x;
			int y = e.getPoint().y;
			dragStartPoint.reset();
			LSPanel.this.requestFocusInWindow();

			if (currentAction == Action.SELECT && selectRect != null) {
				CircuitPart[] parts = circuit.findParts(selectRect);
				for (CircuitPart part : parts) {
					if (part instanceof Wire w) {
						if (w.getTo() instanceof WirePoint)
							w.getTo().select();
						if (w.getFrom() instanceof WirePoint)
							w.getFrom().select();
					}
				}
				fireStatusText(String.format(I18N.tr(Lang.PARTSSELECTED), parts.length));
				currentAction = Action.NONE;
				fireStatusText("");
				selectRect = null;
				repaint();
				return;
			}
			CircuitPart[] parts = circuit.getSelected();
			for (CircuitPart part : parts) {
				part.mouseReleased(x, y);
				if (part instanceof WirePoint wp && part.parent == null) {
					circuit.checkWirePoint(wp);
				}
			}
			CircuitPart cp = circuit.findPartAt(e.getX(), e.getY());
			if (cp != null)
				cp.mouseReleased(x, y);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			zoomBy(e.getX(), e.getY(), -e.getWheelRotation() * zoomingSpeed);
			currentMousePoint.set(e.getX(), e.getY());
			notifyZoomPos(scaleX, currentMousePoint);
		}
	}

	/** Check the pins on the current gate and see if any pins line up with other gates */
	private void checkAutowiring(Gate gate) {
		for (Pin pin : gate.pins) {
			// autowire unconnected pins only
			if (pin.isConnected()) {
				continue;
			}
			final int x = pin.getX();
			final int y = pin.getY();
			for (Gate otherGate : circuit.getGates()) {
				if (otherGate == gate) {
					continue;
				}
				CircuitPart cp = otherGate.findPartAt(x, y);
				if (cp instanceof Pin p
						&& pin.isInput() == p.isOutput()
						&& !pin.isConnected()) {
					// put new wire between pin and p or between p and pin
					final Wire w = pin.isOutput() ? new Wire(pin, p) : new Wire(p, pin);
					if (circuit.addWire(w)) {
						p.connect(w);
						pin.connect(w);
					}
				}
			}
		}
	}

	private static final Color gridColor = Color.black;

	private CircuitChangedListener changeListener;
	public Circuit circuit = new Circuit();

	// current mode
	private Action currentAction = Action.NONE;

	/** used for selecting elements */
	private Rectangle selectRect;

	public LSPanel() {
		final Dimension panelSize = new Dimension(1280, 1024);
		setSize(panelSize);
		setPreferredSize(panelSize);
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		circuit.setRepaintListener(this);

		setPainter(new LogicSimPainterGraphics());

		MouseControl mouseControl = new MouseControl();
		addMouseListener(mouseControl);
		addMouseMotionListener(mouseControl);
		addMouseWheelListener(mouseControl);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				ShortCuts.keyPressed(e);
				myKeyPressed(e);
			}
		});
	}

	@Override
	public void changedCircuit() {
		if (changeListener != null)
			changeListener.changedCircuit();
		repaint();
	}

	@Override
	public void changedStatusText(String text) {
		// just transfer to parent
		changeListener.changedStatusText(text);
	}

	@Override
	public void changedZoomPos(double zoom, ClickPoint pos) {
	}

	public void clear() {
		circuit.deselectAll();
		currentAction = Action.NONE;
		circuit.clear();
		repaint();
	}

	private MouseEvent convertToWorld(MouseEvent e) {
		int x = (int) (getTransformer().screenToWorldX(e.getX()));
		int y = (int) (getTransformer().screenToWorldY(e.getY()));

		return new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiersEx(), x, y,
				e.getClickCount(), e.isPopupTrigger(), e.getButton());
	}

	public void draw(Graphics2D g2) {
		// draw panels first
		for (CircuitPart gate : circuit.getGates()) {
			gate.draw(g2);
		}
		// then wires
		for (CircuitPart wire : circuit.getWires()) {
			wire.draw(g2);
		}
	}

	/**
	 * check for escape, delete and space key
	 */
	protected void myKeyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		CircuitPart[] parts = circuit.getSelected();
		if (parts.length == 0)
			return;

		if (keyCode == KeyEvent.VK_ESCAPE) {
			if (currentAction == Action.EDITWIRE) {
				Wire w = (Wire) parts[0];
				int pointsOfWire = w.removeLastPoint();
				if (pointsOfWire == 0) {
					currentAction = Action.NONE;
					// delete wire
					w.disconnect();
					circuit.remove(w);
					circuit.deselectAll();
					fireStatusText(Lang.ABORTED);
				}
			} else if (currentAction == Action.ADDPOINT || currentAction == Action.DELPOINT
					|| currentAction == Action.SELECT) {
				currentAction = Action.NONE;
				fireStatusText(Lang.ABORTED);
			} else if (parts.length > 1) {
				circuit.deselectAll();
			}
			repaint();
			return;
		}
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT
				|| keyCode == KeyEvent.VK_RIGHT) {
			int dx = 0;
			int dy = 0;
			if (keyCode == KeyEvent.VK_UP)
				dy -= 10;
			else if (keyCode == KeyEvent.VK_DOWN)
				dy += 10;
			else if (keyCode == KeyEvent.VK_LEFT)
				dx -= 10;
			else
				dx += 10;
			for (CircuitPart part : parts)
				part.moveBy(dx, dy);
			fireCircuitChanged();
			return;
		}

		if (keyCode == KeyEvent.VK_DELETE || keyCode == KeyEvent.VK_BACK_SPACE) {
			if (circuit.remove(parts)) {
				currentAction = Action.NONE;
				if (parts.length == 1) {
					fireStatusText(I18N.tr(Lang.PARTDELETED));
				} else {
					fireStatusText(I18N.tr(Lang.PARTSDELETED, String.valueOf(parts.length)));
				}
				fireCircuitChanged();
				repaint();
				return;
			}
			return;
		}

		if (keyCode == KeyEvent.VK_SPACE) {
			CircuitPart[] selected = circuit.getSelected();
			if (selected.length != 1) {
				return;
			}
			if (selected[0] instanceof Gate g) {
				g.interact();
			}
			repaint();
		}
	}

	@Override
	public void needsRepaint(CircuitPart circuitPart) {
		repaint();
	}

	private void fireCircuitChanged() {
		if (changeListener != null) {
			changeListener.changedCircuit();
		}
		repaint();
	}

	private void fireStatusText(Lang token) {
		fireStatusText(I18N.tr(token));
	}

	private void fireStatusText(String msg) {
		if (changeListener != null) {
			changeListener.changedStatusText(msg);
		}
	}

	private void notifyZoomPos(double zoom, ClickPoint mousePos) {
		if (changeListener != null) {
			changeListener.changedZoomPos(zoom, mousePos);
		}
	}

	/**
	 * rotate a gate if selected
	 */
	public void rotateSelected() {
		CircuitPart[] parts = circuit.getSelected();
		if (parts.length == 0) return;
		for (CircuitPart part : parts) {
			if (part instanceof Gate) {
				((Gate) part).rotate();
			}
		}
		fireCircuitChanged();
	}

	public void setAction(CircuitPart g) {
		if (g != null) {
			circuit.deselectAll();
			// place new gate
			int posX = (int) -offsetX / 10 * 10 + 20;
			int posY = (int) -offsetY / 10 * 10 + 20;
			while (circuit.isPartAtCoordinates(posX, posY)) {
				posX += 40;
				posY += 40;
			}
			g.moveTo(posX, posY);
			circuit.addGate((Gate) g);
			g.select();

			fireStatusText(Lang.STATUS_GATE_ADDED);
			fireCircuitChanged();
			repaint();
		}
	}

	public void setAction(Action action) {
		switch (action) {
			case Action.ADDPOINT:
				fireStatusText(Lang.ADDPOINT);
				break;
			case Action.DELPOINT:
				fireStatusText(Lang.REMOVEPOINT);
				break;
			case Action.PINHIGH:
				fireStatusText(Lang.INPUTHIGH);
				break;
			case Action.PINLOW:
				fireStatusText(Lang.INPUTLOW);
				break;
			case Action.PINNORMAL:
				fireStatusText(Lang.INPUTNORM);
				break;
			case Action.PININVERTED:
				fireStatusText(Lang.INPUTINV);
				break;
		}
		currentAction = action;
	}

	public void setChangeListener(CircuitChangedListener changeListener) {
		this.changeListener = changeListener;
	}

	/**
	 * less zoom
	 */
	public void zoomOut() {
		int x = (int) getTransformer().screenToWorldX(getWidth() / 2);
		int y = (int) getTransformer().screenToWorldY(getHeight() / 2);
		zoomBy(x, y, -0.2f);
		currentMousePoint.set(x, y);
		notifyZoomPos(scaleX, currentMousePoint);
	}

	/**
	 * more zoom
	 */
	public void zoomIn() {
		int x = (int) getTransformer().screenToWorldX(getWidth() / 2);
		int y = (int) getTransformer().screenToWorldY(getHeight() / 2);
		zoomBy(x, y, 0.2f);
		currentMousePoint.set(x, y);
		notifyZoomPos(scaleX, currentMousePoint);
	}

	public void addGateAt(Gate g, int worldX, int worldY) {
		if (g == null) return;
		if (Simulation.getInstance().isRunning()) return;
		circuit.deselectAll();
		int posX = CircuitPart.round(worldX);
		int posY = CircuitPart.round(worldY);
		while (circuit.isPartAtCoordinates(posX, posY)) {
			posX += 40;
			posY += 40;
		}
		g.moveTo(posX, posY);
		circuit.addGate(g);
		g.select();
		fireStatusText(Lang.STATUS_GATE_ADDED);
		fireCircuitChanged();
		repaint();
	}
}

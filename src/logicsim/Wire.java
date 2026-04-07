package logicsim;

import logicsim.controllers.Action;
import logicsim.ui.ClickPoint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * Wire representation
 *
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class Wire extends CircuitPart {
	public static float SEL_WIDTH = 3f;

    private static final Color LOW_COLOR = Color.black;
	private static Color HIGH_COLOR = Color.red;

	private static final float LOW_WIDTH = 1.0f;
	private static float HIGH_WIDTH = 1.0f;

	/**
	 * Pin/Wire/WirePoint from which this wire is originating
	 */
	private CircuitPart from;

	/**
	 * list of wire points
	 */
	private final ArrayList<WirePoint> points = new ArrayList<>();

	private ClickPoint tempPoint = null;

	/**
	 * connector to which this wire is targeting
	 */
	private CircuitPart to;

	private boolean level;

	/**
	 * constructor specifying the origin and the end
	 * 
	 * @param fromPart From which Part the wire is coming
	 * @param toPart To which Part the wire is going
	 */
	public Wire(CircuitPart fromPart, CircuitPart toPart) {
		this(0, 0);
		setFrom(fromPart);
		setTo(toPart);
		selected = true;
		loadProperties();
		checkFromTo();
	}

	public Wire(int x, int y) {
		super(x, y);
		loadProperties();
	}

	private void checkFromTo() {
		if (getFrom() instanceof WirePoint fromPoint) {
			fromPoint.show = true;
		}
		if (getTo() instanceof WirePoint toPoint) {
			toPoint.show = true;
		}
		if (getFrom() instanceof Pin pinFrom && getTo() instanceof Pin pinTo
				&& (pinFrom.isInput() == pinTo.isInput())) {
			System.err.println("Warning: Wire goes from " + (pinFrom.isInput() ? "input" : "output") + " of " + pinFrom.parent.getClass().getName()
			+ " to " + (pinTo.isInput() ? "input" : "output") + " of " + pinTo.parent.getClass().getName());
		}
	}

    public void addPoint(int x, int y) {
		WirePoint wp = new WirePoint(x, y);
		addPoint(wp);
	}

	private Path2D convertPointsToPath() {
		Path2D path = new Path2D.Float();
		WirePoint first = getPointFrom();
		path.moveTo(first.getX(), first.getY());

		for (WirePoint point : points) {
			path.lineTo(point.getX(), point.getY());
		}

		if (getTo() != null) {
			path.lineTo(getTo().getX(), getTo().getY());
		} else if (tempPoint != null) {
			path.lineTo(tempPoint.getX(), tempPoint.getY());
		}
		return path;
	}

	@Override
	public void clear() {
		setFrom(null);
		setTo(null);
		tempPoint = null;
		points.clear();
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		final float width;
		if (getLevel()) {
			g2.setColor(HIGH_COLOR);
			width = HIGH_WIDTH;
		} else {
			g2.setColor(LOW_COLOR);
			width = LOW_WIDTH;
		}

		if (selected) {
			g2.setStroke(new BasicStroke(SEL_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		} else {
			g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		}

		g2.draw(convertPointsToPath());

		// draw points
		if (!points.isEmpty()) {
			for (WirePoint point : points) {
				if (selected || point.isSelected() || point.getListeners().size() > 1) {
					point.draw(g2);
				}
			}
		}
		if (getTo() instanceof WirePoint)
			getTo().draw(g2);
		if (getFrom() instanceof WirePoint)
			getFrom().draw(g2);

		if (getTo() == null && tempPoint != null) {
			// add a small red circle
			g2.setPaint(Color.red);
			g2.drawOval(tempPoint.getX() - 1, tempPoint.getY() - 1, 3, 3);
		}

		g2.setColor(Color.black);

		if (!points.isEmpty()) {
			g2.drawString(text, (getFrom().getX() + points.getFirst().getX()) / 2,
					(getFrom().getY() + points.getFirst().getY()) / 2);
		} else {
			if (getFrom() != null && getTo() == null && tempPoint != null) {
				g2.drawString(text, (getFrom().getX() + tempPoint.getX()) / 2, (getFrom().getY() + tempPoint.getY()) / 2);
			} else if (getFrom() != null && getTo() != null) {
				g2.drawString(text, (getFrom().getX() + getTo().getX()) / 2, (getFrom().getY() + getTo().getY()) / 2);
			}
		}
	}

	public CircuitPart findPartAt(int x, int y) {
		int rx = round(x);
		int ry = round(y);

		if (getFrom() instanceof WirePoint)
			if (((WirePoint) getFrom()).isAt(rx, ry))
				return getFrom();

		if (getTo() instanceof WirePoint)
			if (((WirePoint) getTo()).isAt(rx, ry))
				return getTo();

		Vector<WirePoint> ps = getAllPoints();
		for (int i = 0; i < ps.size() - 1; i++) {
			// set current and next wirepoint
			WirePoint c = ps.get(i);
			WirePoint n = ps.get(i + 1);
			if (n.isAt(rx, ry))
				return n;
			Line2D l = new Line2D.Float((float) c.getX(), (float) c.getY(), (float) n.getX(), (float) n.getY());
			double dist = l.ptSegDist((double) x, (double) y);
			if (dist < 4.5f)
				return this;
		}
		return null;
	}

	private Vector<WirePoint> getAllPoints() {
		Vector<WirePoint> ps = new Vector<>();
		ps.add(getPointFrom());
		ps.addAll(points);
		if (getTo() != null)
			ps.add(getPointTo());
		return ps;
	}

	@Override
	public Rectangle getBoundingBox() {
		Rectangle bounds = new Rectangle();
		WirePoint first = getPointFrom();
		bounds.addPoint(first.getX(), first.getY());
		for (WirePoint point : points) {
			bounds.addPoint(point.getX(), point.getY());
		}
		if (getTo() != null) {
			bounds.addPoint(getTo().getX(), getTo().getY());
		} else if (tempPoint != null) {
			bounds.addPoint(tempPoint.getX(), tempPoint.getY());
		}
		return bounds;
	}

	WirePoint getLastPoint() {
		if (getTo() != null) {
			return getPointTo();
		} else if (!points.isEmpty()) {
			return points.getLast();
		} else if (getFrom() != null) {
			return getPointFrom();
		}
		throw new RuntimeException("Wire is empty!");
	}

	public boolean getLevel() {
		return level;
	}

	/**
	 * checks if given point is near polygon node, except first and last
	 * 
	 * @return -1 if no point is near to given position, else number of node
	 */
	private int getNodeIndexAt(int mx, int my) {
		if (points.isEmpty())
			return -1;

		for (int i = 0; i < points.size(); i++) {
			WirePoint p = points.get(i);
			if (mx > p.getX() - 3 && mx < p.getX() + 3 && my > p.getY() - 3 && my < p.getY() + 3)
				return i;
		}
		return -1;
	}

	private WirePoint getPointFrom() {
        return new WirePoint(getFrom().getX(), getFrom().getY(), false);
	}

	private WirePoint getPointTo() {
        return new WirePoint(getTo().getX(), getTo().getY(), false);
	}

	public void insertPointAfter(int n, int mx, int my) {
		WirePoint wp = new WirePoint(mx, my, false);
		wp.parent = this;
		points.add(n, wp);
	}

	/**
	 * check if the wire is near given coordinates
	 * if the distance between the clicked point and the wire is small enough the
	 * point number is returned
	 * 
	 * @return the number of the point from which the line segment is starting
	 */
	public int isAt(int x, int y) {
		Vector<WirePoint> ps = getAllPoints();
		for (int i = 0; i < ps.size() - 1; i++) {
			// set current and next wirepoint
			WirePoint c = ps.get(i);
			WirePoint n = ps.get(i + 1);
			Line2D l = new Line2D.Float((float) c.getX(), (float) c.getY(), (float) n.getX(), (float) n.getY());
			if (l.ptSegDist((double) x, (double) y) < 4.0f)
				return i;
		}
		return -1;
	}

	public boolean isNotFinished() {
		return getTo() == null;
	}

	@Override
	public void mouseDragged(ClickPoint dragStart, ClickPoint currentPos) {
		super.mouseDragged(dragStart, currentPos);
		final int targetX = round(currentPos.getX() - dragStart.getX() + getPreviousPosition().getX());
		final int targetY = round(currentPos.getY() - dragStart.getY() + getPreviousPosition().getY());
		moveTo(targetX, targetY);
		for (WirePoint wp : points) {
			wp.mouseDragged(dragStart, currentPos);
		}
	}

	@Override
	public void mousePressed(LSMouseEvent e) {
		super.mousePressed(e);
		if (Simulation.getInstance().isRunning())
			return;

		int mx = e.getX();
		int my = e.getY();

		if (e.lsAction == Action.ADDPOINT) {
			int p = isAt(mx, my);
			if (p > -1) {
				insertPointAfter(p, round(mx), round(my));
				select();
			}
		} else if (e.lsAction == Action.DELPOINT) {
			if (removePointAt(e.getX(), e.getY())) {
				select();
				fireRepaint();
			}
		} else {
			select();
		}
//		// clicked CTRL on a Wire -> insert node
//		if (e.isControlDown()) {
//			int pointNumberOfSegment = w.isAt(x, y);
//
//			if (pointNumberOfSegment > -1) {
//				if (pointNumberOfSegment == 0)
//					w.insertPointAfterStart(x, y);
//				else
//					w.insertPointAfter(pointNumberOfSegment, x, y);
//				currentPart = w;
//				w.activate();
//				repaint();
//				notifyChangeListener("");
//				notifyChangeListener();
//				return true;
//			}
//		}
	}

	@Override
	public void moveBy(int dx, int dy) {
		// move wirepoints
		for (WirePoint wp : points) {
			wp.moveBy(dx, dy);
		}
	}

	/**
	 * remove the last point of the wire
	 * 
	 * @return points left in wire
	 */
	public int removeLastPoint() {
		// wire is connected to gate, remove the connection and return number of
		// remaining points
		if (getTo() != null) {
			getTo().removeLevelListener(this);
			setTo(null);
			// points + first point
			return points.size() + 1;
		} else if (points.isEmpty()) {
			if (getFrom() == null) {
				throw new RuntimeException("wire is completely empty, may not be");
			}
			getFrom().removeLevelListener(this);
			setFrom(null);
			// wire can be released
			return 0;
		} else {
			points.removeLast();
			return points.size() + 1;
		}
	}

	public boolean removePointAt(int x, int y) {
		for (Iterator<WirePoint> iter = points.iterator(); iter.hasNext();) {
			WirePoint wp = iter.next();
			if (wp.isAt(x, y)) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	public boolean removePoint(WirePoint wp) {
		return points.remove(wp);
	}

	public void setTempPoint(int x, int y) {
		if (tempPoint == null) {
			tempPoint = new ClickPoint();
		}
		tempPoint.set(x, y);
	}

	@Override
	public String toString() {
		return getId();
	}

	/**
	 * disconnect wire from CircuitParts
	 */
	public void disconnect() {
		if (getTo() != null) {
			getTo().removeLevelListener(this);
			this.removeLevelListener(getTo());
			setTo(null);
		}
		if (getFrom() != null) {
			getFrom().removeLevelListener(this);
			this.removeLevelListener(getFrom());
			setFrom(null);
		}
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// a wire can get a level change from a pin or another wire
		if (level != e.level || e.force) {
			level = e.level;
			// forward to other listeners, event must not get back to the origin
			fireRepaint();
			LSLevelEvent evt = new LSLevelEvent(this, e.level, e.force);
			fireChangedLevel(e);
			for (WirePoint wp : points) {
				wp.changedLevel(evt);
			}
		}
	}

	@Override
	public String getId() {
		return (getFrom() != null ? getFrom().getId() : "")
				+ "-"
				+ (getTo() != null ? getTo().getId() : "");
	}

	@Override
	public void deselect() {
		super.deselect();
		if (getFrom() instanceof WirePoint)
			getFrom().deselect();

		if (getTo() instanceof WirePoint)
			getTo().deselect();

		for (WirePoint wp : points) {
			wp.deselect();
		}
	}

	public void finish() {
		tempPoint = null;
	}

	public void addPoint(WirePoint wp) {
		// check if the point is not present
		int x = wp.getX();
		int y = wp.getY();
		if (getFrom() != null && getFrom().getX() == x && getFrom().getY() == y)
			return;
		if (getTo() != null && getTo().getX() == x && getTo().getY() == y)
			return;
		int number = getNodeIndexAt(x, y);
		if (number > -1) {
			// delete every point from this node on
			for (int i = points.size() - 1; i >= number; i--) {
				points.remove(i);
			}
		}
		wp.parent = this;
		// wp.connect(this);
		points.add(wp);
	}

	public List<WirePoint> getPoints() {
		return points;
	}

	public CircuitPart getFrom() {
		return from;
	}

	public void setFrom(CircuitPart from) {
		this.from = from;
		checkFromTo();
	}

	public CircuitPart getTo() {
		return to;
	}

	public void setTo(CircuitPart to) {
		this.to = to;
		checkFromTo();
	}

	@Override
	public void reset() {
		super.reset();
		if (from instanceof Wire w) {
			from.fireChangedLevel(new LSLevelEvent(from, w.getLevel()));
		} else if (from instanceof Pin p) {
			from.fireChangedLevel(new LSLevelEvent(from, p.getLevel()));
		} else if (from instanceof WirePoint wp) {
			if (wp.parent != null) {
				Wire w = (Wire) wp.parent;
				wp.parent.fireChangedLevel(new LSLevelEvent(wp.parent, w.getLevel()));
			}
		}
	}

	public static void setColorMode() {
		String colorMode = LSProperties.getInstance().getProperty(LSProperties.COLORMODE, LSProperties.COLORMODE_ON);
		if (LSProperties.COLORMODE_OFF.equals(colorMode)) {
			HIGH_COLOR = Color.black;
			HIGH_WIDTH = 3f;
		} else {
			HIGH_COLOR = Color.red;
			HIGH_WIDTH = 1f;
		}
	}

	@Override
	public void mouseReleased(int mx, int my) {
		super.mouseReleased(mx, my);
		for (WirePoint wp : points) {
			wp.mouseReleased(mx, my);
		}
	}
}

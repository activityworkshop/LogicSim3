package logicsim;

import logicsim.localization.I18N;
import logicsim.localization.Lang;
import logicsim.ui.ClickPoint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

public abstract class CircuitPart implements LSLevelListener {
	private static final int BOUNDING_SPACE = 6;
	protected static final boolean HIGH = true;
	protected static final boolean LOW = false;

	protected static final Font hugeFont = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
	protected static final Font bigFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
	protected static final Font smallFont = new Font(Font.SANS_SERIF, Font.PLAIN, 7);

	public static final String TEXT = "text";

	private final List<LSLevelListener> listeners = new ArrayList<>();
	private LSRepaintListener repListener;

	public CircuitPart parent;
	/** if part is currently being edited */
	public boolean selected = false;

	private final ClickPoint currentPosition = new ClickPoint();
	private final ClickPoint previousPosition = new ClickPoint();

	public String text;
	private final Properties properties = new Properties();


	public CircuitPart(int x, int y) {
		currentPosition.set(x, y);
		previousPosition.reset();
	}

	public static int round(int num) {
		return ((num + 5) / 10) * 10;
	}

	protected static String indent(String string) {
		return "   " + string.replaceAll("\n", "\n   ");
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(String string) {
		return properties.getProperty(string);
	}

	public int getPropertyInt(String string) {
		return Integer.parseInt(getProperty(string));
	}

	// TODO: Catch parse exceptions
	public int getPropertyIntWithDefault(String string, int idefault) {
		String value = getProperty(string);
		if (value == null)
			return idefault;
		else
			return Integer.parseInt(value);
	}

	public String getPropertyWithDefault(String key, String sdefault) {
		String s = getProperty(key);
		if (s == null)
			return sdefault;
		return s;
	}

	public void loadProperties() {
		text = getPropertyWithDefault(TEXT, "");
	}

	public boolean hasPropertiesUI() {
		return true;
	}

	public boolean showPropertiesUI(Component frame) {
		String h = (String) JOptionPane.showInputDialog(frame, I18N.tr(Lang.TEXT), I18N.tr(Lang.GATE_PROPERTIES),
				JOptionPane.QUESTION_MESSAGE, null, null, text);
		if (h != null) {
			text = h;
			setProperty(TEXT, text);
		}
		return true;
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
		if (TEXT.equals(key)) {
			text = value;
		}
	}

	protected void setPropertyInt(String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	private void checkXY(int x2, int y2) {
		if (x2 % 10 != 0)
			throw new RuntimeException("only move by 10s! tried x=" + x2 + " in part " + this.getId());
		if (y2 % 10 != 0)
			throw new RuntimeException("only move by 10s! tried y=" + y2 + " in part " + this.getId());
	}

	public void addLevelListener(LSLevelListener l) {
		if (!getListeners().contains(l)) {
			getListeners().add(l);
		}
	}

	public void setRepaintListener(LSRepaintListener l) {
		repListener = l;
	}

	public void removeLevelListener(LSLevelListener l) {
		getListeners().remove(l);
	}

	public void clear() {
	}

	public void deselect() {
		selected = false;
	}

	public void draw(Graphics2D g2) {
		drawActiveFrame(g2);
	}

	protected void drawActiveFrame(Graphics2D g2) {
		if (selected) {
			Rectangle rect = getBoundingBox();
			if (rect.isEmpty()) {
				return;
			}

			final int rightX = rect.getMaxX();
			final int upperY = rect.getMinY();
			final int lowerY = rect.getMaxY();
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.blue);

			// oben links
			Integer leftX = rect.getMinX();
			g2.drawLine(leftX - BOUNDING_SPACE, upperY - BOUNDING_SPACE, leftX - BOUNDING_SPACE, upperY);
			g2.drawLine(leftX - BOUNDING_SPACE, upperY - BOUNDING_SPACE, leftX, upperY - BOUNDING_SPACE);
			// unten links
			g2.drawLine(leftX - BOUNDING_SPACE, lowerY + BOUNDING_SPACE, leftX - BOUNDING_SPACE, lowerY);
			g2.drawLine(leftX - BOUNDING_SPACE, lowerY + BOUNDING_SPACE, leftX, lowerY + BOUNDING_SPACE);
			// oben rechts
			g2.drawLine(rightX + BOUNDING_SPACE, upperY - BOUNDING_SPACE, rightX + BOUNDING_SPACE, upperY);
			g2.drawLine(rightX + BOUNDING_SPACE, upperY - BOUNDING_SPACE, rightX, upperY - BOUNDING_SPACE);
			// unten rechts
			g2.drawLine(rightX + BOUNDING_SPACE, lowerY + BOUNDING_SPACE, rightX + BOUNDING_SPACE, lowerY);
			g2.drawLine(rightX + BOUNDING_SPACE, lowerY + BOUNDING_SPACE, rightX, lowerY + BOUNDING_SPACE);
		}
	}

	public abstract Rectangle getBoundingBox();

	public String getId() {
		return getX() + ":" + getY();
	}

	@Override
	public String toString() {
		return getId();
	}

	public int getX() {
		return currentPosition.getX();
	}

	public int getY() {
		return currentPosition.getY();
	}

	public boolean isSelected() {
		return selected;
	}

	public void mouseDragged(ClickPoint dragStart, ClickPoint currentPos) {
		if (!previousPosition.isSet()) {
			previousPosition.set(getX(), getY());
		}
	}

	public void mousePressed(LSMouseEvent e) {
		if (Simulation.getInstance().isRunning())
			mousePressedSim(e);
		else {
			select();
		}

		if (e.isAltDown()) {
			showPropertiesUI(null);
		}
	}

	public void mousePressedSim(LSMouseEvent e) {
	}

	/**
	 * wird aufgerufen, wenn über dem Teil die Maus losgelassen wird
	 */
	public void mouseReleased(int mx, int my) {
		previousPosition.reset();
	}

	public void moveBy(int dx, int dy) {
		if (dx == 0 && dy == 0) {
			return;
		}
		final int nx = getX() + dx;
		final int ny = getY() + dy;
		checkXY(nx, ny);
		currentPosition.set(nx, ny);
	}

	public void moveTo(int x, int y) {
		checkXY(x, y);
		currentPosition.set(x, y);
	}

	/**
	 * all Circuitparts can be resetted: maybe set back inputs or outputs and so on
	 */
	public void reset() {
	}

	public void select() {
		selected = true;
	}

	public void setX(int x) {
		currentPosition.set(x, getY());
	}

	public void setY(int y) {
		currentPosition.set(getX(), y);
	}

	protected ClickPoint getPreviousPosition() {
		return previousPosition;
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
	}

	public void connect(CircuitPart part) {
		this.addLevelListener(part);
		part.addLevelListener(this);
	}

	protected void fireChangedLevel(LSLevelEvent e) {
		// Log.getInstance().print("fireChangedLevel " + e);
		// the event can have a different source (not itself)
		// if so, just forward the event to the others except to the origin
		if (!this.equals(e.source)) {
			for (LSLevelListener l : getListeners()) {
				if (e.source != l) {
					LSLevelEvent evtL = new LSLevelEvent(this, e.level, e.force, l);
					Simulation.getInstance().putEvent(evtL);
					// l.changedLevel(evt);
				}
			}
		} else {
			for (LSLevelListener l : getListeners()) {
				LSLevelEvent evtL = new LSLevelEvent(this, e.level, e.force, l);
				Simulation.getInstance().putEvent(evtL);
				// l.changedLevel(e);
			}
		}
	}

	protected void fireRepaint() {
		if (repListener != null)
			repListener.needsRepaint(this);
	}

	public boolean isConnected() {
		return !getListeners().isEmpty();
	}

	public List<LSLevelListener> getListeners() {
		return listeners;
	}

	protected void clearListeners() {
		listeners.clear();
	}

	public boolean getLevel() {
		return false;
	}
}

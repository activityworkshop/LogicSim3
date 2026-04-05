package logicsim;

import logicsim.ui.ClickPoint;

import java.awt.Graphics2D;

/**
 * WirePoint substructure class for Wire Objects
 * taken from https://argonrain.wordpress.com/2009/10/27/000/
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class WirePoint extends CircuitPart {

	private static final int POINT_SIZE = 6;

	public boolean show = false;
	private boolean level = false;

	public WirePoint(int x, int y) {
		this(x, y, false);
	}

	public WirePoint(int x, int y, boolean show) {
		super(x, y);
		this.show = show;
	}

	@Override
	public String toString() {
		return "(" + getX() + "," + getY() + ",-" + (show ? "w" : "f") + ")";
	}

	@Override
	public Rectangle getBoundingBox() {
		int c = POINT_SIZE / 2;
		return new Rectangle(getX() - c, getY() - c, POINT_SIZE, POINT_SIZE);
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		Rectangle boundingBox = getBoundingBox();
		g2.fillRect(boundingBox.getMinX(), boundingBox.getMinY(), boundingBox.getWidth(), boundingBox.getHeight());
	}

	public boolean isAt(int x, int y) {
		return x > getX() - 4
				&& x < getX() + 4
				&& y > getY() - 4
				&& y < getY() + 4;
	}

	@Override
	public void mousePressed(LSMouseEvent e) {
		super.mousePressed(e);
		select();
	}

	@Override
	public void mouseDragged(ClickPoint dragStart, ClickPoint currentPos) {
		super.mouseDragged(dragStart, currentPos);
		final int targetX = round(currentPos.getX() - dragStart.getX() + getPreviousPosition().getX());
		final int targetY = round(currentPos.getY() - dragStart.getY() + getPreviousPosition().getY());
		moveTo(targetX, targetY);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		if (getLevel() != e.level || e.force) {
			level = e.level;
			fireChangedLevel(e);
			if (parent != null && !e.source.equals(parent)) {
				parent.changedLevel(e);
			}
		}
	}

	@Override
	public String getId() {
		if (parent == null) {
			return super.getId();
		}
		return super.getId() + "@" + parent.getId();
	}

	@Override
	public boolean getLevel() {
		return level;
	}
}

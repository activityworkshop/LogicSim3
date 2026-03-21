package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

public class Pin extends CircuitPart {

	public static final int INPUT = 1;
	public static final int OUTPUT = 2;
	public static final int HIGHIMP = 3;

	public static final int NORMAL = 10;
	public static final int INVERTED = 11;
	public static final int HIGH = 12;
	public static final int LOW = 13;

	public static final int RIGHT = 0xa0;
	public static final int DOWN = 0xa1;
	public static final int LEFT = 0xa2;
	public static final int UP = 0xa3;

	private static final int BOUNDING_SPACE = 5;
	private static final int CONN_SIZE = 6;
	public static final String POS_EDGE_TRIG = "PosEdgeTrig";

	public int number;

	protected boolean level = false;
	public int paintDirection = RIGHT;

	private int ioType = INPUT;

	public int getIoType() {
		return ioType;
	}

	public void setIoType(int ioType) {
		if (ioType == Pin.HIGHIMP && this.ioType != Pin.HIGHIMP) {
			parent.changedLevel(new LSLevelEvent(this, CircuitPart.LOW));
		} else if (ioType != Pin.HIGHIMP && this.ioType == Pin.HIGHIMP) {

		}
		this.ioType = ioType;
	}

	/**
	 * type can be HIGH, LOW, INVERTED or NORMAL
	 */
    public int levelType = NORMAL;

	public Pin(int x, int y, Gate gate, int number) {
		super(x, y);
		this.parent = gate;
		this.number = number;
	}

	/**
	 * A connector can handle this event if there is one activePart and that part is
	 * a wire in other cases we can start a wire if this is an output
	 */
	@Override
	public void mousePressed(LSMouseEvent e) {
		super.mousePressed(e);
	}

	/**
	 * draw pin label (inside gate frame)
	 */
    public void drawLabel(Graphics2D g2) {
		int x = getX();
		int y = getY();

		g2.setFont(smallFont);
		if (text != null && !text.isEmpty()) {
			if (paintDirection == RIGHT) {
				if (POS_EDGE_TRIG.equals(text)) {
					Polygon tr = new Polygon();
					tr.addPoint(x + 1 + CONN_SIZE, y - 3);
					tr.addPoint(x + 1 + CONN_SIZE, y + 3);
					tr.addPoint(x + 1 + CONN_SIZE + 6, y);
					g2.draw(tr);
				} else {
					WidgetHelper.drawString(g2, text, x + CONN_SIZE + 3, y + 3, WidgetHelper.ALIGN_LEFT);
				}
			} else if (paintDirection == LEFT) {
				if (POS_EDGE_TRIG.equals(text)) {
					Polygon tr = new Polygon();
					tr.addPoint(x - 1 - CONN_SIZE, y - 3);
					tr.addPoint(x - 1 - CONN_SIZE, y + 3);
					tr.addPoint(x - 1 - CONN_SIZE - 6, y);
					g2.draw(tr);
				} else {
					WidgetHelper.drawString(g2, text, x - CONN_SIZE - 3, y + 3, WidgetHelper.ALIGN_RIGHT);
				}
			} else if (paintDirection == UP) {
				if (POS_EDGE_TRIG.equals(text)) {
					Polygon tr = new Polygon();
					tr.addPoint(x - 3, y - 1 - CONN_SIZE);
					tr.addPoint(x + 3, y - 1 - CONN_SIZE);
					tr.addPoint(x, y - 1 - CONN_SIZE - 6);
					g2.draw(tr);
				} else {
					WidgetHelper.drawStringRotated(g2, text, x + 3, y - CONN_SIZE - 3, WidgetHelper.ALIGN_LEFT, -90);
				}
			} else if (paintDirection == DOWN) {
				if (POS_EDGE_TRIG.equals(text)) {
					Polygon tr = new Polygon();
					tr.addPoint(x - 3, y + 1 + CONN_SIZE);
					tr.addPoint(x + 3, y + 1 + CONN_SIZE);
					tr.addPoint(x, y + 1 + CONN_SIZE + 6);
					g2.draw(tr);
				} else {
					WidgetHelper.drawStringRotated(g2, text, x + 3, y + CONN_SIZE + 3, WidgetHelper.ALIGN_RIGHT, -90);
				}
			}
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		int x = getX();
		int y = getY();

		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.BLACK);

		int offset = 0;

		if (levelType == INVERTED) {
            //Inverted Circle

            int ovalSize = CONN_SIZE;
            Color bgColor = Color.lightGray;
            Color borderColor = Color.black;
            if (paintDirection == LEFT) {
                g2.setPaint(bgColor);
                g2.fillOval(x - ovalSize, y - ovalSize / 2, ovalSize, ovalSize);
                g2.setPaint(borderColor);
                g2.drawOval(x - ovalSize, y - ovalSize / 2, ovalSize, ovalSize);
            } else if (paintDirection == RIGHT) {
                g2.setPaint(bgColor);
                g2.fillOval(x, y - ovalSize / 2, ovalSize, ovalSize);
                g2.setPaint(borderColor);
                g2.drawOval(x, y - ovalSize / 2, ovalSize, ovalSize);
            } else if (paintDirection == DOWN) {
                g2.setPaint(bgColor);
                g2.fillOval(x - ovalSize / 2, y, ovalSize, ovalSize);
                g2.setPaint(borderColor);
                g2.drawOval(x - ovalSize / 2, y, ovalSize, ovalSize);
            } else {// UP
                g2.setPaint(bgColor);
                g2.fillOval(x - ovalSize / 2, y - ovalSize, ovalSize, ovalSize);
                g2.setPaint(borderColor);
                g2.drawOval(x - ovalSize / 2, y - ovalSize, ovalSize, ovalSize);
            }
		} else if (levelType == HIGH || levelType == LOW) {
            // High/Low Input Label

            String label = (levelType == HIGH) ? "1" : "0";
			if (ioType == OUTPUT)
				throw new RuntimeException("OUTPUT may not be set HIGH/LOW");
			int xp = x + 2;
			int yp = y - 2;
			if (paintDirection == LEFT)
				WidgetHelper.drawStringCentered(g2, label, xp + 2, yp);
			else if (paintDirection == RIGHT)
				WidgetHelper.drawStringCentered(g2, label, xp - 7, yp);
			else if (paintDirection == DOWN)
				WidgetHelper.drawStringCentered(g2, label, xp - 2, yp - 6);
			else // UP
				WidgetHelper.drawStringCentered(g2, label, xp - 2, yp + 6);
		}

		if (levelType == HIGH || levelType == LOW || levelType == NORMAL) {
			// Default Pin Line
			g2.setStroke(new BasicStroke(1));
			if (paintDirection == LEFT || paintDirection == RIGHT) {
                //LEFT or RIGHT
                offset = (paintDirection == LEFT) ? -CONN_SIZE - 1 : -1;

				if (ioType == HIGHIMP) {
					if (paintDirection == LEFT) {
                        //LEFT
						g2.setPaint(level ? Color.red : Color.black);
						g2.fillRoundRect(x + offset + 5, y - 1, CONN_SIZE + 1 - 4, 3, 3, 3);
					} else {
                        //RIGHT
						g2.setPaint(level ? Color.red : Color.black);
						g2.fillRoundRect(x + offset + 1, y - 1, 2, 3, 3, 3);
					}
				} else {
					g2.setPaint(getLevel() ? Color.red : Color.black);
					g2.fillRoundRect(x + offset + 1, y - 1, CONN_SIZE + 1, 3, 3, 3);
				}
			} else {
                //UP or DOWN
				if (paintDirection == UP) {
					offset = -CONN_SIZE;
				}
				if (ioType == HIGHIMP) {
					if (paintDirection == UP) {
						g2.setPaint(level ? Color.red : Color.black);
						g2.fillRoundRect(x - 1, y + offset + 4, 3, CONN_SIZE - 3, 3, 3);
					} else {
						g2.setPaint(level ? Color.red : Color.black);
						g2.fillRoundRect(x - 1, y + offset, 3, 2, 3, 3);
					}
				} else {
					g2.setPaint(getLevel() ? Color.red : Color.black);
					g2.fillRoundRect(x - 2, y + offset, 4, CONN_SIZE + 1, 3, 3);
				}
			}

            if (levelType == HIGH || levelType == LOW) {
                // Draw a small line to indicate HIGH/LOW input
                g2.setPaint(getLevel() ? Color.red : Color.black);
                if (paintDirection == DOWN) {
                    g2.drawLine(x - 2, y + offset + 2, x + 3, y + offset + 2);
                } else if (paintDirection == RIGHT) {
                    g2.drawLine(x + offset + 3, y - 2, x + offset + 3, y + 3);
                } else if (paintDirection == LEFT) {
                    g2.drawLine(x + offset + 6, y - 2, x + offset + 6, y + 3);
                } else if (paintDirection == UP) {
                    g2.drawLine(x - 2, y + offset + 5, x + 3, y + offset + 5);
                }
            }
		}
        //drawLabel(g2);
	}

	public void setLevelType(int levelType) {
        if (levelType == HIGH || levelType == LOW) {
            // TODO if we set a level type of type HIGH or LOW we have to remove the wire completely
        }
		this.levelType = levelType;
	}

	public boolean getLevel() {
		if (ioType == HIGHIMP)
			return false;
		if (levelType == NORMAL)
			return level;
		if (levelType == INVERTED)
			return !level;
		if (levelType == HIGH)
			return true;
		// low
		return false;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(getX() - BOUNDING_SPACE / 2, getY() - BOUNDING_SPACE / 2, BOUNDING_SPACE, BOUNDING_SPACE);
	}

	public boolean isAt(int atX, int atY) {
		return (atX > getX() - 5 && atX < getX() + 5 && atY > getY() - 5 && atY < getY() + 5);
	}

	public void setDirection(int dir) {
		paintDirection = dir;
	}

	public boolean isInput() {
		return ioType == Pin.INPUT;
	}

	public boolean isOutput() {
		return ioType == Pin.OUTPUT;
	}

	@Override
	public String toString() {
		final String ioString = (ioType == Pin.OUTPUT ? "O" : "I");
		final String levelString;
		if (levelType == Pin.HIGH) {
			levelString = "H";
		} else if (levelType == Pin.LOW) {
			levelString = "L";
		} else if (levelType == Pin.INVERTED) {
			levelString = "I";
		} else {
			levelString = "N";
		}
        return number + ioString + levelString
				+ "-" + (text == null ? "" : "\"" + text + "\"") + getX()
                + ":" + getY() + "@" + parent.getId()
                + " - " + (getLevel() ? "HIGH" : "LOW");
	}

	public void setLevel(boolean b) {
		level = b;
	}

	@Override
	public String getId() {
		return number + "@" + parent.getId();
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// source has to be a Gate or a Wire
		if (e.source instanceof Gate) {
			if (isOutput()) {
				if (level != e.level || e.force) {
					level = e.level;
					// propagate this to the outside
					fireChangedLevel(new LSLevelEvent(this, getLevel(), e.force));
				}
			}
		} else if (e.source instanceof Wire) {
			// if the pin is in high imp state, update level but don't push it
			if (this.ioType == Pin.HIGHIMP) {
				level = e.level;
			} else {
				// signal is from outside, propagate this to gate
				// call gate directly
				if (level != e.level || e.force) {
					level = e.level;
					parent.changedLevel(new LSLevelEvent(this, getLevel(), e.force));

					// and call all other wires which are connected to the pin
					fireChangedLevel(e);
				}
			}
		} else {
			throw new RuntimeException("pins communicate with gates or wires only! source is " + e.source.getId()
					+ ", target is " + getId());
		}
	}

	public boolean getInternalLevel() {
		return level;
	}

	public void disconnect() {
		for (int i = 0; i < getListeners().size(); i++) {
			LSLevelListener l = getListeners().get(i);
			if (l instanceof Wire w) {
                w.disconnect(null);
                i--;
			}
		}
	}
}

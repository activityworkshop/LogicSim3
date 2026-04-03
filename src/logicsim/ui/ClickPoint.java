package logicsim.ui;

public class ClickPoint {
    private boolean isSet = false;
    private int x = 0;
    private int y = 0;

    public boolean isSet() {
        return isSet;
    }

    public void set(int x, int y) {
        isSet = true;
        this.x = x;
        this.y = y;
    }

    public void reset() {
        isSet = false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setOrthogonal(ClickPoint other) {
        if (!isSet || other == null || !other.isSet) {
            return;
        }
        setOrthogonal(other.getX(), other.getY());
    }

    public void setOrthogonal(int otherX, int otherY) {
        final int xDiff = Math.abs(x - otherX);
        final int yDiff = Math.abs(y - otherY);
        if (xDiff <= yDiff) {
            set(otherX, y);
        }
        else {
            set(x, otherY);
        }
    }

    public String toString() {
        return "(" + getX() + ", " + getY() + ")";
    }
}

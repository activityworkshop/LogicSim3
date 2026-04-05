package logicsim;

public class Rectangle {
    private Integer minX = null;
    private Integer maxX = null;
    private Integer minY = null;
    private Integer maxY = null;

    public Rectangle() {}

    public Rectangle(int x, int y, int width, int height) {
        addPoint(x, y);
        addPoint(x + width, y + height);
    }

    public void addPoint(int x, int y) {
        if (minX == null) {
            minX = x;
            maxX = x;
            minY = y;
            maxY = y;
        }
        else {
            minX = Math.min(minX, x);
            maxX = Math.max(maxX, x);
            minY = Math.min(minY, y);
            maxY = Math.max(maxY, y);
        }
    }

    public void clear() {
        minX = null;
    }

    boolean isEmpty() {
        return minX == null;
    }

    Integer getMinX() {
        return minX;
    }

    Integer getMaxX() {
        return maxX;
    }

    Integer getMinY() {
        return minY;
    }

    Integer getMaxY() {
        return maxY;
    }

    int getWidth() {
        return isEmpty() ? 0 : getMaxX() - getMinX();
    }

    int getHeight() {
        return isEmpty() ? 0 : getMaxY() - getMinY();
    }

    public boolean contains(Rectangle other) {
        if (minX == null || other == null || other.minX == null) {
            return false;
        }
        return minX <= other.minX
                && maxX >= other.maxX
                && minY <= other.minY
                && maxY >= other.maxY;
    }

    public boolean containsPoint(int x, int y) {
        return minX != null
                && minX <= x
                && maxX >= x
                && minY <= y
                && maxY >= y;
    }

    public String toString() {
        return "Rect: (" + minX + ", " + minY + ") to (" + maxX + ", " + maxY + ")";
    }
}

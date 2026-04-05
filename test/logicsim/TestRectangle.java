package logicsim;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestRectangle {
    @Test
    public void testEmpty() {
        Rectangle rect = new Rectangle();
        Assertions.assertTrue(rect.isEmpty());

        rect.addPoint(0, 0);
        Assertions.assertFalse(rect.isEmpty());
    }

    @Test
    public void testSinglePoint() {
        Rectangle rect = new Rectangle();
        rect.addPoint(5, 8);
        Assertions.assertEquals(5, rect.getMinX());
        Assertions.assertEquals(5, rect.getMaxX());
        Assertions.assertEquals(8, rect.getMinY());
        Assertions.assertEquals(8, rect.getMaxY());
    }

    @Test
    public void testPairIncreasing() {
        Rectangle rect = new Rectangle();
        rect.addPoint(105, 108);
        rect.addPoint(205, 308);
        Assertions.assertEquals(105, rect.getMinX());
        Assertions.assertEquals(205, rect.getMaxX());
        Assertions.assertEquals(108, rect.getMinY());
        Assertions.assertEquals(308, rect.getMaxY());
    }

    @Test
    public void testPairDecreasing() {
        Rectangle rect = new Rectangle();
        rect.addPoint(105, 108);
        rect.addPoint(-205, -308);
        Assertions.assertEquals(-205, rect.getMinX());
        Assertions.assertEquals(105, rect.getMaxX());
        Assertions.assertEquals(-308, rect.getMinY());
        Assertions.assertEquals(108, rect.getMaxY());
    }

    @Test
    public void testContains_inside() {
        Rectangle rect1 = new Rectangle();
        rect1.addPoint(105, 108);
        rect1.addPoint(110, 120);

        Rectangle rect2 = new Rectangle();
        rect2.addPoint(105, 108);
        rect2.addPoint(110, 108);
        Assertions.assertTrue(rect1.contains(rect2));
        Assertions.assertTrue(rect1.contains(rect1));
        Assertions.assertFalse(rect2.contains(rect1));
        Assertions.assertTrue(rect2.contains(rect2));
    }

    @Test
    public void testContains_overlap() {
        Rectangle rect1 = new Rectangle();
        rect1.addPoint(105, 108);
        rect1.addPoint(110, 120);

        Rectangle rect2 = new Rectangle();
        rect2.addPoint(107, 106);
        rect2.addPoint(111, 130);
        Assertions.assertFalse(rect1.contains(rect2));
        Assertions.assertFalse(rect2.contains(rect1));
    }

    @Test
    public void testContainsPoint() {
        Rectangle rect = new Rectangle();
        rect.addPoint(105, 108);
        rect.addPoint(110, 120);

        Assertions.assertTrue(rect.containsPoint(105, 108));
        Assertions.assertTrue(rect.containsPoint(106, 109));
        Assertions.assertFalse(rect.containsPoint(104, 109));
        Assertions.assertFalse(rect.containsPoint(107, 107));
        Assertions.assertFalse(rect.containsPoint(107, 121));
    }
}

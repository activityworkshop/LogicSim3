package logicsim.ui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestClickPoint {
    @Test
    public void testSet() {
        ClickPoint point = new ClickPoint();
        Assertions.assertFalse(point.isSet());

        point.set(12, -13);
        Assertions.assertTrue(point.isSet());
        Assertions.assertEquals(12, point.getX());
        Assertions.assertEquals(-13, point.getY());
    }

    @Test
    public void testOrthogonal_notSet() {
        ClickPoint startPoint = new ClickPoint();
        startPoint.set(100, 200);

        // End point is not set so stays not set
        ClickPoint endPoint = new ClickPoint();
        endPoint.setOrthogonal(startPoint);
        Assertions.assertFalse(endPoint.isSet());

        ClickPoint thirdPoint = new ClickPoint();
        thirdPoint.set(5, 0);
        thirdPoint.setOrthogonal(endPoint);
        // End point isn't set so thirdPoint stays unaltered
        Assertions.assertTrue(thirdPoint.isSet());
        Assertions.assertEquals(5, thirdPoint.getX());
        Assertions.assertEquals(0, thirdPoint.getY());
    }

    @Test
    public void testOrthogonal() {
        ClickPoint startPoint = new ClickPoint();
        startPoint.set(100, 200);

        ClickPoint endPoint = new ClickPoint();
        endPoint.set(110, -2);
        endPoint.setOrthogonal(startPoint);
        // Moved in y more than x, so should restrict to y motion only
        Assertions.assertTrue(endPoint.isSet());
        Assertions.assertEquals(100, endPoint.getX());
        Assertions.assertEquals(-2, endPoint.getY());

        endPoint.set(110, 202);
        endPoint.setOrthogonal(startPoint);
        // Moved in x more than y, so should restrict to x motion only
        Assertions.assertTrue(endPoint.isSet());
        Assertions.assertEquals(110, endPoint.getX());
        Assertions.assertEquals(200, endPoint.getY());
    }

    @Test
    public void testOrthogonal_coords() {
        ClickPoint point = new ClickPoint();
        point.set(100, 200);

        point.setOrthogonal(84, 234);
        // Moved in y more than x, so should restrict to y motion only
        Assertions.assertTrue(point.isSet());
        Assertions.assertEquals(84, point.getX());
        Assertions.assertEquals(200, point.getY());

        point.set(100, 200);
        point.setOrthogonal(83, 214);
        // Moved in x more than y, so should restrict to x motion only
        Assertions.assertTrue(point.isSet());
        Assertions.assertEquals(100, point.getX());
        Assertions.assertEquals(214, point.getY());
    }
}

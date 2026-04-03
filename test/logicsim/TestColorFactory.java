package logicsim;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.List;

public class TestColorFactory {

    @Test
    public void testColorsToStrings() {
        String redString = ColorFactory.getString(Color.RED);
        Assertions.assertEquals("#ff0000", redString);
        String greenString = ColorFactory.getString(Color.GREEN);
        Assertions.assertEquals("#00ff00", greenString);
        String blueString = ColorFactory.getString(Color.BLUE);
        Assertions.assertEquals("#0000ff", blueString);
    }

    @Test
    public void testStringsToColors() {
        Color red = ColorFactory.makeColor("#ff0000", Color.BLACK);
        checkColor(red, 255, 0, 0);
        Color green = ColorFactory.makeColor("#00ff00", Color.BLACK);
        checkColor(green, 0, 255, 0);
        Color blue = ColorFactory.makeColor("#000080", Color.BLACK);
        checkColor(blue, 0, 0, 128);
        Color white = ColorFactory.makeColor("#ffffff", Color.BLACK);
        checkColor(white, 255, 255, 255);
    }

    private void checkColor(Color c, int r, int g, int b) {
        Assertions.assertEquals(r, c.getRed());
        Assertions.assertEquals(g, c.getGreen());
        Assertions.assertEquals(b, c.getBlue());
    }

    @Test
    public void testStringParseFail() {
        List<String> failStrings = List.of("ff0000", "", "#failed",
        "#1234567", "#-1-1-1");
        for (String failString : failStrings) {
            Color fail = ColorFactory.makeColor(failString, Color.BLACK);
            checkColor(fail, 0, 0, 0);
        }
    }
}

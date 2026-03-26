package logicsim.ui;

import logicsim.localization.I18N;
import logicsim.localization.Lang;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class ButtonHelper {
    private static final int buttonSize = 20;
    private static final int totalSize = buttonSize + 10;

    static void initButton(AbstractButton button, String iconName, Lang toolTip) {
        button.setDoubleBuffered(true);
        button.setIcon(getIcon(iconName));
        button.setToolTipText(I18N.tr(toolTip));
        button.setName(toolTip.toString());
        button.setMinimumSize(new Dimension(totalSize, totalSize));
        button.setMaximumSize(new Dimension(totalSize, totalSize));
        button.setPreferredSize(new Dimension(totalSize, totalSize));
        button.setBorder(new LSRoundedBorder());
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(Color.LIGHT_GRAY);
            }

            public void mouseExited(MouseEvent evt) {
                button.setBackground(UIManager.getColor("control"));
            }
        });
    }

    private static ImageIcon getIcon(String imgName) {
        final String filename = "/images/" + imgName + ".png";
        final Image image = new ImageIcon(Objects.requireNonNull(ButtonHelper.class.getResource(filename))).getImage();
        return new ImageIcon(image.getScaledInstance(buttonSize, buttonSize, Image.SCALE_AREA_AVERAGING));
    }
}

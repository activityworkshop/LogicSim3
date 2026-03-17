package logicsim.ui;

import logicsim.localization.I18N;
import logicsim.localization.Lang;

import java.awt.*;
import java.io.Serial;
import java.util.Objects;

import javax.swing.*;

public class LSToggleButton extends JToggleButton {

	@Serial
    private static final long serialVersionUID = 4992541122998327288L;
    private final int size = 20;

	public LSToggleButton(String iconName, Lang toolTip) {
		setDoubleBuffered(true);
		setIcon(getIcon(iconName));
		setToolTipText(I18N.tr(toolTip));
        final int totalSize = size + 10;
        setMinimumSize(new Dimension(totalSize, totalSize));
        setMaximumSize(new Dimension(totalSize, totalSize));
        setPreferredSize(new Dimension(totalSize, totalSize));
        setBorder(new LSRoundedBorder(5));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(Color.LIGHT_GRAY);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(UIManager.getColor("control"));
            }
        });
	}

	private ImageIcon getIcon(String imgName) {
		final String filename = "/images/" + imgName + ".png";
        final Image image = new ImageIcon(Objects.requireNonNull(getClass().getResource(filename))).getImage();
        return new ImageIcon(image.getScaledInstance(size, size, Image.SCALE_AREA_AVERAGING));
	}

}

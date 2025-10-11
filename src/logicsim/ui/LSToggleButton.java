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
    private final int padding = 10;

	public final String id;
	public LSToggleButton(String iconName, Lang toolTip) {
		this.setDoubleBuffered(true);
		this.setIcon(getIcon(iconName));
		this.setToolTipText(I18N.tr(toolTip));
		this.id = I18N.langToStr(toolTip);
        this.setMinimumSize(new Dimension(size + padding, size + padding));
        this.setMaximumSize(new Dimension(size + padding, size + padding));
        this.setPreferredSize(new Dimension(size + padding, size + padding));
        this.setBorder(new LSRoundedBorder(5));
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                setBackground(Color.LIGHT_GRAY);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                setBackground(UIManager.getColor("control"));
            }
        });
	}

	private ImageIcon getIcon(String imgname) {
		String filename = "/logicsim/images/" + imgname + ".png";
		int is = size;
		// return new ImageIcon(LSFrame.class.getResource(filename));
		return new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(filename))).getImage().getScaledInstance(is, is,
				Image.SCALE_AREA_AVERAGING));
	}

}

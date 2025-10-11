package logicsim.ui;

import logicsim.localization.I18N;
import logicsim.localization.Lang;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.Objects;

import javax.swing.*;

public class LSButton extends JButton {

	@Serial
    private static final long serialVersionUID = 4465562539140913810L;
    private final int size = 20;

    public LSButton(String iconName, Lang toolTip) {
		this.setDoubleBuffered(true);
		this.setIcon(getIcon(iconName));
		this.setToolTipText(I18N.tr(toolTip));
		this.setName(toolTip.toString());
        int padding = 10;
        this.setMinimumSize(new Dimension(size + padding, size + padding));
        this.setMaximumSize(new Dimension(size + padding, size + padding));
        this.setPreferredSize(new Dimension(size + padding, size + padding));
        this.setBorder(new LSRoundedBorder(5));
        this.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                setBackground(Color.LIGHT_GRAY);
            }

            public void mouseExited(MouseEvent evt) {
                setBackground(UIManager.getColor("control"));
            }
        });
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	private ImageIcon getIcon(String imgname) {
		String filename = "/logicsim/images/" + imgname + ".png";
		int is = size;
		// return new ImageIcon(LSFrame.class.getResource(filename));
		return new ImageIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(filename))).getImage().getScaledInstance(is, is,
				Image.SCALE_AREA_AVERAGING));
	}
}

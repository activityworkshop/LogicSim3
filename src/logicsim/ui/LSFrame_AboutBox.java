package logicsim.ui;

import logicsim.App;
import logicsim.LSFrame;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.io.Serial;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class LSFrame_AboutBox extends JWindow {
	@Serial
    private static final long serialVersionUID = -3193728228853983319L;

	private final Image imgSplash;

    public LSFrame_AboutBox(Frame parent) {
		super(parent);

		imgSplash = new ImageIcon(Objects.requireNonNull(LSFrame.class.getResource("images/about.jpg"))).getImage();

		final int imgWidth = imgSplash.getWidth(this);
		final int imgHeight = imgSplash.getHeight(this) + 155;
		Dimension pS = parent.getSize();
		Point pL = parent.getLocation();
		setLocation(pL.x + pS.width / 2 - imgWidth / 2, pL.y + pS.height / 2 - imgHeight / 2);
		setSize(imgWidth, imgHeight);
		getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(new SplashPanel(), BorderLayout.CENTER);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);

		setVisible(true);
	}

	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		if (id == MouseEvent.MOUSE_CLICKED) {
			this.setVisible(false);
			this.dispose();
		}
	}

	private class SplashPanel extends JPanel {
		@Serial
        private static final long serialVersionUID = 5564588819196489014L;

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.black);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.drawImage(imgSplash, 0, 0, this);

			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(Color.white);

			FontMetrics fm = g2.getFontMetrics();
			Font of = fm.getFont();
			Font f = new Font(of.getName(), of.getStyle(), 12);
			g2.setFont(f);

			String version = App.class.getPackage().getImplementationVersion();
			g2.drawString("Version " + version, 10, 240);
			g2.drawString("Created by Andreas Tetzl (tetzl.de) in 1995 - 2009.", 10, 270);
			g2.drawString("Developed further by Peter 'codepiet' Gabriel in 2020,", 10, 290);
			g2.drawString("also Matthew 'chocolatepatty' Lister in 2020,", 10, 310);
			g2.drawString("and Benkralex in 2025", 10, 330);
			g2.drawString("LogicSim is free software - Released under the GPL", 10, 360);
		}
	}

}
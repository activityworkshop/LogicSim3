package logicsim.ui;

import logicsim.LSProperties;
import logicsim.localization.I18N;
import logicsim.localization.Lang;

import javax.swing.*;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.net.URL;

/**
 * @author atetzl
 */
public class HTMLHelp extends JFrame {

	public HTMLHelp() {
		final String language = LSProperties.getInstance().getProperty("language", "de");
		final int width = 1100, height = 600;

		getContentPane().setLayout(new BorderLayout(0, 0));
		final JTextPane textPane = initComponents();

		final Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((scrSize.width / 2) - (width / 2), (scrSize.height / 2) - (height / 2));
		setSize(width, height);
		setTitle("LogicSim " + I18N.tr(Lang.HELP));

		try {
			String resourcePath = "/docs/" + language + ".html";
			URL url = getClass().getResource(resourcePath);
			if (url == null) {
				url = getClass().getResource("/docs/en.html");
			}
			if (url != null) {
				textPane.setPage(url);
			} else {
				throw new FileNotFoundException("Docs file not found: " + resourcePath);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			setVisible(false);
			dispose();
		}
		setVisible(true);
	}

	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		if (id == MouseEvent.MOUSE_CLICKED) {
			setVisible(false);
			dispose();
		}
	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			dispose();
		}
	}

	private JTextPane initComponents() {
        final JScrollPane jScrollPane1 = new JScrollPane();
		JTextPane jTextPane = new JTextPane();
		jTextPane.setContentType("text/html;charset=UTF-8");

        // Custom ScrollBar UI
        JScrollBar scrollBarVert = new JScrollBar(JScrollBar.VERTICAL);
        scrollBarVert.setUI(new LSScrollBarUI());
        scrollBarVert.setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        jScrollPane1.setVerticalScrollBar(scrollBarVert);
        jScrollPane1.getVerticalScrollBar().setUnitIncrement(32);

        JScrollBar scrollBarHor = new JScrollBar(JScrollBar.HORIZONTAL);
        scrollBarHor.setUI(new LSScrollBarUI());
        scrollBarHor.setPreferredSize(new Dimension(8, Integer.MAX_VALUE));
        jScrollPane1.setHorizontalScrollBar(scrollBarHor);
        jScrollPane1.getHorizontalScrollBar().setUnitIncrement(32);

		jTextPane.setEditable(false);
		jTextPane.setMinimumSize(new Dimension(400, 300));
		jScrollPane1.setViewportView(jTextPane);

		getContentPane().add(jScrollPane1, BorderLayout.CENTER);

		pack();
		return jTextPane;
	}
}

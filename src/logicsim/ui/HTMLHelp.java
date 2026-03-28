package logicsim.ui;

import logicsim.LSProperties;
import logicsim.localization.I18N;
import logicsim.localization.Lang;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
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

	private JTextPane initComponents() {
		final JTextPane jTextPane = new JTextPane();
		jTextPane.setContentType("text/html;charset=UTF-8");

		getContentPane().add(new JScrollPane(jTextPane), BorderLayout.CENTER);

		pack();
		return jTextPane;
	}
}

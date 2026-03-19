package logicsim.ui;

import logicsim.LSProperties;
import logicsim.localization.I18N;
import logicsim.localization.Lang;

import javax.swing.*;
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
import java.io.Serial;
import java.net.URL;

/**
 * @author atetzl
 */
public class HTMLHelp extends JFrame implements ActionListener {
	@Serial
    private static final long serialVersionUID = 4292051858178374722L;
    private JTextPane jTextPane1;


	/** Creates new form HTMLHelp */
	public HTMLHelp() {
		String language = LSProperties.getInstance().getProperty("language", "de");
		Dimension scrSize;
		int width = 800, height = 600;

		getContentPane().setLayout(new BorderLayout(0, 0));
		initComponents();
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);

		scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((scrSize.width / 2) - (width / 2), (scrSize.height / 2) - (height / 2));
		setSize(width, height);
		this.setTitle("LogicSim " + I18N.tr(Lang.HELP));

		try {
			String resourcePath = "/docs/" + language + ".html";
			URL url = getClass().getResource(resourcePath);
			if (url == null) {
				url = getClass().getResource("/docs/en.html");
			}
			if (url != null) {
				jTextPane1.setPage(url);
			} else {
				throw new FileNotFoundException("Docs file not found: " + resourcePath);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			this.setVisible(false);
			this.dispose();
		}
		this.setVisible(true);
	}

	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		if (id == MouseEvent.MOUSE_CLICKED) {
			this.setVisible(false);
			this.dispose();
		}
	}

	protected void processWindowEvent(WindowEvent e) {
		// super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			this.dispose();
		}
	}

	public void actionPerformed(ActionEvent e) {
	}

	private void initComponents() {
        JScrollPane jScrollPane1 = new JScrollPane();
		jTextPane1 = new JTextPane();
		jTextPane1.setContentType("text/html;charset=UTF-8");

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

		jTextPane1.setEditable(false);
		jTextPane1.setMinimumSize(new Dimension(400, 300));
		jScrollPane1.setViewportView(jTextPane1);

		getContentPane().add(jScrollPane1, BorderLayout.CENTER);

		pack();
	}
}

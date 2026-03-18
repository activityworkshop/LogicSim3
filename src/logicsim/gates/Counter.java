package logicsim.gates;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import logicsim.localization.I18N;
import logicsim.LSLevelEvent;
import logicsim.localization.Lang;
import logicsim.Pin;

/**
 * Counter Component (rising edge driven)
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class Counter extends BinaryOutput {
	private static final String DISPLAY_TYPE = "displaytype";
	private static final String DISPLAY_TYPE_HEX = "hex";
	private static final String DISPLAY_TYPE_DEC = "dec";
	private static final String DISPLAY_TYPE_DEFAULT = "hex";

	int value = 0;

	public Counter() {
		super("counter");
		height = 90;
		createInputs(1);
		createOutputs(8);
		loadProperties();
		reset();
	}

	@Override
	public void loadProperties() {
		String displayType = getPropertyWithDefault(DISPLAY_TYPE, DISPLAY_TYPE_DEFAULT);
		setDisplayType(switch(displayType) {
			case DISPLAY_TYPE_DEC -> DisplayType.DECIMAL;
			default -> DisplayType.HEXADECIMAL;
		});
	}

	@Override
	public void reset() {
		super.reset();
		value = 0;
		setOutputs();
	}

	/**
	 * check for rising edge
	 */
	@Override
	public void changedLevel(LSLevelEvent e) {
		if (e.source.equals(getPin(0)) && e.level == HIGH) {
			// rising edge detection
			value++;
			if (value > 0xff) {
				value = 0;
			}
			setOutputs();
		}
	}

	private void setOutputs() {
		for (int i = 0; i < 8; i++) {
			boolean b = ((value & (1 << i)) != 0);
			LSLevelEvent evt = new LSLevelEvent(this, b);
			getPin(i + 1).changedLevel(evt);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		int x = getX();
		int y = getY();

		g.setPaint(Color.BLACK);
		final String sval = getDisplayType();
		g.setFont(bigFont);
		int sw = g.getFontMetrics().stringWidth(sval);
		g.drawString(sval, x + getWidth() / 2 - sw / 2, y + height / 2 + 18);
		g.setFont(Pin.smallFont);
		final String gateType = "CNT";
		sw = g.getFontMetrics().stringWidth(gateType);
		g.drawString(gateType, x + getWidth() / 2 - sw / 2, y + 16);
		final String dispType = getDisplayType();
		sw = g.getFontMetrics().stringWidth(dispType);
		g.drawString(dispType, x + getWidth() / 2 - sw / 2, y + 27);
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		super.showPropertiesUI(frame);
		JRadioButton jRadioButton1 = new JRadioButton(I18N.getString(type, DISPLAY_TYPE_HEX));
		JRadioButton jRadioButton2 = new JRadioButton(I18N.getString(type, DISPLAY_TYPE_DEC));

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(jRadioButton1);
		group.add(jRadioButton2);

		if (displayType == DisplayType.HEXADECIMAL) {
			jRadioButton1.setSelected(true);
		} else {
			jRadioButton2.setSelected(true);
		}

		final JPanel jPanel1 = new JPanel();

		final TitledBorder titledBorder1 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, Color.white,
				new Color(142, 142, 142)), I18N.getString(type, DISPLAY_TYPE));
		jPanel1.setBorder(titledBorder1);
		jPanel1.setBounds(new Rectangle(11, 11, 171, 150));
		jPanel1.setLayout(new BorderLayout());
		jPanel1.add(jRadioButton1, BorderLayout.NORTH);
		jPanel1.add(jRadioButton2, BorderLayout.CENTER);

		final JOptionPane pane = new JOptionPane(jPanel1);
		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		pane.setOptions(new String[] { I18N.tr(Lang.OK), I18N.tr(Lang.CANCEL) });
		final JDialog dlg = pane.createDialog(frame, I18N.tr(Lang.SETTINGS));
		dlg.setResizable(true);
		dlg.setSize(290, 180);
		dlg.setVisible(true);
		if (I18N.tr(Lang.OK).equals(pane.getValue())) {
			if (jRadioButton1.isSelected()) {
				setDisplayType(DisplayType.HEXADECIMAL);
			} else if (jRadioButton2.isSelected()) {
				setDisplayType(DisplayType.DECIMAL);
			}
			return true;
		}
		return false;
	}
}

package gates;

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
import logicsim.localization.Lang;
import logicsim.WidgetHelper;

/**
 * Binary Display for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class BinDisp extends BinaryOutput {
	private static final String DISPLAY_TYPE = "displaytype";
	private static final String DISPLAY_TYPE_HEX = "hex";
	private static final String DISPLAY_TYPE_DEC = "dec";
	private static final String DISPLAY_TYPE_DEFAULT = "hex";
	private static final String UI_TYPE = "type";


	public BinDisp() {
		super("bindisp");
		height = 90;
		backgroundColor = Color.LIGHT_GRAY;
		createInputs(8);
		loadProperties();
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
	public void draw(Graphics2D g2) {
		super.draw(g2);

		final Rectangle r = new Rectangle(xc - 20, yc - 15, 40, 30);
		g2.setColor(Color.white);
		g2.fill(r);
		g2.setColor(Color.black);
		g2.draw(r);
		
		int value = 0;
		for (int i = 0; i < 8; i++) {
			if (getPin(i).getLevel()) {
				value += (1 << i);
			}
		}

		g2.setFont(smallFont);
		WidgetHelper.drawString(g2, getDisplayType(),
				xc, getY() + 10, WidgetHelper.ALIGN_CENTER);

		g2.setFont(hugeFont);
		WidgetHelper.drawString(g2, getValueAsString(value), xc, yc, WidgetHelper.ALIGN_CENTER);
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
				new Color(142, 142, 142)), I18N.getString(type, UI_TYPE));
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
		dlg.setSize(320, 180);
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

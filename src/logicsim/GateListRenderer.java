package logicsim;

import logicsim.gatelist.GateDefinition;
import logicsim.localization.I18N;

import java.awt.*;

import javax.swing.*;

public class GateListRenderer extends JLabel implements ListCellRenderer<Object> {

	@Override
	public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		setFont(list.getFont());
		setOpaque(true);
		if (value instanceof GateDefinition gateDef) {
            if (isSelected) {
				setBackground(new Color(0xaa, 0xaa, 0xFF));
				setForeground(Color.white);
			} else {
				setForeground(list.getForeground());
				setBackground(list.getBackground());
			}
			setText(gateDef.getTitleString());
			setHorizontalAlignment(SwingConstants.LEFT);
			return this;
		} else if (value instanceof String) {
            JPanel panel = new JPanel(new BorderLayout());

            JSeparator separatorLine = new JSeparator(SwingConstants.HORIZONTAL);
            separatorLine.setForeground(Color.BLACK);
            panel.add(separatorLine, BorderLayout.NORTH);

            JLabel label = new JLabel(I18N.tr((String) value));
            label.setFont(new Font(list.getFont().getName(), Font.BOLD, list.getFont().getSize() + 4));
            label.setForeground(Color.BLACK);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            panel.add(label, BorderLayout.CENTER);

            return panel;
		} else {
			throw new RuntimeException("unknown format of object in getListCellRendererComponent");
		}
	}
}

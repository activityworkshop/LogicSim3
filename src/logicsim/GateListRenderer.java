package logicsim;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.Serial;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

public class GateListRenderer extends JLabel implements ListCellRenderer<Object> {
	@Serial
    private static final long serialVersionUID = -361281475843085219L;

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {

		setFont(list.getFont());
		setOpaque(true);
		if (value instanceof Gate) {
			Gate gate = (Gate) value;
			if (isSelected) {
				setBackground(new Color(0xaa, 0xaa, 0xFF));
				setForeground(Color.white);
			} else {
				setForeground(list.getForeground());
				setBackground(list.getBackground());
			}
			if (value instanceof Module) {
				setText(gate.type);
			} else {
				String s = gate.type;
				if (I18N.hasString("gate." + s + ".title"))
					s = I18N.getString(s, "title");
				setText(s);
			}
			setHorizontalAlignment(SwingConstants.LEFT);
			return this;
		} else if (value instanceof String) {
			String s = (String) value;
			setText("   " + I18N.tr(s));
            setFont(new Font(list.getFont().getName(), Font.BOLD, list.getFont().getSize() + 4));
			setForeground(Color.BLACK);
			setHorizontalAlignment(SwingConstants.LEFT);
			return this;
		} else
			throw new RuntimeException("unknown format of object in getcelllistrenderer");
	}

}

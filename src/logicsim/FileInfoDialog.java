package logicsim;

import logicsim.localization.I18N;
import logicsim.localization.Lang;

import java.awt.*;

import javax.swing.*;


public class FileInfoDialog {

	public static boolean showFileInfo(Component frame, LogicSimFile lsFile) {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

        // Label
		c.gridx = 0; c.gridy = 0;
		c.gridwidth = 1; c.gridheight = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.weightx = 0.0; c.weighty = 0.0;
		c.ipadx = 5; c.ipady = 5;
		c.insets = new Insets(5, 5, 5, 5);
		panel.add(new JLabel(I18N.tr(Lang.LABEL)), c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1.0;
        final JTextField txtLabel = new JTextField(lsFile.getLabel());
        panel.add(txtLabel, c);

        // Name
		c.gridx = 0; c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.weightx = 0.0;
        panel.add(new JLabel(I18N.tr(Lang.NAME)), c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1.0;
        final JTextField txtName = new JTextField(lsFile.getName());
        panel.add(txtName, c);

        // Description
		c.gridx = 0; c.gridy = 2;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.FIRST_LINE_END;
		c.weightx = 0.0;
		panel.add(new JLabel(I18N.tr(Lang.DESCRIPTION)), c);

		c.gridx = 1;
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.weightx = 1.0; c.weighty = 1.0;
		final JTextArea txtDescription = new JTextArea(lsFile.getDescription());
		txtDescription.setLineWrap(true);
		panel.add(new JScrollPane(txtDescription), c);

		JOptionPane pane = new JOptionPane(panel);
		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		String[] options = new String[] { I18N.tr(Lang.OK), I18N.tr(Lang.CANCEL) };
		pane.setOptions(options);
		JDialog dlg = pane.createDialog(frame, I18N.tr(Lang.PROPERTIES));
		dlg.setResizable(true);
		dlg.setSize(500, 285);
		dlg.setVisible(true);
		if (I18N.tr(Lang.OK).equals(pane.getValue())) {
            lsFile.setLabel(txtLabel.getText());
            lsFile.setName(txtName.getText());
			lsFile.setDescription(txtDescription.getText());
			return true;
		}
		return false;
	}
}

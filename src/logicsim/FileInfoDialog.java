package logicsim;

import logicsim.localization.I18N;
import logicsim.localization.Lang;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.LineBorder;

public class FileInfoDialog {

	public static boolean showFileInfo(Component frame, LogicSimFile lsFile) {
		JPanel panel = new JPanel();

        //Label
        JLabel lblLable = new JLabel();
        lblLable.setText(I18N.tr(Lang.LABEL));
        lblLable.setBounds(new Rectangle(15, 15, 100, 23));

        JTextField txtLabel = new JTextField(lsFile.getLabel());
        txtLabel.setBounds(new Rectangle(120, 15, 235, 23));

        //Name
        JLabel lblName = new JLabel();
        lblName.setText(I18N.tr(Lang.NAME));
        lblName.setBounds(new Rectangle(15, 56, 100, 23));

        JTextField txtName = new JTextField(lsFile.getName());
        txtName.setBounds(new Rectangle(120, 56, 235, 23));

        //Description
		JLabel lblDescription = new JLabel();
		lblDescription.setText(I18N.tr(Lang.DESCRIPTION));
		lblDescription.setBounds(new Rectangle(15, 96, 100, 23));

		JTextArea txtDescription = new JTextArea(lsFile.getDescription());
		txtDescription.setBounds(new Rectangle(120, 96, 235, 88));
        txtDescription.setBorder(new LineBorder(Color.gray, 1));

		panel.setLayout(null);
        panel.add(lblLable, null);
        panel.add(txtLabel, null);
        panel.add(lblName, null);
        panel.add(txtName, null);
		panel.add(lblDescription, null);
		panel.add(txtDescription, null);

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

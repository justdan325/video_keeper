import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingsDialog extends JDialog {
	private static final int WIN_X = 200;
	private static final int WIN_Y = 200;
	
	private JTextField dbFileTextField;
	private JPanel textFieldPanel;
	private JFrame parent;
	
	public static void main(String[] args) {
		new SettingsDialog(null);
	}

	public SettingsDialog(JFrame parent) {
		this.dbFileTextField = new JTextField();
		this.textFieldPanel = new JPanel(new GridLayout(3,1));
		this.parent = parent;
		
		textFieldPanel.add(dbFileTextField);
		
		this.add(textFieldPanel);
		
		addListeners();
		
		this.setTitle(MainGui.PROG_NAME + " -- Settings");
		this.setSize(new Dimension(WIN_X, WIN_Y));
		this.setResizable(false);
	}
	
	public void showDialog() {
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
	
	private void addListeners() {
		dbFileTextField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("TODO: Update database file");
			}
		});
	}
}

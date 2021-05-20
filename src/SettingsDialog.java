import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingsDialog extends JDialog {
	private static final int WIN_X = 400;
	private static final int WIN_Y = 400;
	
	private JTextField dbFileTextField;
	private JButton dbFileButton;
	private JFrame parent;
	private DataModel model;
	
//	public static void main(String[] args) {
//		new SettingsDialog(null);
//	}

	public SettingsDialog(JFrame parent, DataModel model) {
		this.dbFileTextField = new JTextField();
		this.dbFileButton = new JButton();
		this.parent = parent;
		this.model = model;
		
		this.add(makeTextFieldPanel());
		
		addListeners();
		
		this.setTitle(MainGui.PROG_NAME + " -- Settings");
		this.setSize(new Dimension(WIN_X, WIN_Y));
		this.setResizable(false);
	}
	
	public void showDialog() {
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
	
	private JPanel makeTextFieldPanel() {
		JPanel textFieldPanel = new JPanel(new GridLayout(3,3));
		JPanel dbSubPanel = new JPanel(new FlowLayout());
		
		dbFileTextField.setPreferredSize(new Dimension(300, 30));
		dbFileTextField.setText(model.getDatabaseFile());
		dbFileTextField.setEditable(false);
		dbFileButton.setPreferredSize(new Dimension(20, 30));
		
		dbSubPanel.add(dbFileTextField);
		dbSubPanel.add(dbFileButton);
		
		textFieldPanel.add(dbSubPanel);
		
		return textFieldPanel;
	}
	
	private void addListeners() {
//		this.addWindowListener(new WindowAdapter() {
//            @Override
//            public void windowClosing(WindowEvent e) {
//                System.out.println("TODO: Save settings...");
//                //do something...
//            }
//        });
		
//		dbFileTextField.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				System.out.println("TODO: Update database file...");
//			}
//		});
		
		dbFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectNewDatabaseFile();
			}
		});
	}
	
	private void selectNewDatabaseFile() {
		File currDatabase = new File(model.getDatabaseFile());
		JFileChooser chooser = new JFileChooser(currDatabase.getParent());
		int option = chooser.showOpenDialog(this);
		
		if(option == JFileChooser.APPROVE_OPTION) {
			currDatabase = chooser.getSelectedFile();
			model.setDatabaseFile(currDatabase.getAbsolutePath());
			dbFileTextField.setText(currDatabase.getAbsolutePath());
		}
	}
}

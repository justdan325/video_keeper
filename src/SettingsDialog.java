import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingsDialog extends JDialog {
	private static final String DIA_TITLE 			= "Settings";
	private static final String BROWSE_BTN_TITLE 	= "Browse";
	private static final String AUTO_SAVE_TITLE		= "Auto Save upon Exit";
	private static final int 	WIN_X 				= 500;
	private static final int 	WIN_Y 				= 275;
	
	private JTextField dbFileTextField;
	private JPanel mainPanel;
	private JButton dbFileButton;
	private JCheckBox autoSaveCheckbox;
	private JFrame parent;
	private DataModel model;
	
//	public static void main(String[] args) {
//		new SettingsDialog(null);
//	}

	public SettingsDialog(JFrame parent, DataModel model) {
		this.dbFileTextField = new JTextField();
		this.dbFileButton = new JButton(BROWSE_BTN_TITLE);
		this.autoSaveCheckbox = new JCheckBox(AUTO_SAVE_TITLE);
		this.mainPanel = new JPanel(new GridLayout(3, 1));
		this.parent = parent;
		this.model = model;
		
		mainPanel.add(makeTitleLabel());
		mainPanel.add(makeTextFieldPanel());
		mainPanel.add(makeCheckboxPanel());
		
		this.add(mainPanel);
		
		addListeners();
		
		this.setTitle(MainGui.PROG_NAME + " -- Settings");
		this.setSize(new Dimension(WIN_X, WIN_Y));
		this.setResizable(false);
	}
	
	public void showDialog() {
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
	
	private JLabel makeTitleLabel() {
		JLabel titleLabel = new JLabel(DIA_TITLE);
		
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setFont(new Font(MainGui.FONT, Font.BOLD, 30));
		
		return titleLabel;
	}
	
	private JPanel makeTextFieldPanel() {
		JPanel textFieldPanel = new JPanel(new GridLayout(2,1));
		JPanel dbSubPanel = new JPanel(new FlowLayout());
		JLabel dbFileLabel = new JLabel("Database File");
		
		dbFileTextField.setPreferredSize(new Dimension(350, 30));
		dbFileTextField.setText(model.getDatabaseFile());
		dbFileTextField.setEditable(false);
		dbFileButton.setPreferredSize(new Dimension(90, 30));
		dbFileLabel.setHorizontalAlignment(JLabel.CENTER);
		dbFileLabel.setFont(new Font(MainGui.FONT, Font.PLAIN, 12));
		
		dbSubPanel.add(dbFileTextField);
		dbSubPanel.add(dbFileButton);
		
		textFieldPanel.add(dbFileLabel);
		textFieldPanel.add(dbSubPanel);
		
		return textFieldPanel;
	}
	
	private JPanel makeCheckboxPanel() {
		JPanel checkboxPanel = new JPanel(new GridLayout());
		
		autoSaveCheckbox.setHorizontalAlignment(JCheckBox.CENTER);
		
		if(model.isAutoSaveOnExit()) {
			autoSaveCheckbox.setSelected(true);
		} else {
			autoSaveCheckbox.setSelected(false);
		}
		
		checkboxPanel.add(autoSaveCheckbox);
		
		return checkboxPanel;
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
		
		autoSaveCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					model.setAutoSaveOnExit(true);
				} else if(arg0.getStateChange() == ItemEvent.DESELECTED) {
					model.setAutoSaveOnExit(false);
				}
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

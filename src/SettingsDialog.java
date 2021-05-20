import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingsDialog extends JDialog {
	private static final String DIA_TITLE 			= "Settings";
	private static final String BROWSE_BTN_TITLE 	= "Browse";
	private static final String AUTO_SAVE_TITLE		= "Auto Save upon Exit";
	private static final String SAVE_TITLE			= "Save";
	private static final String EXPORT_TITLE		= "Export";
	private static final String REFRESH_TITLE		= "Refresh";
	private static final int 	WIN_X 				= 500;
	private static final int 	WIN_Y 				= 325;
	private static final int	BTN_X				= 90;
	private static final int	BTN_Y				= 30;
	
	private JTextField dbFileTextField;
	private JPanel mainPanel;
	private JButton dbFileButton;
	private JButton saveButton;
	private JButton exportButton;
	private JButton refreshButton;
	private JCheckBox autoSaveCheckbox;
	private MainGui parent;
	private DataModel model;
	
//	public static void main(String[] args) {
//		new SettingsDialog(null);
//	}

	public SettingsDialog(MainGui parent, DataModel model) {
		this.dbFileTextField = new JTextField();
		this.dbFileButton = new JButton(BROWSE_BTN_TITLE);
		this.saveButton = new JButton(SAVE_TITLE);
		this.exportButton = new JButton(EXPORT_TITLE);
		this.refreshButton = new JButton(REFRESH_TITLE);
		this.autoSaveCheckbox = new JCheckBox(AUTO_SAVE_TITLE);
		this.mainPanel = new JPanel(new GridLayout(4, 1));
		this.parent = parent;
		this.model = model;
		
		mainPanel.add(makeTitleLabel());
		mainPanel.add(makeTextFieldPanel());
		mainPanel.add(makeCheckboxPanel());
		mainPanel.add(makeButtonPanel());
		
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
		dbFileButton.setPreferredSize(new Dimension(BTN_X, BTN_Y));
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
	
	private JPanel makeButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));

		saveButton.setPreferredSize(new Dimension(BTN_X, BTN_Y));
		exportButton.setPreferredSize(new Dimension(BTN_X, BTN_Y));
		refreshButton.setPreferredSize(new Dimension(BTN_X, BTN_Y));
		
		buttonPanel.add(saveButton);
		buttonPanel.add(exportButton);
		buttonPanel.add(refreshButton);
		
		return buttonPanel;
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
		
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.save();
			}
		});
		
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				refresh();
			}
		});
		
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("TODO: Export the watch list...");
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
	
	private void refresh() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				String mess = "Save changes to watch list before refreshing?";
				int option = JOptionPane.showOptionDialog(parent.getSettingsDialog(), mess, MainGui.PROG_NAME + " -- Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				
				if(option == JOptionPane.YES_OPTION) {
					setLocked(true);
					parent.save();
					parent.refresh();
					setLocked(false);
				} else if(option == JOptionPane.NO_OPTION) {
					setLocked(true);
					parent.refresh();
					setLocked(false);
				}
			}
		});
		
		thread.start();
	}
	
	private void setLocked(boolean locked) {
		this.dbFileTextField.setEnabled(!locked);
		this.dbFileButton.setEnabled(!locked);
		this.saveButton.setEnabled(!locked);
		this.exportButton.setEnabled(!locked);
		this.refreshButton.setEnabled(!locked);
		this.autoSaveCheckbox.setEnabled(!locked);
	}
	
	private void selectNewDatabaseFile() {
		//disable UI elements to prevent race condition
		parent.setLocked(true);
		
		File currDatabase = new File(model.getDatabaseFile());
		JFileChooser chooser = new JFileChooser(currDatabase.getParent());
		int option = chooser.showOpenDialog(this);
		
		if(option == JFileChooser.APPROVE_OPTION) {
			currDatabase = chooser.getSelectedFile();
			model.setDatabaseFile(currDatabase.getAbsolutePath());
			dbFileTextField.setText(currDatabase.getAbsolutePath());
		}
		
		//re-enable UI elements
		parent.setLocked(false);
	}
}

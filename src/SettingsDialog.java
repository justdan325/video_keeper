import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SettingsDialog extends JDialog implements WindowListener {
	private static final String DIA_TITLE 			= "Settings";
	private static final String BROWSE_BTN_TITLE 	= "Browse";
	private static final String AUTO_SAVE_TITLE		= "Auto Save upon Exit";
	private static final String CHECK_DUPL_TITLE	= "Check for Duplicate Videos";
	private static final String SAVE_TITLE			= "Save";
	private static final String EXPORT_TITLE		= "Export";
	private static final String REFRESH_TITLE		= "Refresh";
	private static final String TOOLTIP_SAVE		= "Save changes to the watch list.";
	private static final String TOOLTIP_EXPORT		= "Export watch list to text file of URLs.";
	private static final String TOOLTIP_REFRESH		= "Reload the watch list and re-fetch video metadata.";
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
	private JCheckBox checkDuplCheckbox;
	private MainGui parent;
	private DataModel model;
	private boolean locked;
	private boolean childDialogOpen;
	
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
		this.checkDuplCheckbox = new JCheckBox(CHECK_DUPL_TITLE);
		this.mainPanel = new JPanel(new GridLayout(4, 1));
		this.parent = parent;
		this.model = model;
		this.locked = false;
		this.childDialogOpen = false;
		
		mainPanel.setBackground(MainGui.PROG_COLOR_BKRND);
		
		mainPanel.add(makeTitleLabel());
		mainPanel.add(makeTextFieldPanel());
		mainPanel.add(makeCheckboxPanel());
		mainPanel.add(makeButtonPanel());
		
		this.add(mainPanel);
		
		addListeners();
		monitor();
		
		this.getContentPane().setBackground(MainGui.PROG_COLOR_BKRND);
		this.setTitle(MainGui.PROG_NAME + " -- Settings");
		this.setSize(new Dimension(WIN_X, WIN_Y));
		this.setResizable(false);
		this.addWindowListener(this);
	}
	
	public void showDialog() {
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
	
	private JLabel makeTitleLabel() {
		JLabel titleLabel = new JLabel(DIA_TITLE);
		
		titleLabel.setBackground(MainGui.PROG_COLOR_BKRND);
		titleLabel.setForeground(MainGui.PROG_COLOR_TXT_LT);
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setFont(new Font(MainGui.PROG_FONT, Font.BOLD, 30));
		
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
		dbFileButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
		textFieldPanel.setBackground(MainGui.PROG_COLOR_BKRND);
		dbSubPanel.setBackground(MainGui.PROG_COLOR_BKRND);
		dbFileLabel.setHorizontalAlignment(JLabel.CENTER);
		dbFileLabel.setFont(new Font(MainGui.PROG_FONT, Font.PLAIN, 12));
		dbFileLabel.setForeground(MainGui.PROG_COLOR_TXT_LT);
		
		dbSubPanel.add(dbFileTextField);
		dbSubPanel.add(dbFileButton);
		
		textFieldPanel.add(dbFileLabel);
		textFieldPanel.add(dbSubPanel);
		
		return textFieldPanel;
	}
	
	private JPanel makeCheckboxPanel() {
		JPanel checkboxPanel = new JPanel(new GridLayout());
		
		checkboxPanel.setBackground(MainGui.PROG_COLOR_BKRND);
		checkboxPanel.setForeground(MainGui.PROG_COLOR_TXT_LT);
		autoSaveCheckbox.setHorizontalAlignment(JCheckBox.CENTER);
		autoSaveCheckbox.setBackground(MainGui.PROG_COLOR_BKRND);
		autoSaveCheckbox.setForeground(MainGui.PROG_COLOR_TXT_LT);
		checkDuplCheckbox.setHorizontalAlignment(JCheckBox.CENTER);
		checkDuplCheckbox.setBackground(MainGui.PROG_COLOR_BKRND);
		checkDuplCheckbox.setForeground(MainGui.PROG_COLOR_TXT_LT);
		
		if(model.isAutoSaveOnExit()) {
			autoSaveCheckbox.setSelected(true);
		} else {
			autoSaveCheckbox.setSelected(false);
		}
		
		if(model.isCheckForDupl()) {
			checkDuplCheckbox.setSelected(true);
		} else {
			checkDuplCheckbox.setSelected(false);
		}
		
		checkboxPanel.add(autoSaveCheckbox);
		checkboxPanel.add(checkDuplCheckbox);
		
		return checkboxPanel;
	}
	
	private JPanel makeButtonPanel() {
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
		
		buttonPanel.setBackground(MainGui.PROG_COLOR_BKRND);

		saveButton.setPreferredSize(new Dimension(BTN_X, BTN_Y));
		saveButton.setToolTipText(TOOLTIP_SAVE);
		saveButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
		exportButton.setPreferredSize(new Dimension(BTN_X, BTN_Y));
		exportButton.setToolTipText(TOOLTIP_EXPORT);
		exportButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
		refreshButton.setPreferredSize(new Dimension(BTN_X, BTN_Y));
		refreshButton.setToolTipText(TOOLTIP_REFRESH);
		refreshButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
		
		buttonPanel.add(saveButton);
		buttonPanel.add(exportButton);
		buttonPanel.add(refreshButton);
		
		return buttonPanel;
	}
	
	private void addListeners() {
		dbFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				selectNewDatabaseFile();
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				save();
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
				export();
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
		
		checkDuplCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if(arg0.getStateChange() == ItemEvent.SELECTED) {
					model.setCheckForDupl(true);
				} else if(arg0.getStateChange() == ItemEvent.DESELECTED) {
					model.setCheckForDupl(false);
				}
			}
		});
	}
	
	private void save() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				boolean saved = false;
				
				setLocked(true);
				saved = parent.save();
				
				if(!saved) {
					String mess = "Watch list could not be saved to database file.";
					childDialogOpen = true;
					JOptionPane.showMessageDialog(parent.getSettingsDialog(), mess, MainGui.PROG_NAME + " -- Save Failure", JOptionPane.ERROR_MESSAGE);
					childDialogOpen = false;
				}
				
				setLocked(false);
				
				if(saved) {
					saveButton.setEnabled(false);
					saveButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
				}
			}
		});
		
		thread.start();
	}
	
	private void export() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				childDialogOpen = true;
				String destination = (String)JOptionPane.showInputDialog(parent, "Enter destination file to export to.", MainGui.PROG_NAME + " -- Export", JOptionPane.QUESTION_MESSAGE, null, null, "urls.txt");
				childDialogOpen = false;
				boolean success = false;
				
				if (destination != null) {
					setLocked(true);
					success = parent.export(destination);
					setLocked(false);

					if (success) {
						String mess = "URLs have been exported!";
						childDialogOpen = true;
						JOptionPane.showMessageDialog(parent.getSettingsDialog(), mess, MainGui.PROG_NAME + " -- Export Success", JOptionPane.INFORMATION_MESSAGE);
						childDialogOpen = false;
					} else {
						childDialogOpen = true;
						String mess = "Could not export to specified file.";
						JOptionPane.showMessageDialog(parent.getSettingsDialog(), mess, MainGui.PROG_NAME + " -- Export Failure", JOptionPane.ERROR_MESSAGE);
						childDialogOpen = false;
					}
				}
			}
		});
		
		thread.start();
	}
	
	private void refresh() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				String mess = "Save changes to watch list before refreshing?";
				childDialogOpen = true;
				int option = JOptionPane.showOptionDialog(parent.getSettingsDialog(), mess, MainGui.PROG_NAME + " -- Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
				childDialogOpen = false;
				
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
	
	private void monitor() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				//need to wait here for the keeper to populate
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
				}
				
				int size = parent.getCount();
				saveButton.setEnabled(false);
				saveButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
				
				for(;;) {
					while(locked) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
					}
					
					if(parent.getCount() != size) {
						size = parent.getCount();
						saveButton.setEnabled(true);
						saveButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
					}
					
					if(size > 0) {
						exportButton.setEnabled(true);
						exportButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
						refreshButton.setEnabled(true);
						refreshButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
					} else {
						exportButton.setEnabled(false);
						exportButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
						refreshButton.setEnabled(false);
						refreshButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
					}
					
					if(checkDuplCheckbox.isSelected() != model.isCheckForDupl()) {
						checkDuplCheckbox.setSelected(model.isCheckForDupl());
					}
					
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		
		thread.start();
	}
	
	private void setLocked(boolean locked) {
		this.locked = locked;
		this.dbFileTextField.setEnabled(!locked);
		this.dbFileButton.setEnabled(!locked);
		this.dbFileButton.setBackground(locked ? MainGui.PROG_COLOR_BTN_DIS : MainGui.PROG_COLOR_BTN_EN);
		this.saveButton.setEnabled(!locked);
		this.saveButton.setBackground(locked ? MainGui.PROG_COLOR_BTN_DIS : MainGui.PROG_COLOR_BTN_EN);
		this.exportButton.setEnabled(!locked);
		this.exportButton.setBackground(locked ? MainGui.PROG_COLOR_BTN_DIS : MainGui.PROG_COLOR_BTN_EN);
		this.refreshButton.setEnabled(!locked);
		this.refreshButton.setBackground(locked ? MainGui.PROG_COLOR_BTN_DIS : MainGui.PROG_COLOR_BTN_EN);
		this.autoSaveCheckbox.setEnabled(!locked);
		this.checkDuplCheckbox.setEnabled(!locked);
	}
	
	private void selectNewDatabaseFile() {
		//disable UI elements to prevent race condition
		parent.setLocked(true);
		setLocked(true);
		
		File currDatabase = new File(model.getDatabaseFile());
		JFileChooser chooser = new JFileChooser(currDatabase.getParent());
		this.childDialogOpen = true;
		int option = chooser.showOpenDialog(this);
		this.childDialogOpen = false;
		
		if(option == JFileChooser.APPROVE_OPTION) {
			currDatabase = chooser.getSelectedFile();
			model.setDatabaseFile(currDatabase.getAbsolutePath());
			dbFileTextField.setText(currDatabase.getAbsolutePath());
		}
		
		//re-enable UI elements
		parent.setLocked(false);
		setLocked(false);
	}
	
	public void setChildDialogOpen(boolean childDialogOpen) {
		this.childDialogOpen = childDialogOpen;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		this.setVisible(false);
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		if (!childDialogOpen) {
			this.setVisible(false);
		}
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
}

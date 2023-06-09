import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;
import javax.swing.event.ListDataListener;

@SuppressWarnings("serial")
public class SearchDialog extends JDialog implements WindowListener {
	private static final String DIALOG_TITLE 		= MainGui.PROG_NAME + " -- Search List";
	private static final String DIA_TITLE 			= "Settings";
	private static final String BROWSE_BTN_TITLE 	= "Browse";
	private static final String AUTO_SAVE_TITLE		= "Auto Save upon Exit";
	private static final String CHECK_DUPL_TITLE	= "Check for Duplicate Videos";
//	private static final String SAVE_TITLE			= "Save";
	private static final String EXPORT_TITLE		= "Export";
	private static final String REFRESH_TITLE		= "Refresh All";
	private static final String OPEN_OP_TITLE		= "Open Op.";
//	private static final String TOOLTIP_SAVE		= "Save changes to the watch list.";
	private static final String TOOLTIP_EXPORT		= "Export watch list to text file of URLs.";
	private static final String TOOLTIP_REFRESH		= "Reload the watch list and re-fetch video metadata.";
	private static final String TOOLTIP_OPEN_OP		= "Select the operation for how to open video links.";
	private static final int 	WIN_X 				= 750;
	private static final int 	WIN_Y 				= 500;
	private static final int	BTN_X				= 120;
	private static final int	BTN_Y				= 30;
	private static final int	ROWS_DEFAULT		= 30;
	private static final int	COLS_DEFAULT		= 5;
	
	private JTextField dbFileTextField;
	private JPanel mainPanel;
	private JButton dbFileButton;
//	private JButton saveButton;
	private JButton exportButton;
	private JButton refreshAllButton;
	private JButton openOpButton;
	private JCheckBox autoSaveCheckbox;
	private JCheckBox checkDuplCheckbox;
	private Component parent;
	private DataModel model;
	private boolean locked;
	private boolean childDialogOpen;
	
	public static void main(String[] args) {
		SearchDialog application = new SearchDialog(new DataModel(), null);
		application.setVisible(true);
	}
	
	public SearchDialog(DataModel model, Component parent) {
		this.dbFileTextField = new JTextField();
		this.dbFileButton = new JButton(BROWSE_BTN_TITLE);
		this.exportButton = new JButton(EXPORT_TITLE);
		this.refreshAllButton = new JButton(REFRESH_TITLE);
		this.openOpButton = new JButton(OPEN_OP_TITLE);
		this.autoSaveCheckbox = new JCheckBox(AUTO_SAVE_TITLE);
		this.checkDuplCheckbox = new JCheckBox(CHECK_DUPL_TITLE);
		this.mainPanel = new JPanel(new BorderLayout());
		this.parent = parent;
		this.model = model;
		this.locked = false;
		this.childDialogOpen = false;
		
		mainPanel.setBackground(MainGui.PROG_COLOR_BKRND);
		
		mainPanel.add(makeTablePanel(), BorderLayout.CENTER);
//		mainPanel.add(makeTextFieldPanel());
//		mainPanel.add(makeCheckboxPanel());
//		mainPanel.add(makeButtonPanel());
		
		this.add(mainPanel);
		
//		addListeners();
		monitor();
		
		this.getContentPane().setBackground(MainGui.PROG_COLOR_BKRND);
		this.setTitle(DIALOG_TITLE);
		this.setSize(new Dimension(WIN_X, WIN_Y));
		this.setResizable(false);
		this.addWindowListener(this);
	}
	
	private JPanel makeTablePanel() {
		JPanel listPanel = new JPanel(new GridLayout(1, 1));
		JTable table = new JTable(ROWS_DEFAULT, COLS_DEFAULT);
		JScrollPane scrollPane = new JScrollPane(table);
		
		listPanel.add(scrollPane);
		
		return listPanel;
	}
	
	private void monitor() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(;;) {
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		this.setVisible(false);
		this.dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
}


    

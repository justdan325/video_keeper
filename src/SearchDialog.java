import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Optional;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class SearchDialog extends JDialog implements WindowListener {
	private static final String DIALOG_TITLE 		= MainGui.PROG_NAME + " -- Search List";
	private static final String DIA_TITLE 			= "Settings";
	private static final String COL_TITLE_NUM 		= "#";
	private static final String COL_TITLE_TITLE		= "Title";
	private static final String COL_TITLE_AUTH		= "Author";
	private static final String COL_TITLE_DATE		= "Date";
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
	private static final int	ROWS_DEFAULT		= 0;
	private static final int	COLS_DEFAULT		= 5;
	private static final int 	COL_MAX_WIDTH_NUM 	= 25;
	private static final int 	COL_MAX_WIDTH_TITLE = 600;
	private static final int 	COL_MAX_WIDTH_AUTH 	= 200;
	private static final int 	COL_MAX_WIDTH_DATE 	= 100;
	
	private JTextField dbFileTextField;
	private JTable mainTable;
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
//		this.dbFileTextField = new JTextField();
//		this.dbFileButton = new JButton(BROWSE_BTN_TITLE);
//		this.exportButton = new JButton(EXPORT_TITLE);
//		this.refreshAllButton = new JButton(REFRESH_TITLE);
//		this.openOpButton = new JButton(OPEN_OP_TITLE);
//		this.autoSaveCheckbox = new JCheckBox(AUTO_SAVE_TITLE);
//		this.checkDuplCheckbox = new JCheckBox(CHECK_DUPL_TITLE);
		this.mainPanel = new JPanel(new BorderLayout());
		this.parent = parent;
		this.model = model;
//		this.locked = false;
//		this.childDialogOpen = false;
		
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
		String[] colNames = new String[] {COL_TITLE_NUM, COL_TITLE_TITLE, COL_TITLE_AUTH, COL_TITLE_DATE};
		DefaultTableModel tableModel = new DefaultTableModel(new String[ROWS_DEFAULT][COLS_DEFAULT], colNames);
		JTable table = new JTable();
		JScrollPane scrollPane = new JScrollPane(table);
		
		table.setModel(tableModel);
		table.getColumn(COL_TITLE_NUM).setMaxWidth(COL_MAX_WIDTH_NUM);
		table.getColumn(COL_TITLE_TITLE).setMaxWidth(COL_MAX_WIDTH_TITLE);
		table.getColumn(COL_TITLE_AUTH).setMaxWidth(COL_MAX_WIDTH_AUTH);
		table.getColumn(COL_TITLE_DATE).setMaxWidth(COL_MAX_WIDTH_DATE);
		
		listPanel.add(scrollPane);
		
		this.mainTable = table;
		
		return listPanel;
	}
	
	private void populateList() {
		if (model.getVideoList().isPresent()) {
			Optional<VideoList> videoListOpt = model.getVideoList();
			VideoList videoList = null;

			if (videoListOpt.isPresent()) {
				//make a copy
				videoList = new VideoList(videoListOpt.get());

				while (videoList.size() > 0) {
					Optional<VideoDataNode> curr = videoList.popCurr();

					if (curr.isPresent()) {
						addToList(curr.get());
					}
				}
			}
		}
	}
	
	private void addToList(VideoDataNode node) {
		if(node.isEmpty() == false) {
			Vector<String> row = new Vector<String>();
			
		    row.add("" + (((DefaultTableModel) mainTable.getModel()).getRowCount() + 1));
		    row.add(node.getTitle());
		    row.add(node.getChannel());
		    row.add(node.getDate());
		    
			((DefaultTableModel) mainTable.getModel()).addRow(row);
		}
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
	public void windowOpened(WindowEvent arg0) {
		populateList();
	}
}


    
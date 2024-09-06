import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Optional;
import java.util.Vector;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class SearchDialog extends JDialog implements WindowListener {
	private static final String DIALOG_TITLE 		= MainGui.PROG_NAME + " -- Search List";
	private static final String COL_TITLE_NUM 		= "#";
	private static final String COL_TITLE_TITLE		= "Title";
	private static final String COL_TITLE_AUTH		= "Author";
	private static final String COL_TITLE_DATE		= "Date";
//	private static final String SEARCH_BTN_TITLE 	= "Search";
	private static final String SEARCH_BTN_TITLE 	= "🔎";
	private static final String OPTIONS_BTN_TITLE 	= "⚙️";
	private static final String CLEAR_BTN_TITLE		= "X";
	private static final String PLAY_BTN_TITLE 		= "Play";
	private static final String CPY_URL_BTN_TITLE	= "Copy URL";
	private static final String EDIT_BTN_TITLE		= "Edit";
	private static final String DEL_BTN_TITLE		= "Delete";
	private static final String REFR_BTN_TITLE		= "Resresh";
	private static final String HEAD_BTN_TITLE		= "Move to Head";
	private static final String TAIL_BTN_TITLE		= "Move to Tail";
	private static final String INDEX_BTN_TITLE		= "Move to Index";
	private static final String UP_BTN_TITLE		= "️⬆️";
	private static final String DOWN_BTN_TITLE		= "⬇️";
	private static final int 	WIN_X 				= 1050;
	private static final int 	WIN_Y 				= 600;
	private static final int	SEARCH_BAR_X		= 50;
	private static final int	ROWS_DEFAULT		= 0;
	private static final int	COLS_DEFAULT		= 5;
	private static final int 	COL_MAX_WIDTH_NUM 	= 50;
	private static final int 	COL_MAX_WIDTH_TITLE = 600;
	private static final int 	COL_MAX_WIDTH_AUTH 	= 200;
	private static final int 	COL_MAX_WIDTH_DATE 	= 100;
	
	private JTextField searchBar;
	private JTable mainTable;
	private JPanel mainPanel;
	private JButton searchButton;
	private JButton clearButton;
	private JButton optionsButton;
	private JButton playButton;
	private JButton copyUrlButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JButton moveToHeadButton;
    private JButton moveToTailButton;
    private JButton moveToIndexButton;
    private JButton moveUpButton;
    private JButton moveDownButton;
	@SuppressWarnings("unused")
	private Component parent;
	private DataModel model;
	private EditDialog editor;
	private OptionsDialog optionsDialog;
	private boolean refreshing;
	
	public static void main(String[] args) {
		SearchDialog application = new SearchDialog(new DataModel(), null);
		application.setVisible(true);
	}
	
	public SearchDialog(DataModel model, Component parent) {
		this.mainPanel = new JPanel(new BorderLayout());
		this.parent = parent;
		this.model = model;
		this.editor = new EditDialog(this);
		this.optionsDialog = new OptionsDialog(model, this);
		this.refreshing = false;
		
		mainPanel.setBackground(MainGui.PROG_COLOR_BKRND);
		
		mainPanel.add(makeSearchPanel(), BorderLayout.NORTH);
		mainPanel.add(makeTablePanel(), BorderLayout.CENTER);
		mainPanel.add(makeOptionPanel(), BorderLayout.EAST);
		
		this.add(mainPanel);
		
		setOptionsLocked(true);
		monitor();
		
		this.getContentPane().setBackground(MainGui.PROG_COLOR_BKRND);
		this.setTitle(DIALOG_TITLE);
		this.setSize(new Dimension(WIN_X, WIN_Y));
		this.setResizable(false);
		this.addWindowListener(this);
		
		addActionListeners();
	}
	
	private void addActionListeners() {
		searchBar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchAndSet(searchBar.getText(), model.isCaseSensitive(), model.isSearchThruTitles(), model.isSearchThruChannels(), model.isSearchThruDates());
			}
		});
		
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchAndSet(searchBar.getText(), model.isCaseSensitive(), model.isSearchThruTitles(), model.isSearchThruChannels(), model.isSearchThruDates());
			}
		});
		
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((DefaultTableModel) mainTable.getModel()).setRowCount(0);
				
				populateList();
				
				searchBar.setText("");
				clearButton.setVisible(false);
			}
		});
		
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Optional<VideoDataNode> curr = getCorrespondingNodeFromSelectedCell();
				int listSize = model.getVideoList().get().size();
				int indexToMoveTo = getCorrespondingIndex();
				
				if(curr.isPresent()) {
					model.getVideoKeeper().open(curr, model.isPlayAndDelete());
					populateList();
					
					if (listSize > 0) {
						if (indexToMoveTo >= listSize - 1) {
							indexToMoveTo--;
						}
						
						mainTable.setRowSelectionInterval(indexToMoveTo, indexToMoveTo);
					}
				}
			}
		});
		
		copyUrlButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Optional<VideoDataNode> curr = getCorrespondingNodeFromSelectedCell();
				
				if(curr.isPresent()) {
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(curr.get().getUrl()), null);
				}
			}
		});
		
		moveUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Optional<VideoList> videoList = model.getVideoList();
						int indexOfToMove = getCorrespondingIndex();
						
						if (videoList.isPresent() && videoList.get().size() > 0 && indexOfToMove > 0) {
							int newIndex = indexOfToMove - 1;
							Optional<VideoDataNode> toMove = videoList.get().pop(indexOfToMove);

							if (toMove.isPresent()) {
								videoList.get().insert(newIndex, toMove.get());
								
								populateList();
								mainTable.setRowSelectionInterval(newIndex, newIndex);
								
								model.setRequestSaveButtonEn(true);
							}
							
						}
					}
				}).start();
			}
		});
		
		moveDownButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Optional<VideoList> videoList = model.getVideoList();
						int indexOfToMove = getCorrespondingIndex();
						int newIndex = indexOfToMove + 1;
						
						if (videoList.isPresent() && videoList.get().size() > 0 && indexOfToMove < videoList.get().size()-1) {
							Optional<VideoDataNode> toMove = videoList.get().pop(indexOfToMove);

							if (toMove.isPresent()) {
								videoList.get().insert(newIndex, toMove.get());
								
								populateList();
								mainTable.setRowSelectionInterval(newIndex, newIndex);
							}
							
							model.setRequestSaveButtonEn(true);
						}
					}
				}).start();
			}
		});
		
		moveToHeadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Optional<VideoList> videoList = model.getVideoList();
						
						if (videoList.isPresent() && videoList.get().size() > 0) {
							Optional<VideoDataNode> currInList = videoList.get().peekCurr();
							Optional<VideoDataNode> toMove = videoList.get().pop(getCorrespondingIndex());
							int indexOfCurr = model.getVideoKeeper().getIndexOfNode(currInList);

							if (toMove.isPresent()) {
								videoList.get().prepend(toMove.get());
								
								if(currInList.isPresent()) {
									videoList.get().setIndex(indexOfCurr);
								}
								
								populateList();
							}
							
							model.setRequestSaveButtonEn(true);
						}
					}
				}).start();
			}
		});
		
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						int indexToMoveTo = getCorrespondingIndex();
						
						refreshing = true;
						model.getVideoKeeper().refresh(getCorrespondingIndex());
						populateList();
						model.setRequestSaveButtonEn(true);
						refreshing = false;
						
						mainTable.setRowSelectionInterval(indexToMoveTo, indexToMoveTo);
					}
				}).start();
			}
		});
		
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int indexToMoveTo = getCorrespondingIndex();
				int listSize = model.getVideoList().get().size();
				
				model.getVideoList().get().pop(indexToMoveTo);
				populateList();
				model.setRequestSaveButtonEn(true);
				
				if (listSize > 0) {
					if (indexToMoveTo >= listSize - 1) {
						indexToMoveTo--;
					}
					
					mainTable.setRowSelectionInterval(indexToMoveTo, indexToMoveTo);
				}
			}
		});
		
		moveToTailButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						Optional<VideoList> videoList = model.getVideoList();
						
						if (videoList.isPresent() && videoList.get().size() > 0) {
							Optional<VideoDataNode> currInList = videoList.get().peekCurr();
							Optional<VideoDataNode> toMove = videoList.get().pop(getCorrespondingIndex());
							int indexOfCurr = model.getVideoKeeper().getIndexOfNode(currInList);

							if (toMove.isPresent()) {
								videoList.get().append(toMove.get());
								
								if(currInList.isPresent()) {
									videoList.get().setIndex(indexOfCurr);
								}
								
								populateList();
							}
							
							model.setRequestSaveButtonEn(true);
						}
					}
				}).start();
			}
		});
		
		editButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Optional<VideoDataNode> node = model.getVideoList().get().peek(getCorrespondingIndex());
				
				if (node.isPresent()) {
					editor.editNode(node.get());
					populateList();
					model.setRequestSaveButtonEn(true);
				}
			}
		});
		
		optionsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				optionsDialog.setVisible(true);
			}
		});
		
		mainTable.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					Optional<VideoDataNode> curr = getCorrespondingNodeFromSelectedCell(); 
					
					if(curr.isPresent()) {
						model.getVideoKeeper().open(getCorrespondingIndex(), false);
					}
				}
			}
		});
	}
	
	private JPanel makeSearchPanel() {
		JPanel searchPanel = new JPanel();
		
		this.searchBar = new JTextField();
		this.clearButton = new JButton(CLEAR_BTN_TITLE);
		this.searchButton = new JButton(SEARCH_BTN_TITLE);
		this.optionsButton = new JButton(OPTIONS_BTN_TITLE);
		
		clearButton.setVisible(false);
		searchBar.setColumns(SEARCH_BAR_X);
		searchPanel.add(optionsButton);
		searchPanel.add(searchBar);
		searchPanel.add(clearButton);
		searchPanel.add(searchButton);
		
		return searchPanel;
	}
	
	private JPanel makeTablePanel() {
		JPanel listPanel = new JPanel(new GridLayout(1, 1));
		String[] colNames = new String[] {COL_TITLE_NUM, COL_TITLE_TITLE, COL_TITLE_AUTH, COL_TITLE_DATE};
		SearchTableModel tableModel = new SearchTableModel(ROWS_DEFAULT, COLS_DEFAULT, colNames);
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
	
	private JPanel makeOptionPanel() {
		JPanel optionPanel = new JPanel(new GridLayout(10, 1));
		
		this.playButton = new JButton(PLAY_BTN_TITLE);
        this.copyUrlButton = new JButton(CPY_URL_BTN_TITLE);
        this.editButton = new JButton(EDIT_BTN_TITLE);
        this.deleteButton = new JButton(DEL_BTN_TITLE);
        this.refreshButton = new JButton(REFR_BTN_TITLE);
        this.moveUpButton = new JButton(UP_BTN_TITLE);
        this.moveDownButton = new JButton(DOWN_BTN_TITLE);
        this.moveToIndexButton = new JButton(INDEX_BTN_TITLE);
        this.moveToHeadButton = new JButton(HEAD_BTN_TITLE);
        this.moveToTailButton = new JButton(TAIL_BTN_TITLE);
        
		optionPanel.add(playButton);
		optionPanel.add(copyUrlButton);
		optionPanel.add(editButton);
		optionPanel.add(deleteButton);
		optionPanel.add(refreshButton);
		optionPanel.add(moveUpButton);
		optionPanel.add(moveDownButton);
		optionPanel.add(moveToIndexButton);
		optionPanel.add(moveToHeadButton);
		optionPanel.add(moveToTailButton);
		
		return optionPanel;
	}
	
	private void searchAndSet(String query, boolean caseSensitive, boolean searchByTitle, boolean searchByChannel, boolean searchByDate) {
		clearButton.setVisible(true);
		
		if (model.getVideoList().isPresent() && query.length() > 0) {
			Optional<VideoList> videoListOpt = model.getVideoList();
			VideoList videoList = null;

			if (videoListOpt.isPresent()) {
				int count = 0;
				//make a copy
				videoList = new VideoList(videoListOpt.get());
				
				//reset index
				videoList.resetIndex();

				((DefaultTableModel) mainTable.getModel()).setRowCount(0);

				while (videoList.size() > 0) {
					Optional<VideoDataNode> curr = videoList.popCurr();
					String title = caseSensitive ? curr.get().getTitle().trim() : curr.get().getTitle().trim().toLowerCase();
					String channel = caseSensitive ? curr.get().getChannel().trim() : curr.get().getChannel().trim().toLowerCase();
					String date = caseSensitive ? curr.get().getDate().trim() : curr.get().getDate().trim().toLowerCase();

					query = caseSensitive ? query.trim() : query.toLowerCase().trim();

					count++;

					if (curr.isPresent()) {
						if ((searchByTitle && title.contains(query)) 
								|| (searchByChannel && channel.contains(query))
								|| (searchByDate && date.contains(query.trim()))) {

							addToListNonContiguous(curr.get(), count);
						}
					}
				}
			}
		}
	}
	
	private void clearList() {
		while (((DefaultTableModel) mainTable.getModel()).getRowCount() > 0) {
			((DefaultTableModel) mainTable.getModel()).removeRow(0);
		}
	}
	
	private void populateList() {
		clearList();
		
		if (model.getVideoList().isPresent()) {
			Optional<VideoList> videoListOpt = model.getVideoList();
			VideoList videoList = null;

			if (videoListOpt.isPresent()) {
				//make a copy
				videoList = new VideoList(videoListOpt.get());
				
				//list should be in order from 0 to n, not from index to 0.
				videoList.resetIndex();

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
		    
			if (node.getTitle() != null && node.getTitle().length() > 0) {
				row.add(node.getTitle());
			} else {
				row.add(node.getUrl());
			}
			
		    row.add(node.getChannel());
		    row.add(node.getDate());
		    
			((DefaultTableModel) mainTable.getModel()).addRow(row);
		}
	}
	
	private void addToListNonContiguous(VideoDataNode node, int listNum) {
		if(node.isEmpty() == false) {
			Vector<String> row = new Vector<String>();
			
		    row.add("" + listNum);
		    
			if (node.getTitle() != null && node.getTitle().length() > 0) {
				row.add(node.getTitle());
			} else {
				row.add(node.getUrl());
			}
			
		    row.add(node.getChannel());
		    row.add(node.getDate());
		    
			((DefaultTableModel) mainTable.getModel()).addRow(row);
		}
	}
	
	private Optional<VideoDataNode> getCorrespondingNodeFromSelectedCell() {
		Optional<VideoDataNode> node = Optional.empty();
		
		if (mainTable.getSelectedRow() >= 0) {
			Object valueAt = ((DefaultTableModel) mainTable.getModel()).getValueAt(mainTable.getSelectedRow(), 0);
			
			if (model.getVideoList().isPresent()) {
				node = model.getVideoList().get().peek(Integer.parseInt(((String) valueAt).trim()) - 1);
			}
		}
		
		return node;
	}
	
	private int getCorrespondingIndex() {
		int index = 0;
		
		if (mainTable.getSelectedRow() >= 0) {
			Object valueAt = ((DefaultTableModel) mainTable.getModel()).getValueAt(mainTable.getSelectedRow(), 0);
			
			if (model.getVideoList().isPresent()) {
				index = Integer.parseInt(((String) valueAt).trim()) - 1;
			}
		}
		
		return index;
	}
	
	private void setOptionsLocked(boolean locked) {
		playButton.setEnabled(!locked);
		copyUrlButton.setEnabled(!locked);
		editButton.setEnabled(!locked);
		deleteButton.setEnabled(!locked);
		refreshButton.setEnabled(!locked);
		moveUpButton.setEnabled(!locked);
		moveDownButton.setEnabled(!locked);
		moveToIndexButton.setEnabled(!locked);
		moveToHeadButton.setEnabled(!locked);
		moveToTailButton.setEnabled(!locked);
		
		if(locked) {
			playButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
			copyUrlButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
			editButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
			deleteButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
			refreshButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
			moveUpButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
			moveDownButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
			moveToIndexButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
			moveToHeadButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
			moveToTailButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
		} else {
			playButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
			copyUrlButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
			editButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
			deleteButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
			refreshButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
			moveUpButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
			moveDownButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
			moveToIndexButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
			moveToHeadButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
			moveToTailButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
		}
	}
	
	private void monitor() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(;;) {
					try {
						if (mainTable.getSelectedRow() >= 0 && refreshing == false) {
							setOptionsLocked(false);
						} else {
							setOptionsLocked(true);
						}
						
						Thread.sleep(30);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
	}
	
	@Override
	public void setVisible(boolean visible) {
		populateList();
		super.setVisible(visible);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		((DefaultTableModel) mainTable.getModel()).setRowCount(0);
		populateList();
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
		((SearchTableModel) mainTable.getModel()).setRowCount(0);
		populateList();
	}
}

@SuppressWarnings("serial")
class SearchTableModel extends DefaultTableModel {
	public SearchTableModel(final int ROWS, final int COLS, String[] colNames) {
		super(new String[ROWS][COLS], colNames);
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
}


    

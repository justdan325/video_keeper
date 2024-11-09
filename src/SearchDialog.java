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
	private JDialog thisDialog;
	private DataModel model;
	private EditDialog editor;
	private OptionsDialog optionsDialog;
	private Optional<VideoList> searchResultsOnDisplay;
	private boolean refreshing;
	private boolean displayingSearchResults;
	
	public static void main(String[] args) {
		SearchDialog application = new SearchDialog(new DataModel(), null);
		application.setVisible(true);
	}
	
	public SearchDialog(DataModel model, Component parent) {
		this.mainPanel = new JPanel(new BorderLayout());
		this.parent = parent;
		this.thisDialog = this;
		this.model = model;
		this.editor = new EditDialog(this, model);
		this.optionsDialog = new OptionsDialog(model, this);
		this.searchResultsOnDisplay = Optional.empty();
		this.refreshing = false;
		this.displayingSearchResults = false;
		
		mainPanel.setBackground(MainGui.PROG_COLOR_BKRND);
		
		mainPanel.add(makeSearchPanel(), BorderLayout.NORTH);
		mainPanel.add(makeTablePanel(), BorderLayout.CENTER);
		mainPanel.add(makeOptionPanel(), BorderLayout.EAST);
		
		this.add(mainPanel);
		
		setOptionsLocked(true, displayingSearchResults);
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
				displayingSearchResults = true;
				searchAndSet(searchBar.getText(), model.isCaseSensitive(), model.isSearchThruTitles(), model.isSearchThruChannels(), model.isSearchThruDates());
			}
		});
		
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				displayingSearchResults = true;
				searchAndSet(searchBar.getText(), model.isCaseSensitive(), model.isSearchThruTitles(), model.isSearchThruChannels(), model.isSearchThruDates());
			}
		});
		
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				((DefaultTableModel) mainTable.getModel()).setRowCount(0);
				
				searchResultsOnDisplay = Optional.empty();
				displayingSearchResults = false;
				populateList(model.getVideoList(), false);
				
				searchBar.setText("");
				setTitle(DIALOG_TITLE);
				clearButton.setVisible(false);
			}
		});
		
		playButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VideoList currList = searchResultsOnDisplay.isPresent() ? searchResultsOnDisplay.get() : model.getVideoList().get();
				Optional<VideoDataNode> curr = getCorrespondingNodeFromSelectedCell(model.isPlayAndDelete());
				int listSize = currList.size();
				int indexToMoveTo = mainTable.getSelectedRow();
				
				if(curr.isPresent()) {
					model.getVideoKeeper().open(curr, model.isPlayAndDelete());
					
					if (model.isPlayAndDelete()) {
						populateList(Optional.of(new VideoList(currList)), true);
					}
					
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
				Optional<VideoDataNode> curr = getCorrespondingNodeFromSelectedCell(false);
				
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
								
								populateList(model.getVideoList(), false);
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
								
								populateList(model.getVideoList(), false);
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
								
								populateList(model.getVideoList(), false);
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
						populateList(searchResultsOnDisplay.isPresent() ? searchResultsOnDisplay : model.getVideoList(), true);
						model.setRequestSaveButtonEn(true);
						refreshing = false;
						displayingSearchResults = false;
						
						mainTable.setRowSelectionInterval(indexToMoveTo, indexToMoveTo);
					}
				}).start();
			}
		});
		
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				VideoList currList = searchResultsOnDisplay.isPresent() ? searchResultsOnDisplay.get() : model.getVideoList().get();
				int indexToMoveTo = mainTable.getSelectedRow();
				int listSize = currList.size();
				
				if (searchResultsOnDisplay.isPresent()) {
					model.getVideoList().get().pop(getIndexOfNodeFromMainList(getCorrespondingNodeFromSelectedCell(true)));
				} else {
					currList.pop(getCorrespondingIndex());
				}
				
				populateList(Optional.of(new VideoList(currList)), true);
				model.setRequestSaveButtonEn(true);
				
				if (listSize > 0) {
					if (indexToMoveTo >= listSize - 1) {
						indexToMoveTo--;
					}
					
					mainTable.setRowSelectionInterval(indexToMoveTo, indexToMoveTo);
				}
			}
		});
		
		moveToIndexButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Optional<VideoList> videoList = model.getVideoList();
				
				if (videoList.isPresent() && videoList.get().size() > 0) {
					Optional<VideoDataNode> toMove = videoList.get().pop(getCorrespondingIndex());
					String indexStr = JOptionPane.showInputDialog(thisDialog, "Move to:", MainGui.PROG_NAME + " -- Move to Index", JOptionPane.QUESTION_MESSAGE);
					int index = -1;
					
					while (index == -1 && indexStr != null) {
						try {
							index = Integer.parseInt(indexStr);
						} catch (Exception f) {
							index = -1;
						}
						
						//remember: videoList doesn't contain the video at the head, so need to add 1
						if (index < 1 || index > videoList.get().size() + 1) {
							index = -1;
							indexStr = JOptionPane.showInputDialog(thisDialog, "Enter a valid index:", MainGui.PROG_NAME + " -- Move to Index", JOptionPane.WARNING_MESSAGE);
						}
					}
					
					//compensate for user entry being off by one
					index--;

					//indexStr != null is here in case user selected cancel in input dialog
					if (toMove.isPresent() && indexStr != null) {
						videoList.get().insert(index, toMove.get());
						populateList(model.getVideoList(), false);
					}
					
					model.setRequestSaveButtonEn(true);
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
								
								populateList(model.getVideoList(), false);
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
					Optional<VideoDataNode> curr = getCorrespondingNodeFromSelectedCell(false); 
					
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
		searchResultsOnDisplay = Optional.of(new VideoList());
		
		if (model.getVideoList().isPresent() && query.length() > 0) {
			Optional<VideoList> videoListOpt = model.getVideoList();
			VideoList videoList = null;

			if (videoListOpt.isPresent()) {
				int count = 0;
				int results = 0;
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
							results++;
						}
					}
				}
				
				this.setTitle(DIALOG_TITLE + " [" + results + " Results]");
			}
		}
	}
	
	private void clearList() {
		while (((DefaultTableModel) mainTable.getModel()).getRowCount() > 0) {
			((DefaultTableModel) mainTable.getModel()).removeRow(0);
		}
	}
	
	private void populateList(Optional<VideoList> providedList, boolean updateSearchResultsOnDisplay) {
		VideoList videoList;
		
		clearList();
		
		if (updateSearchResultsOnDisplay && searchResultsOnDisplay.isPresent()) {
			searchResultsOnDisplay.get().clear();
		}
		
		if (providedList.isPresent()) {
			//make a copy
			videoList = new VideoList(providedList.get());
			
			
			//list should be in order from 0 to n, not from index to 0.
			videoList.resetIndex();

			while (videoList.size() > 0) {
				Optional<VideoDataNode> curr = videoList.popCurr();

				if (curr.isPresent()) {
					if (updateSearchResultsOnDisplay && searchResultsOnDisplay.isPresent()) {
						addToListNonContiguous(curr.get(), getIndexOfNodeFromMainList(curr) + 1);
					} else {
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
		//searchResultsOnDisplay should always be present when this method is called. If it isn't, that needs to be addressed in the caller.
		searchResultsOnDisplay.get().append(node);
		
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
	
	private Optional<VideoDataNode> getCorrespondingNodeFromSelectedCell(boolean popNodeFromSearchResults) {
		Optional<VideoDataNode> node = Optional.empty();
		
		if (mainTable.getSelectedRow() >= 0) {
			Object valueAt = ((DefaultTableModel) mainTable.getModel()).getValueAt(mainTable.getSelectedRow(), 0);
			
			if (model.getVideoList().isPresent()) {
				node = model.getVideoList().get().peek(Integer.parseInt(((String) valueAt).trim()) - 1);
			}
		}
		
		if (popNodeFromSearchResults && searchResultsOnDisplay.isPresent()) {
			for (int i = 0; i < searchResultsOnDisplay.get().size(); i++) {
				if (searchResultsOnDisplay.get().peek(i).get().equals(node.get())) {
					searchResultsOnDisplay.get().pop(i);
					break;
				}
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
	
	private int getIndexOfNodeFromMainList(Optional<VideoDataNode> node) {
		int index = -1;
		
		if (node.isPresent()) {
			for (int i = 0; i < model.getVideoList().get().size(); i++) {
				if (model.getVideoList().get().peek(i).equals(node)) {
					index = i;
					break;
				}
			}
		} /*else {
			for (int i = 0; i < model.getVideoList().get().size(); i++) {
				if (model.getVideoList().get().peek(i).equals(getCorrespondingNodeFromSelectedCell(false))) {
					index = i;
					break;
				}
			}
		}*/
		
		return index;
	}
	
	private void setOptionsLocked(boolean locked, boolean displayingSearchRes) {
		playButton.setEnabled(!locked);
		copyUrlButton.setEnabled(!locked);
		editButton.setEnabled(!locked);
		deleteButton.setEnabled(!locked);
		refreshButton.setEnabled(!locked);
		
		if (displayingSearchRes) {
			moveUpButton.setEnabled(false);
			moveDownButton.setEnabled(false);
			moveToIndexButton.setEnabled(false);
			moveToHeadButton.setEnabled(false);
			moveToTailButton.setEnabled(false);
		} else {
			moveUpButton.setEnabled(!locked);
			moveDownButton.setEnabled(!locked);
			moveToIndexButton.setEnabled(!locked);
			moveToHeadButton.setEnabled(!locked);
			moveToTailButton.setEnabled(!locked);
		}
		
		playButton.setBackground(playButton.isEnabled() ? MainGui.PROG_COLOR_BTN_EN : MainGui.PROG_COLOR_BTN_DIS);
		copyUrlButton.setBackground(copyUrlButton.isEnabled() ? MainGui.PROG_COLOR_BTN_EN : MainGui.PROG_COLOR_BTN_DIS);
		editButton.setBackground(editButton.isEnabled() ? MainGui.PROG_COLOR_BTN_EN : MainGui.PROG_COLOR_BTN_DIS);
		deleteButton.setBackground(deleteButton.isEnabled() ? MainGui.PROG_COLOR_BTN_EN : MainGui.PROG_COLOR_BTN_DIS);
		refreshButton.setBackground(refreshButton.isEnabled() ? MainGui.PROG_COLOR_BTN_EN : MainGui.PROG_COLOR_BTN_DIS);
		moveUpButton.setBackground(moveUpButton.isEnabled() ? MainGui.PROG_COLOR_BTN_EN : MainGui.PROG_COLOR_BTN_DIS);
		moveDownButton.setBackground(moveDownButton.isEnabled() ? MainGui.PROG_COLOR_BTN_EN : MainGui.PROG_COLOR_BTN_DIS);
		moveToIndexButton.setBackground(moveToIndexButton.isEnabled() ? MainGui.PROG_COLOR_BTN_EN : MainGui.PROG_COLOR_BTN_DIS);
		moveToHeadButton.setBackground(moveToHeadButton.isEnabled() ? MainGui.PROG_COLOR_BTN_EN : MainGui.PROG_COLOR_BTN_DIS);
		moveToTailButton.setBackground(moveToTailButton.isEnabled() ? MainGui.PROG_COLOR_BTN_EN : MainGui.PROG_COLOR_BTN_DIS);
	}
	
	/*
	 * Editor must call this when a video in the list is being finished editing.
	 */
	protected void editingFinished() {
		populateList(searchResultsOnDisplay.isPresent() ? Optional.of(new VideoList(searchResultsOnDisplay.get())) : model.getVideoList(), true);
		model.setRequestSaveButtonEn(true);
	}
	
	private void monitor() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				for(;;) {
					try {
						if (mainTable.getSelectedRow() >= 0 && refreshing == false) {
							setOptionsLocked(false, displayingSearchResults);
						} else {
							setOptionsLocked(true, displayingSearchResults);
						}
						
						Thread.sleep(30);
					} catch (InterruptedException e) {}
				}
			}
		}).start();
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (searchResultsOnDisplay.isEmpty()) {
			populateList(model.getVideoList(), false);
		} else {
			populateList(searchResultsOnDisplay, false);
		}
		
		super.setVisible(visible);
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {
		((DefaultTableModel) mainTable.getModel()).setRowCount(0);
		populateList(model.getVideoList(), false);
		this.setVisible(false);
		this.editor.setVisible(false);
		this.editor.dispose();
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
		populateList(model.getVideoList(), false);
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


    

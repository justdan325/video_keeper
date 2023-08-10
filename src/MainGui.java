import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.WindowListener;
import java.io.File;
import java.awt.event.WindowEvent;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;

@SuppressWarnings("serial")
public class MainGui extends JFrame implements WindowListener {
	public  static final String PROG_NAME 			= "Video Keeper";
	public  static final String PROG_VER			= "3.0a2";
	public  static final String PROG_FONT			= "Arial";
	public	static final Color 	PROG_COLOR_BKRND	= Main.OS_MAC ? Color.LIGHT_GRAY : new Color(3156004);
	public	static final Color	PROG_COLOR_BTN_EN	= Main.OS_MAC ? Color.WHITE : new Color(8388608);
	public	static final Color	PROG_COLOR_BTN_DIS	= Main.OS_MAC ? Color.LIGHT_GRAY : new Color(4013114);
	public	static final Color	PROG_COLOR_TXT_LT	= Main.OS_MAC ? Color.BLACK : Color.WHITE;
	public	static final Color	PROG_COLOR_TXT_DRK	= Color.BLACK;
	
	private static final String PASTE_MESS 			= "<Paste Video Link Here>";
	private static final String NEXT_BUTTON_TXT 	= "Play Next";
	private static final String PREV_BUTTON_TXT 	= "Play Prev.";
//	private static final String FWRD_BUTTON_TXT 	= ">";
	private static final String FWRD_BUTTON_TXT 	= "➡️";
//	private static final String BKWRD_BUTTON_TXT 	= "<";
	private static final String BKWRD_BUTTON_TXT 	= "⬅️";
	private static final String HEAD_BUTTON_TXT 	= "Head";
	private static final String SETT_BUTTON_TXT 	= "*";
	private static final String ADD_BUTTON_TXT 		= "Add";
	private static final String ADD_LABEL_TXT 		= "Add Video Links to Watch List.";
	private static final String PASTE_BUTTON_TXT 	= "Paste";
//	private static final String SAVE_BUTTON_TXT 	= "Save";
	private static final String SAVE_BUTTON_TXT 	= "💾";
//	private static final String REFR_BUTTON_TXT 	= "Refresh";
	private static final String REFR_BUTTON_TXT 	= "🔄";
//	private static final String DEL_BUTTON_TXT 		= "Delete";
	private static final String DEL_BUTTON_TXT 		= "🗑️";
//	private static final String SEARCH_BUTTON_TXT 	= "Search";
	private static final String SEARCH_BUTTON_TXT 	= "🔎";
	private static final String UP_NEXT_TXT			= " -- Up Next -- ";
	private static final String EMPTY_QUEUE_TXT		= "~ No Video Links in Watch List ~";
	private static final String TOOLTIP_PASTE		= "Paste a video link from the clip board.";
	private static final String TOOLTIP_ADD			= "Add pasted video link to watch list.";
	private static final String TOOLTIP_HEAD		= "Return to head of the watch list.";
	private static final String TOOLTIP_SETTINGS	= "Settings";
	private static final String TOOLTIP_REFRESH		= "Refresh video metadata for the next video.";
	private static final String TOOLTIP_DELETE		= "Delete current video from the list.";
	private static final String TOOLTIP_SEARCH		= "Search through the list of videos.";
	private static final String TOOLTIP_SAVE		= "Save changes to the watch list.";
	private static final String CHANNEL_PREFIX 		= "By: ";
	private static final int 	WIN_X 				= 600;
	private static final int 	WIN_Y 				= Main.OS_MAC ? 425 : 400;
	private static final int 	URL_FIELD_X			= 465;
	private static final int 	URL_FIELD_Y			= 40;
	private static final int 	UP_NEXT_FONT_SIZE 	= 20;
	private static final int 	VID_DATA_FONT_SIZE 	= 14;
	
	private DataModel model;
	private VideoKeeper keeper;
	private SettingsDialog settings;
	private SearchDialog searchDialog;
	private MainGui mainGui;
	private JButton nextButton;
	private JButton prevButton;
	private JButton addButton;
	private JButton pasteButton;
	private JButton fwrdButton;
	private JButton bkwrdButton;
	private JButton headButton;
	private JButton settButton;
	private JButton saveButton;
	private JButton refreshButton;
	private JButton deleteButton;
	private JButton searchButton;
	private JLabel upNextLabel;
	private JLabel counterLabel;
	private JLabel titleLabel;
	private JLabel dateAndTimeLabel;
	private JLabel channelLabel;
	private JTextField urlField;
	private JPanel mainPanel;
	private int count;
	private int index;
	private boolean locked;
	private boolean notAtHead;
	private boolean refreshing;
	
	public MainGui(DataModel model) {
		this.model = model;
		this.keeper = new VideoKeeper(model, this);
		this.mainGui = this;
		this.settings = new SettingsDialog(this, model);
		this.searchDialog = new SearchDialog(model, this);
		this.nextButton = new JButton(NEXT_BUTTON_TXT);
		this.prevButton = new JButton(PREV_BUTTON_TXT);
		this.addButton = new JButton(ADD_BUTTON_TXT);
		this.fwrdButton = new JButton(FWRD_BUTTON_TXT);
		this.bkwrdButton = new JButton(BKWRD_BUTTON_TXT);
		this.headButton = new JButton(HEAD_BUTTON_TXT);
		this.settButton = new JButton(SETT_BUTTON_TXT);
		this.pasteButton = new JButton(PASTE_BUTTON_TXT);
		this.saveButton = new JButton(SAVE_BUTTON_TXT);
		this.refreshButton = new JButton(REFR_BUTTON_TXT);
		this.deleteButton = new JButton(DEL_BUTTON_TXT);
		this.searchButton = new JButton(SEARCH_BUTTON_TXT);
		this.upNextLabel = new JLabel();
		this.counterLabel = new JLabel("0");
		this.titleLabel = new JLabel();
		this.dateAndTimeLabel = new JLabel();
		this.channelLabel = new JLabel();
		this.urlField = new JTextField(PASTE_MESS);
		this.mainPanel = new JPanel(new BorderLayout());
		this.count = 0;
		this.index = 0;
		this.locked = false;
		this.notAtHead = false;
		
		mainPanel.add(makeNorthPanel(), BorderLayout.NORTH);
		mainPanel.add(makeCenterPanel(), BorderLayout.CENTER);
		mainPanel.add(makeSouthPanel(), BorderLayout.SOUTH);
		
		addListeners();
		initKeeper();
		
		this.getContentPane().setBackground(PROG_COLOR_BKRND);
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		this.setTitle(PROG_NAME + " -- v" + PROG_VER);
		this.setSize(new Dimension(WIN_X, WIN_Y));
		this.add(mainPanel);
		this.addWindowListener(this);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	private JPanel makeNorthPanel() {
		JPanel north = new JPanel(new GridLayout(5, 1));
		JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		north.setBackground(new Color(8617596));
		
		upNextLabel.setFont(new Font(PROG_FONT, Font.BOLD, UP_NEXT_FONT_SIZE));
		upNextLabel.setText(UP_NEXT_TXT);
		upNextLabel.setHorizontalAlignment(JLabel.CENTER);
		upNextLabel.setForeground(PROG_COLOR_TXT_DRK);
		titleLabel.setFont(new Font(PROG_FONT, Font.TYPE1_FONT, VID_DATA_FONT_SIZE));
		titleLabel.setText("Title: Test");
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setForeground(PROG_COLOR_TXT_DRK);
		dateAndTimeLabel.setFont(new Font(PROG_FONT, Font.ITALIC, VID_DATA_FONT_SIZE));
		dateAndTimeLabel.setText("Date: Test");
		dateAndTimeLabel.setHorizontalAlignment(JLabel.CENTER);
		dateAndTimeLabel.setForeground(PROG_COLOR_TXT_DRK);
		channelLabel.setFont(new Font(PROG_FONT, Font.TYPE1_FONT, VID_DATA_FONT_SIZE));
		channelLabel.setText("By: Test");
		channelLabel.setHorizontalAlignment(JLabel.CENTER);
		channelLabel.setForeground(PROG_COLOR_TXT_DRK);
		refreshButton.setToolTipText(TOOLTIP_REFRESH);
		deleteButton.setToolTipText(TOOLTIP_DELETE);
		searchButton.setToolTipText(TOOLTIP_SEARCH);
		saveButton.setToolTipText(TOOLTIP_SAVE);
		settButton.setToolTipText(TOOLTIP_SETTINGS);
		settButton.setBackground(PROG_COLOR_BTN_EN);
		
		saveEnabled(false);
		
		topPanel.add(settButton);
		topPanel.add(saveButton);
		topPanel.add(refreshButton);
		topPanel.add(deleteButton);
		topPanel.add(searchButton);
		
		north.add(topPanel);
		north.add(upNextLabel);
		north.add(titleLabel);
		north.add(dateAndTimeLabel);
		north.add(channelLabel);
		
		return north;
	}
	
	private JPanel makeCenterPanel() {
		GridLayout layout = new GridLayout(2, 1);
		FlowLayout urlPanelLayout = new FlowLayout();
		
		layout.setVgap(5);
		urlPanelLayout.setHgap(10);
		
		JPanel center = new JPanel(new BorderLayout());
		JPanel urlPanel = new JPanel(urlPanelLayout);
		JPanel buttonPanel = new JPanel(layout);
		JLabel addLabel = new JLabel(ADD_LABEL_TXT);
		
		center.setBackground(PROG_COLOR_BKRND);
		urlPanel.setBackground(PROG_COLOR_BKRND);
		buttonPanel.setBackground(PROG_COLOR_BKRND);
		addLabel.setBackground(PROG_COLOR_BKRND);
		urlField.setPreferredSize(new Dimension(URL_FIELD_X, URL_FIELD_Y));
		addLabel.setFont(new Font(PROG_FONT, Font.PLAIN, VID_DATA_FONT_SIZE-2));
		addLabel.setHorizontalAlignment(JLabel.CENTER);
		addLabel.setForeground(PROG_COLOR_TXT_LT);
		pasteButton.setBackground(PROG_COLOR_BTN_EN);
		addButton.setBackground(PROG_COLOR_BTN_DIS);
		pasteButton.setToolTipText(TOOLTIP_PASTE);
		addButton.setToolTipText(TOOLTIP_ADD);
		
		buttonPanel.add(pasteButton);
		buttonPanel.add(addButton);
		
		urlPanel.add(urlField);
		urlPanel.add(buttonPanel);
		
		center.add(addLabel, BorderLayout.NORTH);
		center.add(urlPanel, BorderLayout.CENTER);
		
		addButton.setEnabled(false);
		
		return center;
	}
	
	private JPanel makeSouthPanel() {
		FlowLayout mainLayout = new FlowLayout(SwingConstants.LEADING);
		FlowLayout toWatchLayout = new FlowLayout();
		FlowLayout subLayout = new FlowLayout(SwingConstants.LEADING);
		final int BUTTON_LENGTH = 104;
		final int BUTTON_WIDTH = 25;
		
		mainLayout.setHgap(15);
		mainLayout.setVgap(40);
		subLayout.setHgap(15);
		
		JPanel south = new JPanel(mainLayout);
		JPanel buttonPanel = new JPanel(subLayout);
		JPanel toWatchPanel = new JPanel(toWatchLayout);
		JLabel toWatchLabel = new JLabel();
		
		south.setBackground(PROG_COLOR_BKRND);
		buttonPanel.setBackground(PROG_COLOR_BKRND);
		toWatchPanel.setBackground(PROG_COLOR_BKRND);
		toWatchLabel.setBackground(PROG_COLOR_BKRND);
		toWatchLabel.setForeground(PROG_COLOR_TXT_LT);
		counterLabel.setForeground(PROG_COLOR_TXT_LT);
		nextButton.setBackground(PROG_COLOR_BTN_EN);
		nextButton.setPreferredSize(new Dimension(BUTTON_LENGTH, BUTTON_WIDTH));
		fwrdButton.setBackground(PROG_COLOR_BTN_EN);
		fwrdButton.setPreferredSize(new Dimension(BUTTON_LENGTH/2 - 7, BUTTON_WIDTH));
		bkwrdButton.setBackground(PROG_COLOR_BTN_EN);
		bkwrdButton.setPreferredSize(new Dimension(BUTTON_LENGTH/2 - 7, BUTTON_WIDTH));
		headButton.setBackground(PROG_COLOR_BTN_EN);
		headButton.setPreferredSize(new Dimension(BUTTON_LENGTH/2 + 20, BUTTON_WIDTH));
		headButton.setToolTipText(TOOLTIP_HEAD);
		prevButton.setBackground(PROG_COLOR_BTN_EN);
		prevButton.setPreferredSize(new Dimension(BUTTON_LENGTH, BUTTON_WIDTH));
		
		buttonPanel.add(nextButton);
		buttonPanel.add(prevButton);
		buttonPanel.add(bkwrdButton);
		buttonPanel.add(headButton);
		buttonPanel.add(fwrdButton);
		toWatchPanel.add(toWatchLabel);
		toWatchPanel.add(counterLabel);
		
		south.add(buttonPanel);
		south.add(toWatchPanel);
		
		return south;
	}
	
	private void addListeners() {
		nextButton.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keeper.openCurr();
			}
		}));
		
		prevButton.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keeper.openPrev();
			}
		}));
		
		fwrdButton.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				notAtHead = !keeper.skipToNext();
			}
		}));
		
		bkwrdButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				notAtHead = !keeper.goBackToPrev();
			}
		});
		
		headButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				keeper.head();
				notAtHead = false;
			}
		});
		
		settButton.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settings.showDialog();
			}
		}));
		
		addButton.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				add();
			}
		}));
		
		pasteButton.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
				
				if (contents != null) {
					try {
						urlField.setText((String) contents.getTransferData(DataFlavor.stringFlavor));
					} catch (Exception f) {
						f.printStackTrace();
					}
				}
			}
		}));
		
		urlField.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				add();
			}
		}));
		
		urlField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent focusEvent) {
				urlFieldFocusGained(focusEvent);
			}
		});
		
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String mess = "Watch list could not be saved to database file.";
				boolean saved = save();
				
				if(saved) {
					saveEnabled(false);
				} else {
					JOptionPane.showMessageDialog(mainGui, mess, MainGui.PROG_NAME + " -- Save Failure", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						refreshing = true;
						refreshButton.setEnabled(false);
						refreshButton.setBackground(PROG_COLOR_BTN_DIS);
						keeper.refreshCurr(false);
						refreshButton.setBackground(PROG_COLOR_BTN_EN);
						refreshButton.setEnabled(true);
						refreshing = false;			
						saveButton.setEnabled(true);
						saveButton.setBackground(PROG_COLOR_BTN_EN);
					}
				}).start();
			}
		});
		
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				keeper.deleteCurr();
			}
		});
		
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				searchDialog.setVisible(true);
			}
		});
	}
	
	private void add() {
		String toAdd = urlField.getText();

		if (toAdd != null && !toAdd.equals(PASTE_MESS)) {
			keeper.add(new VideoDataNode(toAdd));
			urlField.selectAll();
			urlField.setText(PASTE_MESS);
			urlField.selectAll();
		} else {
			urlField.selectAll();
			urlField.setText(PASTE_MESS);
			urlField.selectAll();
		}
	}
	
	private void initKeeper() {
		//init main prog thread
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				model.setVideoKeeper(keeper);
				
				for(;;) {
					while(locked) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
					}
					
					if(model.isRequestSaveButtonEn()) {
						saveButton.setEnabled(true);
						saveButton.setBackground(PROG_COLOR_BTN_EN);
						model.setRequestSaveButtonEn(false);
					}
					
					File dbFile = new File(model.getDatabaseFile());
					
					if (dbFile.exists()) {
						String name = dbFile.getName();
						
						if(name.length() > 20) {
							name = name.substring(0, 20) + "...";
						}
						
						setTitle(PROG_NAME + " -- v" + PROG_VER + "  [" + name + "]");
					} else {
						setTitle(PROG_NAME + " -- v" + PROG_VER);
					}
					
					count = keeper.getSize();
					index = keeper.getCurrIndex() + 1;
					notAtHead = index > 1 ? true : false;
					counterLabel.setText(index + " / " + count);
					
					if(urlField.getText().trim().equals(PASTE_MESS) == false && urlField.getText().trim().length() > 0) {
						addButton.setEnabled(true);
						addButton.setBackground(PROG_COLOR_BTN_EN);
					} else {
						addButton.setEnabled(false);
						addButton.setBackground(PROG_COLOR_BTN_DIS);
					}

					if(count > 0) {
						nextButton.setEnabled(true);
						nextButton.setBackground(PROG_COLOR_BTN_EN);
						upNextLabel.setText(UP_NEXT_TXT);
						titleLabel.setText(keeper.getCurrTitle(true));
						titleLabel.setToolTipText(keeper.getCurrTitle(false));
						dateAndTimeLabel.setText(keeper.getCurrDateAndTime());
						deleteButton.setEnabled(true);
						deleteButton.setBackground(PROG_COLOR_BTN_EN);
						searchButton.setEnabled(true);
						searchButton.setBackground(PROG_COLOR_BTN_EN);
						
						if (refreshing == false) {
							refreshButton.setEnabled(true);
							refreshButton.setBackground(PROG_COLOR_BTN_EN);
						}

						if(keeper.getNextChannel().length() > 0) {
							channelLabel.setText(CHANNEL_PREFIX + keeper.getNextChannel());
						} else {
							channelLabel.setText(" ");
						}
					} else {
						nextButton.setEnabled(false);
						nextButton.setBackground(PROG_COLOR_BTN_DIS);
						upNextLabel.setText(EMPTY_QUEUE_TXT);
						titleLabel.setText(" ");
						titleLabel.setToolTipText("");
						dateAndTimeLabel.setText(" ");
						channelLabel.setText(" ");
						deleteButton.setEnabled(false);
						deleteButton.setBackground(PROG_COLOR_BTN_DIS);
						searchButton.setEnabled(false);
						searchButton.setBackground(PROG_COLOR_BTN_DIS);
						
						if (refreshing == false) {
							refreshButton.setEnabled(false);
							refreshButton.setBackground(PROG_COLOR_BTN_DIS);
						}
					}
					
					if(count > 1) {
						fwrdButton.setEnabled(true);
						fwrdButton.setBackground(PROG_COLOR_BTN_EN);
						bkwrdButton.setEnabled(true);
						bkwrdButton.setBackground(PROG_COLOR_BTN_EN);
						
						if(notAtHead) {
							headButton.setEnabled(true);
							headButton.setBackground(PROG_COLOR_BTN_EN);
						} else {
							headButton.setEnabled(false);
							headButton.setBackground(PROG_COLOR_BTN_DIS);
						}
					} else {
						fwrdButton.setEnabled(false);
						fwrdButton.setBackground(PROG_COLOR_BTN_DIS);
						bkwrdButton.setEnabled(false);
						bkwrdButton.setBackground(PROG_COLOR_BTN_DIS);
						headButton.setEnabled(false);
						headButton.setBackground(PROG_COLOR_BTN_DIS);
					}

					if(keeper.getPrev() == null) {
						prevButton.setEnabled(false);
						prevButton.setBackground(PROG_COLOR_BTN_DIS);
					} else {
						prevButton.setEnabled(true);
						prevButton.setBackground(PROG_COLOR_BTN_EN);
						
						if(keeper.getPrevTitle().length() > 0) {
							prevButton.setToolTipText("Prev. Video: " + keeper.getPrevTitle());
						} else {
							prevButton.setToolTipText("");
						}
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
	
	private void urlFieldFocusGained(final FocusEvent focusEvent) {
        urlField.selectAll();
    }
	
	public boolean save() {
		boolean saved = false;
		
		setLocked(true);
		saved = keeper.save();
		setLocked(false);
		
		return saved;
	}
	
	public boolean export(String destination) {
		boolean success = false;
		
		setLocked(true);
		success = keeper.exportUrls(destination);
		setLocked(false);
		
		return success;
	}
	
	public void refresh() {
		setLocked(true);
		keeper.refreshAll();
		setLocked(false);
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
		this.nextButton.setEnabled(!locked);
		this.nextButton.setBackground(locked ? PROG_COLOR_BTN_DIS : PROG_COLOR_BTN_EN);
		this.prevButton.setEnabled(!locked);
		this.prevButton.setBackground(locked ? PROG_COLOR_BTN_DIS : PROG_COLOR_BTN_EN);
		this.addButton.setEnabled(!locked);
		this.addButton.setBackground(locked ? PROG_COLOR_BTN_DIS : PROG_COLOR_BTN_EN);
		this.fwrdButton.setEnabled(!locked);
		this.fwrdButton.setBackground(locked ? PROG_COLOR_BTN_DIS : PROG_COLOR_BTN_EN);
		this.settButton.setEnabled(!locked);
		this.settButton.setBackground(locked ? PROG_COLOR_BTN_DIS : PROG_COLOR_BTN_EN);
		this.pasteButton.setEnabled(!locked);
		this.pasteButton.setBackground(locked ? PROG_COLOR_BTN_DIS : PROG_COLOR_BTN_EN);
		this.urlField.setEnabled(!locked);
	}
	
	protected JDialog getSettingsDialog() {
		return settings;
	}
	
	public int getCount() {
		return count;
	}
	
	public void saveEnabled(boolean enable) {
		if(enable) {
			saveButton.setEnabled(true);
			saveButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
		} else {
			saveButton.setEnabled(false);
			saveButton.setBackground(MainGui.PROG_COLOR_BTN_DIS);
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {}
	
	@Override
	public void windowClosed(WindowEvent e) {}
	
	@Override
	public void windowClosing(WindowEvent e) {
		if (model.isAutoSaveOnExit()) {
			save();
			System.exit(0);
		} else {
			String mess = "Would you like to save the watch list?";
			int option = JOptionPane.showOptionDialog(this, mess, PROG_NAME + " -- Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			
			if(option == JOptionPane.YES_OPTION) {
				save();
				System.exit(0);
			} else if(option == JOptionPane.NO_OPTION) {
				System.exit(0);
			}
		}
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}
}
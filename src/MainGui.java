import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.FocusAdapter;
import java.awt.event.WindowListener;
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
	public  static final String PROG_VER			= "2.0";
	public  static final String PROG_FONT			= "Arial";
	public	static final Color 	PROG_COLOR_BKRND	= new Color(3156004);
	public	static final Color	PROG_COLOR_BTN_EN	= new Color(8388608);
	public	static final Color	PROG_COLOR_BTN_DIS	= new Color(4013114);
	public	static final Color	PROG_COLOR_TXT_LT	= Color.WHITE;
	public	static final Color	PROG_COLOR_TXT_DRK	= Color.BLACK;
	
	private static final String PASTE_MESS 			= "<Paste Video Link Here>";
	private static final String NEXT_BUTTON_TXT 	= "Play Next";
	private static final String PREV_BUTTON_TXT 	= "Play Prev.";
	private static final String SKIP_BUTTON_TXT 	= "Skip";
	private static final String HEAD_BUTTON_TXT 	= "Head";
	private static final String SETT_BUTTON_TXT 	= "*";
	private static final String ADD_BUTTON_TXT 		= "Add";
	private static final String ADD_LABEL_TXT 		= "Add Video Links to Watch List.";
	private static final String PASTE_BUTTON_TXT 	= "Paste";
	private static final String TO_WATCH_TXT 		= "Videos:";
	private static final String UP_NEXT_TXT			= " -- Up Next -- ";
	private static final String EMPTY_QUEUE_TXT		= "~ No Video Links in Watch List ~";
	private static final String TOOLTIP_PASTE		= "Paste a video link from the clip board.";
	private static final String TOOLTIP_ADD			= "Add pasted video link to watch list.";
	private static final String TOOLTIP_HEAD		= "Return to head of the watch list.";
	private static final String TOOLTIP_SETTINGS	= "Settings";
	private static final String CHANNEL_PREFIX 		= "By: ";
	private static final int 	WIN_X 				= 600;
	private static final int 	WIN_Y 				= 350;
	private static final int 	URL_FIELD_X			= 450;
	private static final int 	URL_FIELD_Y			= 40;
	private static final int 	UP_NEXT_FONT_SIZE 	= 20;
	private static final int 	VID_DATA_FONT_SIZE 	= 14;
	
	private DataModel model;
	private VideoKeeper keeper;
	private SettingsDialog settings;
	private JButton nextButton;
	private JButton prevButton;
	private JButton addButton;
	private JButton pasteButton;
	private JButton skipButton;
	private JButton headButton;
	private JButton settButton;
	private JLabel upNextLabel;
	private JLabel counterLabel;
	private JLabel titleLabel;
	private JLabel dateLabel;
	private JLabel channelLabel;
	private JTextField urlField;
	private JPanel mainPanel;
	private int count;
	private boolean locked;
	private boolean skipped;
	
	public MainGui(DataModel model) {
		this.model = model;
		this.keeper = new VideoKeeper(model, this);
		this.settings = new SettingsDialog(this, model);
		this.nextButton = new JButton(NEXT_BUTTON_TXT);
		this.prevButton = new JButton(PREV_BUTTON_TXT);
		this.addButton = new JButton(ADD_BUTTON_TXT);
		this.skipButton = new JButton(SKIP_BUTTON_TXT);
		this.headButton = new JButton(HEAD_BUTTON_TXT);
		this.settButton = new JButton(SETT_BUTTON_TXT);
		this.pasteButton = new JButton(PASTE_BUTTON_TXT);
		this.upNextLabel = new JLabel();
		this.counterLabel = new JLabel("0");
		this.titleLabel = new JLabel();
		this.dateLabel = new JLabel();
		this.channelLabel = new JLabel();
		this.urlField = new JTextField(PASTE_MESS);
		this.mainPanel = new JPanel(new BorderLayout());
		this.count = 0;
		this.locked = false;
		this.skipped = false;
		
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
		
		north.setBackground(new Color(8617596));
		
		upNextLabel.setFont(new Font(PROG_FONT, Font.BOLD, UP_NEXT_FONT_SIZE));
		upNextLabel.setText(UP_NEXT_TXT);
		upNextLabel.setHorizontalAlignment(JLabel.CENTER);
		upNextLabel.setForeground(PROG_COLOR_TXT_DRK);
		titleLabel.setFont(new Font(PROG_FONT, Font.TYPE1_FONT, VID_DATA_FONT_SIZE));
		titleLabel.setText("Title: Test");
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setForeground(PROG_COLOR_TXT_DRK);
		dateLabel.setFont(new Font(PROG_FONT, Font.ITALIC, VID_DATA_FONT_SIZE));
		dateLabel.setText("Date: Test");
		dateLabel.setHorizontalAlignment(JLabel.CENTER);
		dateLabel.setForeground(PROG_COLOR_TXT_DRK);
		channelLabel.setFont(new Font(PROG_FONT, Font.TYPE1_FONT, VID_DATA_FONT_SIZE));
		channelLabel.setText("By: Test");
		channelLabel.setHorizontalAlignment(JLabel.CENTER);
		channelLabel.setForeground(PROG_COLOR_TXT_DRK);
		
		north.add(upNextLabel);
		north.add(titleLabel);
		north.add(dateLabel);
		north.add(channelLabel);
		
		return north;
	}
	
	private JPanel makeCenterPanel() {
		GridLayout layout = new GridLayout(2, 1);
		
		layout.setVgap(5);
		
		JPanel center = new JPanel(new BorderLayout());
		JPanel urlPanel = new JPanel(new FlowLayout());
		JPanel buttonPanel = new JPanel(layout);
		JLabel addLabel = new JLabel(ADD_LABEL_TXT);
		
		center.setBackground(PROG_COLOR_BKRND);
		urlPanel.setBackground(PROG_COLOR_BKRND);
		buttonPanel.setBackground(PROG_COLOR_BKRND);
		addLabel.setBackground(PROG_COLOR_BKRND);
		urlField.setPreferredSize(new Dimension(URL_FIELD_X, URL_FIELD_Y));
		settButton.setPreferredSize(new Dimension(URL_FIELD_Y, URL_FIELD_Y));
		settButton.setToolTipText(TOOLTIP_SETTINGS);
		settButton.setBackground(PROG_COLOR_BTN_EN);
		addLabel.setFont(new Font(PROG_FONT, Font.PLAIN, VID_DATA_FONT_SIZE-2));
		addLabel.setHorizontalAlignment(JLabel.CENTER);
		addLabel.setForeground(PROG_COLOR_TXT_LT);
		pasteButton.setBackground(PROG_COLOR_BTN_EN);
		addButton.setBackground(PROG_COLOR_BTN_EN);
		pasteButton.setToolTipText(TOOLTIP_PASTE);
		addButton.setToolTipText(TOOLTIP_ADD);
		
		buttonPanel.add(pasteButton);
		buttonPanel.add(addButton);
		
		urlPanel.add(settButton);
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
		
		mainLayout.setHgap(25);
		mainLayout.setVgap(40);
		subLayout.setHgap(15);
		
		JPanel south = new JPanel(mainLayout);
		JPanel buttonPanel = new JPanel(subLayout);
		JPanel toWatchPanel = new JPanel(toWatchLayout);
		JLabel toWatchLabel = new JLabel(TO_WATCH_TXT);
		
		south.setBackground(PROG_COLOR_BKRND);
		buttonPanel.setBackground(PROG_COLOR_BKRND);
		toWatchPanel.setBackground(PROG_COLOR_BKRND);
		toWatchLabel.setBackground(PROG_COLOR_BKRND);
		toWatchLabel.setForeground(PROG_COLOR_TXT_LT);
		counterLabel.setForeground(PROG_COLOR_TXT_LT);
		nextButton.setBackground(PROG_COLOR_BTN_EN);
		nextButton.setPreferredSize(new Dimension(BUTTON_LENGTH, BUTTON_WIDTH));
		skipButton.setBackground(PROG_COLOR_BTN_EN);
		skipButton.setPreferredSize(new Dimension(BUTTON_LENGTH/2+20, BUTTON_WIDTH));
		headButton.setBackground(PROG_COLOR_BTN_EN);
		headButton.setPreferredSize(new Dimension(BUTTON_LENGTH/2+20, BUTTON_WIDTH));
		headButton.setToolTipText(TOOLTIP_HEAD);
		prevButton.setBackground(PROG_COLOR_BTN_EN);
		prevButton.setPreferredSize(new Dimension(BUTTON_LENGTH, BUTTON_WIDTH));
		
		buttonPanel.add(nextButton);
		buttonPanel.add(prevButton);
		buttonPanel.add(skipButton);
		buttonPanel.add(headButton);
		toWatchPanel.add(toWatchLabel);
		toWatchPanel.add(counterLabel);
		
		south.add(buttonPanel);
		south.add(toWatchPanel);
		
		return south;
	}
	
	private void addListeners() {
		nextButton.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keeper.openNext();
			}
		}));
		
		prevButton.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keeper.openCurr();
			}
		}));
		
		skipButton.addActionListener((new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				keeper.skipNext();
				skipped = true;
			}
		}));
		
		headButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				keeper.addSkipped();
				skipped = false;
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
		
		urlField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(urlField.getText().trim().equals(PASTE_MESS) == false && urlField.getText().trim().length() > 0) {
					addButton.setEnabled(true);
				} else {
					addButton.setEnabled(false);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		
		urlField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(final FocusEvent focusEvent) {
				urlFieldFocusGained(focusEvent);
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
				for(;;) {
					while(locked) {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
						}
					}
					
					count = keeper.getSize();
					counterLabel.setText("" + count);

					if(count > 0) {
						nextButton.setEnabled(true);
						nextButton.setBackground(PROG_COLOR_BTN_EN);
						upNextLabel.setText(UP_NEXT_TXT);
						titleLabel.setText(keeper.getNextTitle(true));
						titleLabel.setToolTipText(keeper.getNextTitle(false));
						dateLabel.setText(keeper.getNextDate());

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
						dateLabel.setText(" ");
						channelLabel.setText(" ");
					}
					
					if(count > 1) {
						skipButton.setEnabled(true);
						skipButton.setBackground(PROG_COLOR_BTN_EN);
						
						if(skipped) {
							headButton.setEnabled(true);
							headButton.setBackground(PROG_COLOR_BTN_EN);
						} else {
							headButton.setEnabled(false);
							headButton.setBackground(PROG_COLOR_BTN_DIS);
						}
					} else {
						skipButton.setEnabled(false);
						skipButton.setBackground(PROG_COLOR_BTN_DIS);
						headButton.setEnabled(false);
						headButton.setBackground(PROG_COLOR_BTN_DIS);
					}

					if(keeper.getCurr() == null) {
						prevButton.setEnabled(false);
						prevButton.setBackground(PROG_COLOR_BTN_DIS);
					} else {
						prevButton.setEnabled(true);
						prevButton.setBackground(PROG_COLOR_BTN_EN);
						
						if(keeper.getCurrTitle().length() > 0) {
							prevButton.setToolTipText("Prev. Video: " + keeper.getCurrTitle());
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
		this.skipButton.setEnabled(!locked);
		this.skipButton.setBackground(locked ? PROG_COLOR_BTN_DIS : PROG_COLOR_BTN_EN);
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
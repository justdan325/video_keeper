import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.DataFlavor;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.WindowListener;
import java.awt.event.WindowEvent;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;

public class MainGui extends JFrame implements WindowListener {
	public  static final String PROG_NAME 			= "Video Keeper";
	public  static final String PROG_VER			= "2.0a6";
	
	private static final String PASTE_MESS 			= "<Paste URL Here>";
	private static final String TITLE 				= "";
	private static final String NEXT_BUTTON_TXT 	= "Play Next";
	private static final String PREV_BUTTON_TXT 	= "Play Prev.";
	private static final String SKIP_BUTTON_TXT 	= "Skip Next";
	private static final String SETT_BUTTON_TXT 	= "*";
	private static final String ADD_BUTTON_TXT 		= "Add";
	private static final String ADD_LABEL_TXT 		= "Add New Video";
	private static final String PASTE_BUTTON_TXT 	= "Paste";
	private static final String TO_WATCH_TXT 		= "Videos:";
	private static final String UP_NEXT_TXT			= " -- Up Next -- ";
	private static final String EMPTY_QUEUE_TXT		= "~ No Videos in Watch List ~";
	private static final String TOOLTIP_SETTINGS	= "Settings";
	private static final String CHANNEL_PREFIX 		= "By: ";
	public  static final String FONT				= "Helvetica";
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
	private JButton settButton;
	private JLabel UP_NEXT_LABEL;
	private JLabel counterLabel;
	private JLabel titleLabel;
	private JLabel dateLabel;
	private JLabel channelLabel;
	private JTextField urlField;
	private JPanel mainPanel;
	private int count;
	private boolean locked;
	
	public MainGui(DataModel model) {
		this.model = model;
		this.keeper = new VideoKeeper(model, this);
		this.settings = new SettingsDialog(this, model);
		this.nextButton = new JButton(NEXT_BUTTON_TXT);
		this.prevButton = new JButton(PREV_BUTTON_TXT);
		this.addButton = new JButton(ADD_BUTTON_TXT);
		this.skipButton = new JButton(SKIP_BUTTON_TXT);
		this.settButton = new JButton(SETT_BUTTON_TXT);
		this.pasteButton = new JButton(PASTE_BUTTON_TXT);
		this.UP_NEXT_LABEL = new JLabel();
		this.counterLabel = new JLabel("0");
		this.titleLabel = new JLabel();
		this.dateLabel = new JLabel();
		this.channelLabel = new JLabel();
		this.urlField = new JTextField(PASTE_MESS);
		this.mainPanel = new JPanel(new BorderLayout());
		this.count = 0;
		this.locked = false;
		
		mainPanel.add(makeNorthPanel(), BorderLayout.NORTH);
		mainPanel.add(makeCenterPanel(), BorderLayout.CENTER);
		mainPanel.add(makeSouthPanel(), BorderLayout.SOUTH);
		
		addListeners();
		initKeeper();
		
		this.setTitle(PROG_NAME + " -- v" + PROG_VER);
		this.setSize(new Dimension(WIN_X, WIN_Y));
		this.add(mainPanel);
		this.addWindowListener(this);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	private JPanel makeNorthPanel() {
		JPanel north = new JPanel(new GridLayout(5, 1));
		
		UP_NEXT_LABEL.setFont(new Font(FONT, Font.BOLD, UP_NEXT_FONT_SIZE));
		UP_NEXT_LABEL.setText(UP_NEXT_TXT);
		UP_NEXT_LABEL.setHorizontalAlignment(JLabel.CENTER);
		titleLabel.setFont(new Font(FONT, Font.TYPE1_FONT, VID_DATA_FONT_SIZE));
		titleLabel.setText("Title: Test");
		titleLabel.setHorizontalAlignment(JLabel.CENTER);
		dateLabel.setFont(new Font(FONT, Font.ITALIC, VID_DATA_FONT_SIZE));
		dateLabel.setText("Date: Test");
		dateLabel.setHorizontalAlignment(JLabel.CENTER);
		channelLabel.setFont(new Font(FONT, Font.TYPE1_FONT, VID_DATA_FONT_SIZE));
		channelLabel.setText("By: Test");
		channelLabel.setHorizontalAlignment(JLabel.CENTER);
		
		north.add(UP_NEXT_LABEL);
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
		
		urlField.setPreferredSize(new Dimension(URL_FIELD_X, URL_FIELD_Y));
		settButton.setPreferredSize(new Dimension(URL_FIELD_Y, URL_FIELD_Y));
		settButton.setToolTipText(TOOLTIP_SETTINGS);
		addLabel.setFont(new Font(FONT, Font.PLAIN, VID_DATA_FONT_SIZE-2));
		addLabel.setHorizontalAlignment(JLabel.CENTER);
		
		buttonPanel.add(pasteButton);
		buttonPanel.add(addButton);
		
		urlPanel.add(settButton);
		urlPanel.add(urlField);
		urlPanel.add(buttonPanel);
		
		center.add(addLabel, BorderLayout.NORTH);
		center.add(urlPanel, BorderLayout.CENTER);
		
		return center;
	}
	
	private JPanel makeSouthPanel() {
		FlowLayout mainLayout = new FlowLayout();
		FlowLayout toWatchLayout = new FlowLayout();
		GridLayout subLayout = new GridLayout();
		
		mainLayout.setHgap(60);
		mainLayout.setVgap(40);
		subLayout.setHgap(25);
		
		JPanel south = new JPanel(mainLayout);
		JPanel buttonPanel = new JPanel(subLayout);
		JPanel toWatchPanel = new JPanel(toWatchLayout);
		JLabel toWatchLabel = new JLabel(TO_WATCH_TXT);
		
		buttonPanel.add(nextButton);
		buttonPanel.add(skipButton);
		buttonPanel.add(prevButton);
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
			}
		}));
		
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
						UP_NEXT_LABEL.setText(UP_NEXT_TXT);
						titleLabel.setText(keeper.getNextTitle());
						dateLabel.setText(keeper.getNextDate());

						if(keeper.getNextChannel().length() > 0) {
							channelLabel.setText(CHANNEL_PREFIX + keeper.getNextChannel());
						} else {
							channelLabel.setText(" ");
						}
					} else {
						nextButton.setEnabled(false);
						UP_NEXT_LABEL.setText(EMPTY_QUEUE_TXT);
						titleLabel.setText(" ");
						dateLabel.setText(" ");
						channelLabel.setText(" ");
					}
					
					if(count > 1) {
						skipButton.setEnabled(true);
					} else {
						skipButton.setEnabled(false);
					}

					if(keeper.getCurr() == null) {
						prevButton.setEnabled(false);
					} else {
						prevButton.setEnabled(true);
						
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
		this.prevButton.setEnabled(!locked);
		this.addButton.setEnabled(!locked);
		this.skipButton.setEnabled(!locked);
		this.settButton.setEnabled(!locked);
		this.pasteButton.setEnabled(!locked);
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
		} else {
			String mess = "Would you like to save the watch list?";
			int option = JOptionPane.showOptionDialog(this, mess, PROG_NAME + " -- Save?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
			
			if(option == JOptionPane.YES_OPTION) {
				save();
			}
		}
		
		System.exit(0);
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
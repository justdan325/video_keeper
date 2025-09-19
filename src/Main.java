import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

public class Main {
	public static final String 		DEFAULT_DATABASE 		= "database.txt";
	public static final String 		DEFAULT_HNDL_LNKS 		= "DEFAULT";
	public static final boolean 	OS_MAC					= System.getProperty("os.name").contains("Mac");
	
	private static final String PROP_KEY_DATABASE		= "database";
	private static final String PROP_KEY_AUTO_SAVE		= "autoSave";
	private static final String PROP_KEY_CHECK_DUPL		= "checkDuplicates";
	private static final String PROP_KEY_HNDL_LNKS		= "handleLinks";
	private static final String PROP_KEY_PREV_HNDL_LNKS	= "prevHandleLinks";
	private static final String PROP_KEY_SRCH_OPTS		= "searchOptions";
	private static final String PROP_KEY_CURR_INDX		= "currentIndex";
	private static final String PROP_FILE				= "videokeeper.properties";
	
	private PropsFileUtil props;
	private DataModel model;
	private String database;
	private String searchOptions;
	private String handleLinks;
	private String prevHandleLinks;
	private int currentIndex;
	private boolean autoSave;
	private boolean checkDuplicates;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		this.model = new DataModel();
		
		setUiColorScheme();
//		setLookAndFeel();
		
		try {
			this.props = new PropsFileUtil(getOrCreatePropsFile());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not obtain properties file.", MainGui.PROG_NAME, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
		
		init();
	}
	
//	private void setLookAndFeel() {
//		try {
//			// Set cross-platform Java L&F (also called "Metal")
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		} catch (UnsupportedLookAndFeelException e) {
//			System.out.println(e);
//		} catch (ClassNotFoundException e) {
//			System.out.println(e);
//		} catch (InstantiationException e) {
//			System.out.println(e);
//		} catch (IllegalAccessException e) {
//			System.out.println(e);
//		}
//	}
	
	@SuppressWarnings("static-access")
	private void setUiColorScheme() {
		UIManager UI = new UIManager();
		UI.put("OptionPane.messageForeground", MainGui.PROG_COLOR_TXT_LT);
		UI.put("OptionPane.background", MainGui.PROG_COLOR_BKRND);
		UI.put("Button.background", MainGui.PROG_COLOR_BTN_EN);
		UI.put("Button.foreground", MainGui.PROG_COLOR_TXT_LT);
		UI.put("Panel.background", MainGui.PROG_COLOR_BKRND);
		UI.put("Label.foreground", MainGui.PROG_COLOR_TXT_LT);
	}

	private void init() {
		this.database = DEFAULT_DATABASE;
		this.autoSave = true;
		this.checkDuplicates = true;
		this.handleLinks = DEFAULT_HNDL_LNKS;
		this.prevHandleLinks = "";
		this.searchOptions = "";
		this.currentIndex = 0;
		
		//get database
		if (!props.containsProp(PROP_KEY_DATABASE)) {
			props.set(PROP_KEY_DATABASE, database);
		} else {
			this.database = props.get(PROP_KEY_DATABASE);
		}
		
		//get auto save
		if (!props.containsProp(PROP_KEY_AUTO_SAVE)) {
			props.set(PROP_KEY_AUTO_SAVE, boolToStr(autoSave));
		} else {
			this.autoSave = strToBool(props.get(PROP_KEY_AUTO_SAVE));

			if (autoSave) {
				model.setAutoSaveOnExit(true);
			} else {
				model.setAutoSaveOnExit(false);
			}
		}
		
		//get check duplicates
		if (!props.containsProp(PROP_KEY_CHECK_DUPL)) {
			props.set(PROP_KEY_CHECK_DUPL, boolToStr(checkDuplicates));
		} else {
			this.checkDuplicates = strToBool(props.get(PROP_KEY_CHECK_DUPL));

			if (checkDuplicates) {
				model.setCheckForDupl(true);
			} else {
				model.setCheckForDupl(false);
			}
		}
		
		//get handle links 
		if (!props.containsProp(PROP_KEY_HNDL_LNKS)) {
			props.set(PROP_KEY_HNDL_LNKS, handleLinks);
		} else {
			this.handleLinks = props.get(PROP_KEY_HNDL_LNKS);
		}
		
		//get prev handle links 
		if (!props.containsProp(PROP_KEY_PREV_HNDL_LNKS)) {
			props.set(PROP_KEY_PREV_HNDL_LNKS, prevHandleLinks);
		} else {
			this.prevHandleLinks = props.get(PROP_KEY_PREV_HNDL_LNKS);
		}
		
		//get search options
		if (!props.containsProp(PROP_KEY_SRCH_OPTS)) {
			props.set(PROP_KEY_SRCH_OPTS, searchOptions);
		} else {
			this.searchOptions = props.get(PROP_KEY_SRCH_OPTS);
		}
		
		//get current index
		if (!props.containsProp(PROP_KEY_CURR_INDX)) {
			props.set(PROP_KEY_CURR_INDX, currentIndex + "");
		} else {
			try {
				currentIndex = Integer.parseInt(props.get(PROP_KEY_CURR_INDX));
			} catch (NumberFormatException e) {
				currentIndex = 0;
			}
		}
		
		model.setDatabaseFile(database);
		model.setHandleLinks(handleLinks);
		model.setPreviousHandleLinks(prevHandleLinks);
		model.setSearchOptions(searchOptions);
		model.setCurrIndex(currentIndex);

		monitorProperties();

		new MainGui(model);
	}
	
	private void monitorProperties() {
		Thread monitorThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (;;) {
					checkAndSaveProperties();
					
					try {
						Thread.sleep(6500); //Only conduct read/write ops every 6.5 seconds to save SSD wear.
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		Thread monitorClosingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (;;) {
					try {
						if (model.isProgramClosing()) {
							checkAndSaveProperties();

							//Only check once while closing dialog might be on screen. Don't kill thread in case user selects "cancel."
							while (model.isProgramClosing()) {
								Thread.sleep(30);
							}
						}

						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		monitorClosingThread.start();
		monitorThread.start();
	}
	
	/*
	 * Check to see if model data differs from props. If so, save the props.
	 */
	protected void checkAndSaveProperties() {
		if (autoSave != model.isAutoSaveOnExit()) {
			this.autoSave = model.isAutoSaveOnExit();
			props.set(PROP_KEY_AUTO_SAVE, boolToStr(model.isAutoSaveOnExit()));
		}
		
		if (checkDuplicates != model.isCheckForDupl()) {
			this.checkDuplicates = model.isCheckForDupl();
			props.set(PROP_KEY_CHECK_DUPL, boolToStr(model.isCheckForDupl()));
		}

		if (!database.trim().equals(model.getDatabaseFile().trim())) {
			this.database = model.getDatabaseFile().trim();
			props.set(PROP_KEY_DATABASE, model.getDatabaseFile().trim());
		}

		if (!handleLinks.trim().equals(model.getHandleLinks().trim())) {
			this.handleLinks = model.getHandleLinks().trim();
			props.set(PROP_KEY_HNDL_LNKS, model.getHandleLinks().trim());
		}

		if (!prevHandleLinks.trim().equals(model.getPreviousHandleLinks().trim())) {
			this.prevHandleLinks = model.getPreviousHandleLinks().trim();
			props.set(PROP_KEY_PREV_HNDL_LNKS, model.getPreviousHandleLinks().trim());
		}
		
		if (!searchOptions.trim().equals(model.getSearchOptions().trim())) {
			this.searchOptions = model.getSearchOptions().trim();
			props.set(PROP_KEY_SRCH_OPTS, model.getSearchOptions().trim());
		}
		
		if (currentIndex != model.getCurrIndex()) {
			this.currentIndex = model.getCurrIndex();
			props.set(PROP_KEY_CURR_INDX, (model.getCurrIndex() + ""));
		}
	}
	
	private File getOrCreatePropsFile() {
		File propsFile = new File(PROP_FILE);
		
		if (!propsFile.exists()) {
			try {
				propsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return propsFile;
	}
	
	private boolean strToBool(String str) {
		return str.trim().equals("1") ? true : false;
	}

	private String boolToStr(boolean bool) {
		return bool ? "1" : "0";
	}
}

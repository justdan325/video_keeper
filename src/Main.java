import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Main {
	public  static final String DEFAULT_DATABASE 	= "database.txt";
	private static final String PROP_KEY_DATABASE	= "database";
	private static final String PROP_KEY_AUTO_SAVE	= "autoSave";
	private static final String PROP_FILE			= "videokeeper.properties";
	
	private PropsFileUtil props;
	private DataModel model;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
		this.model = new DataModel();
		
		try {
			this.props = new PropsFileUtil(getOrCreatePropsFile());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Could not obtain properties file.", MainGui.PROG_NAME, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(-1);
		}
		
		init();
	}
	
	private void init() {
		String database = DEFAULT_DATABASE;
		String autoSave = "1";
		File databaseFile;
		
		//get database
		if(!props.containsProp(PROP_KEY_DATABASE)) {
			props.set(PROP_KEY_DATABASE, database);
		} else {
			database = props.get(PROP_KEY_DATABASE);
		}
		
		//get auto save
		if(!props.containsProp(PROP_KEY_AUTO_SAVE)) {
			props.set(PROP_KEY_AUTO_SAVE, autoSave);
		} else {
			autoSave = props.get(PROP_KEY_AUTO_SAVE);
			
			if(autoSave.trim().equals("1")) {
				model.setAutoSaveOnExit(true);
			} else {
				model.setAutoSaveOnExit(false);
			}
		}
		
		databaseFile = new File(database);
		model.setDatabaseFile(databaseFile.getAbsolutePath());
		
		monitorProperties();
		
		new MainGui(model);
	}
	
	private void monitorProperties() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				for(;;) {
					//check to see if model data differes from props
					//if so, save the props
					if(strToBool(props.get(PROP_KEY_AUTO_SAVE)) != model.isAutoSaveOnExit()) {
						props.set(PROP_KEY_AUTO_SAVE, boolToStr(model.isAutoSaveOnExit()));
					}
					
					if(!props.get(PROP_KEY_DATABASE).trim().equals(model.getDatabaseFile().trim())) {
						props.set(PROP_KEY_DATABASE, model.getDatabaseFile().trim());
					}
					
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		
		thread.start();
	}
	
	private File getOrCreatePropsFile() {
		File propsFile = new File(PROP_FILE);
		
		if(!propsFile.exists()) {
			try {
				propsFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return propsFile;
	}
	
	private boolean strToBool(String str) {
		boolean bool = false;
		
		if(str.trim().equals("1")) {
			bool = true;
		}
		
		return bool;
	}
	
	private String boolToStr(boolean bool) {
		String str = "0";
		
		if(bool) {
			str = "1";
		}
		
		return str;
	}
}

import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Main {
	private static final String DEFAULT_DATABASE 	= "database.txt";
	private static final String PROP_KEY_DATABASE	= "database";
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
		File databaseFile;
		
		if(!props.containsProp(PROP_KEY_DATABASE)) {
			props.set(PROP_KEY_DATABASE, database);
		} else {
			if(props.containsProp(PROP_KEY_DATABASE)) {
				database = props.get(PROP_KEY_DATABASE);
			} else {
				props.set(PROP_KEY_DATABASE, database);
			}
		}
		
		databaseFile = new File(database);
		model.setDatabaseFile(databaseFile.getAbsolutePath());
		
		new MainGui(model);
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
}

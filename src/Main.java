import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

public class Main {
	private static final String DEFAULT_DATABASE 	= "database.txt";
	private static final String PROP_KEY_DATABASE	= "database";
	private static final String PROP_FILE			= "videokeeper.properties";
	
	private PropsFileUtil props;
	
	public static void main(String[] args) {
		new Main();
	}
	
	public Main() {
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
		if(!props.containsProp(PROP_KEY_DATABASE)) {
			try {
				props.set(PROP_KEY_DATABASE, DEFAULT_DATABASE);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		
		new MainGui(DEFAULT_DATABASE);
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

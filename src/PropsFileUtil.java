import java.io.*;
import java.util.Properties;

public class PropsFileUtil {
	private File propsFile;
	private Properties props;
	
//	public static void main(String[] args) {
//		PropsFileUtil util = null;
//		
//		try {
//			util = new PropsFileUtil(new File("videokeeper.properties"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		util.set("database", "database.txt");
//		util.set("database", "database2.txt");
//	}

	public PropsFileUtil(File propsFile) throws Exception {
		if(!propsFile.exists()) {
			throw new FileNotFoundException("Properties file does not exist!");
		}

		this.propsFile = propsFile;
		this.props = setup(propsFile);
	}

	private Properties setup(File propsFile) throws IOException {
		Properties props = new Properties();

		props.load(new FileInputStream(propsFile));

		return props;
	}

	public String get(String prop) {
		String value = null;

		value = props.getProperty(prop);

		return value;
	}

	public boolean set(String prop, String value) {
		FileOutputStream out = null;
		boolean success = true;
		
		props.setProperty(prop, value);
		
		try {
			out = new FileOutputStream(propsFile);
			props.store(out, null);
			out.close();
		} catch (IOException e) {
			success = false;
		}
		
		return success;
	}

	public boolean remove(String prop) {
		FileOutputStream out = null;
		Object value = props.remove(prop);
		boolean removed = false;

		if(value != null) {
			try {
				out = new FileOutputStream(propsFile);
				props.store(out, null);
				removed = true;
				out.close();
			} catch (IOException e) {
			}
		}

		return removed;
	}

	public boolean clearAll() {
		FileOutputStream out = null;
		boolean cleared = true;
		
		props.clear();
		
		try {
			out = new FileOutputStream(propsFile);
			props.store(out, null);
			out.close();
		} catch (IOException e) {
			cleared = false;
		}
		
		return cleared;
	}

	public String listAll() {
		return props.toString();
	}

	public boolean containsProp(String prop) {
		return props.containsKey(prop);
	}

	public boolean containsValue(String value) {
		return props.containsValue(value);
	}

	public boolean isEmpty() {
		return props.isEmpty();
	}

	public int getNumProps() {
		return props.size();
	}
}

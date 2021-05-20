import java.io.*;
import java.util.Hashtable;
import java.util.Properties;

public class PropsFileUtil {
	private File propsFile;
	private Properties props;
	private FileOutputStream out;

	public PropsFileUtil(File propsFile) throws Exception {
		if(!propsFile.exists()) {
			throw new FileNotFoundException("Properties file does not exist!");
		}

		this.propsFile = propsFile;
		this.props = setup(propsFile);
		this.out = new FileOutputStream(propsFile);
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
		boolean success = true;
		
		props.setProperty(prop, value);
		
		try {
			props.store(out, null);
		} catch (IOException e) {
			success = false;
		}
		
		return success;
	}

	public boolean remove(String prop) {
		Object value = props.remove(prop);
		boolean removed = false;

		if(value != null) {
			try {
				props.store(out, null);
				removed = true;
			} catch (IOException e) {
			}
		}

		return removed;
	}

	public boolean clearAll() {
		boolean cleared = true;
		
		props.clear();
		
		try {
			props.store(out, null);
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
	
	public boolean close() {
		boolean closed = true;
		
		try {
			out.close();
		} catch (IOException e) {
			closed = false;
		}
		
		return closed;
	}
}

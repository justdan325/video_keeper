import java.io.*;
import java.util.Hashtable;
import java.util.Properties;

public class PropsFileUtil {
	private File propsFile;
	private Properties props;

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

	public void set(String prop, String value) throws IOException {
		props.setProperty(prop, value);
		props.store(new FileOutputStream(propsFile), null);
	}

	public boolean remove(String prop) throws IOException {
		Object value = props.remove(prop);
		boolean removed = false;

		if(value != null) {
			props.store(new FileOutputStream(propsFile), null);
			removed = true;
		}

		return removed;
	}

	public void clearAll() throws IOException {
		props.clear();
		props.store(new FileOutputStream(propsFile), null);
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

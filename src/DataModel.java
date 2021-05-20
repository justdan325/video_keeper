
public class DataModel {
	private String databaseFile;
	private boolean autoSaveOnExit;
	
	public DataModel() {
		this.databaseFile = Main.DEFAULT_DATABASE;
		this.autoSaveOnExit = true;
	}

	public String getDatabaseFile() {
		return databaseFile;
	}

	public void setDatabaseFile(String databaseFile) {
		this.databaseFile = databaseFile;
	}

	public boolean isAutoSaveOnExit() {
		return autoSaveOnExit;
	}

	public void setAutoSaveOnExit(boolean autoSaveOnExit) {
		this.autoSaveOnExit = autoSaveOnExit;
	}
}

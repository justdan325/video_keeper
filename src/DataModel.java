
public class DataModel {
	private String databaseFile;
	private boolean autoSaveOnExit;
	private boolean checkForDupl;
	
	public DataModel() {
		this.databaseFile = Main.DEFAULT_DATABASE;
		this.autoSaveOnExit = true;
		this.checkForDupl = true;
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

	public boolean isCheckForDupl() {
		return checkForDupl;
	}

	public void setCheckForDupl(boolean checkForDupl) {
		this.checkForDupl = checkForDupl;
	}
}

public class DataModel {
	private String databaseFile;
	private String handleLinks;
	private String previousHandleLinks;
	private boolean autoSaveOnExit;
	private boolean checkForDupl;
	
	public DataModel() {
		this.databaseFile = Main.DEFAULT_DATABASE;
		this.handleLinks = Main.DEFAULT_HNDL_LNKS;
		this.autoSaveOnExit = true;
		this.checkForDupl = true;
	}

	public String getDatabaseFile() {
		return databaseFile;
	}

	public void setDatabaseFile(String databaseFile) {
		this.databaseFile = databaseFile;
	}

	public String getHandleLinks() {
		return handleLinks;
	}

	public void setHandleLinks(String handleLinks) {
		this.handleLinks = handleLinks;
	}

	public String getPreviousHandleLinks() {
		return previousHandleLinks;
	}

	public void setPreviousHandleLinks(String previousHandleLinks) {
		this.previousHandleLinks = previousHandleLinks;
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
import java.util.Optional;

public class DataModel {
	private Optional<VideoList> videoList;
	private String databaseFile;
	private String handleLinks;
	private String previousHandleLinks;
	private boolean autoSaveOnExit;
	private boolean checkForDupl;
	
	public DataModel() {
		this.videoList = Optional.empty();
		this.databaseFile = Main.DEFAULT_DATABASE;
		this.handleLinks = Main.DEFAULT_HNDL_LNKS;
		this.autoSaveOnExit = true;
		this.checkForDupl = true;
	}
	
	public synchronized Optional<VideoList> getVideoList() {
		return videoList;
	}

	public synchronized void setVideoList(Optional<VideoList> videoList) {
		this.videoList = videoList;
	}

	public synchronized String getDatabaseFile() {
		return databaseFile;
	}

	public synchronized void setDatabaseFile(String databaseFile) {
		this.databaseFile = databaseFile;
	}

	public synchronized String getHandleLinks() {
		return handleLinks;
	}

	public synchronized void setHandleLinks(String handleLinks) {
		this.handleLinks = handleLinks;
	}

	public synchronized String getPreviousHandleLinks() {
		return previousHandleLinks;
	}

	public synchronized void setPreviousHandleLinks(String previousHandleLinks) {
		this.previousHandleLinks = previousHandleLinks;
	}

	public synchronized boolean isAutoSaveOnExit() {
		return autoSaveOnExit;
	}

	public synchronized void setAutoSaveOnExit(boolean autoSaveOnExit) {
		this.autoSaveOnExit = autoSaveOnExit;
	}

	public synchronized boolean isCheckForDupl() {
		return checkForDupl;
	}

	public synchronized void setCheckForDupl(boolean checkForDupl) {
		this.checkForDupl = checkForDupl;
	}
}
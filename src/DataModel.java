import java.util.Optional;

public class DataModel {
	private volatile Optional<VideoList> videoList;
	
	private VideoKeeper videoKeeper;
	private String databaseFile;
	private String handleLinks;
	private String previousHandleLinks;
	private boolean autoSaveOnExit;
	private boolean checkForDupl;
	private boolean requestSaveButtonEn;
	private boolean caseSensitive;
	private boolean searchThruTitles;
	private boolean searchThruDates;
	private boolean searchThruChannels;
	private boolean playAndDelete;
	
	public DataModel() {
		this.videoList = Optional.empty();
		this.videoKeeper = null;
		this.databaseFile = Main.DEFAULT_DATABASE;
		this.handleLinks = Main.DEFAULT_HNDL_LNKS;
		this.autoSaveOnExit = true;
		this.checkForDupl = true;
		this.requestSaveButtonEn = false;
		this.caseSensitive = false;
		this.searchThruTitles = true;
		this.searchThruDates = true;
		this.searchThruChannels = true;
		this.playAndDelete = true;
	}
	
	public synchronized Optional<VideoList> getVideoList() {
		return videoList;
	}

	public synchronized void setVideoList(Optional<VideoList> videoList) {
		this.videoList = videoList;
	}

	public VideoKeeper getVideoKeeper() {
		return videoKeeper;
	}

	public void setVideoKeeper(VideoKeeper videoKeeper) {
		this.videoKeeper = videoKeeper;
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

	public boolean isRequestSaveButtonEn() {
		return requestSaveButtonEn;
	}

	public void setRequestSaveButtonEn(boolean setSaveButtonEn) {
		this.requestSaveButtonEn = setSaveButtonEn;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public boolean isSearchThruTitles() {
		return searchThruTitles;
	}

	public void setSearchThruTitles(boolean searchThruTitles) {
		this.searchThruTitles = searchThruTitles;
	}

	public boolean isSearchThruDates() {
		return searchThruDates;
	}

	public void setSearchThruDates(boolean searchThruDates) {
		this.searchThruDates = searchThruDates;
	}

	public boolean isSearchThruChannels() {
		return searchThruChannels;
	}

	public void setSearchThruChannels(boolean searchThruChannels) {
		this.searchThruChannels = searchThruChannels;
	}

	public boolean isPlayAndDelete() {
		return playAndDelete;
	}

	public void setPlayAndDelete(boolean playAndDelete) {
		this.playAndDelete = playAndDelete;
	}
}
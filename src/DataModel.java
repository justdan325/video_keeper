import java.util.Optional;

public class DataModel {
	private volatile Optional<VideoList> videoList;
	
	private VideoKeeper videoKeeper;
	private String databaseFile;
	private String handleLinks;
	private String previousHandleLinks;
	private String searchOptions;
	private String ytdlpLoc;
	private int currIndex;
	private boolean autoSaveOnExit;
	private boolean checkForDupl;
	private boolean autoPopUpEditorCheckbox;
	private boolean requestSaveButtonEn;
	private boolean caseSensitive;
	private boolean searchThruTitles;
	private boolean searchThruDates;
	private boolean searchThruChannels;
	private boolean playAndDelete;
	private boolean programClosing;
	private boolean useYtdlp;
	
	public DataModel() {
		this.videoList = Optional.empty();
		this.videoKeeper = null;
		this.databaseFile = Main.DEFAULT_DATABASE;
		this.handleLinks = Main.DEFAULT_HNDL_LNKS;
		this.searchOptions = "";
		this.ytdlpLoc = Main.DEFAULT_YTDLP_LOC;
		this.currIndex = 0;
		this.autoSaveOnExit = true;
		this.checkForDupl = true;
		this.autoPopUpEditorCheckbox = false;
		this.requestSaveButtonEn = false;
		this.caseSensitive = false;
		this.searchThruTitles = true;
		this.searchThruDates = true;
		this.searchThruChannels = true;
		this.playAndDelete = true;
		this.programClosing = false;
		this.useYtdlp = true;
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

	public String getSearchOptions() {
		return searchOptions;
	}

	public void setSearchOptions(String searchOptions) {
		this.searchOptions = searchOptions;
	}

	public int getCurrIndex() {
		return currIndex;
	}

	public void setCurrIndex(int currIndex) {
		this.currIndex = currIndex;
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

	public boolean isAutoPopUpEditorCheckbox() {
		return autoPopUpEditorCheckbox;
	}

	public void setAutoPopUpEditorCheckbox(boolean autoPopUpEditorCheckbox) {
		this.autoPopUpEditorCheckbox = autoPopUpEditorCheckbox;
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

	public boolean isProgramClosing() {
		return programClosing;
	}

	public void setProgramClosing(boolean programClosing) {
		this.programClosing = programClosing;
	}

	public boolean isUseYtdlp() {
		return useYtdlp;
	}

	public void setUseYtdlp(boolean useYtdlp) {
		this.useYtdlp = useYtdlp;
	}

	public String getYtdlpLoc() {
		return ytdlpLoc;
	}

	public void setYtdlpLoc(String ytdlpLoc) {
		this.ytdlpLoc = ytdlpLoc;
	}
}
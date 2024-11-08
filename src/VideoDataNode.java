public class VideoDataNode {
	private static final String DELIM = "|";
	
	private String url;
	private String title;
	private String date;
	private String channel;
	private String time;
	private boolean empty;
	
	//String format: URL|title|date|channel|time
	
	public VideoDataNode() {
		clear();
	}

	public VideoDataNode(String str) {
		clear();
		decodeStr(str);
	}
	
	public boolean equals(VideoDataNode other) {
		boolean equal = false;
		
		if (url.equals(other.getUrl())) {
			if (title.equals(other.getTitle())) {
				if (date.equals(other.getDate())) {
					if (channel.equals(other.getChannel())) {
						if (time.equals(other.getTime())) {
							if (empty == other.isEmpty()) {
								equal = true;
							}
						}
					}
				}
			}
		}
		
		return equal;
	}

	private void decodeStr(String str) {
		String[] contents = str.split( "\\" + DELIM);
		
		if(contents.length > 0) {
			//make sure that the first element is a URL or IP address
			if(contents[0].contains(".")) {
				this.url = contents[0];
				this.empty = false;
				
				if(contents.length >= 2) {
					this.title = contents[1];
				}
				
				if(contents.length >= 3) {
					this.date = contents[2];
				}
				
				if(contents.length >= 4) {
					this.channel = contents[3];
				}
				
				if(contents.length >= 5) {
					this.time = contents[4];
				}
			} else {
				clear();
			}
		}
	}
	
	private void clear() {
		this.url = "";
		this.title = "";
		this.date = "";
		this.channel = "";
		this.time = "";
		this.empty = true;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		if(url == null || !url.contains(".")) {
			clear();
		} else {
			this.url = url.trim();
		}
	}
	
	public String getTitle() {
		return title;
	}
		   
	public void setTitle(String title) {
		if(title == null) {
			this.title = "";
		} else {
			if(title.contains(DELIM)) {
				title = title.replaceAll("\\" + DELIM, "_");
			}
			
			this.title = title.trim();
		}
	}
	
	public String getDate() {
		return date;
	}
		   
	public void setDate(String date) {
		if(date == null) {
			this.date = "";
		} else {
			this.date = date.trim();
		}
	}
	
	public String getChannel() {
		return channel;
	}
	
	public void setChannel(String channel) {
		if(channel == null) {
			this.channel = "";
		} else {
			if(channel.contains(DELIM)) {
				channel = channel.replaceAll("\\" + DELIM, "_");
			}
			
			this.channel = channel.trim();
		}
	}
	
	public String getTime() {
		return time;
	}
	
	public void setTime(String time) {
		if(time == null) {
			this.channel = "";
		} else {
			if(time.contains(DELIM)) {
				time = time.replaceAll("\\" + DELIM, "_");
			}
			
			this.time = time.trim();
		}
	}
	
	public boolean isEmpty() {
		return empty;
	}
		   
	public String toString() {
		String str = "";
		
		if(!empty) {
			str = url + DELIM + title + DELIM + date + DELIM + channel + DELIM + time;
		}
		
		return str;
	}
}
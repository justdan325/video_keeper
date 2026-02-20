import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class MetadataObtainerYtdlp {
//	private static final String TEST_JSON = "\"id\": \"AjQNDCYL5Rg\", \"is_live\": false, \"description\": \"This is a \\\"descript\\\" description.\", \"title\": \"Bad Bot Problem - Computerphile\"}";
	
	private final String URL;
	private final String KEY_TITLE 		= "title";
	private final String KEY_DATE 		= "upload_date";
	private final String KEY_UPLOADER 	= "uploader";
	private final String KEY_CHANNEL	= "channel";
	private final String KEY_TIME 		= "duration_string";
	private final String KEY_DESC 		= "description";
	private final String KEY_THUMB 		= "thumbnail";
	private final String KEY_VERIFIED 	= "channel_is_verified";
	private final String KEY_IS_LIVE 	= "is_live";
	private final String KEY_WAS_LIVE 	= "was_live";
	
	private DataModel model;
	private Optional<String> title;
	private Optional<String> date;
	private Optional<String> channel;
	private Optional<String> time;
	private Optional<String> description;
	private Optional<String> thumbnailUrl;
	private boolean verified;
	private boolean isLive;
	private boolean wasLive;
	private boolean isRun; //whether or not the run() method has been called
	
	public MetadataObtainerYtdlp(DataModel model, String url) {
		this.URL = url;
		this.model = model;
		this.title = Optional.empty();
		this.date = Optional.empty();
		this.channel = Optional.empty();
		this.time = Optional.empty();
		this.description = Optional.empty();
		this.thumbnailUrl = Optional.empty();
		this.verified = false;
		this.isLive = false;
		this.wasLive = false;
		this.isRun = false;
	}
	
	public static void main(String[] args) throws IOException {
		DataModel testModel = new DataModel();
		String url = "https://www.bitchute.com/video/B2m6kfJ_3gs";
		
		testModel.setYtdlpLoc("/home/dan/.local/share/pipx/venvs/yt-dlp/bin/yt-dlp");
		
		MetadataObtainerYtdlp o = new MetadataObtainerYtdlp(testModel, url);
		
		o.run();
		
		System.out.println("URL provided: [" + url + "]");
		System.out.println("Title       : [" + (o.getTitle().isPresent() ? o.getTitle().get() : "") + "]");
		System.out.println("Date        : [" + (o.getDate().isPresent() ? o.getDate().get() : "") + "]");
		System.out.println("Channel     : [" + (o.getChannel().isPresent() ? o.getChannel().get() : "") + "]");
		System.out.println("Time        : [" + (o.getTime().isPresent() ? o.getTime().get() : "") + "]");
		System.out.println("Description : [" + (o.getDescription().isPresent() ? o.getDescription().get() : "") + "]");
		System.out.println("Verified    : [" + o.isVerified() + "]");
		System.out.println("Is Live     : [" + o.isLive() + "]");
		System.out.println("Was Live    : [" + o.wasLive() + "]");
	}
	
	public boolean run() {
		Optional<String> json = requestJson(URL);
		boolean success = true;

		if (json.isPresent()) {
			success = parse(json.get());
		} else {
			success = false;
		}
		
		//intentionally marking as "run" even if the run failed to prevent multiple successive queries 
		this.isRun = true;
		
		return success;
	}
	
	//TODO: Success currently does nothing.
	private boolean parse(String json) {
		boolean success = true;
		Optional<String> temp;
		String dateStr;
		
		this.title = extractJsonKvp(KEY_TITLE, json, false);
		this.date = extractJsonKvp(KEY_DATE, json, false);
		
		if (URL.startsWith(MetadataObtainer.ODYSEE_PREFIX) == false) {
			this.channel = extractJsonKvp(KEY_UPLOADER, json, false);
		}
		
		this.time = extractJsonKvp(KEY_TIME, json, false);
		
		if (URL.startsWith(MetadataObtainer.TWITCH_PREFIX_MOB) == false && URL.startsWith(MetadataObtainer.TWITCH_PREFIX_W) == false) {
			this.description = extractJsonKvp(KEY_DESC, json, false);
		}
		
		if (URL.startsWith(MetadataObtainer.TWITCH_PREFIX_MOB) == false && URL.startsWith(MetadataObtainer.TWITCH_PREFIX_W) == false) {
			this.thumbnailUrl = extractJsonKvp(KEY_THUMB, json, false);
		}
		
		//format the date human readable
		if (getDate().isPresent()) {
			dateStr = getDate().get();
			dateStr = dateStr.substring(0, 4) + "-" + dateStr.substring(4, 6) + "-" + dateStr.substring(6);
			
			this.date = Optional.of(dateStr);
		}
		
		//in cases where "uploader" is not a field, use channel
		if (channel.isPresent() == false) {
			this.channel = extractJsonKvp(KEY_CHANNEL, json, false);
		}
		
		//parse all boolean values
		if (URL.startsWith(MetadataObtainer.BITCHUTE_PREFIX) == false && URL.startsWith(MetadataObtainer.BITCHUTE_PREFIX_W) == false
				&& URL.startsWith(MetadataObtainer.ODYSEE_PREFIX) == false && URL.startsWith(MetadataObtainer.TWITCH_PREFIX_MOB) == false
				&& URL.startsWith(MetadataObtainer.TWITCH_PREFIX_W) == false) {
			temp = extractJsonKvp(KEY_VERIFIED, json, true);

			if (temp.isPresent()) {
				this.verified = Boolean.parseBoolean(temp.get());
			}
		}
		
		temp = extractJsonKvp(KEY_IS_LIVE, json, true);
				
		if (temp.isPresent()) {
			this.isLive = Boolean.parseBoolean(temp.get());
		}
		
		if (URL.startsWith(MetadataObtainer.BITCHUTE_PREFIX) == false && URL.startsWith(MetadataObtainer.BITCHUTE_PREFIX_W) == false 
				&& URL.startsWith(MetadataObtainer.ODYSEE_PREFIX) == false) {
			temp = extractJsonKvp(KEY_WAS_LIVE, json, true);
					
			if (temp.isPresent()) {
				this.wasLive = Boolean.parseBoolean(temp.get());
			}
		}
		
		return success;
	}
	
	private Optional<String> requestJson(String url) {
		Optional<String> jsonOpt = Optional.empty();
		String json = "";
		final String COMMAND = model.getYtdlpLoc() + " -j " + url.trim() + " --skip-download";
		Process proc;
		BufferedReader stdInput;
		BufferedReader stdError;
		String s = null;
		
		try {
			proc = Runtime.getRuntime().exec(COMMAND);
			stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
			
			//get JSON payload
			while ((s = stdInput.readLine()) != null) {
				json += s;
			}
			
			//print any errors to the console
			while ((s = stdError.readLine()) != null) {
				System.err.println(s);
			}
			
			if (json.trim().length() > 0) {
				jsonOpt = Optional.of(json);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return jsonOpt;
	}
	
	/*
	 * Does not presently work with arrays or anything aside from key value pair.
	 */
	private static Optional<String> extractJsonKvp(String key, String json, boolean primitiveVal) {
		Optional<String> valueOpt = Optional.empty();
		String quotedKey = key = "\"" + key.trim() + "\"";
		String value = "";
		int begin = json.indexOf(quotedKey);
		int end = -1;
		
		if (begin != -1) {
			begin += quotedKey.length();
			
			//primitives like true/false will not have quotation marks around them
			if (primitiveVal) {
				begin = json.indexOf(":", begin) + 1; //plus one to get past colon
				end = json.indexOf(",", begin);
			} else {
				begin = json.indexOf("\"", begin) + 1; //plus one to get past opening quotation mark
				end = json.indexOf("\",", begin);
				
				//ensure that the quotation mark is not escaped via backslash, lest it not be the closing one
				while (end != -1 && json.charAt(end - 1) == '\\') {
					end = json.indexOf("\",", end + 1);
				}
				
				if (end == -1) {
					end = json.indexOf("\"}", begin);
					
					//ensure that the quotation mark is not an escaped mark
					while (end != -1 && json.charAt(end - 1) == '\\') {
						end = json.indexOf("\",", end + 1);
					}
				}
			}
				
			if (end != -1) {
				value = json.substring(begin, end);
				
				//filter out any escape back slashes
				while (value.contains("\\")) {
					value = value.replace("\\", "");
				}
				
				//remove any leading or trailing white space
				value = value.trim();
				
				if (value.trim().length() > 0) {
					valueOpt = Optional.of(value);
				} else {
					System.err.println("Failed to parse value from key: " + quotedKey);
				}
			} else {
				System.err.println("Failed to find end index of value from key: " + quotedKey);
			}
		} else {
			System.err.println("Failed to find key: " + quotedKey);
		}
		
		return valueOpt;
	}

	public Optional<String> getTitle() {
		return title;
	}

	public Optional<String> getDate() {
		return date;
	}

	public Optional<String> getChannel() {
		return channel;
	}

	public Optional<String> getTime() {
		return time;
	}

	public Optional<String> getDescription() {
		return description;
	}

	public Optional<String> getThumbnailUrl() {
		return thumbnailUrl;
	}

	public boolean isVerified() {
		return verified;
	}

	public boolean isLive() {
		return isLive;
	}

	public boolean wasLive() {
		return wasLive;
	}
	
	public boolean isRun() {
		return isRun;
	}
}

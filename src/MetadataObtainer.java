import java.util.Scanner;
import java.net.URLConnection;
import java.net.URL;
import java.util.Calendar;
import java.text.DateFormatSymbols;
import java.time.YearMonth;

public class MetadataObtainer {
	private static final String FETCH_ERROR_PREFIX 	= "ERROR FETCHING HTML: ";
	private static final String YOUTUBE_PREFIX 		= "https://youtube.com/watch?v=";
	private static final String YOUTUBE_PREFIX_W 	= "https://www.youtube.com/watch?v=";
	private static final String YOUTUBE_PREFIX_ABBR = "https://youtu.be/";
	private static final String TWITCH_PREFIX_W		= "https://www.twitch.tv/videos/";
	private static final String TWITCH_PREFIX_MOB	= "https://m.twitch.tv/videos/";
	
	private String urlStr;
	private String html;
	private boolean isSupported;
	
	public MetadataObtainer(String urlStr) {
		this.urlStr = sanitize(urlStr);
		this.html = fetchHtml(this.urlStr);
		this.isSupported = isSupported(urlStr);
	}
	
	public static void main(String[] args){
//		MetadataObtainer o = new MetadataObtainer("https://www.twitch.tv/videos/997396590");
//		System.out.println(o.getTitle());
//		System.out.println(o.getDate());
//		System.out.println(o.getChannel());
//		System.out.println(MetadataObtainer.determineDateOnTwitch("3 days ago"));
	}
	
	public static boolean isSupported(String urlStr) {
		boolean supported = false;

		if(urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W) 
		   || urlStr.startsWith(YOUTUBE_PREFIX_ABBR) || urlStr.startsWith(TWITCH_PREFIX_W) 
		   || urlStr.startsWith(TWITCH_PREFIX_MOB)) {
			
			supported = true;
		}
		
		return supported;
	}
	
	public boolean isSupported() {
		return isSupported;
	}
	
	public boolean isUrlError() {
		boolean error = false;
		
		if(html.startsWith(FETCH_ERROR_PREFIX)) {
			error = true;
		}
		
		return error;
	}
	
	public String getTitle() {
		String title = "";
		
		if(!isUrlError()) {
			//YouTube Regular Links
			if(urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W)) {
				String prefix = "content=\"" + urlStr.trim() + "\"><meta property=\"og:title\" content=\"";
				String suffix = "\"><meta property=\"og:image\" content=\"";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
			//YouTube Shortened Links
			} else if(urlStr.startsWith(YOUTUBE_PREFIX_ABBR)) {
				String prefix = "feature=youtu.be\"><title>";
				String suffix = " - YouTube</title>";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
			//Twitch
			} else if(urlStr.startsWith(TWITCH_PREFIX_MOB)) {
				String prefix = "content=\"default\"/><title>";
				String suffix = " on Twitch</title>";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
				
				//title is first part
				String[] strArr = title.split(" - ");
				
				if(strArr.length >= 1) {
					title = strArr[0];
				}
			}
		}
		
		if(title.length() > 100) {
			title = "";
		}
		
		return title;
	}
	
	public String getChannel() {
		String channel = "";
		
		if(!isUrlError()) {
			//YouTube
			if(urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W) || urlStr.startsWith(YOUTUBE_PREFIX_ABBR)) {
				String prefix = "{\"label\":\"Subscribe to ";
				String suffix = ".\"}},";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if(begin == -1 || end == -1) {
					prefix = "<link itemprop=\"url\" href=\"http://www.youtube.com/channel";
					suffix = "\">";
					begin = html.indexOf(prefix) + prefix.length();
					
					prefix = "\"><link itemprop=\"name\" content=\"";
					begin = html.indexOf(prefix) + prefix.length();
					end = html.indexOf(suffix, begin);
				}
				
				if(begin == -1 || end == -1) {
					prefix = "\"title\":{\"simpleText\":\"Mix - ";
					suffix = "\"}";
					begin = html.indexOf(prefix) + prefix.length();
					end = html.indexOf(suffix, begin);
				}
				
				channel = html.substring(begin, end);
				channel = filterEscapeChars(channel);
				
				channel += " on YouTube";
			//Twitch
			} else if(urlStr.startsWith(TWITCH_PREFIX_MOB)) {
				String prefix = "content=\"default\"/><title>";
				String suffix = " on Twitch</title>";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					channel = html.substring(begin, end);
					channel = filterEscapeChars(channel);
				}
				
				//channel is second part
				String[] strArr = channel.split(" - ");
				
				if(strArr.length >= 2) {
					channel = strArr[1];
				}
				
				channel += " on Twitch";
			}
		}
		
		if(channel.length() > 75) {
			channel = "";
		}
		
		return channel;
	}
	
	public String getDate() {
		String date = "";
		
		if(!isUrlError()) {
			//YouTube
			if(urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W) || urlStr.startsWith(YOUTUBE_PREFIX_ABBR)) {
				String prefix = "\"dateText\":{\"simpleText\":\"";
				String suffix = "\"}";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					date = html.substring(begin, end);
				}
			}
			/*//Twitch
			} else if(urlStr.startsWith(TWITCH_PREFIX_MOB)) {
				String find = " days ago";
				int index = html.indexOf(find);
				
				if(index != -1) {
					String dateStr = html.substring(index-10, index);
					String prefix = "-->";

					int begin = dateStr.indexOf(prefix) + prefix.length();

					//number of days ago
					dateStr = dateStr.substring(begin);

					date = dateStr + " days ago";
					date = determineDateOnTwitch(date);
				}
			}*/
		}
		
		if(date.length() > 30) {
			date = "";
		}
		
		return date;
	}
	
	private static String fetchHtml(String url) {
		String content = null;
		URLConnection connection = null;
		
		try {
			connection =  new URL(url).openConnection();
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
			scanner.close();
		} catch (Exception e) {
			String message = "";
			
			if(e.getMessage() != null){
				message = " -- " + e.getMessage();
			}
				
		    content = FETCH_ERROR_PREFIX + e.toString() + message;
		}

		return content;
	}
	
	private static String sanitize(String urlStr) {
		String sanitized = urlStr;

		//YouTube
		if(urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W) 
		   || urlStr.startsWith(YOUTUBE_PREFIX_ABBR)) {
			
			sanitized = sanitizeYoutube(urlStr);
		//Twitch
		} else if(urlStr.startsWith(TWITCH_PREFIX_W) || urlStr.startsWith(TWITCH_PREFIX_MOB)) {
			sanitized = sanitizeTwitch(urlStr);
		}
		
		return sanitized;
	}
	
	private static String sanitizeYoutube(String urlStr) {
		String sanitized = urlStr;
		
		//remove playlist data
		if(urlStr.contains("&list=")) {
			sanitized = sanitized.substring(0, sanitized.indexOf("&list="));
		}
		
		//remove polymer disable
		if(urlStr.contains("&disable_polymer=1")) {
			sanitized = sanitized.replaceAll("&disable_polymer=1", "");
		}
		
		//remove time tags
		if(urlStr.contains("&t=")) {
			sanitized = sanitized.substring(0, sanitized.indexOf("&t="));
		}
		
		return sanitized;
	}

	private static String sanitizeTwitch(String urlStr) {
		String sanitized = urlStr;
		
		//normal twitch video id i.e. https://www.twitch.tv/videos/997396590
		//must convert to mobile to get data
		if(urlStr.startsWith(TWITCH_PREFIX_W)) {
			sanitized = urlStr.replaceFirst(TWITCH_PREFIX_W, TWITCH_PREFIX_MOB);
		}

		return sanitized;
	}
	
	private static String filterEscapeChars(String raw) {
		String filtered = raw;
		
		filtered = filtered.replaceAll("&amp;", "&");
		filtered = filtered.replaceAll("\\\\u0026", "&");
		filtered = filtered.replaceAll("&#39;", "'");
		filtered = filtered.replaceAll("&#x27;", "'");
		filtered = filtered.replaceAll("&quot;", "\"");
		
		return filtered;
	}
	
	//This method does not work reliably.
//	private static String determineDateOnTwitch(String input) {
//		Calendar cal = Calendar.getInstance();
//		int date = cal.get(Calendar.DAY_OF_MONTH);
//		String output = "";
//		
//		if(input.endsWith(" days ago")) {
//			int thisMonth = cal.get(Calendar.MONTH);
//			int daysAgo = Integer.parseInt(input.substring(0, input.indexOf(" days ago")));
//			int diff = date - daysAgo;
//			
//			if(diff < 1) {
//				int lastMonth = cal.get(Calendar.MONTH);
//				YearMonth yearMonthObject = YearMonth.of(cal.get(Calendar.YEAR), lastMonth);
//				int daysInLastMonth = yearMonthObject.lengthOfMonth();
//				
//				diff = Math.abs(diff);
//				output = new DateFormatSymbols().getMonths()[lastMonth-1];
//				output += " " + (daysInLastMonth - diff);
//				output += ", " + cal.get(Calendar.YEAR);
//				
//			} else {
//				output = new DateFormatSymbols().getMonths()[thisMonth];
//				output += " " + diff;
//				output += ", " + cal.get(Calendar.YEAR);
//			}
//		} else {
//			output = input + " [FIX ME]";
//		}
//		
//		return output;
//	}
}
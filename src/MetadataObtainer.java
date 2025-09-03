import java.util.Scanner;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class MetadataObtainer {
	private static final String FETCH_ERROR_PREFIX 		= "ERROR FETCHING HTML: ";
	private static final String YOUTUBE_PREFIX 			= "https://youtube.com/watch?v=";
	private static final String YOUTUBE_PREFIX_W 		= "https://www.youtube.com/watch?v=";
	private static final String YOUTUBE_PREFIX_ABBR 	= "https://youtu.be/";
	private static final String YOUTUBE_PLAYLIST_TOKEN	= "youtube.com/playlist?list=";
	private static final String YOUTUBE_SHORT_TOKEN		= "youtube.com/shorts";
	private static final String YOUTUBE_LIVE_TOKEN		= "https://youtube.com/live/";
	private static final String YOUTUBE_LIVE_TOKEN_W	= "https://www.youtube.com/live/";
	private static final String YOUTUBE_CHAN_TOKEN_W	= "https://www.youtube.com/@";
	private static final String TWITCH_PREFIX_W			= "https://www.twitch.tv/videos/";
	private static final String TWITCH_PREFIX_MOB		= "https://m.twitch.tv/videos/";
	private static final String VIMEO_PREFIX			= "https://vimeo.com/";
	private static final String ODYSEE_PREFIX			= "https://odysee.com/";
	private static final String DAILYMOTION_PREFIX_W 	= "https://www.dailymotion.com/video/";
	private static final String DAILYMOTION_PREFIX		= "https://dailymotion.com/video/";
	private static final String DAILYMOTION_PREFIX_MOB 	= "https://m.dailymotion.com/video/";
	private static final String BITCHUTE_PREFIX_W		= "https://www.bitchute.com/video/";
	private static final String BITCHUTE_PREFIX			= "https://bitchute.com/video/";
	private static final String RUMBLE_PREFIX			= "https://rumble.com/";
	private static final String PODBEAN_TOKEN			= "podbean.com/e/";
	private static final int	MAX_LEN_TITLE			= 125;
	
	private Optional<String> atTime;
	private String urlStr;
	private String html;
	private boolean isSupported;
	
	public MetadataObtainer(String urlStr) {
		this.atTime = Optional.empty();
		this.urlStr = sanitize(urlStr);
		this.isSupported = isSupported(urlStr);
	
		if (isSupported) {
			this.html = fetchHtml(this.urlStr);
		} else {
			this.html = FETCH_ERROR_PREFIX;
		}
	}
	
	public static void main(String[] args) {
//		System.out.println(fetchHtml("https://odysee.com/win11:6d73df3083e0f634b18f54521763184b47980d8a"));
		final String URL = "https://geruhmyuh.podbean.com/e/god-s-sovereignty/";
		MetadataObtainer o = new MetadataObtainer(URL);
		System.out.println("URL provided: [" + URL + "]");
		System.out.println("Is supported: [" + isSupported(URL) + "]");
		System.out.println("Title       : [" + o.getTitle() + "]");
		System.out.println("Date        : [" + o.getDate() + "]");
		System.out.println("Channel     : [" + o.getChannel() + "]");
		System.out.println("Time        : [" + o.getTime() + "]");
	}
	
	public static boolean isSupported(String urlStr) {
		boolean supported = false;

		if (urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W)
				|| urlStr.contains(YOUTUBE_PLAYLIST_TOKEN) || urlStr.startsWith(YOUTUBE_PREFIX_ABBR)
				|| urlStr.contains(YOUTUBE_SHORT_TOKEN) || urlStr.contains(YOUTUBE_LIVE_TOKEN)
				|| urlStr.startsWith(YOUTUBE_CHAN_TOKEN_W) || urlStr.contains(YOUTUBE_LIVE_TOKEN_W)
				|| urlStr.startsWith(TWITCH_PREFIX_W) || urlStr.startsWith(TWITCH_PREFIX_MOB)
				|| urlStr.startsWith(VIMEO_PREFIX) || urlStr.startsWith(ODYSEE_PREFIX)
				|| urlStr.startsWith(DAILYMOTION_PREFIX_W) || urlStr.startsWith(DAILYMOTION_PREFIX)
				|| urlStr.startsWith(DAILYMOTION_PREFIX_MOB) || urlStr.startsWith(BITCHUTE_PREFIX)
				|| urlStr.startsWith(BITCHUTE_PREFIX_W) || urlStr.startsWith(RUMBLE_PREFIX)
				|| urlStr.contains(PODBEAN_TOKEN)) {

			supported = true;
		}
		
		return supported;
	}
	
	public boolean isSupported() {
		return isSupported;
	}
	
	public boolean isUrlError() {
		boolean error = false;
		
		if (html.startsWith(FETCH_ERROR_PREFIX)) {
			error = true;
		}
		
		return error;
	}
	
	public String getTitle() {
		String title = urlStr;
		
		if (!isUrlError()) {
			//YouTube Regular Links
			if (urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W) || urlStr.contains(YOUTUBE_SHORT_TOKEN)) {
//				final String WATCH_TAG = "/watch?v=";
				String prefix = "content=\"" + urlStr.trim() + "\"><meta property=\"og:title\" content=\"";
				String suffix = "\"><meta property=\"og:image\" content=\"";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);

				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				} 
				
				if (title.length() > MAX_LEN_TITLE) { 
					prefix = "</title><meta name=\"title\" content=\"";
					suffix = "\"><meta name=\"description\"";
					begin = html.indexOf(prefix) + prefix.length();
					end = html.indexOf(suffix, begin);
					
					if (begin != -1 && end != -1) {
						title = html.substring(begin, end);
						title = filterEscapeChars(title);
					}
				}
			} else if (urlStr.startsWith(YOUTUBE_CHAN_TOKEN_W)) {
				title = urlStr.substring(urlStr.indexOf("@") + 1);
				
				//remove trailing URL artifacts if there are any
				if (title.contains("/")) {
					title = title.substring(0, title.indexOf("/"));
				}
			} else if (urlStr.contains(YOUTUBE_PLAYLIST_TOKEN)) {
				String prefix = "property=\"og:title\" content=\"";
				String suffix = "\"><meta property=\"og:description\"";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);

				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
			//Twitch
			} else if(urlStr.startsWith(TWITCH_PREFIX_MOB)) {
				String prefix = "\"/><meta property=\"og:title\" content=\"";
				String suffix = " on Twitch\"/><meta ";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					//using lastIndexOf doesn't work because reasons...
					end -= 2;

					if (begin != -1 && end != -1) {
						while (html.substring(end, end + 3).equals(" - ") == false) {
							end--;
						}

						title = html.substring(begin, end);
						title = filterEscapeChars(title);
					}
				}
			//Vimeo
			} else if (urlStr.startsWith(VIMEO_PREFIX)) {
				String prefix = "<meta property=\"og:title\" content=\"";
				String suffix = "\">";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
			//Odysee
			} else if (urlStr.startsWith(ODYSEE_PREFIX)) {
				String prefix = "<meta charset=\"utf8\"/><title>";
				String suffix = "</title>";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
			//Dailymotion
			} else if (urlStr.startsWith(DAILYMOTION_PREFIX) || urlStr.startsWith(DAILYMOTION_PREFIX_W)
					|| urlStr.startsWith(DAILYMOTION_PREFIX_MOB)) {
				
				String prefix = "{\"title\":\"";
				String suffix = "\",";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
			//Bitchute
			} else if(urlStr.startsWith(BITCHUTE_PREFIX) || urlStr.startsWith(BITCHUTE_PREFIX_W)) {
				String prefix = "<title>";
				String suffix = "</title>";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title).trim();
				}
			//Rumble
			} else if (urlStr.startsWith(RUMBLE_PREFIX)) {
				String prefix = "title=\"";
				String suffix = "\" type";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
			//Podbean
			} else if (urlStr.contains(PODBEAN_TOKEN)) {
				String prefix = "<title>";
				String suffix = "</title>";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
				}
			}
		}
		
		if (title.length() > MAX_LEN_TITLE) {
			title = urlStr;
		}
		
		return title;
	}
	
	public String getChannel() {
		String channel = "";
		
		if (!isUrlError()) {
			//YouTube
			if (urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W) || urlStr.startsWith(YOUTUBE_PREFIX_ABBR) || urlStr.contains(YOUTUBE_SHORT_TOKEN)) {
				String prefix = "{\"label\":\"Subscribe to ";
				String suffix = ".\"}},";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);

				if (begin == -1 || end == -1) {
					prefix = "<link itemprop=\"url\" href=\"http://www.youtube.com/channel";
					suffix = "\">";
					begin = html.indexOf(prefix) + prefix.length();

					prefix = "\"><link itemprop=\"name\" content=\"";
					begin = html.indexOf(prefix) + prefix.length();
					end = html.indexOf(suffix, begin);
				}

				if (begin == -1 || end == -1) {
					prefix = "\"title\":{\"simpleText\":\"Mix - ";
					suffix = "\"}";
					begin = html.indexOf(prefix) + prefix.length();
					end = html.indexOf(suffix, begin);
				}

				channel = html.substring(begin, end);
				channel = filterEscapeChars(channel);

				channel += " on YouTube";
			} else if (urlStr.contains(YOUTUBE_PLAYLIST_TOKEN)) {
				String prefix = "\"shortBylineText\":{\"runs\":[{\"text\":\"";
				String suffix = "\",";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);

				if (begin != -1 && end != -1) {
					channel = html.substring(begin, end);
					channel = filterEscapeChars(channel);
				}
				
				channel += " on YouTube";
			} else if (urlStr.startsWith(YOUTUBE_CHAN_TOKEN_W)) {
				channel = getTitle() + " on YouTube";
			//Twitch
			} else if (urlStr.startsWith(TWITCH_PREFIX_MOB)) {
				String prefix = "name=\"description\"/><meta name=\"title\" content=\"";
				String suffix = " on Twitch\"/><meta ";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					channel = html.substring(begin, end);
					//using lastIndexOf doesn't work because reasons...
					begin = end - 2;

					if (begin != -1 && end != -1) {
						while (html.substring(begin, begin + 3).equals(" - ") == false) {
							begin--;
						}

						begin += 3;
						channel = html.substring(begin, end);
						channel = filterEscapeChars(channel);
					}
				}
				
				if (channel.trim().equals("")) {
					prefix = "<meta property=\"og:type\" content=\"video.other\"/><meta content=\"";
					suffix = " went live on Twitch.";
					begin = html.indexOf(prefix) + prefix.length();
					end = html.indexOf(suffix, begin);
					
					if (begin != -1 && end != -1) {
						channel = html.substring(begin, end);
						channel = filterEscapeChars(channel);
					}
				}
				
				channel += " on Twitch";
			//Vimeo
			} else if (urlStr.startsWith(VIMEO_PREFIX)) {
				channel += "On Vimeo"; //Channel/user name is rendered via JavaScript
			//Odysee
			} else if (urlStr.startsWith(ODYSEE_PREFIX)) {
				if (urlStr.contains("@")) {
					String prefix = "@";
					String suffix = ":";
					int begin = urlStr.indexOf(prefix);
					int end = urlStr.indexOf(suffix, begin);

					if (begin != -1 && end != -1) {
						channel = urlStr.substring(begin, end);
						channel = filterEscapeChars(channel);
					}
				} else {
					String prefix = "content=\"https://odysee.com/@";
					String suffix = ":";
					int begin = html.lastIndexOf(prefix) + prefix.length() - 1;
					int end = html.indexOf(suffix, begin);

					if (begin != -1 && end != -1) {
						channel = html.substring(begin, end);
						channel = filterEscapeChars(channel);
					}
					
					if (!channel.contains("@") || channel.length() > 150) {
						channel = "Anonymous";
					}
				}

				channel += " on Odysee";
			//Dailymotion
			} else if (urlStr.startsWith(DAILYMOTION_PREFIX) || urlStr.startsWith(DAILYMOTION_PREFIX_W)
					|| urlStr.startsWith(DAILYMOTION_PREFIX_MOB)) {
				
				String prefix = "channel\":\"";
				String suffix = "\"";
				int begin = html.lastIndexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);

				if (begin != -1 && end != -1) {
					channel = html.substring(begin, end);
				}
				
				channel += " on Dailymotion";
			//Bitchute
			} else if (urlStr.startsWith(BITCHUTE_PREFIX) || urlStr.startsWith(BITCHUTE_PREFIX_W)) {
				channel = "On BITCHUTE";
			//Rumble
			} else if (urlStr.startsWith(RUMBLE_PREFIX)) {
				String prefix = "data-title=\"";
				String suffix = "\"";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					channel = html.substring(begin, end);
					channel = filterEscapeChars(channel);
				}
				
				channel += " on Rumble";
			//Podbean
			} else if (urlStr.contains(PODBEAN_TOKEN)) {
				String prefix = "://";
				String suffix = ".podbean";
				int begin = urlStr.indexOf(prefix) + prefix.length();
				int end = urlStr.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					channel = urlStr.substring(begin, end);
				}
				
				channel += " on PodBean";
			}
		}
		
		if (channel.length() > 75) {
			channel = "";
		}
		
		return channel;
	}
	
	public String getDate() {
		String date = "";
		
		if (!isUrlError()) {
			//YouTube
			if (urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W) || urlStr.startsWith(YOUTUBE_PREFIX_ABBR)) {
				String prefix = "\"dateText\":{\"simpleText\":\"";
				String suffix = "\"}";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					date = html.substring(begin, end);
				}
			} else if (urlStr.contains(YOUTUBE_PLAYLIST_TOKEN)) {
				date = "[playlist]";
			} else if (urlStr.contains(YOUTUBE_SHORT_TOKEN)) {
				String prefix = "\"publishDate\":{\"simpleText\":\"";
				String suffix = "\"},\"";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					date = html.substring(begin, end);
				}
			} else if (urlStr.startsWith(YOUTUBE_CHAN_TOKEN_W)) {
				String prefix = "\"";
				String suffix = "subscribers\"}";
				int end = html.indexOf(suffix);
				int begin = end;
				boolean foundBegin = false;
				
				for (int i = 0; i < 25; i++) {
					if (html.charAt(--begin) == prefix.toCharArray()[0]) {
						foundBegin = true;
						begin++;
						break;
					}
				}
				
				if (foundBegin) {
					date = html.substring(begin, end);
					date += "Subscribers";
					
					suffix = "videos\",\"styleRuns\"";
					end = html.indexOf(suffix, begin);
					begin = end;
					foundBegin = false;
					
					for (int i = 0; i < 25; i++) {
						if (html.charAt(--begin) == '"') {
							foundBegin = true;
							begin++;
							break;
						}
					}
					
					if (foundBegin) {
						date += " ~ " + html.substring(begin, end);
						date += "Videos";
					}
				}
			//Vimeo
			} else if (urlStr.startsWith(VIMEO_PREFIX)) {
				String prefix = "<meta property=\"og:updated_time\" content=\"";
				String suffix = "\">";
				String htmlSubstr = "";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					date = html.substring(begin, end);
					date = date.substring(0, date.indexOf("T"));
				} else {
					prefix = "<span class=\"clip_info-time\"><time datetime=\"";
					
					htmlSubstr = html.substring(html.indexOf(prefix) + prefix.length());
					
					prefix = "\" title=\"";
					
					begin = htmlSubstr.indexOf(prefix) + prefix.length();
					end = htmlSubstr.indexOf(suffix, begin);
					
					if (begin != -1 && end != -1) {
						date = html.substring(begin, end);
					}
				}
			//Odysee
			} else if (urlStr.startsWith(ODYSEE_PREFIX)) {
				String prefix = "\"uploadDate\": \"";
				String suffix = "\",";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					date = html.substring(begin, end);
				}
				
				date = Instant.parse(date).atZone(ZoneId.of("America/Montreal"))
						.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(Locale.US));
				date = date.replaceAll("Eastern Daylight Time", "EDT");
				date = date.replaceAll("Eastern Standard Time", "EST");
			//Dailymotion
			} else if (urlStr.startsWith(DAILYMOTION_PREFIX) || urlStr.startsWith(DAILYMOTION_PREFIX_W)
					|| urlStr.startsWith(DAILYMOTION_PREFIX_MOB)) {
				
				String prefix = "\"created_time\":";
				String suffix = ",\"";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					date = html.substring(begin, end);
					date = parseUnixDateAndTime(date);
				}
			//Bitchute
			} else if (urlStr.startsWith(BITCHUTE_PREFIX) || urlStr.startsWith(BITCHUTE_PREFIX_W)) {
				date = "--";
			//Rumble
			} else if (urlStr.startsWith(RUMBLE_PREFIX)) {
				final String STREAMED_INDICATOR = "</clipPath></svg>			Streamed on:			<time datetime=\"";
				final String STREAMING_INDICATOR = "/clipPath></svg>				Streaming now\n"
						+ "									</div>";
				
				//Stream on Rumble have the date located in a different tag
				if (html.contains(STREAMED_INDICATOR)) {
					String prefix = STREAMED_INDICATOR;
					String suffix = "\"";
					int begin = html.indexOf(prefix) + prefix.length();
					int end = html.indexOf(suffix, begin);

					if (begin != -1 && end != -1) {
						date = html.substring(begin, end);
						date = date.replaceAll("\n", "");
						
						//format: 2024-09-21T02:55:15+00:00
						date = date.substring(0, 10) + " " + date.substring(11, 19);
						
						date = "Streamed on " + date;
					}
				} else if (html.contains(STREAMING_INDICATOR)) {
					date = "Streaming Now";
				} else {
					String prefix = "</clipPath></svg>							<div title=\"";
					String suffix = "\">";
					int begin = html.indexOf(prefix) + prefix.length();
					int end = html.indexOf(suffix, begin);

					if (begin != -1 && end != -1) {
						date = html.substring(begin, end);
						date = date.replaceAll("\n", "");
					}
				}
			//Podbean
			} else if (urlStr.contains(PODBEAN_TOKEN)) {
				String prefix = "class=\"episode-date\">";
				String suffix = "</span>";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					date = html.substring(begin, end);
				}
			}
		}
		
		if (date.length() > 100) {
			date = "";
		}
		
		return date;
	}
	
	public String getTime() {
		String time = "";
		int seconds = 0;
		
		if (!isUrlError()) {
			//YouTube
			if (urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W) || urlStr.startsWith(YOUTUBE_PREFIX_ABBR) || urlStr.contains(YOUTUBE_SHORT_TOKEN)) {
				//get URL for the embedded YouTube video
				String prefix = "<meta property=\"og:video:url\" content=\"";
				String suffix = "\">";
				String embedded = null;
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					embedded = html.substring(begin, end);
					
					//get the embedded video html, which has a category for video seconds
					String embeddedHtml = fetchHtml(embedded);
					prefix = "videoDurationSeconds";
					suffix = "\\\"";
					begin = embeddedHtml.indexOf(prefix) + 5 + prefix.length();
					end = embeddedHtml.indexOf(suffix, begin);
					
					if (begin != -1 && end != -1) {
						try {
							seconds = Integer.parseInt(embeddedHtml.substring(begin, end));
						} catch (Exception e) {
							seconds = -1;
						}

						time = convertSecondsToTimeStr(seconds);
					}
				}

				if (atTime.isPresent()) {
					time += " (in progress " + convertSecondsToTimeStr(Integer.parseInt(atTime.get())) + ")";
				}
				
				if (urlStr.contains(YOUTUBE_SHORT_TOKEN)) {
					time += " (Short)";
				}
			//Odysee and Twitch (not 100% Reliable for Twitch)
			} else if (urlStr.startsWith(ODYSEE_PREFIX) || urlStr.startsWith(TWITCH_PREFIX_MOB)) {
				String prefix = "<meta property=\"og:video:duration\" content=\"";
				String suffix = "\"/";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);

				if (begin != -1 && end != -1) {
					time = html.substring(begin, end);
				}
				
				try {
					seconds = Integer.parseInt(time);
				} catch (Exception e) {
					seconds = -1;
				}
				
				time = convertSecondsToTimeStr(seconds);
				
				if (atTime.isPresent() && urlStr.startsWith(TWITCH_PREFIX_MOB)) {
					String progress = atTime.get();

					progress = progress.replace("h", ":");
					progress = progress.replace("m", ":");
					progress = progress.replace("s", "");

					time += " (in progress " + progress + ")";
				} else if (atTime.isPresent() && urlStr.startsWith(ODYSEE_PREFIX)) {
					try {
						time += " (in progress " + convertSecondsToTimeStr(Integer.parseInt(atTime.get())) + ")";
					} catch (Exception e) {
						time += " (in progress)";
					}
				}
			//Vimeo (only gets the progress of the video)
			} else if (urlStr.startsWith(VIMEO_PREFIX)) {
				final String TIME_Q_PARAM = "#t=";
				
				if (urlStr.contains(TIME_Q_PARAM)) {
					time = urlStr.substring(urlStr.indexOf(TIME_Q_PARAM) + TIME_Q_PARAM.length(), urlStr.lastIndexOf("s"));
					
					try {
						time = " In progress at " + convertSecondsToTimeStr(Integer.parseInt(time));
					} catch(Exception e) {
						time = "Video is in progress.";
					}
				}
			} else if (urlStr.startsWith(DAILYMOTION_PREFIX) || urlStr.startsWith(DAILYMOTION_PREFIX_W)
					|| urlStr.startsWith(DAILYMOTION_PREFIX_MOB)) {
				
				String prefix = "\"duration\":";
				String suffix = "}";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);

				if (begin != -1 && end != -1) {
					try {
						time = html.substring(begin, end);
						seconds = Integer.parseInt(time);
					} catch (Exception e) {
						seconds = -1;
					}

					if (seconds > -1) {
						time = convertSecondsToTimeStr(seconds);
					}
				}
			//Podbean
			} else if (urlStr.contains(PODBEAN_TOKEN)) {
				String prefix = "duration\\\":";
				String suffix = ",";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					time = html.substring(begin, end);
					
					try {
						time = html.substring(begin, end);
						seconds = Integer.parseInt(time);
					} catch (Exception e) {
						seconds = -1;
					}

					if (seconds > -1) {
						time = convertSecondsToTimeStr(seconds);
					}
				}
			}
		}
		
		return time;
	}
	
	private String convertSecondsToTimeStr(int seconds) {
		String time = "";
		int minutes = 0;
		int hours = 0;
		
		if (seconds > 0) {
			if (seconds >= 60) {
				minutes = seconds/60;
				seconds = seconds%60;
				
				if (minutes >= 60) {
					hours = minutes / 60;
					minutes = minutes % 60;
				}
				
				if (hours > 0) {
					time = hours + ":";
				}

				if (minutes >= 0 && minutes < 10) {
					time += "0";
				}

				time += minutes + ":";

				if (seconds >= 0 && seconds < 10) {
					time += "0";
				}

				time += seconds;
			} else {
				if (seconds >= 10) {
					time = "00:" + seconds;
				} else {
					time = "00:0" + seconds;
				}
			}
		}
		
		return time;
	}
	
	private static String fetchHtml(String url) {
		String content = null;
		HttpURLConnection connection = null;
		
		try {
			//Daily Motion has a public API for getting title, date, and channel. The DOM of the actual web page only renders these in JavaScript.
			if (url.startsWith(DAILYMOTION_PREFIX) || url.startsWith(DAILYMOTION_PREFIX_MOB) || url.startsWith(DAILYMOTION_PREFIX_W)) {
				if (url.contains("?")) {
					url = url.substring(0, url.indexOf("?"));
				} else if (url.contains("&")) {
					url = url.substring(0, url.indexOf("&"));
				}
				
				url = "https://api.dailymotion.com/" + url.substring(url.indexOf("video")) + "?fields=title,created_time,channel,duration";
			}
			
			connection =  (HttpURLConnection) new URL(url).openConnection();
			
			//This user agent screws up YouTube for some reason.
			if (!url.startsWith(YOUTUBE_PREFIX) && !url.startsWith(YOUTUBE_PREFIX_ABBR) && !url.startsWith(YOUTUBE_PREFIX_W)
					&& !url.contains(YOUTUBE_PLAYLIST_TOKEN) && !url.contains(YOUTUBE_SHORT_TOKEN)) {
				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10.4; en-US; rv:1.9.2.2) Gecko/20100316 Firefox/3.6.2");
//				connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36");
			} 
			
			Scanner scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
			scanner.close();
			
			if (connection.getResponseCode() != 200) {
				System.out.println("HTTP Response for " + url + ": " + connection.getResponseCode() + " " + connection.getResponseMessage());
			}
		} catch (Exception e) {
			String message = "";
			
			if(e.getMessage() != null){
				message = " -- " + e.getMessage();
			}
				
		    content = FETCH_ERROR_PREFIX + e.toString() + message;
		}
		
		return content;
	}
	
	private String sanitize(String urlStr) {
		String sanitized = urlStr;
		
		//https fix
		if (sanitized.startsWith("http") && !sanitized.startsWith("https")) {
			sanitized = sanitized.replaceFirst("http", "https");
		} else if (!sanitized.startsWith("http")) {
			sanitized = "https://" + urlStr;
		}

		//YouTube
		if (sanitized.startsWith(YOUTUBE_PREFIX) || sanitized.startsWith(YOUTUBE_PREFIX_W) || sanitized.startsWith(YOUTUBE_PREFIX_ABBR) 
				|| sanitized.contains(YOUTUBE_PLAYLIST_TOKEN) || sanitized.contains(YOUTUBE_LIVE_TOKEN) || sanitized.contains(YOUTUBE_LIVE_TOKEN_W)) {

			sanitized = sanitizeYoutube(sanitized, false);
		//Twitch
		} else if (sanitized.startsWith(TWITCH_PREFIX_W) || sanitized.startsWith(TWITCH_PREFIX_MOB) || sanitized.startsWith(ODYSEE_PREFIX)) {
			sanitized = sanitizeTwitchAndOdysee(sanitized);
		}
		
		return sanitized;
	}
	
	/*
	 * Remove unwanted metadata from URL to store in DB file. Currently will only support YouTube as of 9/21/24.
	 */
	public Optional<String> sanitizeForStorage(String url) {
		Optional<String> sanitized = Optional.empty();
		
		//YouTube
		if (url.startsWith(YOUTUBE_PREFIX) || url.startsWith(YOUTUBE_PREFIX_W) || url.startsWith(YOUTUBE_PREFIX_ABBR) || url.contains(YOUTUBE_PLAYLIST_TOKEN)) {
			sanitized = Optional.of(sanitizeYoutube(url, true));
		}
		
		return sanitized;
	}
	
	/*
	 * TODO: This method does not account for the possibility of "?" or "&" being in the value itself
	 */
	private Optional<String> findEndOfArgKeyValuePair(String url, String param) {
		Optional<String> restOfUrl = Optional.empty();
		String temp = "";
		int paramLoc = -1;
		int nextIndexQuery = -1;
		int nextIndexParam = -1;
		int endOfKeyValuePair = -1;

		paramLoc = url.indexOf(param) + 1;

		if (paramLoc > 0) {
			temp = url.substring(paramLoc);
			nextIndexQuery = temp.indexOf("?");
			nextIndexParam = temp.indexOf("&");

			if (nextIndexQuery > 0 || nextIndexParam > 0) {
				if (nextIndexParam > 0 && ((nextIndexQuery > 0 && nextIndexParam < nextIndexQuery) || nextIndexQuery <= 0)) {
					endOfKeyValuePair = nextIndexParam;
				} else {
					endOfKeyValuePair = nextIndexQuery;
				}
			}
			
			if (endOfKeyValuePair > -1) {
				restOfUrl = Optional.of(temp.substring(endOfKeyValuePair));
			}
		}

		return restOfUrl;
	}
	
	/*
	 * boolean keepCritialData : useful for wanting to keep time param, but get rid of preview & list data, for example
	 */
	private String sanitizeYoutube(String urlStr, boolean keepCritialData) {
		final String TIME_PARAM_1 = "&t=";
		final String TIME_PARAM_2 = "?t=";
		final String LIST_PARAM_1 = "&list=";
		final String LIST_PARAM_2 = "?list=";
		final String INDX_PARAM_1 = "&index=";
		final String INDX_PARAM_2 = "&index=";
		final String DIS_POLY_PARAM = "&disable_polymer=1";
		final String PIC_PREVIEW_PARAM = "&pp=";
		String sanitized = urlStr;
		
		//convert to non-abbreviated link
		if (sanitized.startsWith(YOUTUBE_PREFIX_ABBR)) {
			sanitized = YOUTUBE_PREFIX_W + sanitized.substring(YOUTUBE_PREFIX_ABBR.length());
		}
		
		if (sanitized.startsWith(YOUTUBE_LIVE_TOKEN)) {
			sanitized = YOUTUBE_PREFIX_W + sanitized.substring(YOUTUBE_LIVE_TOKEN.length());
		} else if (sanitized.startsWith(YOUTUBE_LIVE_TOKEN_W)) {
			sanitized = YOUTUBE_PREFIX_W + sanitized.substring(YOUTUBE_LIVE_TOKEN_W.length());
		}
		
		//remove playlist data if individual video was navigated to from a playlist but is not a playlist link itself
		if (sanitized.contains(YOUTUBE_PLAYLIST_TOKEN) == false) {
			Optional<String> restOfUrl;
			
			if (sanitized.contains(LIST_PARAM_1)) {
				restOfUrl = findEndOfArgKeyValuePair(sanitized, LIST_PARAM_1);
				
				if (restOfUrl.isPresent()) {
					sanitized = sanitized.substring(0, sanitized.indexOf(LIST_PARAM_1)) + restOfUrl.get();
				} else {
					sanitized = sanitized.substring(0, sanitized.indexOf(LIST_PARAM_1));
				}
			} else if (sanitized.contains(LIST_PARAM_2)) {
				restOfUrl = findEndOfArgKeyValuePair(sanitized, LIST_PARAM_2);

				if (restOfUrl.isPresent()) {
					sanitized = sanitized.substring(0, sanitized.indexOf(LIST_PARAM_2)) + restOfUrl.get();
				} else {
					sanitized = sanitized.substring(0, sanitized.indexOf(LIST_PARAM_2));
				}
			}
			
			if (sanitized.contains(INDX_PARAM_1)) {
				restOfUrl = findEndOfArgKeyValuePair(sanitized, INDX_PARAM_1);
				
				if (restOfUrl.isPresent()) {
					sanitized = sanitized.substring(0, sanitized.indexOf(INDX_PARAM_1)) + restOfUrl.get();
				} else {
					sanitized = sanitized.substring(0, sanitized.indexOf(INDX_PARAM_1));
				}
			} else if (sanitized.contains(INDX_PARAM_2)) {
				restOfUrl = findEndOfArgKeyValuePair(sanitized, INDX_PARAM_2);

				if (restOfUrl.isPresent()) {
					sanitized = sanitized.substring(0, sanitized.indexOf(INDX_PARAM_2)) + restOfUrl.get();
				} else {
					sanitized = sanitized.substring(0, sanitized.indexOf(INDX_PARAM_2));
				}
			}
		}
		
		//remove polymer disable
		if (sanitized.contains(DIS_POLY_PARAM)) {
			sanitized = sanitized.replaceAll(DIS_POLY_PARAM, "");
		}
		
		//remove picture preview param
		if (sanitized.contains(PIC_PREVIEW_PARAM)) {
			int picPreParamInd = sanitized.indexOf(PIC_PREVIEW_PARAM);
			String trailingStr = sanitized.substring(picPreParamInd);
			
			//keep other query parameters in case they're start time or something else useful.
			if (trailingStr.contains("?") || trailingStr.substring(1).contains("&")) {
				//determine the innermost parameter beginning
				int questionInd = trailingStr.indexOf("?", 1);
				int ampersandInd = trailingStr.indexOf("&", 1);
				
				//if str is not present, set largest to give appearance of being outermost.
				questionInd = questionInd == -1 ? trailingStr.length() : questionInd;
				ampersandInd = ampersandInd == -1 ? trailingStr.length() : ampersandInd;
				
				if (questionInd < ampersandInd) {
					sanitized = sanitized.substring(0, picPreParamInd) + trailingStr.substring(questionInd);
				} else if (ampersandInd < questionInd) {
					sanitized = sanitized.substring(0, picPreParamInd) + trailingStr.substring(ampersandInd);
				} else {
					//TODO: Should never be able to make it into this block. Remove in the future once stress testing is complete. -DJM 1/9/24 
					System.err.println("ERROR: Logic for determining whether to split string at \"?\" or \"&\" is not sound.");
				}
			} else {
				sanitized = sanitized.substring(0, picPreParamInd);
			}
		}
		
		//Oddly YouTube provides links with the progress time stamp, yet when going to them in the browser, it chops the timestamp off
		//and redirects to the regular URL when using the question mark. This began happening recently, but using the ampersand seems to
		//work fine, so we're subbing it in here on our end. -9/30/24
		sanitized = sanitized.replace(TIME_PARAM_2, TIME_PARAM_1);
		
		if (keepCritialData == false) {
			//remove time tags
			if (sanitized.contains(TIME_PARAM_1)) {
				this.atTime = Optional.of(sanitized.substring(sanitized.indexOf(TIME_PARAM_1) + TIME_PARAM_1.length()));
				sanitized = sanitized.substring(0, sanitized.indexOf(TIME_PARAM_1));
			} 
		}
		
		return sanitized;
	}

	private String sanitizeTwitchAndOdysee(String urlStr) {
		final String TIME_Q_PARAM = "?t=";
		String sanitized = urlStr;
		
		//normal twitch video id e.g. https://www.twitch.tv/videos/997396590
		//must convert to mobile to get data
		if (urlStr.startsWith(TWITCH_PREFIX_W)) {
			sanitized = urlStr.replaceFirst(TWITCH_PREFIX_W, TWITCH_PREFIX_MOB);

			if (sanitized.contains(TIME_Q_PARAM)) {
				atTime = Optional.of(sanitized.substring(sanitized.indexOf(TIME_Q_PARAM) + TIME_Q_PARAM.length(), sanitized.lastIndexOf("s") + 1));
			}
		} else if (urlStr.contains(ODYSEE_PREFIX)) {
			if (urlStr.contains(TIME_Q_PARAM)) {
				atTime = Optional.of(sanitized.substring(sanitized.indexOf(TIME_Q_PARAM) + TIME_Q_PARAM.length()));
			}
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
		filtered = filtered.replaceAll("&#039;", "'");
		
		return filtered;
	}
	
	private static String parseUnixDateAndTime(String unixTime) {
        long milliseconds = Long.parseLong(unixTime) * 1000;
        Instant instant = Instant.ofEpochMilli(milliseconds);
//        LocalDateTime utcDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
//        String formattedDate = utcDateTime.format(formatter);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("America/New_York"));
        String formattedLocalDate = localDateTime.format(formatter);
        
        return formattedLocalDate;
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
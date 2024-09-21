import java.util.Scanner;
import java.net.URLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.Optional;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class MetadataObtainer {
	private static final String FETCH_ERROR_PREFIX 		= "ERROR FETCHING HTML: ";
	private static final String YOUTUBE_PREFIX 			= "https://youtube.com/watch?v=";
	private static final String YOUTUBE_PREFIX_W 		= "https://www.youtube.com/watch?v=";
	private static final String YOUTUBE_PREFIX_ABBR 	= "https://youtu.be/";
	private static final String YOUTUBE_PLAYLIST_TOKEN	= "youtube.com/playlist?list=";
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
	
	public static void main(String[] args){
//		System.out.println(fetchHtml("https://odysee.com/win11:6d73df3083e0f634b18f54521763184b47980d8a"));
		MetadataObtainer o = new MetadataObtainer("https://www.youtube.com/playlist?list=PL8iRGqXdAya_lOQlJXKjoFAPgG7UUOrh2");
		System.out.println(o.getTitle());
		System.out.println(o.getDate());
		System.out.println(o.getChannel());
		System.out.println(o.getTime());
	}
	
	public static boolean isSupported(String urlStr) {
		boolean supported = false;

		if (urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W)
				|| urlStr.contains(YOUTUBE_PLAYLIST_TOKEN) || urlStr.startsWith(YOUTUBE_PREFIX_ABBR)
				|| urlStr.startsWith(TWITCH_PREFIX_W) || urlStr.startsWith(TWITCH_PREFIX_MOB)
				|| urlStr.startsWith(VIMEO_PREFIX) || urlStr.startsWith(ODYSEE_PREFIX)
				|| urlStr.startsWith(DAILYMOTION_PREFIX_W) || urlStr.startsWith(DAILYMOTION_PREFIX)
				|| urlStr.startsWith(DAILYMOTION_PREFIX_MOB) || urlStr.startsWith(BITCHUTE_PREFIX)
				|| urlStr.startsWith(BITCHUTE_PREFIX_W) || urlStr.startsWith(RUMBLE_PREFIX)) {

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
		String title = urlStr;
		
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
				String prefix = "<title>";
				String suffix = "</title>";
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
			//Vimeo
			} else if(urlStr.startsWith(VIMEO_PREFIX)) {
				String prefix = "<meta property=\"og:title\" content=\"";
				String suffix = "\">";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
			//Odysee
			} else if(urlStr.startsWith(ODYSEE_PREFIX)) {
				String prefix = "<meta charset=\"utf8\"/><title>";
				String suffix = "</title>";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
			//Dailymotion
			} else if(urlStr.startsWith(DAILYMOTION_PREFIX) || urlStr.startsWith(DAILYMOTION_PREFIX_W)
					|| urlStr.startsWith(DAILYMOTION_PREFIX_MOB)) {
				
				String prefix = "<meta property=\"og:title\" content=\"";
				String suffix = " - video Dailymotion\"  />";
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
					title = filterEscapeChars(title);
				}
			//Rumble
			} else if(urlStr.startsWith(RUMBLE_PREFIX)) {
				String prefix = "title=\"";
				String suffix = "\" type";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					title = html.substring(begin, end);
					title = filterEscapeChars(title);
				}
			}
		}
		
		if(title.length() > 125) {
			title = urlStr;
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
			} else if (urlStr.contains(YOUTUBE_PLAYLIST_TOKEN)) {
				String prefix = "\"ownerText\":{\"runs\":[{\"text\":\"";
				String suffix = "\",\"navigationEn";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);

				if (begin != -1 && end != -1) {
					channel = html.substring(begin, end);
					channel = filterEscapeChars(channel);
				}
				
				channel += " on YouTube";
			//Twitch
			} else if(urlStr.startsWith(TWITCH_PREFIX_MOB)) {
				String prefix = "<title>";
				String suffix = "</title>";
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
				
				//This is already contained in the title on Twitch
//				channel += " on Twitch";
			//Vimeo
			} else if(urlStr.startsWith(VIMEO_PREFIX)) {
				String prefix = "<span class=\"userlink userlink--md\">";
				String suffix = "</a>";
				String htmlSubstr = html.substring(html.indexOf(prefix) + prefix.length());
				
				prefix = "\">";
				
				int begin = htmlSubstr.indexOf(prefix) + prefix.length();
				int end = htmlSubstr.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					channel = htmlSubstr.substring(begin, end);
					channel = filterEscapeChars(channel);
				}
				
				channel += " on Vimeo";
			//Odysee
			} else if(urlStr.startsWith(ODYSEE_PREFIX)) {
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
					
					if(!channel.contains("@") || channel.length() > 150) {
						channel = "Anonymous";
					}
				}

				channel += " on Odysee";
			//Dailymotion
			} else if(urlStr.startsWith(DAILYMOTION_PREFIX) || urlStr.startsWith(DAILYMOTION_PREFIX_W)
					|| urlStr.startsWith(DAILYMOTION_PREFIX_MOB)) {
				
				String prefix = "<meta property=\"video:director\" content=\"https://www.dailymotion.com/";
				String suffix = "\"  />";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					channel = html.substring(begin, end);
					channel = filterEscapeChars(channel);
				}
				
				channel += " on Dailymotion";
			//Bitchute
			} else if(urlStr.startsWith(BITCHUTE_PREFIX) || urlStr.startsWith(BITCHUTE_PREFIX_W)) {
				String prefix = "<a href=\"/channel/";
				String suffix = "/";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					channel = html.substring(begin, end);
					channel = filterEscapeChars(channel);
				}
				
				channel += " on BITCHUTE";
			//Rumble
			} else if(urlStr.startsWith(RUMBLE_PREFIX)) {
				String prefix = "data-title=\"";
				String suffix = "\"";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					channel = html.substring(begin, end);
					channel = filterEscapeChars(channel);
				}
				
				channel += " on Rumble";
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
			} else if(urlStr.contains(YOUTUBE_PLAYLIST_TOKEN)) {
				date = "[playlist]";
			//Vimeo
			} else if(urlStr.startsWith(VIMEO_PREFIX)) {
				String prefix = "<span class=\"clip_info-time\"><time datetime=\"";
				String suffix = "\">";
				String htmlSubstr = html.substring(html.indexOf(prefix) + prefix.length());
				
				prefix = "\" title=\"";
				
				int begin = htmlSubstr.indexOf(prefix) + prefix.length();
				int end = htmlSubstr.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					date = htmlSubstr.substring(begin, end);
				}
			//Odysee
			} else if(urlStr.startsWith(ODYSEE_PREFIX)) {
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
			} else if(urlStr.startsWith(DAILYMOTION_PREFIX) || urlStr.startsWith(DAILYMOTION_PREFIX_W)
					|| urlStr.startsWith(DAILYMOTION_PREFIX_MOB)) {
				
				String prefix = "<meta property=\"video:release_date\" content=\"";
				String suffix = "T";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					date = html.substring(begin, end);
				}
			//Bitchute
			} else if(urlStr.startsWith(BITCHUTE_PREFIX) || urlStr.startsWith(BITCHUTE_PREFIX_W)) {
				String prefix = "<div class=\"video-publish-date\">";
				String suffix = "</div>";
				int begin = html.indexOf(prefix) + prefix.length();
				int end = html.indexOf(suffix, begin);
				
				if (begin != -1 && end != -1) {
					date = html.substring(begin, end);
					date = date.replaceAll("\n", "");
				}
				//Rumble
			} else if(urlStr.startsWith(RUMBLE_PREFIX)) {
				final String STREAM_INDICATOR = "<div class=\"streamed-on\">";
				
				//Stream on Rumble have the date located in a different tag
				if (html.contains(STREAM_INDICATOR)) {
					String prefix = ">";
					String suffix = "</time>";
					int begin = html.indexOf(STREAM_INDICATOR) + STREAM_INDICATOR.length();
					String htmlTrimmed = html.substring(begin);

					begin = htmlTrimmed.indexOf(prefix) + 1;
					int end = htmlTrimmed.indexOf(suffix, begin);

					if (begin != -1 && end != -1) {
						date = "Streamed on " + htmlTrimmed.substring(begin, end).trim();
					}
				} else {
					String prefix = "<div class=\"media-published\" title=\"";
					String suffix = "\">";
					int begin = html.indexOf(prefix) + prefix.length();
					int end = html.indexOf(suffix, begin);

					if (begin != -1 && end != -1) {
						date = html.substring(begin, end);
						date = date.replaceAll("\n", "");
					}
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
		
		if(date.length() > 100) {
			date = "";
		}
		
		return date;
	}
	
	public String getTime() {
		String time = "";
		int seconds = 0;
		
		if(!isUrlError()) {
			//YouTube
			if(urlStr.startsWith(YOUTUBE_PREFIX) || urlStr.startsWith(YOUTUBE_PREFIX_W) || urlStr.startsWith(YOUTUBE_PREFIX_ABBR)) {
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
			//Odysee and Twitch (not 100% Reliable for Twitch)
			} else if(urlStr.startsWith(ODYSEE_PREFIX) || urlStr.startsWith(TWITCH_PREFIX_MOB)) {
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
				
				if(urlStr.contains(TIME_Q_PARAM)) {
					time = urlStr.substring(urlStr.indexOf(TIME_Q_PARAM) + TIME_Q_PARAM.length(), urlStr.lastIndexOf("s"));
					
					try {
						time = " In progress at " + convertSecondsToTimeStr(Integer.parseInt(time));
					} catch(Exception e) {
						time = "Video is in progress.";
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
	
	private String sanitize(String urlStr) {
		String sanitized = urlStr;
		
		//https fix
		if(sanitized.startsWith("http") && !sanitized.startsWith("https")) {
			sanitized = sanitized.replaceFirst("http", "https");
		} else if(!sanitized.startsWith("http")) {
			sanitized = "https://" + urlStr;
		}

		//YouTube
		if(sanitized.startsWith(YOUTUBE_PREFIX) || sanitized.startsWith(YOUTUBE_PREFIX_W) 
		   || sanitized.startsWith(YOUTUBE_PREFIX_ABBR)) {
			
			sanitized = sanitizeYoutube(sanitized);
		//Twitch
		} else if(sanitized.startsWith(TWITCH_PREFIX_W) || sanitized.startsWith(TWITCH_PREFIX_MOB) || sanitized.startsWith(ODYSEE_PREFIX)) {
			sanitized = sanitizeTwitchAndOdysee(sanitized);
		}
		
		return sanitized;
	}
	
	private String sanitizeYoutube(String urlStr) {
		final String TIME_PARAM_1 = "&t=";
		final String TIME_PARAM_2 = "?t=";
		final String LIST_PARAM = "&list=";
		final String DIS_POLY_PARAM = "&disable_polymer=1";
		final String PIC_PREVIEW_PARAM = "&pp=";
		String sanitized = urlStr;
		
		//convert to non-abbreviated link
		if(urlStr.startsWith(YOUTUBE_PREFIX_ABBR)) {
			sanitized = YOUTUBE_PREFIX_W + sanitized.substring(YOUTUBE_PREFIX_ABBR.length());
		}
		
		//remove playlist data if individual video was navigated to from a playlist
		if(urlStr.contains(LIST_PARAM)) {
			sanitized = sanitized.substring(0, sanitized.indexOf(LIST_PARAM));
		}
		
		//remove polymer disable
		if(urlStr.contains(DIS_POLY_PARAM)) {
			sanitized = sanitized.replaceAll(DIS_POLY_PARAM, "");
		}
		
		//remove picture preview param
		if(urlStr.contains(PIC_PREVIEW_PARAM)) {
			int picPreParamInd = urlStr.indexOf(PIC_PREVIEW_PARAM);
			String trailingStr = urlStr.substring(picPreParamInd);
			
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
		
		//remove time tags
		if(urlStr.contains(TIME_PARAM_1)) {
			this.atTime = Optional.of(sanitized.substring(sanitized.indexOf(TIME_PARAM_1) + TIME_PARAM_1.length()));
			sanitized = sanitized.substring(0, sanitized.indexOf(TIME_PARAM_1));
		} else if(urlStr.contains(TIME_PARAM_2)) {
			this.atTime = Optional.of(sanitized.substring(sanitized.indexOf(TIME_PARAM_2) + TIME_PARAM_2.length()));
			sanitized = sanitized.substring(0, sanitized.indexOf(TIME_PARAM_2));
		}
		
		return sanitized;
	}

	private String sanitizeTwitchAndOdysee(String urlStr) {
		final String TIME_Q_PARAM = "?t=";
		String sanitized = urlStr;
		
		//normal twitch video id i.e. https://www.twitch.tv/videos/997396590
		//must convert to mobile to get data
		if(urlStr.startsWith(TWITCH_PREFIX_W)) {
			sanitized = urlStr.replaceFirst(TWITCH_PREFIX_W, TWITCH_PREFIX_MOB);
			
			if(sanitized.contains(TIME_Q_PARAM)) {
				atTime = Optional.of(sanitized.substring(sanitized.indexOf(TIME_Q_PARAM) + TIME_Q_PARAM.length(), sanitized.lastIndexOf("s") + 1));
			}
		} else if(urlStr.contains(ODYSEE_PREFIX)) {
			if(urlStr.contains(TIME_Q_PARAM)) {
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

/*
 * Regression test to ensure that supported URL metadata still gets obtained. Useful for detecting changes in site DOM as well as program bugs.
 */
public class Test {
	private static final boolean EXIT_UPON_FAILURE = false; //if true, will exit testing when a link fails. Setting false can be handy when testing wonky sites.
	
	//YouTube
	private static final String URL_YT_NORMAL 		= "https://www.youtube.com/watch?v=UTosKh0M42o";
	private static final String TYPE_YT_NORMAL 		= "YouTube Normal";
	private static final String TTL_YT_NORMAL 		= "I Bought A $500 Gaming PC From Temu...";
	private static final String DATE_YT_NORMAL 		= "Jul 20, 2024";
	private static final String CHNL_YT_NORMAL 		= "Dawid Does Tech Stuff on YouTube";
	private static final String TIME_YT_NORMAL 		= "10:53";
	
	private static final String URL_YT_SHORT		= "https://www.youtube.com/shorts/Qx6lTY8UnPw";
	private static final String TYPE_YT_SHORT 		= "YouTube Short";
	private static final String TTL_YT_SHORT 		= "Power Outage - An Actual Short";
	private static final String DATE_YT_SHORT 		= "Dec 17, 2022";
	private static final String CHNL_YT_SHORT 		= "Alan Becker on YouTube";
	private static final String TIME_YT_SHORT 		= "00:47 (Short)";
	
	private static final String URL_YT_PLYLST		= "https://www.youtube.com/playlist?list=PLT515qV87IFIcL1LvgMVy_IpbWWkiQaXo";
	private static final String TYPE_YT_PLYLST 		= "YouTube Playlist";
	private static final String TTL_YT_PLYLST 		= "Random Tech Crap";
	private static final String DATE_YT_PLYLST 		= "[playlist]";
	private static final String CHNL_YT_PLYLST 		= "Dawid Does Tech Stuff on YouTube";
	private static final String TIME_YT_PLYLST 		= "";
	
	private static final String URL_YT_LIST_TM		= "https://youtu.be/rRn31oxQARY?list=PLT515qV87IFIcL1LvgMVy_IpbWWkiQaXo&t=34";
	private static final String TYPE_YT_LIST_TM 	= "YouTube From Playlist with Timestamp";
	private static final String TTL_YT_LIST_TM 		= "This Liquid Cooled Mini PC Is WILD";
	private static final String DATE_YT_LIST_TM 	= "Sep 11, 2024";
	private static final String CHNL_YT_LIST_TM 	= "Dawid Does Tech Stuff on YouTube";
	private static final String TIME_YT_LIST_TM 	= "13:52 (in progress 00:34)";
	
	private static final String URL_YT_TIMESTMP		= "https://www.youtube.com/watch?v=rRn31oxQARY&t=32";
	private static final String TYPE_YT_TIMESTMP 	= "YouTube with Timestamp";
	private static final String TTL_YT_TIMESTMP 	= "This Liquid Cooled Mini PC Is WILD";
	private static final String DATE_YT_TIMESTMP 	= "Sep 11, 2024";
	private static final String CHNL_YT_TIMESTMP 	= "Dawid Does Tech Stuff on YouTube";
	private static final String TIME_YT_TIMESTMP 	= "13:52 (in progress 00:32)";
	
	private static final String URL_YT_PREVIEW		= "https://www.youtube.com/watch?v=iRV-XdwRkO4&pp=ygUVZW1waXJlIHN0YXRlIGJ1aWxkaW5n";
	private static final String TYPE_YT_PREVIEW 	= "YouTube with Pic Preview";
	private static final String TTL_YT_PREVIEW 		= "Inside the Empire State Building’s 21st Century Upgrade";
	private static final String DATE_YT_PREVIEW 	= "Dec 9, 2020";
	private static final String CHNL_YT_PREVIEW 	= "The B1M on YouTube";
	private static final String TIME_YT_PREVIEW 	= "06:34";
	
	private static final String URL_YT_CHANNEL		= "https://www.youtube.com/@DaveMcRaeOfficial/videos";
	private static final String TYPE_YT_CHANNEL 	= "YouTube Channels";
	private static final String TTL_YT_CHANNEL 		= "DaveMcRaeOfficial";
	private static final String DATE_YT_CHANNEL 	= "30K Subscribers ~ 1.5K Videos";
	private static final String CHNL_YT_CHANNEL 	= "DaveMcRaeOfficial on YouTube";
	private static final String TIME_YT_CHANNEL 	= "";
	
	//Odysee
	private static final String URL_OD_NORMAL		= "https://odysee.com/@MeekerExtreme:9/ebox-2.0-v2-is-the-only-mini-bike-you:3";
	private static final String TYPE_OD_NORMAL 		= "Odysee Normal";
	private static final String TTL_OD_NORMAL 		= "EBOX 2.0 V2 is the ONLY Mini Bike You Should Consider";
	private static final String DATE_OD_NORMAL 		= "Friday, September 20, 2024 7:00:41 AM EDT";
	private static final String CHNL_OD_NORMAL	 	= "@MeekerExtreme on Odysee";
	private static final String TIME_OD_NORMAL 		= "07:20";
	
	//Vimeo
	private static final String URL_VMO_NORMAL		= "https://vimeo.com/580025019";
	private static final String TYPE_VMO_NORMAL 	= "Vimeo Normal";
	private static final String TTL_VMO_NORMAL 		= "In Memory of Brian.mov";
	private static final String DATE_VMO_NORMAL 	= "2025-03-23";
	private static final String CHNL_VMO_NORMAL	 	= "On Vimeo";
	private static final String TIME_VMO_NORMAL 	= "";
	
	//Twitch
	private static final String URL_TWI_MOBILE		= "https://www.twitch.tv/videos/2552343677";
	private static final String TYPE_TWI_MOBILE 	= "Twitch Mobile (and normal since normal gets converted to mobile)";
	private static final String TTL_TWI_MOBILE 		= "every crash out is valid";
	private static final String DATE_TWI_MOBILE 	= "";
	private static final String CHNL_TWI_MOBILE	 	= "ptony on Twitch";
	private static final String TIME_TWI_MOBILE 	= "2:14:18";
	
	//DailyMotion
	private static final String URL_DLYMTN_NORMAL	= "https://www.dailymotion.com/video/x9ppy20";
	private static final String TYPE_DLYMTN_NORMAL 	= "Daily Motion Normal";
	private static final String TTL_DLYMTN_NORMAL 	= "Top 10 Superhero Movie Storylines That Got KILLED (And Where They Were Headed)";
	private static final String DATE_DLYMTN_NORMAL 	= "08-30-2025";
	private static final String CHNL_DLYMTN_NORMAL	= "shortfilms on Dailymotion";
	private static final String TIME_DLYMTN_NORMAL 	= "10:50";
	
	//Rumble
	private static final String URL_RUM_NORMAL		= "https://rumble.com/v6y3w2q-slu-pp-332-my-results-so-far-update.html?playlist_id=MAnJ6cuQdtA";
	private static final String TYPE_RUM_NORMAL 	= "Rumble Normal";
	private static final String TTL_RUM_NORMAL 		= "SLU-PP-332: My Results So Far (Update)";
	private static final String DATE_RUM_NORMAL 	= "August 26, 2025";
	private static final String CHNL_RUM_NORMAL		= "TigerFitness on Rumble";
	private static final String TIME_RUM_NORMAL 	= "";
	
	private static final String URL_RUM_STRMING		= "https://rumble.com/v6yel6y-mousehold-farm-all-weather-riding-arena.html?e9s=src_v1_sports";
	private static final String TYPE_RUM_STRMING 	= "Rumble Streaming";
	private static final String TTL_RUM_STRMING 	= "Mousehold Farm- Riding arena";
	private static final String DATE_RUM_STRMING 	= "Streaming Now";
	private static final String CHNL_RUM_STRMING	= "Mousehold Farm on Rumble";
	private static final String TIME_RUM_STRMING 	= "";
	
	private static final String URL_RUM_STRMD		= "https://rumble.com/v6ya0xo-mousehold-farm-all-weather-riding-arena.html?e9s=src_v1_cbl%2Csrc_v1_ucp_l";
	private static final String TYPE_RUM_STRMD 		= "Rumble Streamed Previously";
	private static final String TTL_RUM_STRMD 		= "Mousehold Farm Live Stream - Replay - VHS Archives";
	private static final String DATE_RUM_STRMD 		= "Streamed on 2025-08-30 16:48:03";
	private static final String CHNL_RUM_STRMD		= "Mousehold Farm on Rumble";
	private static final String TIME_RUM_STRMD 		= "";
	
	//Bitchute
	private static final String URL_BTCHT_NORMAL	= "https://www.bitchute.com/video/JTQL5LMXj50";
	private static final String TYPE_BTCHT_NORMAL 	= "Bitchute Normal";
	private static final String TTL_BTCHT_NORMAL 	= "Southwest & Spirit Planes Nearly Collide Midair, American Transportation Must Be Fixed";
	private static final String DATE_BTCHT_NORMAL 	= "--";
	private static final String CHNL_BTCHT_NORMAL	= "On BITCHUTE";
	private static final String TIME_BTCHT_NORMAL 	= "";
	
	private boolean failureOccured;
	
	public static void main(String[] args) {
		new Test();
	}
	
	public Test() {
		this.failureOccured = false;
		
		//YouTube
		runTest(testLink(URL_YT_NORMAL, TYPE_YT_NORMAL, TTL_YT_NORMAL, DATE_YT_NORMAL, CHNL_YT_NORMAL, TIME_YT_NORMAL), TYPE_YT_NORMAL);
		runTest(testLink(URL_YT_SHORT, TYPE_YT_SHORT, TTL_YT_SHORT, DATE_YT_SHORT, CHNL_YT_SHORT, TIME_YT_SHORT), TYPE_YT_SHORT);
		runTest(testLink(URL_YT_PLYLST, TYPE_YT_PLYLST, TTL_YT_PLYLST, DATE_YT_PLYLST, CHNL_YT_PLYLST, TIME_YT_PLYLST), TYPE_YT_PLYLST);
		runTest(testLink(URL_YT_LIST_TM, TYPE_YT_LIST_TM, TTL_YT_LIST_TM, DATE_YT_LIST_TM, CHNL_YT_LIST_TM, TIME_YT_LIST_TM), TYPE_YT_LIST_TM);
		runTest(testLink(URL_YT_TIMESTMP, TYPE_YT_TIMESTMP, TTL_YT_TIMESTMP, DATE_YT_TIMESTMP, CHNL_YT_TIMESTMP, TIME_YT_TIMESTMP), TYPE_YT_TIMESTMP);
		runTest(testLink(URL_YT_PREVIEW, TYPE_YT_PREVIEW, TTL_YT_PREVIEW, DATE_YT_PREVIEW, CHNL_YT_PREVIEW, TIME_YT_PREVIEW), TYPE_YT_PREVIEW);
		runTest(testLink(URL_YT_CHANNEL, TYPE_YT_CHANNEL, TTL_YT_CHANNEL, DATE_YT_CHANNEL, CHNL_YT_CHANNEL, TIME_YT_CHANNEL), TYPE_YT_CHANNEL);
		
		//Odysee
		runTest(testLink(URL_OD_NORMAL, TYPE_OD_NORMAL, TTL_OD_NORMAL, DATE_OD_NORMAL, CHNL_OD_NORMAL, TIME_OD_NORMAL), TYPE_OD_NORMAL);
		
		//Vimeo
		runTest(testLink(URL_VMO_NORMAL, TYPE_VMO_NORMAL, TTL_VMO_NORMAL, DATE_VMO_NORMAL, CHNL_VMO_NORMAL, TIME_VMO_NORMAL), TYPE_VMO_NORMAL);
		
		//Twitch
		runTest(testLink(URL_TWI_MOBILE, TYPE_TWI_MOBILE, TTL_TWI_MOBILE, DATE_TWI_MOBILE, CHNL_TWI_MOBILE, TIME_TWI_MOBILE), TYPE_TWI_MOBILE);
		
		//DailyMotion
		runTest(testLink(URL_DLYMTN_NORMAL, TYPE_DLYMTN_NORMAL, TTL_DLYMTN_NORMAL, DATE_DLYMTN_NORMAL, CHNL_DLYMTN_NORMAL, TIME_DLYMTN_NORMAL), TYPE_DLYMTN_NORMAL);
		
		//Rumble
		runTest(testLink(URL_RUM_NORMAL, TYPE_RUM_NORMAL, TTL_RUM_NORMAL, DATE_RUM_NORMAL, CHNL_RUM_NORMAL, TIME_RUM_NORMAL), TYPE_RUM_NORMAL);
		runTest(testLink(URL_RUM_STRMING, TYPE_RUM_STRMING, TTL_RUM_STRMING, DATE_RUM_STRMING, CHNL_RUM_STRMING, TIME_RUM_STRMING), TYPE_RUM_STRMING);
		runTest(testLink(URL_RUM_STRMD, TYPE_RUM_STRMD, TTL_RUM_STRMD, DATE_RUM_STRMD, CHNL_RUM_STRMD, TIME_RUM_STRMD), TYPE_RUM_STRMD);
		
		//Bitchute
		runTest(testLink(URL_BTCHT_NORMAL, TYPE_BTCHT_NORMAL, TTL_BTCHT_NORMAL, DATE_BTCHT_NORMAL, CHNL_BTCHT_NORMAL, TIME_BTCHT_NORMAL), TYPE_BTCHT_NORMAL);
		
		if (failureOccured) {
			System.err.println("\nFailure(s) occured while testing!");
		} else {
			System.out.println("\nTest successful!");
		}
	}
	
	private void runTest(boolean success, String type) {
		if (success) {
			System.out.println(type + " : SUCCESS!");
		} else {
			System.err.println(type + " : FAILED!");
			
			this.failureOccured = true;
			
			if (EXIT_UPON_FAILURE) {
				System.exit(-1);
			}
		}
		
		try {
			Thread.sleep(750);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private boolean testLink(String url, String linkType, String title, String date, String channel, String time) {
		MetadataObtainer obtainer = new MetadataObtainer(url);
		boolean success = false;

		if (obtainer.isSupported()) {
			if (obtainer.isUrlError()) {
				System.err.println("Error when fetching " + linkType + " HTML!");
			} else {
				if (obtainer.getTitle().trim().equals(title)) {
					if (obtainer.getDate().trim().equals(date)) {
						if (obtainer.getChannel().trim().equals(channel)) {
							if (obtainer.getTime().trim().equals(time)) {
								success = true;
							} else {
								System.err.println("Failed to get proper time for " + linkType + "!");
							}
						} else {
							System.err.println("Failed to get proper channel for " + linkType + "!");
						}
					} else {
						System.err.println("Failed to get proper date for " + linkType + "!");
					}
				} else {
					System.err.println("Failed to get proper title for " + linkType);
				}
			}
		} else {
			System.err.println(linkType + " claims to not be supported!");
		}

		return success;
	}
}

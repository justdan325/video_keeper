
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
	private static final String TIME_YT_SHORT 		= "00:47";
	
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
	
	//Odysee
	private static final String URL_OD_NORMAL		= "https://odysee.com/@MeekerExtreme:9/ebox-2.0-v2-is-the-only-mini-bike-you:3";
	private static final String TYPE_OD_NORMAL 		= "Odysee Normal";
	private static final String TTL_OD_NORMAL 		= "EBOX 2.0 V2 is the ONLY Mini Bike You Should Consider";
	private static final String DATE_OD_NORMAL 		= "Friday, September 20, 2024 at 7:00:41 AM EDT";
	private static final String CHNL_OD_NORMAL	 	= "@MeekerExtreme on Odysee";
	private static final String TIME_OD_NORMAL 		= "07:20";
	
	//Twitch
	private static final String URL_TWI_MOBILE		= "https://m.twitch.tv/videos/2296202202";
	private static final String TYPE_TWI_MOBILE 	= "Twitch Mobile (and normal since normal gets converted to mobile)";
	private static final String TTL_TWI_MOBILE 		= "ptony streams in the evening???";
	private static final String DATE_TWI_MOBILE 	= "";
	private static final String CHNL_TWI_MOBILE	 	= "ptony on Twitch";
	private static final String TIME_TWI_MOBILE 	= "3:36:31";
	
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
		
		//Odysee
		runTest(testLink(URL_OD_NORMAL, TYPE_OD_NORMAL, TTL_OD_NORMAL, DATE_OD_NORMAL, CHNL_OD_NORMAL, TIME_OD_NORMAL), TYPE_OD_NORMAL);
		
		//Twitch
		runTest(testLink(URL_TWI_MOBILE, TYPE_TWI_MOBILE, TTL_TWI_MOBILE, DATE_TWI_MOBILE, CHNL_TWI_MOBILE, TIME_TWI_MOBILE), TYPE_TWI_MOBILE);
		
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

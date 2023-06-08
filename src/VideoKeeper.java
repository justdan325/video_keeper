import java.util.ArrayList;
import java.util.Optional;
import java.io.*;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.awt.*;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.net.URL;
import java.awt.datatransfer.StringSelection;


public class VideoKeeper {
	public static final String LNK_HNDL_DEFAULT 	= Main.DEFAULT_HNDL_LNKS;
	public static final String LNK_HNDL_COPY 		= "COPY";
	public static final String LNK_HNDL_CUST 		= "CUSTOM<";
	public static final String LNK_HNDL_LNK_VAR 	= "%VIDEOLINK%";
	
	private DataModel		model;
	private VideoList		vidNodeList;
	private VideoDataNode 	prev;
	private MainGui 		mainGui;
	private String			database;
	
	public VideoKeeper(DataModel model, MainGui mainGui) {
		this.model			= model;
		this.vidNodeList 	= new VideoList();
		this.mainGui 		= mainGui;
		this.prev 			= null;
		this.database		= model.getDatabaseFile();
		
		populateList();
		monitorDatabase();
	}
	
	public int getSize() {
		return vidNodeList.size();
	}
	
	public int getCurrIndex() {
		return vidNodeList.getIndex();
	}
	
	public VideoDataNode getPrev() {
		return prev;
	}
	
	public void head() {
		vidNodeList.resetIndex();
	}
	
	public void add(VideoDataNode item) {
		boolean addItem = true;
		
		if(model.isCheckForDupl() && vidNodeList.contains(item.getUrl())) {
			int option = JOptionPane.showConfirmDialog(mainGui, "Link is already in watch list. Add anyway?", MainGui.PROG_NAME + " -- Duplicate Video",  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if(option == JOptionPane.YES_OPTION) {
				option = JOptionPane.showConfirmDialog(mainGui, "Check for duplicates going forward?", MainGui.PROG_NAME + " -- Check for Duplicates?",  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if(option == JOptionPane.NO_OPTION) {
					model.setCheckForDupl(false);
				}
			} else {
				addItem = false;
			}
		} else if((!item.getUrl().contains(".") && !item.getUrl().contains("/")) || item.getUrl().trim().length() < 3) {
			addItem = false;
			JOptionPane.showMessageDialog(mainGui, "Must enter a valid URL.", MainGui.PROG_NAME + " -- Invalid URL", JOptionPane.ERROR_MESSAGE);
		}

		if(addItem) {
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
					if(item.getTitle().length() < 1 || item.getDate().length() < 1 
					   || item.getChannel().length() < 1) {
						
						MetadataObtainer obtainer = new MetadataObtainer(item.getUrl());

						if(item.getTitle().length() < 1) {
							item.setTitle(obtainer.getTitle());
						}

						if(item.getDate().length() < 1) {
							item.setDate(obtainer.getDate());
						}

						if(item.getChannel().length() < 1) {
							item.setChannel(obtainer.getChannel());
						}
						
						if(item.getTime().length() < 1) {
							item.setTime(obtainer.getTime());
						}
					}
				}
			});
			
			thread.start();
			
			vidNodeList.append(item);
		}
	}
		
	public void openPrev() {
		if(prev != null && !prev.isEmpty()) {
			handleLink(prev.getUrl());
		}
	}
	
	public void openNext() {
		if(vidNodeList.size() > 0) {
			prev = vidNodeList.popCurr().get();
			
			if(!prev.isEmpty()) {
				handleLink(prev.getUrl());
			}
			
			refreshCurrInSepThread(true);
//		} else if(skipQueue.size() > 0) {
//			addSkipped();
//			openNext();
		}
	}
	
	public boolean skipToNext() {
		Optional<VideoDataNode> opt = Optional.empty();
		boolean atHead = false;
		
		refreshCurrInSepThread(true);
		
		opt = vidNodeList.peek(vidNodeList.getIndex());
		
		if (opt.isPresent()) {
			prev = opt.get();
			vidNodeList.incrementIndex();
			
			if(vidNodeList.getIndex() <= 0) {
				atHead = true;
			}
		}
		
		return atHead;
	}
	
	public boolean goBackToPrev() {
		Optional<VideoDataNode> opt = Optional.empty();
		boolean atHead = false;
		
		opt = vidNodeList.peek(vidNodeList.getIndex());
		
		if (opt.isPresent()) {
			prev = opt.get();
			vidNodeList.deccrementIndex();
			
			if(vidNodeList.getIndex() <= 0) {
				atHead = true;
			}
		}
		
		return atHead;
	}
	
//	public synchronized void addSkipped() {
//		Queue temp = new Queue();
//		
//		if(skipQueue.size() > 0) {
//			while(skipQueue.size() > 0) {
//				VideoDataNode node = skipQueue.pop();
//				temp.push(node);
//			}
//
//			while(mainQueue.size() > 0) {
//				VideoDataNode node = mainQueue.pop();
//				temp.push(node);
//			}
//
//			while(temp.size() > 0) {
//				VideoDataNode node = temp.pop();
//				mainQueue.push(node);
//			}
//		}
//	}
	
	public void refreshCurrInSepThread(boolean abortIfNotEmpty) {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				refreshCurr(abortIfNotEmpty);
			}
		});
		
		thread.start();
	}
	
	public void refreshCurr(boolean abortIfNotEmpty) {
		if(vidNodeList.size() > 0) {
			Optional<VideoDataNode> opt = vidNodeList.peekCurr();

			if(opt.isPresent() && opt.get().isEmpty() == false) {
				VideoDataNode temp = opt.get();
				
				if(abortIfNotEmpty == false || temp.getTitle().length() < 1 || temp.getDate().length() < 1 || temp.getChannel().length() < 1) {
					MetadataObtainer obtainer = new MetadataObtainer(temp.getUrl());

					if(abortIfNotEmpty == false || temp.getTitle().length() < 1) {
						temp.setTitle(obtainer.getTitle());
					}

					if(abortIfNotEmpty == false || temp.getDate().length() < 1) {
						temp.setDate(obtainer.getDate());
					}

					if(abortIfNotEmpty == false || temp.getChannel().length() < 1) {
						temp.setChannel(obtainer.getChannel());
					}
					
					if(abortIfNotEmpty == false || temp.getTime().length() < 1) {
						temp.setTime(obtainer.getTime());
					}
				}
			}
		}
	}
	
	public void refreshAll() {
		ProgressBar progBar = new ProgressBar(mainGui);
		
		progBar.setMax(vidNodeList.size());
		progBar.showProgressBar();
		vidNodeList.clear();
		populateList();
		vidNodeList.resetIndex();
		
		if(vidNodeList.size() > 0) {
			VideoList tempList = new VideoList();

			while(vidNodeList.size() > 0) {
				VideoDataNode temp = vidNodeList.popCurr().get();
				
				if(temp.isEmpty() == false) {
					MetadataObtainer obtainer = new MetadataObtainer(temp.getUrl());
					
					temp.setTitle(obtainer.getTitle());
					temp.setDate(obtainer.getDate());
					temp.setChannel(obtainer.getChannel());
					temp.setTime(obtainer.getTime());
				}
				
				tempList.append(temp);
				progBar.progress();
			}
			
			while(tempList.size() > 0) {
				vidNodeList.append(tempList.popCurr().get());
			}
		}
		
		progBar.kill();
	}
	
	public String getCurrTitle(boolean truncate) {
		String nextTitle = "";

		if (vidNodeList.size() > 0) {
			nextTitle = vidNodeList.peekCurr().get().getTitle();
		}

		if (nextTitle.length() > 60 && truncate) {
			nextTitle = nextTitle.substring(0, 60) + ". . .";
		}
		
		return nextTitle;
	}
	
	public String getPrevTitle() {
		String prevTitle = "";

		if(prev != null) {
			prevTitle = prev.getTitle();
		}
		
		return prevTitle;
	}
	
	public String getCurrDateAndTime() {
		String nextDate = "";

		if (vidNodeList.size() > 0) {
			if (vidNodeList.peekCurr().get().getDate().length() > 1 && vidNodeList.peekCurr().get().getTime().length() > 1) {
				nextDate = vidNodeList.peekCurr().get().getDate() + "   ~   Len. " + vidNodeList.peekCurr().get().getTime();
			} else {
				nextDate = vidNodeList.peekCurr().get().getDate() + vidNodeList.peekCurr().get().getTime();
			}
		}
		
		return nextDate;
	}
	
	public String getNextChannel() {
		String nextChannel = "";

		if(vidNodeList.size() > 0) {
			nextChannel = vidNodeList.peekCurr().get().getChannel();
		}
		
		return nextChannel;
	}
	
	public void populateList() {
		vidNodeList.clear();
		
		String[] list = readFile(database).split("\n");
		
		for(int i = 0; i < list.length; i++) {
			VideoDataNode node = new VideoDataNode(list[i]);
			
			if(!(i == 0 || i == list.length-1) || !node.isEmpty()) {
				vidNodeList.append(node);
			}
		}
		
		refreshCurrInSepThread(true);
	}
	
	public synchronized boolean save() {
		VideoList mainCopy = new VideoList(vidNodeList);
		String toWrite = "";
		boolean saved = false;
		int i = 0;
		
		mainCopy.resetIndex();
		
		while (mainCopy.size() > 0) {
			if (i > 0) {
				toWrite += "\n" + mainCopy.popCurr().get().toString();
			} else {
				toWrite += mainCopy.popCurr().get().toString();
			}

			i++;
		}
		
		clearFile(database);
		
		saved = writeFile(toWrite, database);
		
		return saved;
	}
	
	public boolean exportUrls(String destination) {
		String urls = "";
		VideoList tempQ = new VideoList(vidNodeList);
		boolean success = true;
		
		while (tempQ.size() > 0) {
			urls += tempQ.popCurr().get().getUrl() + "\n";
		}

		tempQ = new VideoList(vidNodeList);

		while (tempQ.size() > 0) {
			urls += tempQ.popCurr().get().getUrl() + "\n";
		}

		if (urls.length() > 7) {
			success = writeFile(urls, destination);
		} else {
			success = false;
		}

		return success;
	}
	
	private void monitorDatabase() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				for(;;) {
					//if database changes, save and reload with new database
					if(!database.equals(model.getDatabaseFile())) {
						if(model.isAutoSaveOnExit()) {
							save();
						} else {
							String mess = "Would you like to save changes to \nthe current database before switching?";
							((SettingsDialog) mainGui.getSettingsDialog()).setChildDialogOpen(true);
							int option = JOptionPane.showOptionDialog(mainGui.getSettingsDialog(), mess, MainGui.PROG_NAME + " -- Save?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
							((SettingsDialog) mainGui.getSettingsDialog()).setChildDialogOpen(false);
							
							if(option == JOptionPane.YES_OPTION) {
								save();
							}
						}
						
						database = model.getDatabaseFile();
						populateList();
					}
					
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		
		thread.start();
	}
	
	private void handleLink(String s) {
		//open the webpage in default browser. Cache as a backup.
		if (model.getHandleLinks().equalsIgnoreCase(LNK_HNDL_DEFAULT)) {
			try {
				Desktop.getDesktop().browse(new URL(s).toURI());
			} catch (Exception e) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
				JOptionPane.showMessageDialog(mainGui, "Could not open in browser. Video link has \nbeen copied to the clip board.",
						MainGui.PROG_NAME + " -- Link Copied", JOptionPane.WARNING_MESSAGE);
			}
		} else if (model.getHandleLinks().equalsIgnoreCase(LNK_HNDL_COPY)) {
			JDialog dialog = (new JOptionPane("Video link has been copied to the clip board.", JOptionPane.INFORMATION_MESSAGE)).createDialog(mainGui, MainGui.PROG_NAME + " -- Link Copied");
			
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					dialog.setVisible(true);
				}
			}).start();

			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					dialog.dispose();
				}
			}, 2000);
		} else if(model.getHandleLinks().toUpperCase().startsWith(LNK_HNDL_CUST)) {
			String command = model.getHandleLinks();
			
			command = command.substring(LNK_HNDL_CUST.length());
			command = command.substring(0, command.lastIndexOf(">"));
			command = command.replaceAll(LNK_HNDL_LNK_VAR, s);
			
			try {
				Runtime.getRuntime().exec(command);
			} catch (Exception e) {
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
				JOptionPane.showMessageDialog(mainGui, "Could not execute custom command. Video link has \nbeen copied to the clip board.",
						MainGui.PROG_NAME + " -- Link Copied", JOptionPane.WARNING_MESSAGE);
				e.printStackTrace();
			}
		} else {
			JOptionPane.showMessageDialog(mainGui, "Invalid link handling method specified in properties file.",
					MainGui.PROG_NAME + " -- Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private static String readFile(String fileName) {
		File file;
		Scanner inputFile;
		String errMess = null;

		file = new File(fileName);
		
		if(!file.exists()) {
			return "";
		}

		try {
			inputFile = new Scanner(file);
		} catch(FileNotFoundException e) {
			errMess = "\nFileNotFoundException when reading " + fileName + "\n";
			return "";
		}

		assert errMess == null : errMess;

		int count = 0;
		String str = "";

		while(inputFile.hasNext()) {
			if(count != 0) {
				str += '\n';
			}
			
			str += inputFile.nextLine();
			count++;
		}
		
		inputFile.close();
		
		return str;
	}

	private static boolean writeFile(String fileStr, String destination) {
		final long LENGTH = fileStr.length();
		boolean success = true;
		File file = new File(destination);
		PrintWriter outputFile = null;

		if(LENGTH > 0) {
			try {
				if(!file.exists()) {
					success = file.createNewFile();
				}
				
				if(success) {
					outputFile = new PrintWriter(file);
				}
			} catch(Exception e) {
				success = false;
			}

			if (success && outputFile != null) {
				for (int i = 0; i < LENGTH; i++) {
					if (fileStr.substring(i, i + 1).equals("\n")) {
						outputFile.println();
					} else if (fileStr.substring(i, i + 1).equals("\t")) {
						outputFile.write("\t");
					} else {
						outputFile.append(fileStr.charAt(i));
					}
				}

				outputFile.close();
			} else {
				success = false;
			}
		}
		
		return success;
	}
	
	private static void clearFile(String fileName) {
		final File file = new File(fileName);
		PrintWriter outputFile;
		
		try {
			outputFile = new PrintWriter(file);
		} catch (FileNotFoundException e) {
			return;
		}

		outputFile.write("");
		outputFile.close();
    }
	
	/*********************************************************************************************************************/
	
	private class VideoList {
		private ArrayList<VideoDataNode> list;
		private int index = -1;
		
		public VideoList() {
			this.list = new ArrayList<>();
		}
		
		public VideoList(VideoList orig) {
			if (orig != null) {
				this.list = new ArrayList<>();
				
				if (orig.size() > 0) {
					for (int i = 0; i < orig.size(); i++) {
						Optional<VideoDataNode> curr = orig.peek(i);

						if (curr.isPresent()) {
							this.append(curr.get());
						}
					}

					this.index = orig.getIndex();
				}
			}
		}
		
		public synchronized void append(VideoDataNode node) {
			list.add(node);
			
			if(index < 0) {
				resetIndex();
			}
		}
		
		@SuppressWarnings("unused")
		public synchronized void prepend(VideoDataNode node) {
			list.add(0, node);
			
			if (index != 0) {
				incrementIndex();
			} else {
				resetIndex();
			}
		}
		
		@SuppressWarnings("unused")
		public synchronized boolean insert(int index, VideoDataNode node) {
			boolean added = false;
			
			if (index >= 0 && index <= this.index) {
				list.add(index, node);
				
				if (index <= this.index) {
					incrementIndex();
				}
				
				added = true;
			}
			
			return added;
		}
		
		public synchronized Optional<VideoDataNode> popCurr() {
			Optional<VideoDataNode> curr = Optional.empty();
			
			if (list.size() > 0) {
				curr = Optional.of(list.remove(index));

				//reset index to head if it walks off of end of list or if list is empty
				if (index > list.size() - 1) {
					resetIndex();
				}
			}
			
			return curr;
		}
		
		@SuppressWarnings("unused")
		public synchronized Optional<VideoDataNode> pop(int index) {
			Optional<VideoDataNode> curr = Optional.empty();

			if (index >= 0 && index <= list.size() ) {
				curr = Optional.of(list.remove(index));

				//reset index to head if it walks off of end of list or if list is empty
				if (index > list.size() - 1) {
					resetIndex();
				}
			}

			return curr;
		}
		
		public synchronized Optional<VideoDataNode> peekCurr() {
			Optional<VideoDataNode> curr = Optional.empty();

			if (list.size() > 0) {
				curr = Optional.of(list.get(index));
			}

			return curr;
		}
		
		@SuppressWarnings("unused")
		public synchronized Optional<VideoDataNode> peekNext() {
			Optional<VideoDataNode> curr = Optional.empty();

			if (list.size() > 0) {
				if (index + 1 == list.size()) {
					curr = Optional.of(list.get(0));
				} else {
					curr = Optional.of(list.get(index + 1));
				}
			}

			return curr;
		}
		
		public synchronized Optional<VideoDataNode> peek(int index) {
			Optional<VideoDataNode> curr = Optional.empty();

			if (index >= 0 && index <= list.size()) {
				curr = Optional.of(list.get(index));
			}

			return curr;
		}
		
		public synchronized int getIndex() {
			return index;
		}

		@SuppressWarnings("unused")
		public synchronized boolean setIndex(int index) {
			boolean set = false;

			if (index <= list.size() - 1) {
				this.index = index;
				set = true;
			}

			return set;
		}

		public synchronized void incrementIndex() {
			index++;

			if (index == list.size()) {
				index = 0;
			}
		}
		
		public synchronized void deccrementIndex() {
			index--;

			if (index < 0 && list.size() >= 0) {
				index = list.size() - 1;
			}
		}

		public synchronized int size() {
			return list.size();
		}
		
		public synchronized void clear() {
			list.clear();
			index = -1;
		}

		public synchronized void resetIndex() {
			if (list.size() == 0) {
				index = -1;
			} else {
				index = 0;
			}
		}

		public synchronized boolean contains(String url) {
			boolean contains = false;

			for (VideoDataNode node : list) {
				if (node.getUrl().equals(url)) {
					contains = true;
					break;
				}
			}

			return contains;
		}
	}
	
//	private class Queue {
//		private LinkedList<VideoDataNode> list;
//		
//		public Queue() {
//			list = new LinkedList<VideoDataNode>();
//		}
//		
//		@SuppressWarnings("unused")
//		public synchronized void push(String item) {
//			list.addLast(new VideoDataNode(item));
//		}
//		
//		public synchronized void push(VideoDataNode item) {
//			list.addLast(item);
//		}
//		
//		public synchronized VideoDataNode pop() {
//			return list.removeFirst();
//		}
//		
//		public synchronized int size() {
//			return list.size();
//		}
//		
//		public synchronized VideoDataNode peek() {
//			return list.peekFirst();
//		}
//		
//		public synchronized void clear() {
//			list.clear();
//		}
//
//		//only compares url since that is the key
//		@SuppressWarnings("unused")
//		public synchronized boolean contains(String key) {
//			boolean contains = false;
//
//			for(VideoDataNode node : list) {
//				if(node.getUrl().equals(key)) {
//					contains = true;
//				}
//			}
//			
//			return contains;
//		}
//		
//		//only compares url since that is the key
//		public synchronized boolean contains(VideoDataNode node) {
//			boolean contains = false;
//
//			for(VideoDataNode i : list) {
//				if(i.getUrl().equals(node.getUrl())) {
//					contains = true;
//				}
//			}
//			
//			return contains;
//		}
//		
//		public synchronized Queue duplicate() {
//			Queue copy = new Queue();
//			
//			for(VideoDataNode node : list) {
//				copy.push(node);
//			}
//			
//			return copy;
//		}
//	}
}

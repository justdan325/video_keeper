import java.util.ArrayList;
import java.util.LinkedList;
import java.io.*;
import java.util.Scanner;
import java.awt.*;
import javax.swing.JOptionPane;
import java.net.URL;
import java.awt.datatransfer.StringSelection;
import javax.swing.JFrame;


public class VideoKeeper
{
	private DataModel		model;
	private Queue 			mainQueue;
	private Queue 			addedQueue;
	private Queue			skipQueue;
	private VideoDataNode 	curr;
	private MainGui 		mainGui;
	private String			database;
	private boolean			checkForDuplicates;
	
	public VideoKeeper(DataModel model, MainGui mainGui) {
		this.model				= model;
		this.mainQueue 			= new Queue();	
		this.addedQueue 		= new Queue();
		this.skipQueue			= new Queue();
		this.mainGui 			= mainGui;
		this.curr 				= null;
		this.database			= model.getDatabaseFile();
		this.checkForDuplicates = true;
		
		populateQueue();
		monitorDatabase();
	}
	
	public int getSize() {
		return mainQueue.size() + skipQueue.size();
	}
	
	public VideoDataNode getCurr() {
		return curr;
	}
	
	public void add(VideoDataNode item) {
		boolean addItem = true;
		
		if(checkForDuplicates && mainQueue.contains(item)) {
			int option = JOptionPane.showConfirmDialog(mainGui, "Link is already in watch list. Add anyway?", MainGui.PROG_NAME + " -- Duplicate Video",  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if(option == JOptionPane.YES_OPTION) {
				option = JOptionPane.showConfirmDialog(mainGui, "Check for duplicates going forward?", MainGui.PROG_NAME + " -- Check for Duplicates?",  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if(option == JOptionPane.NO_OPTION) {
					checkForDuplicates = false;
				}
			} else {
				addItem = false;
			}
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
					}
				}
			});
			
			thread.start();
			
//			addedQueue.push(item);
			mainQueue.push(item);
		}
	}
		
	public void openCurr() {
		if(curr != null && !curr.isEmpty()) {
			openWebpage(curr.getUrl());
		}
	}
	
	public void openNext() {
		if(mainQueue.size() > 0) {
			curr = mainQueue.pop();
			
			if(!curr.isEmpty()) {
				openWebpage(curr.getUrl());
			}
			
			refreshNext();
		} else if(skipQueue.size() > 0) {
			addSkipped();
			openNext();
		}
	}
	
	public void skipNext() {
		if(mainQueue.size() > 0) {
			curr = mainQueue.pop();
			skipQueue.push(curr);
			refreshNext();
		} else if(mainQueue.size() == 0 && skipQueue.size() > 0) {
			addSkipped();
			skipNext();
		}
	}
	
	private synchronized void addSkipped() {
		Queue temp = new Queue();
		int total = mainQueue.size() + skipQueue.size();
		int finalTotal = -1;
		
		if(skipQueue.size() > 0) {
			while(skipQueue.size() > 0) {
				VideoDataNode node = skipQueue.pop();
				temp.push(node);
			}

			while(mainQueue.size() > 0) {
				VideoDataNode node = mainQueue.pop();
				temp.push(node);
			}

			while(temp.size() > 0) {
				VideoDataNode node = temp.pop();
				mainQueue.push(node);
			}
		}
		
		finalTotal = mainQueue.size() + skipQueue.size();
		
		if(finalTotal != total) {
			System.out.println("ya fucked up: " + total + " " + finalTotal);
		}
	}
	
	public void refreshNext() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if(mainQueue.size() > 0) {
					VideoDataNode temp = mainQueue.peek();

					if(!temp.isEmpty()) {
						if(temp.getTitle().length() < 1 || temp.getDate().length() < 1 || temp.getChannel().length() < 1) {
							MetadataObtainer obtainer = new MetadataObtainer(temp.getUrl());

							if(temp.getTitle().length() < 1) {
								temp.setTitle(obtainer.getTitle());
							}

							if(temp.getDate().length() < 1) {
								temp.setDate(obtainer.getDate());
							}

							if(temp.getChannel().length() < 1) {
								temp.setChannel(obtainer.getChannel());
							}
						}
					}
				}
			}
		});
		
		thread.start();
	}
	
	public void refreshAll() {
		skipQueue.clear();
		mainQueue.clear();
		populateQueue();
		
		if(mainQueue.size() > 0) {
			Queue tempQ = new Queue();

			while(mainQueue.size() > 0) {
				VideoDataNode temp = mainQueue.pop();
				
				if(!temp.isEmpty()) {
					MetadataObtainer obtainer = new MetadataObtainer(temp.getUrl());
					temp.setTitle(obtainer.getTitle());
					temp.setDate(obtainer.getDate());
					temp.setChannel(obtainer.getChannel());
				}
				
				tempQ.push(temp);
			}
			
			while(tempQ.size() > 0) {
				mainQueue.push(tempQ.pop());
			}
		}
	}
	
	public String getNextTitle() {
		String nextTitle = "";

		if(mainQueue.size() > 0) {
			nextTitle = mainQueue.peek().getTitle();
		} else if(mainQueue.size() == 0 && skipQueue.size() > 0) {
			nextTitle = skipQueue.peek().getTitle();
		}
		
		return nextTitle;
	}
	
	public String getCurrTitle() {
		String currTitle = "";

		if(curr != null) {
			currTitle = curr.getTitle();
		}
		
		return currTitle;
	}
	
	public String getNextDate() {
		String nextDate = "";

		if(mainQueue.size() > 0) {
			nextDate = mainQueue.peek().getDate();
		} else if(mainQueue.size() == 0 && skipQueue.size() > 0) {
			nextDate = skipQueue.peek().getDate();
		}
		
		return nextDate;
	}
	
	public String getNextChannel() {
		String nextChannel = "";

		if(mainQueue.size() > 0) {
			nextChannel = mainQueue.peek().getChannel();
		} else if(mainQueue.size() == 0 && skipQueue.size() > 0) {
			nextChannel = skipQueue.peek().getChannel();
		}
		
		return nextChannel;
	}
	
	public void populateQueue() {
//		Queue temp = new Queue();
//		VideoDataNode hold;
		mainQueue.clear();
		skipQueue.clear();
		
		String[] list = readFile(database).split("\n");
		
		for(int i = 0; i < list.length; i++) {
			VideoDataNode node = new VideoDataNode(list[i]);
			
			if(!(i == 0 || i == list.length-1) || !node.isEmpty()) {
				mainQueue.push(node);
			}
		}
		
		//I have no clue why I had added this, but all it was doing was cross-contaminating databases.
		//Leaving it in case I remember what it does.
//		while(addedQueue.size() > 0) {
//			hold = addedQueue.pop();
//			mainQueue.push(hold);
//			temp.push(hold);
//		}
//		
//		addedQueue = temp;
		
		refreshNext();
	}
	
	public synchronized boolean save() {
		Queue skipCopy = skipQueue.duplicate();
		Queue mainCopy = mainQueue.duplicate();
		String toWrite = "";
		boolean saved = false;
		int i = 0;
		
		while(skipCopy.size() > 0) {
			if(i > 0) {
				toWrite += "\n" + skipCopy.pop().toString();
			} else {
				toWrite += skipCopy.pop().toString();
			}
			
			i++;
		}
		
		while(mainCopy.size() > 0) {
			if(i > 0) {
				toWrite += "\n" + mainCopy.pop().toString();
			} else {
				toWrite += mainCopy.pop().toString();
			}
			
			i++;
		}
		
		clearFile(database);
		
		saved = writeFile(toWrite, database);
		
		return saved;
	}
	
	public boolean exportUrls(String destination) {
		String urls = "";
		Queue tempQ = skipQueue.duplicate();
		boolean success = true;
		
		while(tempQ.size() > 0) {
			urls += tempQ.pop().getUrl() + "\n";
		}
		
		tempQ = mainQueue.duplicate();
		
		while(tempQ.size() > 0) {
			urls += tempQ.pop().getUrl() + "\n";
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
							int option = JOptionPane.showOptionDialog(mainGui.getSettingsDialog(), mess, MainGui.PROG_NAME + " -- Save?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
							
							if(option == JOptionPane.YES_OPTION) {
								save();
							}
						}
						
						database = model.getDatabaseFile();
						populateQueue();
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
	
	private void openWebpage(String s) {
     	try {
			Desktop.getDesktop().browse(new URL(s).toURI());
		} catch (Exception e) {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
			JOptionPane.showMessageDialog(mainGui, "URL has been cached.", MainGui.PROG_NAME + " -- URL Cached", 1);
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
	
	private class Queue {
		private LinkedList<VideoDataNode> list;
		
		public Queue() {
			list = new LinkedList<VideoDataNode>();
		}
		
		public synchronized void push(String item) {
			list.addLast(new VideoDataNode(item));
		}
		
		public synchronized void push(VideoDataNode item) {
			list.addLast(item);
		}
		
		public synchronized VideoDataNode pop() {
			return list.removeFirst();
		}
		
		public synchronized int size() {
			return list.size();
		}
		
		public synchronized VideoDataNode peek() {
			return list.peekFirst();
		}
		
		public synchronized void clear() {
			list.clear();
		}

		//only compares url since that is the key
		public synchronized boolean contains(String key) {
			boolean contains = false;

			for(VideoDataNode node : list) {
				if(node.getUrl().equals(key)) {
					contains = true;
				}
			}
			
			return contains;
		}
		
		//only compares url since that is the key
		public synchronized boolean contains(VideoDataNode node) {
			boolean contains = false;

			for(VideoDataNode i : list) {
				if(i.getUrl().equals(node.getUrl())) {
					contains = true;
				}
			}
			
			return contains;
		}
		
		public synchronized Queue duplicate() {
			Queue copy = new Queue();
			
			for(VideoDataNode node : list) {
				copy.push(node);
			}
			
			return copy;
		}
	}
}

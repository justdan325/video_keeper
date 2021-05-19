import java.util.LinkedList;
import java.io.*;
import java.nio.file.*;
import java.util.Scanner;
import java.awt.*;
import javax.swing.JOptionPane;
import java.net.URL;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import javax.swing.JFrame;


public class VideoKeeper
{
	private Queue 			mainQueue;
	private Queue 			addedQueue;
	private Queue			skipQueue;
	private String 		database;
	private VideoDataNode 	curr;
	private JFrame 		frame;
	private boolean		checkForDuplicates;
	
	public VideoKeeper(String database, JFrame frame) {
		this.mainQueue 		= new Queue();	
		this.addedQueue 		= new Queue();
		this.skipQueue			= new Queue();
		this.database 			= database;
		this.frame 			= frame;
		this.curr 			= null;
		this.checkForDuplicates 	= true;
		
		populateQueue();
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
			int option = JOptionPane.showConfirmDialog(frame, "Link is already in watch queue. Add anyway?", MainGui.PROG_NAME,  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if(option == JOptionPane.YES_OPTION) {
				option = JOptionPane.showConfirmDialog(frame, "Check for duplicates going forward?", MainGui.PROG_NAME,  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

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
			
			addedQueue.push(item);
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
		Queue temp = new Queue();
		VideoDataNode hold;
		mainQueue.clear();
		
		String[] list = readFile(database).split("\n");
		
		for(int i = 0; i < list.length; i++) {
			VideoDataNode node = new VideoDataNode(list[i]);
			
			if(!(i == 0 || i == list.length-1) || !node.isEmpty()) {
				mainQueue.push(node);
			}
		}
		
		while(addedQueue.size() > 0) {
			hold = addedQueue.pop();
			mainQueue.push(hold);
			temp.push(hold);
		}
		
		addedQueue = temp;
		
		refreshNext();
	}
	
	public void save() {
		String toWrite = "";
		int i = 0;
		
		while(skipQueue.size() > 0) {
			if(i > 0) {
				toWrite += "\n" + skipQueue.pop().toString();
			} else {
				toWrite += skipQueue.pop().toString();
			}
			
			i++;
		}
		
		while(mainQueue.size() > 0) {
			if(i > 0) {
				toWrite += "\n" + mainQueue.pop().toString();
			} else {
				toWrite += mainQueue.pop().toString();
			}
			
			i++;
		}
		
		clearFile(database);
		writeFile(toWrite, database);
	}
	
	private void openWebpage(String s) {
     	try {
			Desktop.getDesktop().browse(new URL(s).toURI());
		} catch (Exception e) {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
			JOptionPane.showMessageDialog(frame, "URL has been cached.", "URL Cached", 1);
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

	private static void writeFile(String fileStr, String destination) {
		final long LENGTH = fileStr.length();
		File file = new File(destination);
		PrintWriter outputFile;

		if(LENGTH > 0) {
			try {
				outputFile = new PrintWriter(file);
			} catch(FileNotFoundException e) {
				System.out.println("File " + destination + " could not be found...\n");
				e.printStackTrace();
				
				return;
			}

			for(int i = 0; i < LENGTH; i++) {
				if(fileStr.substring(i,i+1).equals("\n")) {
					outputFile.println();
				} else if(fileStr.substring(i,i+1).equals("\t")) {
					outputFile.write("\t");
				} else {
					outputFile.append(fileStr.charAt(i));
				}
			}

			outputFile.close();
		}
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
	}
}

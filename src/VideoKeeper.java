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
		
		model.setVideoList(Optional.of(vidNodeList));
		
		populateList();
		monitor();
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
					if (item.getTitle().length() < 1 || item.getDate().length() < 1 || item.getChannel().length() < 1) {

						MetadataObtainer obtainer = new MetadataObtainer(item.getUrl());
						Optional<String> sanitizedUrl = obtainer.sanitizeForStorage(item.getUrl());

						//insert sanitized URL if supported
						if (sanitizedUrl.isPresent()) {
							item.setUrl(sanitizedUrl.get());
						}

						if (item.getTitle().length() < 1) {
							item.setTitle(obtainer.getTitle());
						}

						if (item.getDate().length() < 1) {
							item.setDate(obtainer.getDate());
						}

						if (item.getChannel().length() < 1) {
							item.setChannel(obtainer.getChannel());
						}

						if (item.getTime().length() < 1) {
							item.setTime(obtainer.getTime());
						}
					}
					
					System.gc();
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
	
	public void openCurr() {
		if(vidNodeList.size() > 0) {
			prev = vidNodeList.popCurr().get();
			
			if(!prev.isEmpty()) {
				handleLink(prev.getUrl());
			}
			
			refreshCurrInSepThread(true);
		}
	}
	
	public void open(int index, boolean removeFromList) {
		if (vidNodeList.size() >= index - 1) {
			Optional<VideoDataNode> node = vidNodeList.peek(index);

			if (node.isPresent()) {
				if (removeFromList) {
					vidNodeList.pop(index);
					prev = node.get();
				}

				handleLink(node.get().getUrl());
			}

			if (removeFromList) {
				refreshCurrInSepThread(true);
			}
		}
	}
	
	public void open(Optional<VideoDataNode> node, boolean removeFromList) {
		if (vidNodeList.size() > 0) {
			if (node.isPresent()) {
				VideoList copy = new VideoList(vidNodeList);
				String url = node.get().getUrl();

				if (url != null && url.length() > 0) {
					copy.resetIndex();

					for (int i = 0; i < copy.size(); i++) {
						String urlFromList = copy.peek(i).get().getUrl();

						if (urlFromList != null && urlFromList.length() > 0 && urlFromList.equals(url)) {
							if (removeFromList) {
								vidNodeList.pop(i);
								prev = node.get();
							}

							handleLink(url);
							break;
						}
					}
				}
			}

			if (removeFromList) {
				refreshCurrInSepThread(true);
			}
		}
	}
	
	public void deleteCurr() {
		if(vidNodeList.size() > 0) {
			prev = vidNodeList.popCurr().get();
			
			refreshCurrInSepThread(true);
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
	
	public int getIndexOfNode(Optional<VideoDataNode> node) {
		int indexLoc = -1;
		
		if(vidNodeList.size() > 0) {
			if (node.isPresent()) {
				VideoList copy = new VideoList(vidNodeList);
				String url = node.get().getUrl();
				
				if(url != null && url.length() > 0) {
					copy.resetIndex();
					
					for(int i = 0; i < copy.size(); i++) {
						String urlFromList = copy.peek(i).get().getUrl();
						
						if(urlFromList != null && urlFromList.length() > 0 && urlFromList.equals(url)) {
							indexLoc = i;
							break;
						}
					}
				}
			}
		}
		
		return indexLoc;
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
		boolean updated = false;
		
		if(vidNodeList.size() > 0) {
			Optional<VideoDataNode> opt = vidNodeList.peekCurr();

			if (opt.isPresent() && opt.get().isPopulated()) {
				VideoDataNode temp = opt.get();
				
				if (abortIfNotEmpty == false || temp.getTitle().length() < 1 || temp.getDate().length() < 1 || temp.getChannel().length() < 1) {
					MetadataObtainer obtainer = new MetadataObtainer(temp.getUrl());

					if (abortIfNotEmpty == false || temp.getTitle().length() < 1) {
						temp.setTitle(obtainer.getTitle());
						updated = true;
					}

					if (abortIfNotEmpty == false || temp.getDate().length() < 1) {
						temp.setDate(obtainer.getDate());
						updated = true;
					}

					if (abortIfNotEmpty == false || temp.getChannel().length() < 1) {
						temp.setChannel(obtainer.getChannel());
						updated = true;
					}

					if (abortIfNotEmpty == false || temp.getTime().length() < 1) {
						temp.setTime(obtainer.getTime());
						updated = true;
					}
					
					if (updated) {
						model.setRequestSaveButtonEn(true);
					}
				}
			}
		}
	}
	
	public void refresh(int index) {
		if(vidNodeList.size() > 0) {
			Optional<VideoDataNode> opt = vidNodeList.peek(index);

			if(opt.isPresent()) {
				VideoDataNode temp = opt.get();
				
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
					
					if(temp.getTime().length() < 1) {
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
		VideoList tempList = new VideoList(vidNodeList);
		boolean success = true;
		
		while (tempList.size() > 0) {
			urls += tempList.popCurr().get().getUrl() + "\n";
		}

		if (urls.length() > 7) {
			success = writeFile(urls, destination);
		} else {
			success = false;
		}

		return success;
	}
	
	private void monitor() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				for(;;) {
					//if database changes, save and reload with new database
					if(database.equals(model.getDatabaseFile()) == false) {
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
						model.setVideoList(Optional.of(vidNodeList));
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
	
	@SuppressWarnings("deprecation")
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
}

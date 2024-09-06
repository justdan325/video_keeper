import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class EditDialog extends JDialog implements WindowListener{
	private static final int 	WIN_X 				= 550;
	private static final int 	WIN_Y 				= 325;
	private static final int	TEXT_FIELD_X		= 400;
	private static final int	TEXT_FIELD_Y		= 25;
	private static final String	DIALOG_TITLE		= MainGui.PROG_NAME + "--Edit Video Metadata";
	private static final String	URL_FLD_TITLE		= "URL         ";
	private static final String	TTL_FLD_TITLE		= "Title        ";
	private static final String	DATE_FLD_TITLE		= "Date        ";
	private static final String	TIME_FLD_TITLE		= "Duration ";
	private static final String	CHNL_FLD_TITLE		= "Channel   ";
	private static final String	SAVE_BUTTON_TTL		= "Save";
	
	private Optional<VideoDataNode> node;
	private Component parent;
	private JLabel urlLabel;
	private JLabel titleLabel;
	private JLabel dateLabel;
	private JLabel timeLabel;
	private JLabel channelLabel;
	private JTextField urlField;
	private JTextField titleField;
	private JTextField dateField;
	private JTextField timeField;
	private JTextField channelField;
	private JButton saveButton;
	private JPanel mainPanel;

	public static void main(String[] args) {
		EditDialog dialog = new EditDialog(null);
		
		dialog.setVisible(true);
	}
	
	public EditDialog(Component parent) {
		this.parent = parent;
		this.node = Optional.empty();
		
		this.getContentPane().setBackground(MainGui.PROG_COLOR_BKRND);
		this.setTitle(DIALOG_TITLE);
		this.setSize(new Dimension(WIN_X, WIN_Y));
		this.setResizable(false);
		this.addWindowListener(this);
		
		buildDialog();
	}
	
	public VideoDataNode editNode(VideoDataNode node) {
		this.node = Optional.of(node);
		
		urlField.setText(node.getUrl());
		titleField.setText(node.getTitle());
		dateField.setText(node.getDate());
		timeField.setText(node.getTime());
		channelField.setText(node.getChannel());
		
		if (parent != null) {
			this.setLocationRelativeTo(parent);
		}
		
		this.setVisible(true);
		
		return node;
	}
	
	private void buildDialog() {
		this.urlLabel = new JLabel(URL_FLD_TITLE);
		this.titleLabel = new JLabel(TTL_FLD_TITLE);
		this.dateLabel = new JLabel(DATE_FLD_TITLE);
		this.timeLabel = new JLabel(TIME_FLD_TITLE);
		this.channelLabel = new JLabel(CHNL_FLD_TITLE);
		this.urlField = new JTextField();
		this.titleField = new JTextField();
		this.dateField = new JTextField();
		this.timeField = new JTextField();
		this.channelField = new JTextField();
		this.saveButton = new JButton(SAVE_BUTTON_TTL);
		this.mainPanel = new JPanel(new GridLayout(6, 1));
		
		urlField.setPreferredSize(new Dimension(TEXT_FIELD_X, TEXT_FIELD_Y));
		titleField.setPreferredSize(new Dimension(TEXT_FIELD_X, TEXT_FIELD_Y));
		dateField.setPreferredSize(new Dimension(TEXT_FIELD_X, TEXT_FIELD_Y));
		timeField.setPreferredSize(new Dimension(TEXT_FIELD_X, TEXT_FIELD_Y));
		channelField.setPreferredSize(new Dimension(TEXT_FIELD_X, TEXT_FIELD_Y));
		saveButton.setBackground(MainGui.PROG_COLOR_BTN_EN);
		
		JPanel urlPanel = new JPanel();
		JPanel titlePanel = new JPanel();
		JPanel datePanel = new JPanel();
		JPanel timePanel = new JPanel();
		JPanel channelPanel = new JPanel();
		JPanel saveButtonPanel = new JPanel();
		
		urlPanel.add(urlLabel);
		urlPanel.add(urlField);
		titlePanel.add(titleLabel);
		titlePanel.add(titleField);
		datePanel.add(dateLabel);
		datePanel.add(dateField);
		timePanel.add(timeLabel);
		timePanel.add(timeField);
		channelPanel.add(channelLabel);
		channelPanel.add(channelField);
		saveButtonPanel.add(saveButton);
		
		mainPanel.add(urlPanel);
		mainPanel.add(titlePanel);
		mainPanel.add(datePanel);
		mainPanel.add(timePanel);
		mainPanel.add(channelPanel);
		mainPanel.add(saveButtonPanel);
		
		this.add(mainPanel);
		
		addActionListeners();
	}
	
	private void addActionListeners() {
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (node.isPresent()) {
					node.get().setUrl(urlField.getText().trim());
					node.get().setTitle(titleField.getText().trim());
					node.get().setDate(dateField.getText().trim());
					node.get().setTime(timeField.getText().trim());
					node.get().setChannel(channelField.getText().trim());
					
					setVisible(false);
					
					node = Optional.empty();
					urlField.setText("");
					titleField.setText("");
					dateField.setText("");
					timeField.setText("");
					channelField.setText("");
				}
			}
		});
	}

	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowClosing(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}
}

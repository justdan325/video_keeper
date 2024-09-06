import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class OptionsDialog extends JDialog {
	private static final String DIALOG_TITLE 		= "Seach Options";
	private static final String OK_BTN_TTITLE 		= "Ok";
	private static final String CASE_SENS_CHKBX_LBL = "case sensitive";
	private static final String SRCH_TTL_CHKBX_LBL 	= "search through video titles";
	private static final String SRCH_DATE_CHKBX_LBL = "search through video dates and times";
	private static final String SRCH_CHNL_CHKBX_LBL = "search through video channel authors";
	private static final String PLAY_DEL_CHKBX_LBL 	= "delete video from list after clicking \"play\"";
	private static final int 	FONT_SIZE_TITLE 	= 25;
	private static final int 	WIN_X 				= 470;
	private static final int 	WIN_Y 				= 250;
	
	private DataModel mainModel;
	private Component parent;
	
	private JPanel mainPanel;
	private JPanel primaryOptionsPanel;
	private JPanel checkBoxPanel;
	
	private JButton okButton;
	
	private JCheckBox caseSensitiveCheckBox;
	private JCheckBox searchTitleCheckBox;
	private JCheckBox searchDateCheckBox;
	private JCheckBox searchChannelCheckBox;
	private JCheckBox playDeleteCheckbox;;
	
	public static void main(String[] args) {
		OptionsDialog dialog = new OptionsDialog(new DataModel(), null);

		dialog.setVisible(true);
	}
	
	public OptionsDialog(DataModel mainModel, Component parent) {
		this.mainModel = mainModel;
		this.parent = parent;
		this.mainPanel = new JPanel(new BorderLayout());
		this.primaryOptionsPanel = new JPanel();
		this.okButton = new JButton(OK_BTN_TTITLE);
		
		JLabel titleDialog = new JLabel(DIALOG_TITLE);
		titleDialog.setHorizontalAlignment(JLabel.CENTER);
		titleDialog.setFont(new Font(MainGui.PROG_FONT, Font.BOLD, FONT_SIZE_TITLE));
		
		makeCheckBoxPanel();
		addActionListeners();
		
		primaryOptionsPanel.add(checkBoxPanel);
		
		mainPanel.add(titleDialog, BorderLayout.NORTH);
		mainPanel.add(primaryOptionsPanel, BorderLayout.CENTER);
		mainPanel.add(okButton, BorderLayout.SOUTH);
		
		this.add(mainPanel);
		this.pack();
		
		this.setTitle(MainGui.PROG_NAME + " -- " + DIALOG_TITLE);
		this.setSize(new Dimension(WIN_X, WIN_Y));
	}

	private void makeCheckBoxPanel() {
		this.checkBoxPanel = new JPanel(new GridLayout(5, 1));
		this.caseSensitiveCheckBox = new JCheckBox(CASE_SENS_CHKBX_LBL);
		this.searchTitleCheckBox = new JCheckBox(SRCH_TTL_CHKBX_LBL);
		this.searchDateCheckBox = new JCheckBox(SRCH_DATE_CHKBX_LBL);
		this.searchChannelCheckBox = new JCheckBox(SRCH_CHNL_CHKBX_LBL);
		this.playDeleteCheckbox = new JCheckBox(PLAY_DEL_CHKBX_LBL);
		
		caseSensitiveCheckBox.setSelected(mainModel.isCaseSensitive());
		searchTitleCheckBox.setSelected(mainModel.isSearchThruTitles());
		searchDateCheckBox.setSelected(mainModel.isSearchThruDates());
		searchChannelCheckBox.setSelected(mainModel.isSearchThruChannels());
		playDeleteCheckbox.setSelected(mainModel.isPlayAndDelete());
		
		checkBoxPanel.add(caseSensitiveCheckBox);
		checkBoxPanel.add(searchTitleCheckBox);
		checkBoxPanel.add(searchDateCheckBox);
		checkBoxPanel.add(searchChannelCheckBox);
		checkBoxPanel.add(playDeleteCheckbox);
	}
	
	@Override
	public void setVisible(boolean visible) {
		if (parent != null && visible) {
			this.setLocationRelativeTo(parent);
		}
		
		super.setVisible(visible);
	}
	
	private void addActionListeners() {
		caseSensitiveCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		searchTitleCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		searchDateCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		searchChannelCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
			}
		});
		
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ok();
			}
		});
	}
	
	private void ok() {
		boolean caseSensitive = caseSensitiveCheckBox.isSelected() ? true : false;
		boolean searchThruTitles = searchTitleCheckBox.isSelected() ? true : false;
		boolean searchThruDates = searchDateCheckBox.isSelected() ? true : false;
		boolean searchThruChannels = searchChannelCheckBox.isSelected() ? true : false;
		boolean playAndDelete = playDeleteCheckbox.isSelected() ? true : false;
		
		mainModel.setCaseSensitive(caseSensitive);
		mainModel.setSearchThruTitles(searchThruTitles);
		mainModel.setSearchThruDates(searchThruDates);
		mainModel.setSearchThruChannels(searchThruChannels);
		mainModel.setPlayAndDelete(playAndDelete);
		
		this.dispose();
		
	}
}

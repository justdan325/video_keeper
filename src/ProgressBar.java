import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class ProgressBar extends JDialog {
	private final String PROGRESS_PREFIX = "Reloaded ";
	
	private JProgressBar progressBar;
	private JFrame parent;
	private JLabel progressCountLabel;
	private int count;
	private int max;

	public ProgressBar(JFrame parent) {
		this.progressBar = new JProgressBar();
		this.parent = parent;
		this.progressCountLabel = new JLabel();
		this.count = 0;
		this.max = 0;
		
		this.progressBar.setBounds(10, 45, 371, 22);
		this.progressCountLabel.setText(PROGRESS_PREFIX + "0 of " + max);
		this.progressCountLabel.setHorizontalTextPosition(JLabel.CENTER);
		this.progressCountLabel.setHorizontalAlignment(JLabel.CENTER);
		
		this.setTitle(MainGui.PROG_NAME + " -- Reloading Videos");
		this.getContentPane().add(progressBar);
		this.setBounds(100, 100, 407, 119);
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.getContentPane().setLayout(new BorderLayout(0, 20));
		this.setResizable(false);
		this.getContentPane().add(progressCountLabel, BorderLayout.NORTH);
		this.getContentPane().add(new JLabel(" "), BorderLayout.WEST);
		this.getContentPane().add(new JLabel(" "), BorderLayout.EAST);
		this.getContentPane().add(progressBar, BorderLayout.CENTER);
		this.getContentPane().add(new JLabel(" "), BorderLayout.SOUTH);
	}
	
	public boolean progress() {
		boolean progressed = false;
		
		if (count < max) {
			count++;
			progressCountLabel.setText(PROGRESS_PREFIX + count + " of " + max);
			progressBar.setValue(count);
		}
		
		return progressed;
	}
	
	public void setMax(int max) {
		this.progressBar.setMaximum(max);
		this.max = max;
	}
	
	public int getMax() {
		return max;
	}
	
	public void showProgressBar() {
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
	
	public void kill() {
		this.dispose();
	}
}
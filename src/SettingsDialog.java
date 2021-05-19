import java.awt.Dimension;

import javax.swing.JDialog;
import javax.swing.JFrame;

public class SettingsDialog extends JDialog {
	private static final int WIN_X = 200;
	private static final int WIN_Y = 200;
	
	private JFrame parent;
	
	public static void main(String[] args) {
		new SettingsDialog(null);
	}

	public SettingsDialog(JFrame parent) {
		this.parent = parent;
		
		this.setTitle(MainGui.PROG_NAME + " -- Settings");
		this.setSize(new Dimension(WIN_X, WIN_Y));
		this.setResizable(false);
	}
	
	public void showDialog() {
		this.setLocationRelativeTo(parent);
		this.setVisible(true);
	}
}

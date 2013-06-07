package com.mcode.mamoi.mcide;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

public class mcideStatusPanel extends JPanel {
	public mcideStatusPanel() {
		setBorder(new BevelBorder(BevelBorder.LOWERED));
		//setPreferredSize(new Dimension(frame.getWidth(), 16));
		//setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		JLabel statusLabel = new JLabel("status");
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
	}
}

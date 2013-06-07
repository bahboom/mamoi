package com.mcode.mamoi.mcide;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

public class mcide extends JPanel {
	private JTextPane mainTextPane = null;
	private mcideStatusPanel statusPanel = null;
	
	public mcide() {
		setLayout(new BorderLayout());
		mainTextPane = new JTextPane();
		statusPanel = new mcideStatusPanel();
		JScrollPane mainScrollPane = new JScrollPane(mainTextPane);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(mainScrollPane, BorderLayout.CENTER);
		this.add(statusPanel, BorderLayout.SOUTH);
	}
	
	public static void main(String args[]) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JFrame mcideFrame = new JFrame("mcide");
				JPanel mcidePanel = new mcide();
				mcideFrame.setContentPane(mcidePanel);
				mcideFrame.setSize(600, 400);
				mcideFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				mcideFrame.setVisible(true);
			}
		});
	}
}

package com.mcode.mamoi.mcide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class mcide extends JPanel {
	private JTextPane mainTextPane = null;
	private StyledDocument doc = null;
	private mcideStatusPanel statusPanel = null;
	private Set<String> mccKeywords = null;
	
	public mcide() {
		mccKeywords = new HashSet<String>();
		mccKeywords.add("pz");
		mccKeywords.add("def");
		mccKeywords.add("edef");
		mccKeywords.add("include");
		mccKeywords.add("hex");
		mccKeywords.add("bin");
		mccKeywords.add("dec");
		mccKeywords.add("str");
		mccKeywords.add("lr");
		setLayout(new BorderLayout());
		doc = (StyledDocument) new DefaultStyledDocument();
		mainTextPane = new JTextPane(doc);
		Style plainStyle = mainTextPane.addStyle("plain", null);
		Style keywordStyle = mainTextPane.addStyle("keyword", null);
		StyleConstants.setForeground(keywordStyle, Color.MAGENTA);
		StyleConstants.setBold(keywordStyle, true);
		
		StyleConstants.setForeground(plainStyle, Color.BLACK);
		
		mainTextPane.addKeyListener(new KeyListener() {

			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
			}

			public void keyTyped(KeyEvent e) {
				walk(doc);
			}
			
		});
		
		statusPanel = new mcideStatusPanel();
		JScrollPane mainScrollPane = new JScrollPane(mainTextPane);
        mainScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.add(mainScrollPane, BorderLayout.CENTER);
		this.add(statusPanel, BorderLayout.SOUTH);
	}
	
	private void walk(StyledDocument doc) {
		// reset all syntax highlighting
		doc.setCharacterAttributes(0, doc.getLength() + 1, mainTextPane.getStyle("plain"), true);
		
		String text = mainTextPane.getText();
		for(String keyword : mccKeywords) {
		int i = text.indexOf(keyword);
			while(i != -1) {
				doc.setCharacterAttributes(i, keyword.length(), mainTextPane.getStyle("keyword"), true);
				i = text.indexOf(keyword, i + 1);
			}
		}
		
		//doc.setParagraphAttributes(0, doc.getLength(), mainTextPane.getStyle("plain"), true);
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

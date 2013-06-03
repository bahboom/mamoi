package com.mcode.mamoi.mcc.interpreter;

import java.util.Stack;

public class ModeManager {
	private Stack<Integer> radixStack = null;
	private String definitionName = null; // the command that it is defining in defineMode
	private boolean defineMode = false;
	private boolean quoteMode = false;
	private boolean includeMode = false;
	private boolean padZeroMode = false;
	
	public ModeManager() {
		radixStack = new Stack<Integer>();
	}
	public void pushRadix(int radix) {
		radixStack.push(radix);
	}
	public int popRadix() {
		return radixStack.pop();
	}
	public int peekRadix() {
		return radixStack.peek();
	}
	public boolean isPadZeroMode() {
		return padZeroMode;
	}
	public void setPadZeroMode(boolean padding) {
		padZeroMode = padding;
	}
	public boolean isDefineMode() {
		return defineMode;
	}
	public void setDefineMode(boolean defining) {
		defineMode = defining;
	}
	
	public boolean isIncludeMode() {
		return includeMode;
	}
	
	public void setIncludeMode(boolean including) {
		includeMode = including;
	}
	public void setDefinitionName(String name) {
		definitionName = name;
	}
	public String getDefinitionName() {
		return definitionName;
	}
	
	public boolean isQuoteMode() {
		return quoteMode;
	}
	
	public void setQuoteMode(boolean quoting) {
		quoteMode = quoting;
	}
}

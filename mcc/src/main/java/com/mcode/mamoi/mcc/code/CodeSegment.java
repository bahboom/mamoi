package com.mcode.mamoi.mcc.code;

import java.util.ArrayList;
import java.util.List;

import com.mcode.mamoi.mcc.interpreter.CodeInterpreter;


public class CodeSegment implements CodeElement {
	CodeInterpreter ci = null;
	List<CodeElement> codeElements = new ArrayList<CodeElement>();
	
	public CodeSegment(CodeInterpreter ci) {
		this.ci = ci;
	}
	
	public void addCodeElement(CodeElement element) {
		if(element != null) {
			codeElements.add(element);
		}
	}
	 
	public List<Integer> getBytes() {
		List<Integer> completeCode = new ArrayList<Integer>();
		for(int i = 0; i < codeElements.size(); i++) {
			CodeElement element = codeElements.get(i);
			completeCode.addAll(element.getBytes());
		}
		
		return completeCode;
	}
}

package com.mcode.mamoi.mcc.code;

import java.util.ArrayList;
import java.util.List;

public class PadZeroCodeElement implements CodeElement {
	List<Integer> bytes = null;
	public PadZeroCodeElement(int numOfZeros) {
		bytes = new ArrayList<Integer>();
		
		for(int i = 0; i < numOfZeros; i++) {
			bytes.add(0);
		}
	}
	public List<Integer> getBytes() {
		return bytes;
	}
	
}

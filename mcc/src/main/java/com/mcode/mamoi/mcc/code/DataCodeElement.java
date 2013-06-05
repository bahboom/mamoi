package com.mcode.mamoi.mcc.code;

import java.util.ArrayList;
import java.util.List;

import com.mcode.mamoi.mcc.ByteFactory;

public class DataCodeElement implements CodeElement {
	List<Integer> bytes = null; 
	public DataCodeElement(String data, int radix) {
		if(radix == 0) { // str mode
			bytes = new ArrayList<Integer>();
			byte[] dataBytes = data.getBytes();
			for(int i = 0; i < dataBytes.length; i++) {
				bytes.add((int)dataBytes[i]);
			}
			
		} else {
			long value = Long.parseLong(data, radix);
			bytes = ByteFactory.asBytes(value);
		}
	}
	
	public List<Integer> getBytes() {
		return bytes;
	}
}

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
	
	// return value in a little endian address bytes
	private byte[] valueToBytes(long address, int numBytes) {
		boolean isNeg = false;
		if(address < 0) {
			isNeg = true;
		}
		byte[] b = new byte[numBytes];
		
		for(int i = 1; i <= numBytes; i++) {
			byte v = (byte)(address % 256);
			if(isNeg) {
				v += 255;
			}
		    b[i-1] = v;
		    address /= 256;
		}
		
		return b;
	}
	
	public List<Integer> getBytes() {
		return bytes;
	}
}

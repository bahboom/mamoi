package com.mcode.mamoi.mcc;

import java.util.ArrayList;
import java.util.List;

public class ByteFactory {
	
	public static List<Integer> asBytes(long value) {
		List<Integer> bytes = new ArrayList<Integer>();
        boolean isNegative = value < 0;	
        int normalizer = isNegative? 255 : 0;
		int b = (int)(value % 256) + normalizer ;
		if(isNegative) {
			b += 1;
		}
		bytes.add(b);
		value /= 256;
		while(value != 0) {
			
			b = (int)(value % 256) + normalizer;
			
			bytes.add(b);
			value /= 256;
		}
				
		return bytes;
	}
	
	public static List<Integer> asBytes(long value, int byteCount) {
		List<Integer> bytes = asBytes(value);
		int missingBytes = byteCount - bytes.size();
		for(int i = 0; i < missingBytes; i ++) {
			if(value < 0) {
				bytes.add(255);
			} else {
				bytes.add(0);
			}
		}
		
		return bytes;
	}
}

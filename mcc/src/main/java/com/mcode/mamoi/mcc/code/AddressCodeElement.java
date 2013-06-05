package com.mcode.mamoi.mcc.code;

import java.util.List;

import com.mcode.mamoi.mcc.ByteFactory;
import com.mcode.mamoi.mcc.interpreter.ReferenceManager;

public class AddressCodeElement implements CodeElement {
	private ReferenceManager rm = null;
	private String referenceLabel = null;
	private int currentByteLocation = 0;
	public AddressCodeElement(ReferenceManager rm, String referenceLabel, int currentByteLocation) {
		this.rm = rm;
		this.referenceLabel = referenceLabel;
		this.currentByteLocation = currentByteLocation;
	}
	public List<Integer> getBytes() {
		String labelName = referenceLabel.substring(referenceLabel.indexOf(":") + 1);
		boolean relative = referenceLabel.startsWith("rel");
		int numBytes = Integer.parseInt(referenceLabel.substring(3, referenceLabel.indexOf(":"))) / 8;
		int address = rm.getAddress(labelName);
		if(relative) {
			address = (address - (currentByteLocation+numBytes)) ;
			//if(address < 0) {
			//	address ++;
			//}
		}
		
		return ByteFactory.asBytes(address, numBytes);
	}

}

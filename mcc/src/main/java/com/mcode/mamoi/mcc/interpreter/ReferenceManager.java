package com.mcode.mamoi.mcc.interpreter;

import java.util.HashMap;
import java.util.Map;

public class ReferenceManager {
	private Map<String, Integer> addresses = null;
	public ReferenceManager() {
		addresses = new HashMap<String, Integer>();
	}
	public boolean registerAddress(String label, int address) {
		if(addresses.containsKey(label)) {
			return false;
		} else {
			addresses.put(label, address);
			return true;
		}
	}
}

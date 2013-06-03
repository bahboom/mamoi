package com.mcode.mamoi.mcc.interpreter;

import java.util.HashSet;
import java.util.Set;

public class IncludedSourceManager {
	Set<String> includedSources = null;
	public IncludedSourceManager() {
		includedSources = new HashSet<String>();
	}
	public boolean registerInclude(String absPath) {
		if(includedSources.contains(absPath)) {
			return false;
		} else {
			includedSources.add(absPath);
			return true;
		}
		
	}
}

package com.mcode.mamoi.mcc.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mcode.mamoi.mcc.exception.MCCException;

public class UserDefinedCommandManager {
	private Map<String, List<String>> commands = null;
	
	public UserDefinedCommandManager() {
		commands = new HashMap<String, List<String>>();
	}
	
	public boolean containsCommand(String command) {
		return commands.containsKey(command);
	}
	
	public List<String> getUserDefinedCode(String command) {
		return commands.get(command);
	}
	
	/**
	 * Returns false if command already exist.
	 */
	public boolean createUserDefinedCode(String command) throws MCCException {
		if(commands.containsKey(command)) {
			return false;
		} else {
			List<String> codeList = new ArrayList<String>();
			commands.put(command, codeList);
			return true;
		}
	}
	
	/**
	 * Returns false if command is not yet defined.
	 */
	public boolean addUserDefinedCode(String command, String code) throws MCCException {
		List<String> codeList = commands.get(command);
		if(codeList == null) {
			return false;
		}
		codeList.add(code);
		return true;
	}
}

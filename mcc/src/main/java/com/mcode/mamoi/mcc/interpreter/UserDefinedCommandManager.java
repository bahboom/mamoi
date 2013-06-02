package com.mcode.mamoi.mcc.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mcode.mamoi.mcc.exception.UserDefinedCommandException;

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
	
	public void createUserDefinedCode(String command) throws UserDefinedCommandException {
		if(commands.containsKey(command)) {
			throw new UserDefinedCommandException("ERROR: " + command + " is already defined!");
		} else {
			List<String> codeList = new ArrayList<String>();
			commands.put(command, codeList);
		}
	}
	
	public void addUserDefinedCode(String command, String code) throws UserDefinedCommandException {
		List<String> codeList = commands.get(command);
		if(codeList == null) {
			throw new UserDefinedCommandException("ERROR: " + command + " is not createdd yet!");
		}
		codeList.add(code);
	}
}

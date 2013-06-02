package com.mcode.mamoi.mcc.interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mcode.mamoi.mcc.code.CodeSegment;
import com.mcode.mamoi.mcc.code.DataCodeElement;
import com.mcode.mamoi.mcc.exception.MCCException;
import com.mcode.mamoi.mcc.exception.UserDefinedCommandException;

public class CodeInterpreter {
	private Set<String> mccKeywords = null;
	private ModeManager mm = null;
	private UserDefinedCommandManager udcm = null;
	
	public CodeInterpreter(ModeManager mm, UserDefinedCommandManager udcm) {
		this.mm = mm;
		this.udcm = udcm;
		mccKeywords = new HashSet<String>();
		mccKeywords.add("pz");
		mccKeywords.add("def");
		mccKeywords.add("edef");
		mccKeywords.add("include");
		mccKeywords.add("hex");
		mccKeywords.add("bin");
		mccKeywords.add("dec");
		mccKeywords.add("lr");
		
	}
	
	public CodeSegment translate(File mccFile) throws MCCException, IOException {
		CodeSegment cs = new CodeSegment(this);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mccFile)));
		
		String line = br.readLine();
		while ( line != null ) {
			line = line.replaceAll(";.*", ""); // remove comments from code
			if(!line.trim().isEmpty()) {
				String[] elements = line.split( " " );
				for(int i = 0; i < elements.length; i++) {
					String element = elements[i];
					if(!element.trim().isEmpty()) {
						interpret(cs, element);
					}
				}
			}
			line = br.readLine();
		}
		br.close();
		return cs;
	}
	
	private void interpret(CodeSegment cs, String element) throws MCCException, IOException {
		if(mm.isDefineMode()) {
			if(mm.getDefinitionName() == null) {
				if(mccKeywords.contains(element)) {
					throw new UserDefinedCommandException("ERROR: " + element + " is a keyword.  Cannot define command!");
				}
				if(udcm.containsCommand(element)) {
					throw new UserDefinedCommandException("ERROR: User defined definition already exist");
				}
				
				mm.setDefinitionName(element);
				udcm.createUserDefinedCode(element);
			} else {
				if (element.equals("edef")) {
					if(!mm.isDefineMode()) {
						throw new MCCException("ERROR: Cannot 'edef' without 'def'");
					}
					mm.setDefineMode(false);
					mm.setDefinitionName(null);
				} else {
					udcm.addUserDefinedCode(mm.getDefinitionName(), element);
				}
			}
			return;
		} 
		
		if(mm.isIncludeMode()) {
			mm.setIncludeMode(false);
			cs.addCodeElement(translate(new File(element)));
			return;
		}
		
		if(element.equals("def")) {
			mm.setDefineMode(true);
		} else if(element.equals("include")){
			mm.setIncludeMode(true);
		} else if (element.equals("hex")) {
			mm.pushRadix(16);
		} else if (element.equals("dec")) {
			mm.pushRadix(10);
		} else if (element.equals("bin")) {
			mm.pushRadix(2);
		} else if (element.equals("lr")) {
			mm.popRadix();
		} else if (udcm.containsCommand(element)) {
			List<String> codeList = udcm.getUserDefinedCode(element);
			for(String code : codeList) {
				interpret(cs, code);
			}
		} else {
			try {
				cs.addCodeElement(new DataCodeElement(element, mm.peekRadix()));
			} catch(NumberFormatException e) {
				throw new MCCException("'" + element + "' is not a valid data value for radix " + mm.peekRadix());
			}
		}
	}
}

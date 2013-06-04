package com.mcode.mamoi.mcc.interpreter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mcode.mamoi.mcc.code.AddressCodeElement;
import com.mcode.mamoi.mcc.code.CodeSegment;
import com.mcode.mamoi.mcc.code.DataCodeElement;
import com.mcode.mamoi.mcc.code.PadZeroCodeElement;
import com.mcode.mamoi.mcc.exception.MCCException;

public class CodeInterpreter {
	private Set<String> mccKeywords = null;
	private ModeManager mm = null;
	private UserDefinedCommandManager udcm = null;
	private ReferenceManager rm = null;
	private IncludedSourceManager ism = null;
	
	
	public CodeInterpreter(ModeManager mm, UserDefinedCommandManager udcm, ReferenceManager rm, IncludedSourceManager ism) {
		this.mm = mm;
		this.udcm = udcm;
		this.rm = rm;
		this.ism = ism;
		mccKeywords = new HashSet<String>();
		mccKeywords.add("pz");
		mccKeywords.add("def");
		mccKeywords.add("edef");
		mccKeywords.add("include");
		mccKeywords.add("hex");
		mccKeywords.add("bin");
		mccKeywords.add("dec");
		mccKeywords.add("str");
		mccKeywords.add("lr");
	}
	
	public CodeSegment translate(File mccFile, int byteOffset) throws MCCException, IOException {
		CodeSegment cs = new CodeSegment(this);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(mccFile)));
		
		String line = br.readLine();
		int lineNum = 1;
		while ( line != null ) {
			line = line.replaceAll(";.*", ""); // remove comments from code
			if(!line.trim().isEmpty()) {
				String[] elements = line.split( " " );
				for(int i = 0; i < elements.length; i++) {
					String element = elements[i];
					if(!element.trim().isEmpty()) {
						interpret(cs, element, byteOffset, mccFile.getAbsolutePath(), lineNum);
					}
				}
			}
			line = br.readLine();
			lineNum++;
		}
		br.close();
		if(mm.isDefineMode()) {
			throw new MCCException("Missing edef!", mccFile.getAbsolutePath(), lineNum-1);
		}
		if(mm.isIncludeMode()) {
			throw new MCCException("Missing include source file!", mccFile.getAbsolutePath(), lineNum-1);
		}
		if(mm.isPadZeroMode()) {
			throw new MCCException("Missing pz parameter!", mccFile.getAbsolutePath(), lineNum-1);
		}
		return cs;
	}
	
	private void interpret(CodeSegment cs, String element, int byteOffset, String sourceFile, int lineNum) throws MCCException, IOException {
		if(mm.isDefineMode()) {
			if(mm.getDefinitionName() == null) {
				if(mccKeywords.contains(element)) {
					throw new MCCException("ERROR: " + element + " is a keyword.  Cannot define command!", sourceFile, lineNum);
				}
				if(udcm.containsCommand(element)) {
					throw new MCCException("ERROR: User defined definition already exist", sourceFile, lineNum);
				}
				
				mm.setDefinitionName(element);
				if(!udcm.createUserDefinedCode(element)) {
					throw new MCCException(element + " is already defined!", sourceFile, lineNum);
				}
			} else {
				if (element.equals("edef")) {
					if(!mm.isDefineMode()) {
						throw new MCCException("Cannot 'edef' without 'def'", sourceFile, lineNum);
					}
					mm.setDefineMode(false);
					mm.setDefinitionName(null);
				} else {
					if(!udcm.addUserDefinedCode(mm.getDefinitionName(), element)) {
						throw new MCCException(element + " command is not created yet!", sourceFile, lineNum);
					}
				}
			}	
			return;
		} 
		
		if(mm.isIncludeMode()) {
			mm.setIncludeMode(false);
			File importFile = new File(element);
			if(!ism.registerInclude(importFile.getAbsolutePath())) {
				throw new MCCException("Circular include: " + element, sourceFile, lineNum);
			}
			cs.addCodeElement(translate(importFile, cs.getBytes().size() + byteOffset));
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
		} else if (element.equals("str")) {
			mm.pushRadix(0);
		} else if (element.equals("lr")) {
			mm.popRadix();
		} else if (element.equals("pz")) {
			mm.setPadZeroMode(true);
		} else if (element.endsWith(":")) {
			if(!rm.registerAddress(element.substring(0, element.length() - 1), cs.getBytes().size() + byteOffset)) {
				throw new MCCException(element + " label already exist!", sourceFile, lineNum);
			}
		} else if (element.matches("loc[1-9][0-9]*:.*") || element.matches("rel[1-9][0-9]*:.*")) {
			cs.addCodeElement(new AddressCodeElement(rm, element, cs.getBytes().size() + byteOffset));
		} else if (udcm.containsCommand(element)) {
			List<String> codeList = udcm.getUserDefinedCode(element);
			for(String code : codeList) {
				interpret(cs, code, byteOffset, sourceFile, lineNum);
			}
		} else {
			try {
				if(mm.isPadZeroMode()) {
					mm.setPadZeroMode(false);
					int currentBytes = cs.getBytes().size() + byteOffset;
					int pzParam = Integer.parseInt(element, mm.peekRadix());
					int zerosNeeded = pzParam - currentBytes;
					if(zerosNeeded < 0) {
						throw new MCCException("Cannot pad zero, byte pointer already passed! Current bytes: " + currentBytes + ", Pad location: " + pzParam, sourceFile, lineNum);
					}
					
					cs.addCodeElement(new PadZeroCodeElement(zerosNeeded));
				} else {
					cs.addCodeElement(new DataCodeElement(element, mm.peekRadix()));
				}
				
			} catch(NumberFormatException e) {
				throw new MCCException("'" + element + "' is not a valid data value for radix " + mm.peekRadix(), sourceFile, lineNum);
			}
		}
	}
}

package com.mcode.mamoi.mcc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class mcc {
	private int lastRadix = 10;
	private int radix = 10;
	private boolean padding = false;
	
	private boolean defining = false;
	private String definitionName = null;
	private List<String> elements = new ArrayList<String>();
	private Map<String, List<String>> userdefinedCommands = new HashMap<String, List<String>>(); 
	
	public mcc() {
		
	}
	
	public void addElement(String e) throws Exception {
		e = e.toLowerCase();
		
		if(e.equals("def")) {
			if(defining) {
				throw new Exception("error: already defining");
			}
			defining = true;
			return;
		} else if (e.equals("edef")) {
			if(!defining) {
				throw new Exception("error: not defining");
			}
			defining = false;
			definitionName = null;
			return;
		} 
		
		if(defining) {
			if(definitionName == null) {
				if(userdefinedCommands.containsKey(e)) {
					throw new Exception("error: definition already exist");
				}
				definitionName = e;
				userdefinedCommands.put(e, new ArrayList<String>());
			} else {
				userdefinedCommands.get(definitionName).add(e);
			}
			
		} else {
			elements.add(e);
		}
	}
	
	private void expandDefinitions() {
		
		while(containsUserDefinition()) {
			List<String> expandedElements = new ArrayList<String>();
			for(String element : elements) {
				if(userdefinedCommands.containsKey(element)) {
					expandedElements.addAll(userdefinedCommands.get(element));
				} else {
					expandedElements.add(element);
				}
			}
			
			elements = expandedElements;
			
		}
	}
	
	private boolean containsUserDefinition() {
		for(String element : elements) {
			if(userdefinedCommands.containsKey(element))
				return true;
		}
		return false;
	}
	
	public int compile(String binaryFile) throws Exception {
		expandDefinitions();
		int bytesWritten = 0;
		OutputStream fos = new FileOutputStream(binaryFile);
		
		
		for(String element : elements) {
			if("pz".equals(element)) {
				padding = true;
				continue;
			} else if("hex".equals(element)) {
				lastRadix = radix;
				radix = 16;
				continue;
			} else if("bin".equals(element)) {
				lastRadix = radix;
				radix = 2;
				continue;
			} else if("dec".equals(element)) {
				lastRadix = radix;
				radix = 10;
				continue;
			} else if("lr".equals(element)) {
				int tmp = radix;
				radix = lastRadix;
				lastRadix = tmp;
				continue;
			}
			
			if(padding) {
				while(bytesWritten < Integer.parseInt(element)) {
					fos.write(0);
					bytesWritten++;
				}
				padding = false;
				continue;
			}
			
			fos.write(Integer.parseInt(element, radix));
			bytesWritten++;
		}
		fos.close();
		return bytesWritten;
	}
	
    public static void main( String[] args ) throws Exception {
    	String sourceFile = args[0];
    	String binaryFile = args[1];
    	mcc compiler = new mcc();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceFile)));
		
		String line = br.readLine();
		while ( line != null ) {
			line = line.replaceAll(";.*", ""); // remove comments from code
			if(!line.trim().isEmpty()) {
				String[] elements = line.split( " " );
				for(int i = 0; i < elements.length; i++) {
					String element = elements[i];
					if(!element.trim().isEmpty()) {
						compiler.addElement(element);
					}
				}
			}
			line = br.readLine();
		}
		br.close();
    	System.out.println("Bytes written: " + compiler.compile(binaryFile));
    }
}
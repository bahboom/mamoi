package com.mcode.mamoi.mcc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class mcc {
	private Stack<Integer> radix = new Stack<Integer>();
	private Map<String, Long> addressLocations = new HashMap<String, Long>();
	private Map<String, ArrayList<Long>> addressRefs = new HashMap<String, ArrayList<Long>>();
	private boolean padding = false;
	
	private boolean defining = false;
	private String definitionName = null;
	private List<String> elements = new ArrayList<String>();
	private Map<String, List<String>> userdefinedCommands = new HashMap<String, List<String>>(); 
	
	public mcc() {
		radix.push(10);
	}
	
	public void addElement(String e) throws Exception {
		
		// Do not add def and edef to elements
        // Create internal definition for defs instead
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
		}/* else if (e.startsWith("loc8:")) {
			return;
		} else if (e.startsWith("loc16:")) {
			return;
		} else if (e.startsWith("loc32:")) {
			return;
		} else if (e.startsWith("loc64:")) {
			return;
		} else if (e.endsWith(":")) {
		    return; 
		} */
		
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
	
	// Display element contents to be written in byte code
	private void debug() {
		for(String element : elements) {
			System.out.print("[" + element + "]");
		}
		System.out.println();
	}
	
	private void addAddressRef(String ref, long offset) {
		if(addressRefs.get(ref) == null) {
			ArrayList<Long> offsets = new ArrayList<Long>();
			offsets.add(offset);
			addressRefs.put(ref, offsets);
		} else {
			addressRefs.get(ref).add(offset);
		}
	}
	
	private void saveAddressLocation(String label, long address) {
		addressLocations.put(label, address);
	}
	
	public long compile(String binaryFile) throws Exception {
		expandDefinitions();
		debug();
		long bytesWritten = 0;
		RandomAccessFile fos = new RandomAccessFile(new File(binaryFile), "rw");
		
		for(String element : elements) {
			if(element.startsWith("loc8:") || element.startsWith("rel8:")) {
				addAddressRef(element, bytesWritten);
				fos.write(0);
				bytesWritten++;
				continue;
			} else if(element.startsWith("loc16:") || element.startsWith("rel16:")) {
				addAddressRef(element, bytesWritten);
				fos.write(0);
				fos.write(0);
				bytesWritten+=2;
				continue;
			} else if(element.startsWith("loc32:") || element.startsWith("rel32:")) {
				addAddressRef(element, bytesWritten);
				fos.write(0);
				fos.write(0);
				fos.write(0);
				fos.write(0);
				bytesWritten+=4;
				continue;
			} else if(element.startsWith("loc64:") || element.startsWith("rel64:")) {
				addAddressRef(element, bytesWritten);
				fos.write(0);
				fos.write(0);
				fos.write(0);
				fos.write(0);
				fos.write(0);
				fos.write(0);
				fos.write(0);
				fos.write(0);
				bytesWritten+=8;
				
				continue;
			} else if(element.endsWith(":")) {
				saveAddressLocation(element, bytesWritten);
				continue;
			}
			
			if("pz".equals(element)) {
				padding = true;
				continue;
			} else if("hex".equals(element)) {
				radix.push(16);
				continue;
			} else if("bin".equals(element)) {
				radix.push(2);
				continue;
			} else if("dec".equals(element)) {
				radix.push(10);
				continue;
			} else if("str".equals(element)) {
				radix.push(0); // 0 == str mode
				continue;
			} else if("lr".equals(element)) {
				radix.pop();
				continue;
			}
			
			if(padding) {
				while(bytesWritten < Integer.parseInt(element, radix.peek())) {
					fos.write(0);
					bytesWritten++;
				}
				padding = false;
				continue;
			}
			if(radix.peek() == 0) { // str mode
				for(int i = 0; i < element.length(); i++) {
					fos.write(element.charAt(i));
					bytesWritten++;
				}
			} else {
				fos.write(Integer.parseInt(element, radix.peek()));
				bytesWritten++;
			}
			
		}
		
		// Fill in address locations
		for(String ref : addressRefs.keySet()) {
			int length = addressRefLength(ref);
			String label = ref.substring(ref.indexOf(":") + 1, ref.length()) + ":";
			ArrayList<Long> offsets = addressRefs.get(ref);
			for(long offset : offsets) {
				long address = addressLocations.get(label);
				fos.seek(offset);
				
				
				if(ref.startsWith("loc")) {
					fos.write(addressToBytes(address, length));
				} else {
					// need to fix this stupid logic
					/// dont know what will happen when trying to use rel8+
					//only do 8 bits for now
					int l = (int)(address - offset) - 1;
					if( l < 0 )
						l += 256;
					
					fos.write(l);
				}
				//fos.write(addressToBytes(address, length));
			}
		}
		
	
		fos.close();
		return bytesWritten;
		
	}
	private byte[] addressToBytes(long address, int numBytes) {
		byte[] b = new byte[numBytes];
		
		for(int i = 1; i <= numBytes; i++) {
		    b[i-1] = (byte)(address % Math.pow(256, i));
		    address -= b[i-1];
		}
		
		return b;
	}
	private int addressRefLength(String ref) throws Exception {
		if(ref.startsWith("loc8:") || ref.startsWith("rel8:")) {
			return 1;
		} else if(ref.startsWith("loc16:") || ref.startsWith("rel16:")) {
			return 2;
		} else if(ref.startsWith("loc32:") || ref.startsWith("rel32:")) {
			return 4;
		} else if(ref.startsWith("loc64:") || ref.startsWith("rel64:")) {
			return 8;
		} else {
			throw new Exception("Error: " + ref);
		}
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
package com.mcode.mamoi.mcc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mcode.mamoi.mcc.code.CodeSegment;
import com.mcode.mamoi.mcc.interpreter.CodeInterpreter;
import com.mcode.mamoi.mcc.interpreter.IncludedSourceManager;
import com.mcode.mamoi.mcc.interpreter.ModeManager;
import com.mcode.mamoi.mcc.interpreter.ReferenceManager;
import com.mcode.mamoi.mcc.interpreter.UserDefinedCommandManager;

// mcc = machine code compilers
public class mcc {
	private CodeInterpreter ci = null;
	private ModeManager mm = new ModeManager();
	private UserDefinedCommandManager udcm = new UserDefinedCommandManager();
	private ReferenceManager rm = new ReferenceManager();
	
	private Map<String, Long> addressLocations = new HashMap<String, Long>();
	private Map<String, ArrayList<Long>> addressRefs = new HashMap<String, ArrayList<Long>>();
	private boolean padding = false;
	
	private String definitionName = null;
	private List<String> elements = new ArrayList<String>();
	private Map<String, List<String>> userdefinedCommands = new HashMap<String, List<String>>(); 
	
	public mcc() {
		mm.pushRadix(10);
	}
	
	public void addElement(String e) throws Exception {
		
		// Do not add def and edef to elements
        // Create internal definition for defs instead
		if(e.equals("def")) {
			if(mm.isDefineMode()) {
				throw new Exception("error: already defining");
			}
			mm.setDefineMode(true);
			return;
		} else if (e.equals("edef")) {
			if(!mm.isDefineMode()) {
				throw new Exception("error: not defining");
			}
			mm.setDefineMode(false);
			definitionName = null;
			return;
		}
		
		if(mm.isDefineMode()) {
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
		// Expand all user defined definitions
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
	
	// save rel8*:location and loc8*:location calls 
	private void addAddressRef(String ref, long offset) {
		if(addressRefs.get(ref) == null) {
			ArrayList<Long> offsets = new ArrayList<Long>();
			offsets.add(offset);
			addressRefs.put(ref, offsets);
		} else {
			addressRefs.get(ref).add(offset);
		}
	}
	
	// save addressPosition: 
	private void saveAddressLocation(String label, long address) {
		addressLocations.put(label, address);
	}
	
	public long compile(String binaryFile) throws Exception {
		expandDefinitions();
		debug();
		long bytesWritten = 0;
		File f = new File(binaryFile);
		if(f.exists()) {
			f.delete();
		}
		RandomAccessFile fos = new RandomAccessFile(f, "rw");
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
				mm.pushRadix(16);
				continue;
			} else if("bin".equals(element)) {
				mm.pushRadix(2);
				continue;
			} else if("dec".equals(element)) {
				mm.pushRadix(10);
				continue;
			} else if("str".equals(element)) {
				mm.pushRadix(0); // 0 == str mode
				continue;
			} else if("lr".equals(element)) {
				mm.popRadix();
				continue;
			}
			
			if(padding) {
				while(bytesWritten < Integer.parseInt(element, mm.peekRadix())) {
					fos.write(0);
					bytesWritten++;
				}
				padding = false;
				continue;
			}
			if(mm.peekRadix() == 0) { // str mode
				for(int i = 0; i < element.length(); i++) {
					fos.write(element.charAt(i));
					bytesWritten++;
				}
			} else {
				fos.write(Integer.parseInt(element, mm.peekRadix()));
				bytesWritten++;
			}
			
		}
		
		// Fill in address locations
		for(String ref : addressRefs.keySet()) {
			int length = addressRefLength(ref);
			String label = ref.substring(ref.indexOf(":") + 1, ref.length()) + ":";
			ArrayList<Long> offsets = addressRefs.get(ref);
			for(long offset : offsets) {
				if(!addressLocations.containsKey(label)) {
					fos.close();
					throw new Exception("Error: label + " + label + " not found!");
				}
				long address = addressLocations.get(label);
				fos.seek(offset);
				
				
				if(ref.startsWith("loc")) {
					fos.write(addressToBytes(address, length));
				} else { // rel
					if(address > offset) {
						fos.write(addressToBytes((address - offset)-length, length));
					} else {
						fos.write(addressToBytes((address - offset), length));
					}
				}
			}
		}
		
		fos.close();
		return bytesWritten;
	}
	
	// return address in a little endian address bytes
	private byte[] addressToBytes(long address, int numBytes) {
		boolean isNeg = false;
		if(address < 0) {
			isNeg = true;
		}
		byte[] b = new byte[numBytes];
		
		for(int i = 1; i <= numBytes; i++) {
			byte v = (byte)(address % 256);
			if(isNeg) {
				v += 255;
			}
		    b[i-1] = v;
		    address /= 256;
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
			throw new Exception("Internal Error: " + ref + " is not a legal address reference!");
		}
	}
	
    public static void main( String[] args ) throws Exception {
    	String sourceFile = args[0];
    	String binaryFile = args[1];
    	
    	ModeManager mm = new ModeManager();
    	mm.pushRadix(16); // default radix to hex.
    	UserDefinedCommandManager udcm = new UserDefinedCommandManager();
    	ReferenceManager rm = new ReferenceManager();
    	IncludedSourceManager ism = new IncludedSourceManager();
    	CodeInterpreter ci = new CodeInterpreter(mm, udcm, rm, ism);
    	File f = new File(sourceFile);
    	ism.registerInclude(f.getAbsolutePath());
    	
    	CodeSegment cs = ci.translate(f);
    	
    	FileOutputStream fos = new FileOutputStream(new File(binaryFile));
    	int bytesWritten = 0;
    	for(int b : cs.getBytes()) {
    		fos.write(b);
    		bytesWritten++;
    		System.out.print("[" + b + "]");
    	}
    	System.out.println("\nBytes Written: " + bytesWritten);
    	fos.close();
    		
    	/*
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
    	*/
    	
    }
}
package com.mcode.mamoi.mcc;

import java.io.File;
import java.io.FileOutputStream;

import com.mcode.mamoi.mcc.code.CodeSegment;
import com.mcode.mamoi.mcc.interpreter.CodeInterpreter;
import com.mcode.mamoi.mcc.interpreter.IncludedSourceManager;
import com.mcode.mamoi.mcc.interpreter.ModeManager;
import com.mcode.mamoi.mcc.interpreter.ReferenceManager;
import com.mcode.mamoi.mcc.interpreter.UserDefinedCommandManager;

// mcc = machine code compilers
public class mcc {
	
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
    	
    	CodeSegment cs = ci.translate(f, 0);
    	
    	FileOutputStream fos = new FileOutputStream(new File(binaryFile));
    	int bytesWritten = 0;
    	for(int b : cs.getBytes()) {
    		fos.write(b);
    		bytesWritten++;
    		System.out.print("[" + b + "]");
    	}
    	System.out.println("\nBytes Written: " + bytesWritten);
    	fos.close();
    		

    }
}
package com.mcode.mamoi.isomaker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class IsoMaker {
	// Volume Descriptor type
	public static final byte BOOT_RECORD = 0;
	public static final byte PRIMARY_VOLUME_DESCRIPTOR = 1;
	public static final byte SUPPLEMENTARY_VOLUME_DESCRIPTOR = 2;
	public static final byte VOLUME_PARTITION_DESCRIPTOR = 3;
	public static final byte VOLUME_DESCRIPTOR_SET_TERMINATOR = (byte)255;
	
	public static final byte[] IDENTIFIER = new byte[]{'C', 'D', '0', '0', '1'};
	public static final byte   VERSION = 0x1;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File f = new File("image.iso");
		FileOutputStream fos = new FileOutputStream(new File("image.iso"));
		
		// ISO format begins with 32768 bytes of 0x0.
		for(int i = 0; i < 32768; i++) {
			fos.write(0x0);
		}
		
		fos.write(PRIMARY_VOLUME_DESCRIPTOR);
		fos.write(IDENTIFIER);
		fos.write(VERSION);
		
		// Data
		for(int i = 0; i < 2041; i++) {
			fos.write(0x0);
		}
		
		// Set Terminator
		fos.write(VOLUME_DESCRIPTOR_SET_TERMINATOR);
		fos.write(IDENTIFIER);
		fos.write(VERSION);
		
		fos.close();
		System.out.println("Output: " + f.getAbsoluteFile());

		
	}

}

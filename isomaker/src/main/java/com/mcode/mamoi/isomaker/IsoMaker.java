package com.mcode.mamoi.isomaker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.omg.CORBA_2_3.portable.OutputStream;

public class IsoMaker {
	// Volume Descriptor type
	public static final byte BOOT_RECORD = 0;
	public static final byte PRIMARY_VOLUME_DESCRIPTOR = 1;
	public static final byte SUPPLEMENTARY_VOLUME_DESCRIPTOR = 2;
	public static final byte VOLUME_PARTITION_DESCRIPTOR = 3;
	public static final byte VOLUME_DESCRIPTOR_SET_TERMINATOR = (byte)255;
	
	public static final byte[] IDENTIFIER = new byte[]{'C', 'D', '0', '0', '1'};
	public static final byte[] BOOT_SYSTEM_IDENTIFIER = new byte[] {'E', 'L', ' ', 'T', 'O', 'R','I', 'T', 'O', ' ', 
		                                                             'S', 'P', 'E', 'C', 'I', 'F', 'I', 'C', 'A', 'T', 'I', 'O', 'N',
		                                                             0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
	public static final byte   VERSION = 0x1;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File f = new File("image.iso");
		FileOutputStream fos = new FileOutputStream(new File("image.iso"));
		
		// ISO format begins with 32768 bytes of 0x0.
		writeZeros(fos, 32768);
		
		// Primary Volume Descriptor
		fos.write(PRIMARY_VOLUME_DESCRIPTOR);
		fos.write(IDENTIFIER);
		fos.write(VERSION);
		writeZeros(fos, 2041); // Data
		
		// Boot Record
		fos.write(BOOT_RECORD);
		fos.write(IDENTIFIER);
		fos.write(VERSION);
		fos.write(BOOT_SYSTEM_IDENTIFIER);
		writeZeros(fos, 32); // unused
		// Place holder...
		writeZeros(fos, 4); // Booting Catalog sector address
		writeZeros(fos, 1974); // unused
		
		// Set Terminator
		fos.write(VOLUME_DESCRIPTOR_SET_TERMINATOR);
		fos.write(IDENTIFIER);
		fos.write(VERSION);
		
		// Booting Catalog
		
		
		fos.close();
		System.out.println("Output: " + f.getAbsoluteFile());
	}
	
	private static void writeZeros(FileOutputStream fos, int zeroCount) throws IOException {
		for(int i = 0; i < zeroCount; i++) {
			fos.write(0x0);
		}
	}

}

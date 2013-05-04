package com.mcode.mamoi.isomaker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class IsoMaker {
	// Volume Descriptor type
	public static final byte BOOT_RECORD = 0;
	public static final byte PRIMARY_VOLUME_DESCRIPTOR = 1;
	public static final byte SUPPLEMENTARY_VOLUME_DESCRIPTOR = 2;
	public static final byte VOLUME_PARTITION_DESCRIPTOR = 3;
	public static final byte VOLUME_DESCRIPTOR_SET_TERMINATOR = (byte) 255;
	
	public static final byte[] IDENTIFIER = new byte[]{'C', 'D', '0', '0', '1'};
	public static final byte[] BOOT_SYSTEM_IDENTIFIER = new byte[] {'E', 'L', ' ', 'T', 'O', 'R','I', 'T', 'O', ' ', 
		                                                             'S', 'P', 'E', 'C', 'I', 'F', 'I', 'C', 'A', 'T', 'I', 'O', 'N',
		                                                             0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0};
	public static final byte   VERSION = 0x1;
	
	//  Boot Catalog
	public static final byte HEADER_ID = 0x1;
	public static final byte PLATFORM_ID_80x86 = 0x0;
	public static final byte PLATFORM_ID_POWER_PC = 0x1;
	public static final byte PLATFORM_ID_MAC = 0x2;
	public static final String DEVELOPER = "mcode";
	
	public static final byte BOOTABLE = (byte) 0x88;
	public static final byte NONBOOTABLE = 0x00;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File f = new File("image.iso");
		FileOutputStream fos = new FileOutputStream(new File("image.iso"));
		
		// Sector 0
		// ISO format begins with 32768 bytes of 0x0.
		writeZeros(fos, 32768);
		
		// Sector 16
		// Primary Volume Descriptor
		fos.write(PRIMARY_VOLUME_DESCRIPTOR);
		fos.write(IDENTIFIER);
		fos.write(VERSION);
		writeZeros(fos, 2041); // Data
		
		// Sector 17
		// Boot Record
		fos.write(BOOT_RECORD);
		fos.write(IDENTIFIER);
		fos.write(VERSION);
		fos.write(BOOT_SYSTEM_IDENTIFIER);
		writeZeros(fos, 32); // unused
		writeZeros(fos, 19); // Booting Catalog sector address
		writeZeros(fos, 1974); // unused
		
		// Sector 18
		// Set Terminator
		fos.write(VOLUME_DESCRIPTOR_SET_TERMINATOR);
		fos.write(IDENTIFIER);
		fos.write(VERSION);
		writeZeros(fos, 2041); // Data
		
		// Sector 19
		// Booting Catalog
		// Validation Entry
		fos.write(HEADER_ID);
		fos.write(PLATFORM_ID_80x86);
		writeZeros(fos, 2); // reserved
		fillText(fos, DEVELOPER, 24);
		fos.write(0x0); // todo: checksum
		fos.write(0x55);
		fos.write(0xAA);
		
		fos.close();
		System.out.println("Osutput: " + f.getAbsoluteFile());
	}
	
	private static void writeZeros(OutputStream os, int zeroCount) throws IOException {
		for(int i = 0; i < zeroCount; i++) {
			os.write(0x0);
		}
	}
	
	private static void fillText(OutputStream os, String text, int totalBytes) throws IOException {
		for(int i = 0; i < text.length(); i++) {
			os.write(text.charAt(i));
		}
		
		for(int i = 0; i < totalBytes - text.length(); i++) {
			os.write(0x0);
		}
		
	}

}

package com.mcode.mamoi.isomaker;

import static com.mcode.mamoi.binaryio.BinaryWriter.copyBytes;
import static com.mcode.mamoi.binaryio.BinaryWriter.fillText;
import static com.mcode.mamoi.binaryio.BinaryWriter.writeZeros;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class IsoMaker {
	// Volume Descriptor type
	public static final byte BOOT_RECORD = 0;
	public static final byte PRIMARY_VOLUME_DESCRIPTOR = 1;
	public static final byte SUPPLEMENTARY_VOLUME_DESCRIPTOR = 2;
	public static final byte VOLUME_PARTITION_DESCRIPTOR = 3;
	public static final byte VOLUME_DESCRIPTOR_SET_TERMINATOR = (byte) 255;
	
	public static final String IDENTIFIER = "CD001";
	public static final String BOOT_SYSTEM_IDENTIFIER = "EL TORITO SPECIFICATION";
	public static final byte   VERSION = 0x1;
	
	//  Boot Catalog
	public static final byte HEADER_ID = 0x1;
	public static final byte PLATFORM_ID_80x86 = 0x0;
	public static final byte PLATFORM_ID_POWER_PC = 0x1;
	public static final byte PLATFORM_ID_MAC = 0x2;
	public static final String DEVELOPER = "mcode";
	
	public static final byte BOOTABLE = (byte) 0x88;
	public static final byte NONBOOTABLE = 0x00;
	
	public static final String VOLUME_SET_IDENTIFIER = "All Volumes";

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Date now = new Date();
		File f = new File("image.iso");
		OutputStream fos = new FileOutputStream(new File("image.iso"));
		InputStream fis = new FileInputStream(new File("boot.bin"));
		
		// Sector 0
		// ISO format begins with 32768 bytes of 0x0.
		writeZeros(fos, 32768);
		
		// Sector 16
		// Primary Volume Descriptor
		fos.write(PRIMARY_VOLUME_DESCRIPTOR);
		fillText(fos, IDENTIFIER, 4);
		fos.write(VERSION);
		
		fos.write(0x0); // unused
		writeZeros(fos, 32); // The name of the system that can act sectors 0x00-0x0F for the volume.
		fillText(fos, "mamoi v1.0", 32); // Identification of this volume.
		writeZeros(fos, 8); // unused
		fos.write(0x1C); // Number of Logical Blocks in which the volume is recorded
		writeZeros(fos, 3); // Just copying from mikeos iso now... 
		writeZeros(fos, 3); // int32_LSB-MSD format
		fos.write(0x1C); 
		writeZeros(fos, 32); // unused
		fos.write(0x1); // The size of the set in this logical volume (number of disks).
		fos.write(0x0); // int16_LSB-MSB format
		fos.write(0x0);
		fos.write(0x1); 
		fos.write(0x1); // The number of this disk in the Volume Set.
		fos.write(0x0); // int16_LSB-MSB format
		fos.write(0x0);
		fos.write(0x1); 
		fos.write(0x0); // The size in bytes of a logical block. NB: This means that a logical block on a CD could be something other than 2 KiB!
		fos.write(0x8); // int16_LSB-MSB format
		fos.write(0x8); // using 0x800 byte
		fos.write(0x0); 
		fos.write(0xA); // The size in bytes of the path table.
		writeZeros(fos, 3); // int32_LSB-MSB format
		writeZeros(fos, 3); // Just copying from mikeos iso now... 
		fos.write(0xA);
		fos.write(20); // LBA location of the path table. The path table pointed to contains only little-endian values.
		writeZeros(fos, 3); // int32_LSB format
		writeZeros(fos, 4); // LBA location of the optional path table. The path table pointed to contains only little-endian values. Zero means that no optional path table exists.
		writeZeros(fos, 3); // int32_MSB
		fos.write(22); // LBA location of the path table. The path table pointed to contains only big-endian values.
		writeZeros(fos, 4); // LBA location of the optional path table. The path table pointed to contains only big-endian values. Zero means that no optional path table exists.
		// Directory entry for the root directory
		// Note that this is not an LBA address, it is the actual Directory Record, which contains a zero-length Directory Identifier, hence the fixed 34 byte size.
		fos.write(34); // Length of Directory Record.
		fos.write(0x0); // Extended Attribute Record length.
		fos.write(21); // Location of extent (LBA) in both-endian format.
		writeZeros(fos, 3);  
		writeZeros(fos,  3); // Big endian
		fos.write(21); 
		fos.write(0x00); // Data length (size of extent) in both-endian format.
		fos.write(0x08); // Little endian
		fos.write(0x00);
		fos.write(0x00);
		fos.write(0x00); // Big endian
		fos.write(0x00);
		fos.write(0x08); 
		fos.write(0x00); // 0x800 bytes (or 2048 bytes)
		// Recording date and time
		fos.write(now.getYear() - 1900); // Number of years since 1900.
		fos.write(now.getMonth()); // Month of the year from 1 to 12.
		fos.write(now.getDate()); // Day of the month from 1 to 31.
		fos.write(now.getHours()); // Hour of the day from 0 to 23.
		fos.write(now.getMinutes()); // Minute of the hour from 0 to 59.
		fos.write(now.getSeconds()); // Second of the minute from 0 to 59.
		fos.write(0); // Offset from GMT in 15 minute intervals from -48 (West) to +52 (East).
		// File flags
		fos.write(2); // Copying from mikeos for now.  It means it's a directory I think.
		fos.write(0x0); // File unit size for files recorded in interleaved mode, zero otherwise.
		fos.write(0x0); // Interleave gap size for files recorded in interleaved mode, zero otherwise.
		fos.write(0x1); // Volume sequence number - the volume that this extent is recorded on, in 16 bit both-endian format.
		fos.write(0x0); // Little endian
		fos.write(0x0); // Big endian
		fos.write(0x1);
		fos.write(0x0); // Length of file identifier (file name). This terminates with a ';' character followed by the file ID number in ASCII coded decimal ('1').
		                // MikeOS uses 1...
		fos.write(0x0); // Padding field - zero if length of file identifier is odd, otherwise, this field is not present. This means that a directory entry will always start on an even byte number.
		                // MikeOS uses 1...
		fillText(fos, VOLUME_SET_IDENTIFIER, 128); // Identifier of the volume set of which this volume is a member.
		
		
		
		
		
		
		writeZeros(fos, 2041); // Fill sector
		 
		
		
		
		// Sector 17
		// Boot Record
		fos.write(BOOT_RECORD);
		fillText(fos, IDENTIFIER, 4);
		fos.write(VERSION);
		fillText(fos, BOOT_SYSTEM_IDENTIFIER, 32);
		writeZeros(fos, 32); // unused
		writeZeros(fos, 19); // Booting Catalog sector address
		writeZeros(fos, 1974); // unused
		
		// Sector 18
		// Set Terminator
		fos.write(VOLUME_DESCRIPTOR_SET_TERMINATOR);
		fillText(fos, IDENTIFIER, 4);
		fos.write(VERSION);
		writeZeros(fos, 2041); // Data
		
		// Sector 19
		// Booting Catalog
		// Validation Entry
		fos.write(HEADER_ID);
		fos.write(PLATFORM_ID_80x86);
		writeZeros(fos, 2); // reserved
		fillText(fos, DEVELOPER, 24);
		fos.write(0x69); // check sum
		fos.write(0x8d); // manually calculated
		fos.write(0x55);
		fos.write(0xAA);
		// Initial/Default Entry
		fos.write(BOOTABLE);
		fos.write(0x0); // No Emulation
		writeZeros(fos, 2); // Default load segment (7c0) 
		fos.write(0x0); // System Type. This must be a copy of byte 5 (System Type) from the Partition Table found in the boot image. 
		fos.write(0x0); // Unused.
		fos.write(0x1); // Number of virtual/emulated sectors (512 bytes) the system will store at Load Segment during initial boot procedure.
		fos.write(0x0); // 2 byte word.
		fos.write(23); // Start address of virtual disk (boot bin)
		writeZeros(fos, 3); // 4 byte word.
		writeZeros(fos, 1988); // Fill sector
		
		// Sector 20
		// Path table (little endian)
		
		// Sector 21
		// Directories
		
		// Sector 22
		// Path table (big endian)
		
		
		// Sector 23
		// Boot bin
		copyBytes(fos, fis);
		
		fos.close();
		fos.close();
		
		
		System.out.println("Output: " + f.getAbsoluteFile());
	}
	


}

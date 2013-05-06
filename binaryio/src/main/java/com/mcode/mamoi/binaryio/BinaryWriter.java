package com.mcode.mamoi.binaryio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class BinaryWriter 
{
	public static void writeZeros(OutputStream os, int zeroCount) throws IOException {
		for(int i = 0; i < zeroCount; i++) {
			os.write(0x0);
		}
	}

	public static void fillText(OutputStream os, String text, int totalBytes) throws IOException {
		for(int i = 0; i < text.length(); i++) {
			os.write(text.charAt(i));
		}

		for(int i = 0; i < totalBytes - text.length(); i++) {
			os.write(0x0);
		}

	}
	
	public static void copyBytes(OutputStream os, InputStream is) throws IOException {
		int i = is.read();
		while(i != -1) {
			os.write(i);
			i = is.read();
		}
	}
}

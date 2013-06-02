package com.mcode.mamoi.mcc;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

public class ByteFactoryTests {

	@Test
	public void testAsBytes() {
		long value = 0xFAEC;
		List<Integer> bytes = ByteFactory.asBytes(value);
		
		assertEquals(0xEC, (int)bytes.get(0));
		assertEquals(0xFA, (int)bytes.get(1));
		
		value = -1;
		bytes = ByteFactory.asBytes(value);
		assertEquals(0xFE, (int)bytes.get(0));
	}

}

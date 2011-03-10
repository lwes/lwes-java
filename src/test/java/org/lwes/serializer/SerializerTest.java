package org.lwes.serializer;

import static org.junit.Assert.*;

import org.junit.Test;
import org.lwes.EventSystemException;

public class SerializerTest {
	@Test
	public void testSerializeValidUBYTEs() throws EventSystemException {
		for (short x=0; x<256; ++x) {
			final byte[] bytes = new byte[1];
			Serializer.serializeUBYTE(x, bytes, 0);
			assertEquals(x, Deserializer.deserializeUBYTE(new DeserializerState(), bytes));
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNegativeUBYTE() throws IllegalArgumentException {
		Serializer.serializeUBYTE((short)-1, new byte[1], 0);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testOverflowUBYTE() throws IllegalArgumentException {
		Serializer.serializeUBYTE((short)256, new byte[1], 0);
	}
}

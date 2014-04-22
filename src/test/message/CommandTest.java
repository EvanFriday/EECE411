package test.message;

import static org.junit.Assert.assertEquals;
import main.message.Command;

import org.junit.Test;

public class CommandTest {
	/**
	 * Test that Command.getCommand can retrieve a Command object that exists.
	 */
	@Test
	public void testGetCommandExists() {
		Command c = Command.getCommand((byte) 0x01);
		assertEquals(Command.PUT, c);
	}
	
	/**
	 * Test that Command.getCommand cannot retrieve a Command object that does
	 * not exist.
	 */
	@Test
	public void testGetCommandNull() {
		Command c = Command.getCommand((byte) 0xFF);
		assertEquals(null, c);
	}
}
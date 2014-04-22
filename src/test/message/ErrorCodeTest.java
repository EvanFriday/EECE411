package test.message;

import static org.junit.Assert.assertEquals;
import main.message.ErrorCode;

import org.junit.Test;

public class ErrorCodeTest {
	/**
	 * Test that ErrorCode.getErrorCode can retrieve a ErrorCode object that exists.
	 */
	@Test
	public void testGetErrorCodeExists() {
		ErrorCode c = ErrorCode.getErrorCode((byte) 0x00);
		assertEquals(ErrorCode.OK, c);
	}
	
	/**
	 * Test that ErrorCode.getErrorCode cannot retrieve a ErrorCode object that does
	 * not exist.
	 */
	@Test
	public void testGetErrorCodeNull() {
		ErrorCode c = ErrorCode.getErrorCode((byte) 0xFF);
		assertEquals(null, c);
	}
}
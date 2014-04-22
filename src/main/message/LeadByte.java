package main.message;

/**
 * The first byte in a Message. Can be either a Command or an ErrorCode.
 * 
 * @author kevinvkpetersen, EvanFriday, cameronjohnston
 */
interface LeadByte {
	public byte getHexValue();
}
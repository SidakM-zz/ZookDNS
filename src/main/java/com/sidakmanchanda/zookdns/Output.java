package com.sidakmanchanda.zookdns;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Output {
	/**
	 * An abstraction for around java.nio.ByteBuffer to be used to formulate responses.
	 * 
	 * @author sidak
	 */
	
	private ByteBuffer buffer;
	private static final int MAX_CAPACITY = 256;
	
	public Output() {
		buffer = ByteBuffer.allocate(MAX_CAPACITY);
	}
	
	public void writeByte(byte b) {
		buffer.put(b);
	}
	
	public void writeBytes(byte[] b) {
		buffer.put(b);
	}
	
	public int getPos() {
		return buffer.position();
	}
	
	public void jump(int numBytes) {
		buffer.position(buffer.position() + numBytes);
	}
	
	public void write8BitInt(int number) {
		buffer.put((byte) (number & 0xFF));
	}
	
	public void write16BitInt(int number) {
		buffer.put((byte)((number >> 8) & 0xFF));
		buffer.put((byte)(number & 0xFF));
	}
	
	public void write16BitInt(int number, int index) {
		buffer.put(index, (byte)((number >> 8) & 0xFF));
		buffer.put(index + 1, (byte)(number & 0xFF));
	}
	
	public void write32BitInt(long number) {
		buffer.put((byte)((number >> 24) & 0xFF));
		buffer.put((byte)((number >> 16) & 0xFF));
		buffer.put((byte)((number >> 8) & 0xFF));
		buffer.put((byte)(number & 0xFF));
	}

	/**
	 * Returns an array of bytes up to the current position of the buffer.
	 * 
	 * @return byte[] 
	 */
	public byte[] getBytes() {
		int curPos = buffer.position();
		return Arrays.copyOfRange(buffer.array(), 0, curPos);
	}
}

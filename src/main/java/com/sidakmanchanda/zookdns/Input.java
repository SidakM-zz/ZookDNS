package com.sidakmanchanda.zookdns;

import java.nio.ByteBuffer;

/**
 * An abstraction for around java.nio.ByteBuffer to be used to consume the input from a client.
 * This class is stateful in the sense that every method call unless otherwise noted 
 * consumes inputed bytes provided by the client thereby, modifying the state of the buffer.
 * 
 * @author sidak
 */
public class Input {
	private ByteBuffer buffer;
	
	public Input(byte[] data) {
		this.buffer = ByteBuffer.wrap(data);
	}
	
	public void getbyteArray(byte[] arr, int length) {
		this.buffer.get(arr, 0, length);
	}
	
	public int get16BitInt() {
		return this.buffer.getShort() & 0xFFFF;
	}
	
	public byte getByte() {
		return this.buffer.get();
	}
	
	public byte[] peekbyteArray(int length, int pos) {
		byte[] arr = new byte[length];
		for (int x = 0; x < length; x++) {
			arr[x] = this.buffer.get(pos + x);
		}
		return arr;
	}
	
	public byte peekByte(int pos) {
		return this.buffer.get(pos);
	}
	
	public int getPos() {
		return this.buffer.position();
	}
	
	public void setPosition(int pos) {
		this.buffer.position(pos);
	}
}

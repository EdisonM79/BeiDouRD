package com.jzsk.seriallib.msg.msgv40;


import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.msg.BasePacket;
import com.jzsk.seriallib.util.ArrayUtils;

import java.util.Arrays;

/**
 * Base class for v4.0 messages.
 * <p>
 * Every message has a unique ID. Optionally, the "cipher", "phone", and "body"
 * fields can be set.
 *
 */
public class Message extends BaseMessage {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String TAG = "Message";

	// Beginning Marker
	private static final byte PREFIX = '$';

	private final byte[] mAddr;
	private final byte[] mBody;

	public Message(byte[] raw) {
		if (raw[0] != PREFIX){
			throw new IllegalArgumentException(
					"Packet prefix or suffix not found.");
		}
		if (raw.length < 5) {
			throw new IllegalArgumentException("Insufficient packet length.");
		}
		mAddr = Arrays.copyOfRange(raw,0,5);
		mBody = Arrays.copyOfRange(raw, 0,raw.length);
	}

	public Message(byte[] preffix, byte[] body, byte[] suffix) {
		if ( preffix[0] != PREFIX ){
			throw new IllegalArgumentException(
					"Packet prefix or suffix not found.");
		}
		byte[] raw = ArrayUtils.concatenate(preffix,body,suffix);
		if (raw.length < 5) {
			throw new IllegalArgumentException("Insufficient packet length.");
		}
		mAddr = Arrays.copyOfRange(raw,0,5);
		mBody = Arrays.copyOfRange(raw, 0,raw.length);
	}
	public byte[] getBody() {
		return mBody;
	}

	@Override
	public String getAddr(){
		return new String(mAddr);
	}

	@Override
	public String toString() {
		return new StringBuilder("{ ").append(new String(mBody))
				.append(" }").toString();
	}

	@Override
	public BasePacket[] getPackets() {
		BasePacket[] packets = new Packet[1];
		packets[0] = new Packet(mBody);
		return packets;
	}

}

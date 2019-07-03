package com.jzsk.seriallib.msg.msgv21;


import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.msg.BasePacket;
import com.jzsk.seriallib.util.ArrayUtils;

import java.util.Arrays;

/**
 * Base class for JT/T808 messages.
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
	private static final byte PREFIX1 = '!';
	// Ending Marker
	private static final byte SUFFIX = 0x0D;//<CR>
	private static final byte SUFFIX1 = 0x0A;//<LF>


	private final byte[] mAddr;
	private final byte[] mBody;

	public Message(byte[] raw) {
		if ((raw[0] != PREFIX && raw[0] != PREFIX1) || (raw[raw.length - 2] != SUFFIX && raw[raw.length - 1] != SUFFIX1)){
			throw new IllegalArgumentException(
					"Packet prefix or suffix not found.");
		}
		if (raw.length < 5) {
			throw new IllegalArgumentException("Insufficient packet length.");
		}
		mAddr = Arrays.copyOfRange(raw,0,6);
		mBody = Arrays.copyOfRange(raw, 0,raw.length);
	}

	public Message(byte[] preffix, byte[] body, byte[] suffix) {
		if ((preffix[0] != PREFIX && preffix[0] != PREFIX1) || (suffix[0] != SUFFIX && suffix[1] != SUFFIX1)){
			throw new IllegalArgumentException(
					"Packet prefix or suffix not found.");
		}
		byte[] raw = ArrayUtils.concatenate(preffix,body,suffix);
		if (raw.length < 5) {
			throw new IllegalArgumentException("Insufficient packet length.");
		}
		mAddr = Arrays.copyOfRange(raw,0,6);
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
		/*return new StringBuilder("{ ").append(new String(mBody))
				.append(" }").toString();*/
		/** 2019/02/26 去掉了消息体两边的括号 */
		return new String(mBody);
	}

	@Override
	public BasePacket[] getPackets() {
		BasePacket[] packets = new Packet[1];
		packets[0] = new Packet(mBody);
		return packets;
	}

}

package com.jzsk.seriallib.msg.msgv21;

import android.annotation.SuppressLint;

import com.jzsk.seriallib.msg.BasePacket;

import java.util.Arrays;
import java.util.logging.Logger;


/**
 * Represents BD-2 message packets.
 *
 */
public class Packet extends BasePacket {

	private static final Logger LOGGER = Logger.getLogger(Packet.class.getSimpleName());

	static final short MAX_LENGTH = 300;

	// Beginning Marker
	public static final byte PREFIX = '$';
	public static final byte PREFIX1 = '!';
	// Ending Marker
	public static final byte SUFFIX = 0x0D;//<CR>
	public static final byte SUFFIX1 = 0x0A;//<LF>

	private final byte[] mAddr; //地址字段，前五个字符
	private final byte[] mPayload;//包含地址字段

	@SuppressLint("NewApi")
	public Packet(byte[] raw) {
		if ((raw[0] != PREFIX && raw[0] != PREFIX1) || (raw[raw.length - 2] != SUFFIX && raw[raw.length - 1] != SUFFIX1)) {
			throw new IllegalArgumentException(
					"Packet prefix or suffix not found.");
		}
		if (raw.length < 5) {
			throw new IllegalArgumentException("Insufficient packet length.");
		}
		mAddr = Arrays.copyOfRange(raw,0,6);
		mPayload = Arrays.copyOfRange(raw, 0,raw.length);

	}

	@Override
	public byte[] getBytes() {
		return mPayload;
	}

	public int length() {
		return mPayload.length;
	}

	public byte[] getPayload() {
		return mPayload;
	}

	@Override
	public String toString() {
		return new StringBuilder().append("{ ").append(Arrays.toString(mPayload)).append(" }").toString();
	}

}

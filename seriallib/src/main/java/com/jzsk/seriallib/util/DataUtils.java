package com.jzsk.seriallib.util;

import android.content.Context;

import java.lang.ref.SoftReference;


/**
 * Created by DeW on 2017/3/31.
 */

public class DataUtils {
	private static SoftReference<String> imeiRef = new SoftReference<String>(null);
	
	/**
	 * transfer manufacturer id into byte[5];
	 * 
	 * @param id
	 * @return
	 */
	public static byte[] getPhoneBytes(long id) {
		if (id > 1099511627775L) {
			throw new IllegalArgumentException(
					"id is bigger than 1099511627775L");
		}
		byte[] idBytes = new byte[5];
		idBytes[0] = (byte) ((id >> 32) & 0xFF);
		idBytes[1] = (byte) ((id >> 24) & 0xFF);
		idBytes[2] = (byte) ((id >> 16) & 0xFF);
		idBytes[3] = (byte) ((id >> 8) & 0xFF);
		idBytes[4] = (byte) (id & 0xFF);
		return idBytes;
	}

	public static byte[] convertSim2Bytes(Context context, String simNo) {
		byte[] phone = null;
		try {
			phone = new byte[6];
			long hascode = Long.parseLong(simNo);
			phone = ArrayUtils.ensureLowLength(IntegerUtils.str2Bcd(String.valueOf(hascode)), 6);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return phone;
	}
	
	public static byte[] getPhoneBySerialNo(String serialNo) {
		byte[] phone = null;
		try {
			phone = new byte[6];
			String SimNo = IntegerUtils.bcd2Str(IntegerUtils.str2Bcd(serialNo));
			phone = ArrayUtils.ensureLowLength(IntegerUtils.str2Bcd(SimNo), 6);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return phone;
	}
}

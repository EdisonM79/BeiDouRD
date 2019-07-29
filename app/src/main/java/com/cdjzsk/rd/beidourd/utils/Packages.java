package com.cdjzsk.rd.beidourd.utils;

public class Packages {

	public static byte[] CCTXA (String Id , int cardLevel, String message) {
		//三级民卡最长支持628byte
		StringBuffer sb = new StringBuffer();
		sb.append("CCTXA," + Id);
		sb.append(",1,2,");
		sb.append(message);
		byte[] bytes = sb.toString().getBytes();
		return bytes;
	}

	public static int returnActualLength(byte[] data) {
		int i = 0;
		for (; i < data.length; i++) {
			if (data[i] == '\0')
				break;
		}
		return i;
	}
}

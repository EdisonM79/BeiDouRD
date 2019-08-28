package com.cdjzsk.rd.beidourd.utils;

import java.io.UnsupportedEncodingException;

public class Packages {

	public static byte[] CCTXA (String Id , int cardLevel, String message) {
		//三级民卡最长支持628byte
		StringBuilder sb = new StringBuilder();
		sb.append("CCTXA," + Id);
		sb.append(",1,2,");
		sb.append(message);
		try {
			byte[] bytes = sb.toString().getBytes("GB2312");
			return bytes;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new byte[0];
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

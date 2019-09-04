package com.cdjzsk.rd.beidourd.utils;


import android.support.annotation.NonNull;

import com.jzsk.seriallib.ClientStateCallback;
import com.jzsk.seriallib.SerialClient;
import com.jzsk.seriallib.SupportProtcolVersion;
import com.jzsk.seriallib.conn.MessageListener;
import com.jzsk.seriallib.msg.msgv21.Message;
import com.jzsk.seriallib.util.ArrayUtils;

public class SerialPortUtils implements ClientStateCallback {

	//初始化串口对象
	private static SerialClient mSerialClient;
	private static MessageListener ml;

	public static SerialClient getSerialClient() {
		return mSerialClient;
	}

	/**
	 * 创建一个构造函数，在主Activity初始化的时候调用，传一个监听器过来，避免出现没有监听器的现象
	 * @param ml
	 */
	public SerialPortUtils(@NonNull MessageListener ml) {

		this.ml = ml;
		mSerialClient = new SerialClient();
		//从外部传一个监听器过来
		mSerialClient.setMessageListener(ml);
		//模拟串口读取模式
		mSerialClient.setDebugMode(false);
		//mSerialClient.setDebugMode(true);
		//打开串口
		mSerialClient.connect(this,SupportProtcolVersion.V21);
	}

	/**
	 * 更改监听类，在跳转页面的时候有用
	 * @param messageListener
	 */
	public static void exchangeListener(@NonNull MessageListener messageListener) {
		ml = messageListener;
		//从外部传一个监听器过来
		mSerialClient.setMessageListener(ml);
	}

	/**
	 * 发送北斗短报文消息
	 * @param cardId
	 * @param message
	 */
	public static void sendMessage(String cardId, String message) {
		//先包装成不含校验码的byte数组
		byte[] send = Packages.CCTXA(cardId, 3, message);
		//计算校验码，将byte数组转换为十六进制
		byte[] sendMsgToSerial = ArrayUtils.concatenate(
				new byte[]{'$'}, send, new byte[]{'*'}, ArrayUtils.bytesToHexString(new byte[]{ArrayUtils.xorCheck(send)}).getBytes(), new byte[]{0x0D, 0x0A});
		com.jzsk.seriallib.msg.msgv21.Message msg = new com.jzsk.seriallib.msg.msgv21.Message(sendMsgToSerial);
		try {
			mSerialClient.sendMessage(msg);
		} catch (IllegalStateException e) {
			throw e;
		}
	}

	/**
	 * 发送串口控制指令
	 * @param message
	 */
	public static void sendControl(String message) {

		byte[] byteMessage = message.getBytes();
		byte[] sendMsgICA = ArrayUtils.concatenate(new byte[]{'$'}, byteMessage, new byte[]{'*'}, ArrayUtils.bytesToHexString(new byte[]{ArrayUtils.xorCheck(byteMessage)}).getBytes(), new byte[]{0x0D, 0x0A});
		Message msgICA = new Message(sendMsgICA);
		try {
			mSerialClient.sendMessage(msgICA);
		} catch (IllegalStateException e) {
			throw e;
		}
	}

	@Override
	public void connectSuccess() {
		System.out.println("Serila connectSuccess");

	}

	@Override
	public void connectFail() {
		System.out.println("Serila connectFail");
	}

	@Override
	public void connectionClosed() {
		System.out.println("Serila connectionClosed");
	}
}

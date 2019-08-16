package com.cdjzsk.rd.beidourd.utils;

import com.cdjzsk.rd.beidourd.R;

public class Constant {
	/** 读卡指令*/
	public static final String ICA = "CCICA,0,00";
	/** 打开波束功率*/
	public static final String BSI = "CCRMO,BSI,2,1";
	/** 消息已读标志*/
	public static final String MESSAGE_READ = "1";
	/** 消息未读标志*/
	public static final String MESSAGE_NOTREAD = "0";
	/** 消息是本机发送*/
	public static final String MESSAGE_MYSEND = "1";
	/** 消息非本机发送*/
	public static final String MESSAGE_NOTMYSEND = "0";
	/** 收到的消息标志*/
	public final static int TYPE_RECEIVER_MSG = 0x21;
	/** 发送的消息标志*/
	public final static int TYPE_SENDER_MSG = 0x22;
	/** 时间戳标志*/
	public final static int TYPE_TIME_STAMP = 0x23;
	/** 我的聊天头像*/
	public final static int MY_IMAGE = R.drawable.hdimg_4;
	/** 其他人聊天头像*/
	public final static int OTHER_IMAGE = R.drawable.hdimg_3;
}

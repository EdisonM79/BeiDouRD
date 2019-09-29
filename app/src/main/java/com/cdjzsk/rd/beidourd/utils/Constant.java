package com.cdjzsk.rd.beidourd.utils;

import com.cdjzsk.rd.beidourd.R;

public class Constant {

	/** false时为测试模式 */
	public static final boolean TEST_UI_MODEL = true;
	/** 通信测试收发指令*/
	public static final String TEST_MESSAGE = "北斗模块通信测试[混发模式]-!@#%^&*()~1234567890_ABCDEFGHIJKLMNOPQRSTUVWXYZ.";
	/** 读卡指令*/
	public static final String ICA = "CCICA,0,00";
	/** 打开波束功率*/
	public static final String OPEN_BSI = "CCRMO,BSI,2,1";
	/** 关闭波束功率*/
	public static final String CLOSE_BSI = "$CCRMO,BSI,1,1";
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
	public final static int MY_IMAGE = R.drawable.icon_d;
	/** 其他人聊天头像*/
	public final static int OTHER_IMAGE = R.drawable.icon_d;
	/** 消息发送成功标志*/
	public final static String MESSAGE_SEND_SUCCESS = "Y";
	/** 本机用户*/
	public final static String MY_NAME = "本机用户";
}

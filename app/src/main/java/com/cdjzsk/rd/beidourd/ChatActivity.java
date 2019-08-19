package com.cdjzsk.rd.beidourd;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cdjzsk.rd.beidourd.adapter.ChatAdapter;
import com.cdjzsk.rd.beidourd.adapter.ContactAdapter;
import com.cdjzsk.rd.beidourd.bean.ContactShowInfo;
import com.cdjzsk.rd.beidourd.bean.MsgData;
import com.cdjzsk.rd.beidourd.data.MyDataHander;
import com.cdjzsk.rd.beidourd.data.entity.MessageInfo;
import com.cdjzsk.rd.beidourd.data.entity.User;
import com.cdjzsk.rd.beidourd.utils.Constant;
import com.cdjzsk.rd.beidourd.utils.HelpUtils;
import com.cdjzsk.rd.beidourd.utils.SerialPortUtils;
import com.jzsk.seriallib.conn.MessageListener;
import com.jzsk.seriallib.msg.BaseMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * @author Edison 2019/8/19.
 */

public class ChatActivity extends AppCompatActivity {

	//
	ListView contactListView;
	/** 聊天对方的Id*/
    private String otherId;
    //本机的卡号Id*/
	private String myId;

    /** 原始的聊天数据列表*/
    public List<MessageInfo> messageInfos;
	/** 封装的聊天信息数据*/
    public List<MsgData> displayMessageData;
	 /** 聊天信息适配器*/
    private ChatAdapter chatadapter;

	/** 数据库联系人列表 */
	public List<User> contacts;
	/** 封装后联系人列表*/
	List<ContactShowInfo> contactShowInfo;
	/** 联系人适配器 */
	ContactAdapter contactAdapter;

	/** 搜索框等工具栏的高度*/
	private int toolbarHeight;
	/** 状态栏的高度*/
	private int statusBarHeight;

	private Handler mHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_chat);
        initComponents();
	    initSerialClient();
    }
	/**
	 * 初始化串口工具，设置串口监听类
	 */
	private void initSerialClient() {

		SerialPortUtils.exchangeListener(new MessageListener() {
			@Override
			public void processMessage(BaseMessage msg) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updataUI(msg.toString());
					}
				});
			}
		});
	}
	/**
	 * 处理接收到的数据，并且更改UI的显示
	 * @param msg
	 */
	private void updataUI(final String msg){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//发送状态反馈
				if(msg.contains("BDFKI"))
				{
					String[]  result = msg.split(",");
				}
				/**
				 *  在聊天页面接收到消息需要3重处理
				 *  1.存入数据库
				 *  2.如果是当前聊天对象，更新聊天信息
				 *  3.更新联系人列表
				 *  先实现功能，再来简化
				 * */
				if(msg.contains("BDTXR"))
				{
					String[] msgList = msg.split(",");
					MessageInfo messageInfo = new MessageInfo();
					//接收ID
					messageInfo.setReceiveId("0412159");
					//发送ID
					String userId = msgList[2];
					messageInfo.setSendId(userId);
					//计算长度
					int index = msgList[5].length();
					//去掉末尾的*34/r/n以后的消息内容
					String message = msgList[5].substring(0,(index-5));
					//消息内容
					messageInfo.setMessage(message);
					//获取当前系统时间
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
					String time = sdf.format(new Date());
					//接收消息的本地时间
					messageInfo.setTime(time);
					//设置消息为未读
					messageInfo.setRead(Constant.MESSAGE_NOTREAD);
					//设置消息标志--不是本机发送
					messageInfo.setMySend(Constant.MESSAGE_NOTMYSEND);
					//将信息存入数据库
					MyDataHander.addMessage(messageInfo);
					//用发送人ID去查询是否是已有联系人
					boolean save = MyDataHander.isUserExit(userId);
					//不存在，保存联系人
					if (!save) {
						User user = new User();
						user.setUserId(userId);
						//暂时先把名字设置为卡号
						user.setUserName(userId);
						//暂时先把头像设置为统一头像
						user.setImage(R.drawable.hdimg_1);
						//保存用户
						MyDataHander.addUser(user);
					}

					/************更新联系人列表*************/
					//查询联系人列表里面是否有当前消息的发送人
					int listLength = contactShowInfo.size();




				}
			}
		});
	}
    private void initComponents() {

    	/**  显示聊天对象的姓名*/
        TextView tv_userName = findViewById(R.id.activity_wechat_chat_tv_name);
	    /**  发送信息按钮*/
        Button btn_send = findViewById(R.id.activity_wechat_chat_btn_send);
	    btn_send.startAnimation(getVisibleAnim(false, btn_send));
	    btn_send.setVisibility(View.GONE);
	    /**  返回按钮*/
	    ImageView iv_back = findViewById(R.id.activity_wechat_chat_back);
	    iv_back.setOnClickListener((v) -> finish());
	    /**  聊天内容显示界面*/
        RecyclerView rv = findViewById(R.id.activity_wechat_chat_rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
	    /**  聊天内容编辑*/
	    EditText et_msg = findViewById(R.id.activity_wechat_chat_et_msg);
	    //内容改变监听器
        et_msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("tag", "onTextChanged --- start -> " + start + " , count ->" + count + "，before ->" + before);
                if (start == 0 && count > 0) {
                    btn_send.startAnimation(getVisibleAnim(true, btn_send));
                    btn_send.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        /** 数据库中取出所有的联系人 */
        contacts = MyDataHander.getAllUser();
        if (contacts.size() != 0) {
	        for (int i = 0; i < contacts.size(); i++) {
		        String contactId = contacts.get(i).getUserId();
		        MessageInfo messageInfo = MyDataHander.getContactShowInfoByCardId( myId, contactId);
		        ContactShowInfo csi = new ContactShowInfo();
		        //联系人卡号
		        csi.setCardId(contactId);
		        //联系人头像
		        csi.setHeadImage(contacts.get(i).getImage());
		        //最后一次消息内容
		        csi.setLastMsg(messageInfo.getMessage());
		        //最后一次消息时间
		        csi.setLastMsgTime(messageInfo.getTime());
		        //消息是否已读
		        Boolean isRead = (messageInfo.getRead()).equals("1")?true:false;
		        csi.setRead(isRead);
		        //联系人昵称
		        csi.setUsername(contacts.get(i).getUserName());
		        contactShowInfo.add(csi);
	        }
	        contactAdapter = new ContactAdapter(this, R.layout.item_wechat_main, contactShowInfo);
	        //获取联系人列表UI
	        contactListView = findViewById(R.id.activity_wechat_lv);
	        //给联系人列表UI设置适配器和数据
	        contactListView.setAdapter(contactAdapter);
	        /** 数据库中取出20条最新的消息数据 */
	        //从数据库中取出20条最新的消息数据
	        messageInfos = MyDataHander.getScrollMessageBySendIdOrReceiveId(otherId,0,20);
	        //将这20条数据设置为已读
	        int length = messageInfos.size();
	        for(int i = 0; i < length; i++) {
		        MyDataHander.updateReadStateByMessageId(Constant.MESSAGE_READ, messageInfos.get(i).getId());
	        }

	        displayMessageData = new ArrayList<>();
	        for (int i = 0; i < length; i++) {
		        MessageInfo messageInfo = messageInfos.get(length - i - 1);
		        //Java中String类型转换成数据库中的日期类型，添加到数据库
		        //创建sdf对象，指定日期格式类型
		        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		        //sdf将字符串转化成java.util.Date
		        java.util.Date parse=null;
		        String timeString = messageInfo.getTime();
		        try {
			        parse = sdf.parse(timeString);
		        } catch (ParseException e) {
			        e.printStackTrace();
		        }
		        //java.util.Date转换成long
		        long time = parse.getTime();
		        MsgData msgData = new MsgData();
		        //设置消息时间
		        msgData.setTimeStamp(time);
		        //设置消息内容
		        msgData.setMsg(messageInfo.getMessage());
		        //设置消息类别
		        int msgType = messageInfo.getMySend().equals(Constant.MESSAGE_MYSEND)?Constant.TYPE_SENDER_MSG:Constant.TYPE_RECEIVER_MSG;
		        msgData.setMsgType(msgType);
		        msgData.setProfile_res(Constant.OTHER_IMAGE);
		        displayMessageData.add(i, msgData);
	        }

	        chatadapter = new ChatAdapter(this, displayMessageData);
	        rv.setAdapter(chatadapter);
	        rv.scrollToPosition(displayMessageData.size() - 1);
        }

        btn_send.setOnClickListener((v) -> {
            //将发送的信息显示到聊天界面，并且清楚发送文本
            String sendMsg = et_msg.getText().toString();
            //我发送的消息
            MsgData msgData = new MsgData(sendMsg, HelpUtils.getCurrentMillisTime(), Constant.MY_IMAGE, Constant.TYPE_SENDER_MSG);
	        displayMessageData.add(displayMessageData.size(), msgData);
	        chatadapter.notifyDataSetChanged();
            rv.scrollToPosition(displayMessageData.size() - 1);
            et_msg.setText("");
            //将发送的数据打包成为RD的格式
            //通过串口将数据发送到RD模块
            SerialPortUtils.sendMessage(otherId,sendMsg);

            //将数据存储到数据库
            MessageInfo messageInfo = new MessageInfo();
            //设置成已读
            messageInfo.setRead(Constant.MESSAGE_READ);
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String time = sdf.format(new Date());
            //设置发送本地时间
            messageInfo.setTime(time);
            //发送人ID
            messageInfo.setSendId(myId);
            //接收人ID
            messageInfo.setReceiveId(otherId);
            //发送的内容
            messageInfo.setMessage(sendMsg);
            //保存到数据库里面
            MyDataHander.addMessage(messageInfo);
        });
    }

    private Animation getVisibleAnim(boolean show, View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int y = view.getMeasuredHeight() / 4;
        int x = view.getMeasuredWidth() / 4;
        if (show) {
            ScaleAnimation showAnim = new ScaleAnimation(0.01f, 1f, 0.01f, 1f, x, y);
            showAnim.setDuration(200);
            return showAnim;

        } else {

            ScaleAnimation hiddenAnim = new ScaleAnimation(1f, 0.01f, 1f, 0.01f, x, y);
            hiddenAnim.setDuration(200);
            return hiddenAnim;
        }
    }

    public void refreshDisplay(String sendMsg) {
        RecyclerView rv = findViewById(R.id.activity_wechat_chat_rv);
        MsgData msgData = new MsgData(sendMsg, HelpUtils.getCurrentMillisTime(), Constant.MY_IMAGE, Constant.TYPE_RECEIVER_MSG);
	    displayMessageData.add(displayMessageData.size(), msgData);
	    chatadapter.notifyDataSetChanged();
        rv.scrollToPosition(displayMessageData.size() - 1);
    }

}

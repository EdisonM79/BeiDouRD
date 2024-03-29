package com.cdjzsk.rd.beidourd;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.UnsupportedEncodingException;
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


	/** 名称显示 */
	private TextView nameText;
	/** 添加联系人*/
	private ImageView addContact;
	/** 查看和修改联系人*/
	private ImageView editContact;
	/** 联系人ListView */
	private ListView contactListView;
	/** 搜索框 */
	private SearchView searchView;
	/** 消息发送按钮 */
	private Button btn_send;
	/** 聊天对方的Id*/
    private String otherId;
    /**本机的卡号Id*/
	private String myId;
    /** 原始的聊天数据列表*/
    public List<MessageInfo> messageInfos;
	/** 封装的聊天信息数据*/
    public List<MsgData> displayMessageData = new ArrayList<>();
	 /** 聊天信息适配器*/
    private ChatAdapter chatadapter;
	/** 数据库联系人列表 */
	public List<User> contacts;
	/** 封装后联系人列表*/
	List<ContactShowInfo> contactShowInfo = new ArrayList<>();
	/** 联系人适配器 */
	ContactAdapter contactAdapter;
	/** 发送倒计时器 */
	public TimeCount timeCount;
	/**  聊天内容显示界面*/
	RecyclerView rv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_chat);
	    nameText = findViewById(R.id.activity_wechat_chat_tv_name);

	    Intent intent = getIntent();
	    myId = intent.getStringExtra("myId");
	    //获取联系人列表UI
	    contactListView = findViewById(R.id.activity_wechat_lv);

		if (Constant.TEST_UI_MODEL) {
			initComponents();
			initSerialClient();
		}
		//启动自动过滤
		contactListView.setTextFilterEnabled(true);

	    searchView = findViewById(R.id.searchView);
	    //显示搜索按钮
	    searchView.setSubmitButtonEnabled(true);
	    //默认提示文本
	    searchView.setQueryHint("查找联系人");

	    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
		    @Override
		    public boolean onQueryTextSubmit(String s) {
			    return false;
		    }

		    @Override
		    public boolean onQueryTextChange(String s) {
			    if (TextUtils.isEmpty(s)){
				    contactListView.clearTextFilter();
				    contactAdapter.getFilter().filter("");
			    }
			    else {
				    contactAdapter.getFilter().filter(s);
			    }
			    return true;
		    }
	    });


		/** 添加用户的按钮 */
	    addContact = findViewById(R.id.addContact);
	    /** 编辑用户的按钮 */
	    editContact = findViewById(R.id.activity_wechat_chat_profile);

	    //编辑用户按钮点击事件
	    editContact.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
			    Intent intent = new Intent(ChatActivity.this, EditContactActivity.class);
			    intent.putExtra("otherId",otherId);
			    intent.putExtra("otherName",nameText.getText().toString());
			    startActivity(intent);
		    }
	    });
	    //添加用户按钮点击事件
	    addContact.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
			    Intent intent = new Intent(ChatActivity.this, AddContactActivity.class);
			    startActivity(intent);
		    }
	    });
    }

	@Override
	protected void onRestart() {
		super.onRestart();

		Log.e("onRestart", "updateContactList()");
		updateContactList();
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
					if (result.length >= 6 && "TXA".equals(result[1])) {
						int time = Integer.valueOf(result[5].substring(0,4));
						sendMassageState(result[2],result[3], time);
					}
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
					//解析信息数据
					MessageInfo msgInfo = analysis(msg);
					//保存信息和联系人数据
					saveMessage(msgInfo);
					//更新联系人列表
					updateContactList(msgInfo);
					//更新对话信息
					updataChatList(msgInfo);
				}
			}
		});
	}

	/**
	 * 消息发送状态
	 * @param state1
	 * @param state2
	 */
	private void sendMassageState(String state1, String state2, int lastTime) {

		if (Constant.MESSAGE_SEND_SUCCESS.equals(state1)) {
			Toast.makeText(this, "消息发送成功！", Toast.LENGTH_SHORT).show();
			//消息发送成功，倒计时开始
			timeCount.start();
		} else if (!Constant.MESSAGE_SEND_SUCCESS.equals(state1) && lastTime > 0){
			//取消以前的按钮倒计时
			timeCount.cancel();
			//按照最新的计时
			TimeCount newTimeCount = new TimeCount(lastTime * 1000,1000,btn_send);
			newTimeCount.start();
			String context = "频度未到，还需等待"+ lastTime + "秒";
			Toast.makeText(this, context, Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "消息发送失败！", Toast.LENGTH_SHORT).show();
		}
	}
	public static String getWholeText(String text, int byteCount){
		try {
			if (text != null && text.getBytes("GB2312").length > byteCount) {
				char[] tempChars = text.toCharArray();
				int sumByte = 0;
				int charIndex = 0;
				for (int i = 0, len = tempChars.length; i < len; i++) {
					char itemChar = tempChars[i];
					// 根据Unicode值，判断它占用的字节数
					if (itemChar >= 0x0000 && itemChar <= 0x007F) {
						sumByte += 1;
					} else {
						sumByte += 2;
					}
					if (sumByte > byteCount) {
						charIndex = i;
						break;
					}
				}
				return String.valueOf(tempChars, 0, charIndex);
			}
		} catch (UnsupportedEncodingException e) {
		}
		return text;
	}

    private void initComponents() {

	    /**  发送信息按钮*/
	    btn_send = findViewById(R.id.activity_wechat_chat_btn_send);
		/** 给发送按钮设置发送倒计时 */
	    timeCount = new TimeCount(60*1000,1000,btn_send);
	    /**  返回按钮*/
	    ImageView iv_back = findViewById(R.id.activity_wechat_chat_back);
		//返回按钮点击事件
	    iv_back.setOnClickListener(new View.OnClickListener() {
		    @Override
		    public void onClick(View view) {
			    finish();
		    }
	    });
	    /**  聊天内容显示界面*/
        rv = findViewById(R.id.activity_wechat_chat_rv);
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
	            Editable editable = et_msg.getText();
	            int len = 0;
	            try {
		            len = editable.toString().getBytes("GB2312").length;
	            } catch (Exception e) {
	            	e.printStackTrace();
	            }
	            if(len > Constant.MAX_BYTE)
	            {
		            int selEndIndex = Selection.getSelectionEnd(editable);
		            String str = editable.toString();
		            //截取新字符串
		            String newStr = getWholeText(str, Constant.MAX_BYTE);
		            et_msg.setText(newStr);
		            editable = et_msg.getText();

		            //新字符串的长度
		            int newLen = editable.length();
		            //旧光标位置超过字符串长度
		            if(selEndIndex > newLen)
		            {
			            selEndIndex = editable.length();
		            }
		            //设置新光标所在的位置
		            Selection.setSelection(editable, selEndIndex);
	            }
            }
            @Override
            public void afterTextChanged(Editable s) {}

        });


	    /**
	     * 发送按钮点击事情
	     */
	    btn_send.setOnClickListener((v) -> {
		    //将发送的信息显示到聊天界面，并且清除发送文本
		    String sendMsg = et_msg.getText().toString();
		    //避免发送空白的消息
		    if(("").equals(sendMsg) || null == sendMsg) {
		    	return;
		    }
		    //我发送的消息
		    MsgData msgData = new MsgData(sendMsg, HelpUtils.getCurrentMillisTime(), Constant.MY_IMAGE, Constant.MESSAGE_MYSEND);
		    displayMessageData.add(displayMessageData.size(), msgData);
		    chatadapter.notifyDataSetChanged();
		    rv.scrollToPosition(displayMessageData.size() - 1);
		    et_msg.setText("");
		    //将发送的数据打包成为RD的格式
		    //通过串口将数据发送到RD模块
		    SerialPortUtils.sendMessage(otherId,sendMsg);

		    /**将数据存储到数据库*/
		    MessageInfo messageInfo = new MessageInfo();
		    //设置成已读
		    messageInfo.setRead(Constant.MESSAGE_READ);
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		    String time = sdf.format(new Date());
		    //设置发送本地时间
		    messageInfo.setTime(time);
		    //发送人ID
		    messageInfo.setSendId(myId);
		    //接收人ID
		    messageInfo.setReceiveId(otherId);
		    //发送的内容
		    messageInfo.setMessage(sendMsg);
		    //设置成由我发送
		    messageInfo.setMySend(Constant.MESSAGE_MYSEND);
		    //保存到数据库里面
		    MyDataHander.addMessage(messageInfo);
	    });

	    /**
	     * 设置联系人列表单项点击事件
	     */
	    contactListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
			    ContactShowInfo sci = contactShowInfo.get(i);
			    otherId = sci.getCardId();
			    //清除以前的数据
			    displayMessageData.clear();
			    nameText.setText(sci.getUsername());

			    /** 数据库中取出消息数据 */
			    //从数据库中消息数据
			    messageInfos = MyDataHander.getScrollMessageBySendIdOrReceiveId(myId, otherId);
			    if (null != messageInfos) {
				    //将数据设置为已读
				    int length = messageInfos.size();
				    //重新装新的数据
				    for (int k = 0; k < length; k++) {
					    MessageInfo messageInfo = messageInfos.get(length - k - 1);
					    //设置未读的消息为已读k
					    if (messageInfo.getRead().equals(Constant.MESSAGE_NOTREAD)) {
						    MyDataHander.updateReadStateByMessageId(Constant.MESSAGE_READ, messageInfo.getId());
					    }
					    //Java中String类型转换成数据库中的日期类型，添加到数据库
					    //创建sdf对象，指定日期格式类型
					    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					    //sdf将字符串转化成java.util.Date
					    java.util.Date parse = null;
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
					    msgData.setMsgType(messageInfo.getMySend());
					    msgData.setProfile_res(Constant.OTHER_IMAGE);
					    displayMessageData.add(k, msgData);
				    }
			    }
			    Log.e("onItemClick", "419");
			    chatadapter.notifyDataSetChanged();
			    rv.scrollToPosition(displayMessageData.size() - 1);

			    //取消选项的小红点
			    contactShowInfo.get(i).setRead(true);
			    contactAdapter.notifyDataSetChanged();
		    }
	    });

        /** 数据库中取出所有的联系人 */
        contacts = MyDataHander.getAllUser();

        if (contacts.size() != 0) {
	        /** 设置对方的卡号为当前第一个人 */
	        otherId = contacts.get(0).getUserId();
	        /** 设置聊天名称 */
	        nameText.setText(contacts.get(0).getUserName());
	        for (int i = 0; i < contacts.size(); i++) {
		        String contactId = contacts.get(i).getUserId();
		        MessageInfo messageInfo = MyDataHander.getContactShowInfoByCardId(myId, contactId);
		        ContactShowInfo csi = new ContactShowInfo();
		        //联系人卡号
		        csi.setCardId(contactId);
		        //联系人头像
		        //因为更改了数据库，所以检测一下头像是否是空值
		        if (contacts.get(i).getImage() != Constant.OTHER_IMAGE){
			        csi.setHeadImage(Constant.OTHER_IMAGE);
		        } else {
			        csi.setHeadImage(contacts.get(i).getImage());
		        }
		        if (null != messageInfo) {
			        //最后一次消息内容
			        csi.setLastMsg(messageInfo.getMessage());
			        //最后一次消息时间
			        csi.setLastMsgTime(messageInfo.getTime());
			        //消息是否已读
			        Boolean isRead = (messageInfo.getRead()).equals(Constant.MESSAGE_READ);
			        csi.setRead(isRead);
		        }
		        //联系人昵称
		        csi.setUsername(contacts.get(i).getUserName());
		        contactShowInfo.add(csi);
	        }
        }
	        //给联系人列表UI设置适配器和数据
	        contactAdapter = new ContactAdapter(this, R.layout.item_wechat_main, contactShowInfo);
	        contactListView.setAdapter(contactAdapter);
	        /** 数据库中取出消息数据 */
	        messageInfos = MyDataHander.getScrollMessageBySendIdOrReceiveId(myId,otherId);
	        displayMessageData = new ArrayList<>();
	        if (null != messageInfos) {
		        //将数据设置为已读
		        int length = messageInfos.size();
		        for (int i = 0; i < length; i++) {
			        MessageInfo messageInfo = messageInfos.get(length - i - 1);
			        //只要不是未读，都设置为已读，兼容其他情况
			        if (!messageInfo.getRead().equals(Constant.MESSAGE_READ)) {
				        MyDataHander.updateReadStateByMessageId(Constant.MESSAGE_READ, messageInfo.getId());
			        }
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
			        msgData.setMsgType(messageInfo.getMySend());
			        msgData.setProfile_res(Constant.OTHER_IMAGE);
			        displayMessageData.add(i, msgData);
		        }
	        }
	        chatadapter = new ChatAdapter(this, displayMessageData);
	        rv.setAdapter(chatadapter);
	        rv.scrollToPosition(displayMessageData.size() - 1);
    }

	/**
	 * 解析BDTXR信息数据
	 * @param BDTXR
	 * @return
	 */
	private MessageInfo analysis(String BDTXR) {

		//用“，”号将消息进行分段
		String[] msgList = BDTXR.split(",");
		MessageInfo messageInfo = new MessageInfo();
		//接收ID
		if (null != myId && !("").equals(myId) ) {
			messageInfo.setReceiveId(myId);
		}
		String userId = "";
		if (msgList.length >= 5) {
			//发送ID
			userId = msgList[2];
			messageInfo.setSendId(userId);
			//去掉末尾的*34/r/n以后的消息内容
			String message = msgList[5].substring(0,(msgList[5].length()-5));
			//消息内容
			messageInfo.setMessage(message);
		}

		//获取当前系统时间
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		String time = sdf.format(new Date());
		//接收消息的本地时间
		messageInfo.setTime(time);
		//设置消息为未读
		messageInfo.setRead(Constant.MESSAGE_NOTREAD);
		//设置消息标志--不是本机发送
		messageInfo.setMySend(Constant.MESSAGE_NOTMYSEND);
		//将信息存入数据库
		return messageInfo;
	}

	/**
	 * 保存消息数据
	 * @param messageInfo
	 */
	private void saveMessage(MessageInfo messageInfo) {
	    //将信息存入数据库
	    MyDataHander.addMessage(messageInfo);
	    //用发送人ID去查询是否是已有联系人
		String sendId = messageInfo.getSendId();
	    boolean save = MyDataHander.isUserExit(sendId);
	    //不存在，保存联系人
	    if (!save) {
		    User user = new User();
		    user.setUserId(sendId);
		    //暂时先把名字设置为卡号
		    user.setUserName(sendId);
		    //暂时先把头像设置为统一头像
		    user.setImage(Constant.OTHER_IMAGE);
		    //保存用户
		    MyDataHander.addUser(user);
	    }
    }

	/**
	 * 通过消息去更新联系人列表
	 * @param messageInfo
	 */
	private void updateContactList(MessageInfo messageInfo) {

		/************更新联系人列表*************/
	    //查询联系人列表里面是否有当前消息的发送人
		int listLength = contactShowInfo.size();
	    Integer currentUserId = Integer.valueOf(messageInfo.getSendId());
	    boolean isHave = false;
		for (int i = 0; i < listLength; i++) {
			ContactShowInfo cs = contactShowInfo.get(i);
			Integer havaUserId = Integer.valueOf(cs.getCardId());
			if (currentUserId.equals(havaUserId)) {
				//设置为未读
				cs.setRead(false);
				//最后一条消息的时间
				cs.setLastMsgTime(messageInfo.getTime());
				//更新消息
				cs.setLastMsg(messageInfo.getMessage());
				//更新列表
				contactShowInfo.set(i,cs);
				//设置为有
				isHave = true;
			}
		}
	    //如果在联系人列表里面没有，则需要新添加
	    if (!isHave) {
		    ContactShowInfo newContact = new ContactShowInfo();
		    newContact.setCardId(messageInfo.getSendId());
		    newContact.setHeadImage(Constant.OTHER_IMAGE);
		    newContact.setLastMsg(messageInfo.getMessage());
		    newContact.setLastMsgTime(messageInfo.getTime());
		    newContact.setUsername(messageInfo.getSendId());
		    newContact.setRead(false);
		    contactShowInfo.add(newContact);
	    }
	    //通知联系人适配器数据更改
	    contactAdapter.notifyDataSetChanged();
    }

	/**
	 * 通过数据库取更新联系人列表
	 */
	private void updateContactList() {
		//清空联系人列表
		contacts.clear();
		//从数据库里面取出所有的联系人
		contacts = MyDataHander.getAllUser();
		//清空联系人显示列表
		contactShowInfo.clear();
		//循环遍历查找所有的联系人最新信息
		if (contacts.size() != 0) {
			for (int i = 0; i < contacts.size(); i++) {
				String contactId = contacts.get(i).getUserId();
				//用联系人卡号去搜索最后一次信息
				MessageInfo messageInfo = MyDataHander.getContactShowInfoByCardId( myId, contactId);
				ContactShowInfo csi = new ContactShowInfo();
				//联系人卡号
				csi.setCardId(contactId);
				//联系人头像
				//因为更改了数据库，所以检测一下头像是否是空值
				if (contacts.get(i).getImage() != Constant.OTHER_IMAGE){
					csi.setHeadImage(Constant.OTHER_IMAGE);
				} else {
					csi.setHeadImage(contacts.get(i).getImage());
				}
				if (null != messageInfo) {
					//最后一次消息内容
					csi.setLastMsg(messageInfo.getMessage());
					//最后一次消息时间
					csi.setLastMsgTime(messageInfo.getTime());
					//消息是否已读
					Boolean isRead = (messageInfo.getRead()).equals(Constant.MESSAGE_READ);
					csi.setRead(isRead);
				}
				//联系人昵称
				csi.setUsername(contacts.get(i).getUserName());
				contactShowInfo.add(csi);
			}

			//通知适配器更新
			contactAdapter.notifyDataSetChanged();
		} else {
			//清空联系人
			contactShowInfo.clear();
			//通知联系人列表适配器更新
			contactAdapter.notifyDataSetChanged();
			//清空聊天对话信息
			displayMessageData.clear();
			//通知聊天信息更新
			chatadapter.notifyDataSetChanged();

		}
	}

	/**
	 * 通过聊天对象的Id来更改聊天界面的信息
	 * @param messageInfo
	 */
	private void updataChatList(MessageInfo messageInfo) {
	//来信人正好为当前联系人,则更新界面，否则不更新
	if (Integer.valueOf(otherId).equals(Integer.valueOf(messageInfo.getSendId()))) {
		//Java中String类型转换成数据库中的日期类型，添加到数据库
		//创建sdf对象，指定日期格式类型
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		//sdf将字符串转化成java.util.Date
		java.util.Date parse=null;
		String timeString = messageInfo.getTime();
		try {
			parse = sdf2.parse(timeString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//java.util.Date转换成long
		long time2 = parse.getTime();
		MsgData msgData = new MsgData();
		//设置消息时间
		msgData.setTimeStamp(time2);
		//设置消息内容
		msgData.setMsg(messageInfo.getMessage());
		//设置消息类别
		msgData.setMsgType(messageInfo.getMySend());
		msgData.setProfile_res(Constant.OTHER_IMAGE);
		displayMessageData.add(displayMessageData.size(), msgData);
		chatadapter.notifyDataSetChanged();
		rv.scrollToPosition(displayMessageData.size() - 1);
		}
	}
	/**
	 * 内部类-用于给按钮设置定时
	 */
	class TimeCount extends CountDownTimer {

		private Button button;

		public TimeCount(long millisInFuture, long countDownInterval, Button button) {
			super(millisInFuture, countDownInterval);
			this.button = button;
		}
		@Override
		public void onTick(long millisUntilFinished) {
			this.button.setClickable(false);
			this.button.setText("("+millisUntilFinished / 1000 +") ");
			this.button.setTextColor(Color.parseColor("#ff0000"));
		}
		@Override
		public void onFinish() {
			this.button.setText("发送");
			this.button.setClickable(true);
			this.button.setTextColor(Color.parseColor("#ffffff"));
		}
	}
}

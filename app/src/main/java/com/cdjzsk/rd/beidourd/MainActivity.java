package com.cdjzsk.rd.beidourd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.cdjzsk.rd.beidourd.adapter.ContactsAdapter;
import com.cdjzsk.rd.beidourd.bean.Contact;
import com.cdjzsk.rd.beidourd.data.MyDataBaseHelper;
import com.cdjzsk.rd.beidourd.data.MyDataHander;
import com.cdjzsk.rd.beidourd.data.entity.User;
import com.jzsk.seriallib.ClientStateCallback;
import com.jzsk.seriallib.SerialClient;
import com.jzsk.seriallib.SupportProtcolVersion;
import com.jzsk.seriallib.conn.MessageListener;
import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ClientStateCallback {

	private static final String TAG = LogUtils.makeTag(MainActivity.class);
//	@BindView(R.id.textView)
//	TextView mTextView;
//	@BindView(R.id.btn_regsiter)
//	Button mBtnRegsiter;
//	@BindView(R.id.btn_connect)
//	Button mBtnConnect;
//	@BindView(R.id.btn_close)
//	Button mBtnClose;
//	@BindView(R.id.btn_select)
//	Button mBtnSelect;
	@BindView(R.id.contacts)
	ListView listView;//ListView组件


	private SerialClient mSerialClient;
	private MyDataBaseHelper dbHelper;
	private MyDataHander myDataHander;
	private byte[] ICA = "CCICA,0,00".getBytes();
	private byte[] BSI = "$CCRMO,BSI,2,2*24".getBytes();

	private List<Contact> contacts = new ArrayList<Contact>();//存储数据
	private ContactsAdapter contactsAdapter;//ListView的数据适配器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
//		mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		myDataHander = new MyDataHander(this);
		//创建一个数据库助手类
//		dbHelper = new MyDataBaseHelper(this, "BDStore.db", null, 1); // 执行这句并不会创建数据库文件
		//获取数据库输入对象，对于增删改都可以用db.execSQL(String sql);  来执行sql语句
		//db.execSQL("insert into book(name , author, pages, price) values(\"Android数据库操作指南\", \"panda fang\", 200, 35.5)");
		//遇到字符串要转义 有没有觉得很蛋疼， 用下面的方法就好多了
		//db.execSQL("insert into book(name , author, pages, price) values(?, ? ,? ,? )", new String[]{"Android数据库操作指南", "panda fang", "200", "35.5"});
//		SQLiteDatabase db =  dbHelper.getWritableDatabase();

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				Contact contact = (Contact)listView.getItemAtPosition(i);
				Log.d(TAG, "onItemClick: "+ contact.getCardId());
				//把点击到的卡号发送给Activity
				//contactcallback.SendContactValue(contact.getCardId());
				//Toast.makeText(getActivity(),contact.getCardId(),Toast.LENGTH_SHORT);
			}
		});
		initContact();
	}
	public void initContact(){
		//User user = new User("655326","Json");
		//myDataHander.addUser(user);
		List<User> contacts = myDataHander.getAllUser();
		for (int i = 0; i < contacts.size(); i++) {
			addContact(contacts.get(i).getUserId(),"2018-02-24 14:43");
		}
	}

	/**
	 * 添加联系人
	 * @param cardId
	 */
	public void addContact(String cardId, String time){
		Contact newContact = new Contact(cardId, R.mipmap.ic_launcher,time);
		contacts.add(newContact);
		//通知ListView更改数据源
		if (contactsAdapter != null) {
			contactsAdapter.notifyDataSetChanged();
			listView.setSelection(contacts.size() - 1);//设置显示列表的最后一项
		} else {
			contactsAdapter = new ContactsAdapter(this, contacts);
			listView.setAdapter(contactsAdapter);
			listView.setSelection(contacts.size() - 1);
		}
	}

	private void initSerialClient() {
		//初始化串口对象
		mSerialClient = new SerialClient();
		//设置串口消息监听类
		mSerialClient.setMessageListener(new MessageListener() {
			@Override
			public void processMessage(final BaseMessage msg) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setTxt(msg.toString());
					}
				});
			}
		});
		//模拟串口读取模式
		mSerialClient.setDebugMode(false);
		//mSerialClient.setDebugMode(true);
		mSerialClient.connect(this,SupportProtcolVersion.V21);
	}

	private void setTxt(final String msg){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(msg.contains("BDICI"))
				{
					String[] msgList = msg.split(",");
					String carId = msgList[1];
					String frequency = msgList[5];
				}
				if(msg.contains("BDFKI"))
				{
					String[]  result = msg.split(",");
				}
				if(msg.contains("BDTXR"))
				{
					String[] msgList = msg.split(",");

				}
			}
		});

	}
	private void closeSerialClient(){
		if(mSerialClient != null) {
			mSerialClient.close();
			mSerialClient = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		closeSerialClient();
	}

	@Override
	public void connectSuccess() {
		setTxt("串口连接成功");
		Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void connectFail() {
		setTxt("串口连接失败");
		Toast.makeText(this, "连接失败", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void connectionClosed() {
		setTxt("串口连接关闭");
		Toast.makeText(this, "已关闭连接", Toast.LENGTH_SHORT).show();
	}

//	@OnClick({R.id.btn_regsiter,R.id.btn_connect, R.id.btn_close, R.id.btn_select})
//	public void onViewClicked(View view) {
//		switch (view.getId()) {
//			case R.id.btn_regsiter:
//				if(mSerialClient != null && mSerialClient.isConnected()) {
//					byte[] sendMsg = ArrayUtils.concatenate(new byte[]{'$'},ICA,new byte[]{'*'},ArrayUtils.bytesToHexString(new byte[]{ArrayUtils.xorCheck(ICA)}).getBytes(),new byte[]{0x0D,0x0A});
//					Message msg  = new Message(sendMsg);
//					mSerialClient.sendMessage(msg);
//					long currentTime = System.currentTimeMillis();
//					String timeNow = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
//					com.cdjzsk.rd.beidourd.data.entity.Message bdmessage = new com.cdjzsk.rd.beidourd.data.entity.Message("234","345","sfksaj",timeNow);
//					myDataHander.addMessage(bdmessage);
//					setTxt("写入成功:" + msg.toString());
//				}
//				break;
//			case R.id.btn_connect:
//				initSerialClient();
//				break;
//			case R.id.btn_close:
//				closeSerialClient();
//				break;
//			case R.id.btn_select:
//				List<com.cdjzsk.rd.beidourd.data.entity.Message> messageList = myDataHander.getScrollMessageBySendIdOrReceiveId("234",0,5);
//				for (int i =0; i< messageList.size(); i++)
//				{
//					setTxt(messageList.get(i).toString());
//				}
//		}
//	}
}
package com.cdjzsk.rd.beidourd;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import com.jzsk.seriallib.msg.msgv21.Message;
import com.jzsk.seriallib.util.ArrayUtils;
import com.jzsk.seriallib.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements ClientStateCallback {

	private static final String TAG = LogUtils.makeTag(MainActivity.class);
	@BindView(R.id.cardId)
	TextView cardId;
	@BindView(R.id.frequency)
	TextView frequency;
	@BindView(R.id.contacts)
	ListView listView;//ListView组件
	@BindView(R.id.bs_tv_0)
	TextView bsiTv_0;
	@BindView(R.id.bs_tv_1)
	TextView bsiTv_1;
	@BindView(R.id.bs_tv_2)
	TextView bsiTv_2;
	@BindView(R.id.bs_tv_3)
	TextView bsiTv_3;
	@BindView(R.id.bs_tv_4)
	TextView bsiTv_4;
	@BindView(R.id.bs_tv_5)
	TextView bsiTv_5;
	@BindView(R.id.bs_tv_6)
	TextView bsiTv_6;
	@BindView(R.id.bs_tv_7)
	TextView bsiTv_7;
	@BindView(R.id.bs_tv_8)
	TextView bsiTv_8;
	@BindView(R.id.bs_tv_9)
	TextView bsiTv_9;
	@BindView(R.id.vertical_progressbar0)
	ProgressBar progressBar0;
	@BindView(R.id.vertical_progressbar1)
	ProgressBar progressBar1;
	@BindView(R.id.vertical_progressbar2)
	ProgressBar progressBar2;
	@BindView(R.id.vertical_progressbar3)
	ProgressBar progressBar3;
	@BindView(R.id.vertical_progressbar4)
	ProgressBar progressBar4;
	@BindView(R.id.vertical_progressbar5)
	ProgressBar progressBar5;
	@BindView(R.id.vertical_progressbar6)
	ProgressBar progressBar6;
	@BindView(R.id.vertical_progressbar7)
	ProgressBar progressBar7;
	@BindView(R.id.vertical_progressbar8)
	ProgressBar progressBar8;
	@BindView(R.id.vertical_progressbar9)
	ProgressBar progressBar9;


	private String mCardId;
	private SerialClient mSerialClient;
	private MyDataBaseHelper dbHelper;
	private MyDataHander myDataHander;
	private byte[] ICA = "CCICA,0,00".getBytes();
	private byte[] BSI = "CCRMO,BSI,2,2".getBytes();

	private List<Contact> contacts = new ArrayList<Contact>();//存储数据
	private ContactsAdapter contactsAdapter;//ListView的数据适配器

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		myDataHander = new MyDataHander(this);
		initSerialClient();
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
		byte[] sendMsgICA = ArrayUtils.concatenate(new byte[]{'$'}, ICA, new byte[]{'*'}, ArrayUtils.bytesToHexString(new byte[]{ArrayUtils.xorCheck(ICA)}).getBytes(), new byte[]{0x0D, 0x0A});
		Message msgICA = new Message(sendMsgICA);
		mSerialClient.sendMessage(msgICA);
		//打开波束功率输出
		byte[] sendMsgBSI = ArrayUtils.concatenate(new byte[]{'$'}, BSI, new byte[]{'*'}, ArrayUtils.bytesToHexString(new byte[]{ArrayUtils.xorCheck(BSI)}).getBytes(), new byte[]{0x0D, 0x0A});
		Message msgBSI = new Message(sendMsgBSI);
		mSerialClient.sendMessage(msgBSI);

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
		//mSerialClient.setDebugMode(false);
		mSerialClient.setDebugMode(true);
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
					String freqy = msgList[5];
					cardId.setText(carId);
					mCardId = carId;
					frequency.setText(freqy);
				}
				if(msg.contains("BDFKI"))
				{
					String[]  result = msg.split(",");
				}
				if(msg.contains("BDTXR"))
				{
					String[] msgList = msg.split(",");
					com.cdjzsk.rd.beidourd.data.entity.Message message = new com.cdjzsk.rd.beidourd.data.entity.Message();
					message.setReceiveId(mCardId);
					message.setSendId(msgList[2]);
					int index = msgList[5].length();
					//去掉末尾的*34/r/n
					message.setMessage(msgList[5].substring(0,(index-5)));
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
					String time = sdf.format(new Date());
					message.setTime(time);
					myDataHander.addMessage(message);
				}
				if(msg.contains("BDBSI"))
				{
					String[] bsiList = msg.split(",");
					bsiTv_0.setText(bsiList[3]);
					progressBar0.setProgress(Integer.parseInt(bsiList[3]));
					bsiTv_1.setText(bsiList[4]);
					progressBar1.setProgress(Integer.parseInt(bsiList[4]));
					bsiTv_2.setText(bsiList[5]);
					progressBar2.setProgress(Integer.parseInt(bsiList[5]));
					bsiTv_3.setText(bsiList[6]);
					progressBar3.setProgress(Integer.parseInt(bsiList[6]));
					bsiTv_4.setText(bsiList[7]);
					progressBar4.setProgress(Integer.parseInt(bsiList[7]));
					bsiTv_5.setText(bsiList[8]);
					progressBar5.setProgress(Integer.parseInt(bsiList[8]));
					bsiTv_6.setText(bsiList[9]);
					progressBar6.setProgress(Integer.parseInt(bsiList[9]));
					bsiTv_7.setText(bsiList[10]);
					progressBar7.setProgress(Integer.parseInt(bsiList[10]));
					bsiTv_8.setText(bsiList[11]);
					progressBar8.setProgress(Integer.parseInt(bsiList[11]));
					//最后一位包含波束功率，"*"和校验和
					String last = bsiList[12].substring(0,1);
					bsiTv_9.setText(last);
					progressBar9.setProgress(Integer.parseInt(last));

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
}
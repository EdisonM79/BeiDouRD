package com.cdjzsk.rd.beidourd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cdjzsk.rd.beidourd.adapter.ContactAdapter;
import com.cdjzsk.rd.beidourd.bean.ContactShowInfo;
import com.cdjzsk.rd.beidourd.data.MyDataHander;
import com.cdjzsk.rd.beidourd.data.entity.MessageInfo;
import com.cdjzsk.rd.beidourd.data.entity.User;
import com.cdjzsk.rd.beidourd.utils.HelpUtils;
import com.jzsk.seriallib.ClientStateCallback;
import com.jzsk.seriallib.SerialClient;
import com.jzsk.seriallib.SupportProtcolVersion;
import com.jzsk.seriallib.conn.MessageListener;
import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.msg.msgv21.Message;
import com.jzsk.seriallib.util.ArrayUtils;
import com.jzsk.seriallib.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
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
	@BindView(R.id.activity_wechat_lv)
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

	ListView lv;
	private String mCardId;
	private SerialClient mSerialClient;
	private MyDataHander myDataHander;
	private byte[] ICA = "CCICA,0,00".getBytes();
	private byte[] BSI = "CCRMO,BSI,2,1".getBytes();

	public static final String MESSAGE_READ = "1";
	public static final String MESSAGE_NOTREAD = "0";
	private int toolbarHeight, statusBarHeight;
	//联系人展示列表
	List<ContactShowInfo> infos = new LinkedList<>();
	//联系人适配器
	private ContactAdapter adapter;
	private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		//初始化串口
		initSerialClient();
		//初始化数据库
		myDataHander = new MyDataHander(this);
		//初始化模拟数据
		initData();


		statusBarHeight = HelpUtils.getStatusBarHeight(MainActivity.this);

		View view = getLayoutInflater().inflate(R.layout.activity_main, null);
		LinearLayout linearlayout1 = (LinearLayout)view.findViewById(R.id.toolLayout);
		LinearLayout linearlayout2 = (LinearLayout)view.findViewById(R.id.Bsi_Box);
		//measure方法的参数值都设为0即可
		linearlayout1.measure(0,0);
		linearlayout2.measure(0,0);
		toolbarHeight = linearlayout1.getMeasuredHeight();
		//获取组件高度
		statusBarHeight += linearlayout2.getMeasuredHeight();
		statusBarHeight += 50;


		byte[] sendMsgICA = ArrayUtils.concatenate(new byte[]{'$'}, ICA, new byte[]{'*'}, ArrayUtils.bytesToHexString(new byte[]{ArrayUtils.xorCheck(ICA)}).getBytes(), new byte[]{0x0D, 0x0A});
		Message msgICA = new Message(sendMsgICA);
		mSerialClient.sendMessage(msgICA);
		//打开波束功率输出
		byte[] sendMsgBSI = ArrayUtils.concatenate(new byte[]{'$'}, BSI, new byte[]{'*'}, ArrayUtils.bytesToHexString(new byte[]{ArrayUtils.xorCheck(BSI)}).getBytes(), new byte[]{0x0D, 0x0A});
		Message msgBSI = new Message(sendMsgBSI);
		mSerialClient.sendMessage(msgBSI);
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
					MessageInfo messageInfo = new MessageInfo();
					//接收ID
					//messageInfo.setReceiveId(mCardId);
					messageInfo.setReceiveId(mCardId);
					//发送ID
					String userId = msgList[2];
					messageInfo.setSendId(userId);
					int index = msgList[5].length();
					//去掉末尾的*34/r/n
					//消息内容
					String message = msgList[5].substring(0,(index-5));
					messageInfo.setMessage(message);
					//获取当前系统时间
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
					String time = sdf.format(new Date());
					//接收消息的本地时间
					messageInfo.setTime(time);
					//设置消息为未读
					messageInfo.setRead(MESSAGE_NOTREAD);
					//将信息存入数据库
					myDataHander.addMessage(messageInfo);
					//用发送人ID去查询是否是已有联系人
					boolean save = myDataHander.isUserExit(userId);
					//不存在，保存联系人
					if (!save) {
						User user = new User();
						user.setUserId(userId);
						//暂时先把名字设置为卡号
						user.setUserName(userId);
						//暂时先把头像设置为统一头像
						user.setImage(R.drawable.hdimg_1);
						//保存用户
						myDataHander.addUser(user);

						//将新消息添加到队列
						infos.add(new ContactShowInfo(userId,R.drawable.hdimg_1,userId,message,time,false));
						if (infos.size() > 1) {
							//置顶消息
							stickyTop(adapter,infos,infos.size()-1);
						} else {
							adapter.notifyDataSetChanged();
						}

					} else { //存在联系人，直接更新就可以了
						//有新消息,需要更新联系人list
						for (int i = 0; i < infos.size(); i++) {
							String infoCardId = infos.get(i).getCardId();
							if (infoCardId.equals(userId)) {
								//设置为新的消息
								infos.get(i).setLastMsg(message);
								//设置为新的时间
								infos.get(i).setLastMsgTime(time);
								//设置消息为未读
								infos.get(i).setRead(false);
								//将消息置顶
								stickyTop(adapter,infos,i);
							}
						}
					}

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
		myDataHander = null;
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

	@SuppressLint("ClickableViewAccessibility")
	private void initData() {
		/**************/
		mCardId = "0412159";

		lv = findViewById(R.id.activity_wechat_lv);
		List<User> contacts = myDataHander.getAllUser();
		//初始化数据
		for (int i = 0; i < contacts.size(); i++) {
			String others = contacts.get(i).getUserId();
			int image = contacts.get(i).getImage();
			String name = contacts.get(i).getUserName();
			MessageInfo messageInfo = myDataHander.getContactShowInfoByCardId(mCardId,others);
			//判断是否已读
			boolean read;
			if(messageInfo.getRead().equals("1")) {
				read = true;
			} else {
				read = false;
			}
			infos.add(i, new ContactShowInfo(others, image, name, messageInfo.getMessage(), messageInfo.getTime(),read));
		}
		//初始化适配器
		adapter = new ContactAdapter(this, R.layout.item_wechat_main, infos);
		//设置适配器
		lv.setAdapter(adapter);

		//触摸监听器
		lv.setOnTouchListener(new View.OnTouchListener() {
			int preX, preY;
			//滑过&长按
			boolean isSlip = false, isLongClick = false;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						//长按时间暂不响应
						preX = (int) event.getX();
						preY = (int) event.getY();
						mHandler.postDelayed(() -> {
							isLongClick = true;
							int x = (int) event.getX();
							int y = (int) event.getY();
							//延时500ms后，其Y的坐标加入了Toolbar和statusBar高度
							int position = lv.pointToPosition(x, y - toolbarHeight - statusBarHeight);
							//initPopupMenu(v, x, y, adapter, position, infos);

						}, 500);
						break;

					case MotionEvent.ACTION_MOVE:
						int nowX = (int) event.getX();
						int nowY = (int) event.getY();

						int movedX = Math.abs(nowX - preX);
						int movedY = Math.abs(nowY - preY);
						if (movedX > 50 || movedY > 50) {
							isSlip = true;
							mHandler.removeCallbacksAndMessages(null);
							//处理滑动事件
						}
						break;


					case MotionEvent.ACTION_UP:
						mHandler.removeCallbacksAndMessages(null);
						if (!isSlip && !isLongClick) {
							//处理单击事件
							int position = lv.pointToPosition(preX, preY);

							Intent intent = new Intent(MainActivity.this, ChatActivity.class);
							String name = infos.get(position).getUsername();
							int image = infos.get(position).getHeadImage();
							intent.putExtra("name", name);
							intent.putExtra("profileId", image);
							startActivity(intent);
						} else {
							isSlip = false;
							isLongClick = false;
						}
						v.performClick();
						break;
				}

				return false;
			}
		});
	}

	private void updateContacts(ContactAdapter adapter, List<ContactShowInfo> datas) {

	}
	/**
	 * 设置已读还是未读
	 *
	 * @param isRead   true已读，false未读
	 * @param position item position
	 * @param adapter  数据源
	 * @param datas
	 */
	private void setIsRead(boolean isRead, int position, ContactAdapter adapter, List<ContactShowInfo> datas) {
		ContactShowInfo info = datas.get(position);
		info.setRead(isRead);
		adapter.notifyDataSetChanged();
	}

	/**
	 * 删除指定位置item
	 *
	 * @param position 指定删除position
	 * @param adapter  数据源
	 * @param datas
	 */
	private void deleteMsg(int position, ContactAdapter adapter, List<ContactShowInfo> datas) {
		datas.remove(position);
		adapter.notifyDataSetChanged();
	}
	/**
	 * 置顶item
	 *
	 * @param adapter
	 * @param datas
	 */
	private void stickyTop(ContactAdapter adapter, List<ContactShowInfo> datas, int position) {
		if (position > 0) {
			ContactShowInfo stickyTopItem = datas.get(position);
			datas.remove(position);
			datas.add(0, stickyTopItem);
		} else {
			return;
		}
		adapter.notifyDataSetChanged();
	}
}
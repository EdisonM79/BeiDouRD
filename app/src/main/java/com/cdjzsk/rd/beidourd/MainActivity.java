package com.cdjzsk.rd.beidourd;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cdjzsk.rd.beidourd.adapter.ContactAdapter;
import com.cdjzsk.rd.beidourd.adapter.SimpleMenuAdapter;
import com.cdjzsk.rd.beidourd.bean.ContactShowInfo;
import com.cdjzsk.rd.beidourd.component.PopupMenuWindows;
import com.cdjzsk.rd.beidourd.data.MyDataBaseHelper;
import com.cdjzsk.rd.beidourd.data.MyDataHander;
import com.jzsk.seriallib.ClientStateCallback;
import com.jzsk.seriallib.SerialClient;
import com.jzsk.seriallib.SupportProtcolVersion;
import com.jzsk.seriallib.conn.MessageListener;
import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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


	private String mCardId;
	private SerialClient mSerialClient;
	private MyDataBaseHelper dbHelper;
	private MyDataHander myDataHander;
	private byte[] ICA = "CCICA,0,00".getBytes();
	private byte[] BSI = "CCRMO,BSI,1,1".getBytes();

	public static final int TYPE_USER = 0x11;
	public static final int TYPE_SERVICE = 0X12;
	public static final int TYPE_SUBSCRIBE = 0x13;
	private int toolbarHeight, statusBarHeight;

	private Handler mHandler = new Handler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		initSerialClient();
		myDataHander = new MyDataHander(this);
		initData();
//		byte[] sendMsgICA = ArrayUtils.concatenate(new byte[]{'$'}, ICA, new byte[]{'*'}, ArrayUtils.bytesToHexString(new byte[]{ArrayUtils.xorCheck(ICA)}).getBytes(), new byte[]{0x0D, 0x0A});
//		Message msgICA = new Message(sendMsgICA);
//		mSerialClient.sendMessage(msgICA);
//		//打开波束功率输出
//		byte[] sendMsgBSI = ArrayUtils.concatenate(new byte[]{'$'}, BSI, new byte[]{'*'}, ArrayUtils.bytesToHexString(new byte[]{ArrayUtils.xorCheck(BSI)}).getBytes(), new byte[]{0x0D, 0x0A});
//		Message msgBSI = new Message(sendMsgBSI);
//		mSerialClient.sendMessage(msgBSI);
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
					//获取当前系统时间
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
					String time = sdf.format(new Date());
					message.setTime(time);
					//将信息存入数据库
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

	private void initData() {
		ListView lv = findViewById(R.id.activity_wechat_lv);
		int[] headImgRes = {R.drawable.hdimg_3, R.drawable.group1, R.drawable.hdimg_2, R.drawable.user_2,
				R.drawable.user_3, R.drawable.user_4, R.drawable.user_5, R.drawable.hdimg_4,
				R.drawable.hdimg_5, R.drawable.hdimg_6};

		String[] usernames = {"Fiona", "  ...   ", "冯小", "深圳社保", "服务通知", "招商银行信用卡",
				"箫景、Fiona", "吴晓晓", "肖箫", "唐小晓"};
		//最新消息
		String[] lastMsgs = {"我看看", "吴晓晓：无人超市啊", "最近在忙些什么", "八月一号猛料，内地社保在这2...",
				"微信支付凭证", "#今日签到#你能到大的，比想象...", "箫景:准备去哪嗨", "[Video Call]", "什么东西？", "[微信红包]"};

		String[] lastMsgTimes = {"17:40", "10:56", "7月26日", "昨天", "7月27日", "09:46",
				"7月18日", "星期一", "7月26日", "4月23日"};

		int[] types = {TYPE_USER, TYPE_USER, TYPE_USER, TYPE_SUBSCRIBE, TYPE_SERVICE, TYPE_SUBSCRIBE,
				TYPE_USER, TYPE_USER, TYPE_USER, TYPE_USER};
		//静音&已读
		boolean[] isMutes = {false, true, false, false, false, false, true, false, false, false};
		boolean[] isReads = {true, true, true, true, true, true, true, true, true, true};

		List<ContactShowInfo> infos = new LinkedList<>();

		for (int i = 0; i < headImgRes.length; i++) {
			infos.add(i, new ContactShowInfo(headImgRes[i], usernames[i], lastMsgs[i], lastMsgTimes[i], isMutes[i], isReads[i], types[i]));
		}
		ContactAdapter adapter = new ContactAdapter(this, R.layout.item_wechat_main, infos);
		lv.setAdapter(adapter);


		lv.setOnTouchListener(new View.OnTouchListener() {
			int preX, preY;
			boolean isSlip = false, isLongClick = false;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						preX = (int) event.getX();
						preY = (int) event.getY();
						mHandler.postDelayed(() -> {
							isLongClick = true;
							int x = (int) event.getX();
							int y = (int) event.getY();
							//延时500ms后，其Y的坐标加入了Toolbar和statusBar高度
							int position = lv.pointToPosition(x, y - toolbarHeight - statusBarHeight);
							initPopupMenu(v, x, y, adapter, position, infos);

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
							intent.putExtra("name", usernames[position]);
							intent.putExtra("profileId", headImgRes[position]);
							startActivity(intent);
						} else {
							isSlip = false;
							isLongClick = false;
						}
						break;
				}
				return false;
			}
		});
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
	 * 初始化popup菜单
	 */
	private void initPopupMenu(View anchorView, int posX, int posY, ContactAdapter adapter, int itemPos, List<ContactShowInfo> data) {
		List<String> list = new ArrayList<>();
		ContactShowInfo showInfo = data.get(itemPos);
		//初始化弹出菜单项
		switch (showInfo.getAccountType()) {
			case TYPE_SERVICE:
				list.clear();
				if (showInfo.isRead())
					list.add("标为未读");
				else
					list.add("标为已读");
				list.add("删除该聊天");
				break;

			case TYPE_SUBSCRIBE:
				list.clear();
				if (showInfo.isRead())
					list.add("标为未读");
				else
					list.add("标为已读");
				list.add("置顶公众号");
				list.add("取消关注");
				list.add("删除该聊天");
				break;

			case TYPE_USER:
				list.clear();
				if (showInfo.isRead())
					list.add("标为未读");
				else
					list.add("标为已读");
				list.add("置顶聊天");
				list.add("删除该聊天");
				break;
		}
		SimpleMenuAdapter<String> menuAdapter = new SimpleMenuAdapter<>(this, R.layout.item_menu, list);
		PopupMenuWindows ppm = new PopupMenuWindows(this, R.layout.popup_menu_general_layout, menuAdapter);
		int[] posArr = ppm.reckonPopWindowShowPos(posX, posY);
		ppm.setAutoFitStyle(true);
		ppm.setOnMenuItemClickListener((parent, view, position, id) -> {

			switch (list.get(position)) {
				case "标为未读":
					setIsRead(false, itemPos, adapter, data);
					break;

				case "标为已读":
					setIsRead(true, itemPos, adapter, data);
					break;

				case "置顶聊天":
				case "置顶公众号":
					stickyTop(adapter, data, itemPos);
					break;

				case "取消关注":
				case "删除该聊天":
					deleteMsg(itemPos, adapter, data);
					break;
			}
			ppm.dismiss();
		});
		ppm.showAtLocation(anchorView, Gravity.NO_GRAVITY, posArr[0], posArr[1]);
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
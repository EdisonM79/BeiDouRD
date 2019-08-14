package com.cdjzsk.rd.beidourd;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cdjzsk.rd.beidourd.adapter.ContactAdapter;
import com.cdjzsk.rd.beidourd.bean.ContactShowInfo;
import com.cdjzsk.rd.beidourd.data.MyDataHander;
import com.cdjzsk.rd.beidourd.data.entity.MessageInfo;
import com.cdjzsk.rd.beidourd.data.entity.User;
import com.cdjzsk.rd.beidourd.utils.SerialPortUtils;
import com.jzsk.seriallib.SerialClient;
import com.jzsk.seriallib.conn.MessageListener;
import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = LogUtils.makeTag(MainActivity.class);
	@BindView(R.id.card_id)
	TextView cardId;
	@BindView(R.id.frequency)
	TextView frequency;
	@BindView(R.id.readButton)
	Button readButton;
	@BindView(R.id.messageButton)
	Button messageButton;
	@BindView(R.id.sosButton)
	Button sosButton;
//	@BindView(R.id.bs_tv_2)
//	TextView bsiTv_2;
//	@BindView(R.id.bs_tv_3)
//	TextView bsiTv_3;
//	@BindView(R.id.bs_tv_4)
//	TextView bsiTv_4;
//	@BindView(R.id.bs_tv_5)
//	TextView bsiTv_5;
//	@BindView(R.id.bs_tv_6)
//	TextView bsiTv_6;
//	@BindView(R.id.bs_tv_7)
//	TextView bsiTv_7;
//	@BindView(R.id.bs_tv_8)
//	TextView bsiTv_8;
//	@BindView(R.id.bs_tv_9)
//	TextView bsiTv_9;
//	@BindView(R.id.vertical_progressbar0)
//	ProgressBar progressBar0;
//	@BindView(R.id.vertical_progressbar1)
//	ProgressBar progressBar1;
//	@BindView(R.id.vertical_progressbar2)
//	ProgressBar progressBar2;
//	@BindView(R.id.vertical_progressbar3)
//	ProgressBar progressBar3;
//	@BindView(R.id.vertical_progressbar4)
//	ProgressBar progressBar4;
//	@BindView(R.id.vertical_progressbar5)
//	ProgressBar progressBar5;
//	@BindView(R.id.vertical_progressbar6)
//	ProgressBar progressBar6;
//	@BindView(R.id.vertical_progressbar7)
//	ProgressBar progressBar7;
//	@BindView(R.id.vertical_progressbar8)
//	ProgressBar progressBar8;
//	@BindView(R.id.vertical_progressbar9)
//	ProgressBar progressBar9;

	ListView lv;
	//本机卡号
	public String mCardId;
	//串口操作
	public SerialClient mSerialClient;
	//串口工具类
	public SerialPortUtils serialPortUtils;
	//数据库
	private MyDataHander myDataHander;
	//读卡指令
	private String ICA = "CCICA,0,00";
	//打开波束功率
	private String BSI = "CCRMO,BSI,2,1";
	//消息已读标志
	public static final String MESSAGE_READ = "1";
	//消息未读标志
	public static final String MESSAGE_NOTREAD = "0";
	//高度
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
		//点击读卡按钮，发送读卡指令，打开波束功率输出
		readButton.setOnClickListener((View view)-> {
				//发送读卡指令
				SerialPortUtils.sendControl(ICA);
				//发送打开波束功率输出指令
				SerialPortUtils.sendControl(BSI);
		});
		//点击短报文按钮跳转到聊天界面
		messageButton.setOnClickListener((View view)-> {

				Intent intent = new Intent(MainActivity.this, ChatActivity.class);
				startActivity(intent);

		});
		//点击SOS按钮，暂时不做处理
		sosButton.setOnClickListener((View view)->{
			System.out.println("this is lambda");
		});
		//发送读卡指令
		SerialPortUtils.sendControl(ICA);
		//发送打开波束功率输出指令
		SerialPortUtils.sendControl(BSI);
	}

	/**
	 * 初始化串口工具，设置串口监听类
	 */
	private void initSerialClient() {
		//初始化串口工具类，设置串口消息监听类
		serialPortUtils = new SerialPortUtils(new MessageListener() {
			@Override
			public void processMessage(final BaseMessage msg) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						updataUI(msg.toString());
					}
				});
			}
		});
		mSerialClient = SerialPortUtils.getSerialClient();
	}

	private void updataUI(final String msg){
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
							}
						}
					}

				}
				if(msg.contains("BDBSI"))
				{
					String[] bsiList = msg.split(",");
//					bsiTv_0.setText(bsiList[3]);
//					progressBar0.setProgress(Integer.parseInt(bsiList[3]));
//					bsiTv_1.setText(bsiList[4]);
//					progressBar1.setProgress(Integer.parseInt(bsiList[4]));
//					bsiTv_2.setText(bsiList[5]);
//					progressBar2.setProgress(Integer.parseInt(bsiList[5]));
//					bsiTv_3.setText(bsiList[6]);
//					progressBar3.setProgress(Integer.parseInt(bsiList[6]));
//					bsiTv_4.setText(bsiList[7]);
//					progressBar4.setProgress(Integer.parseInt(bsiList[7]));
//					bsiTv_5.setText(bsiList[8]);
//					progressBar5.setProgress(Integer.parseInt(bsiList[8]));
//					bsiTv_6.setText(bsiList[9]);
//					progressBar6.setProgress(Integer.parseInt(bsiList[9]));
//					bsiTv_7.setText(bsiList[10]);
//					progressBar7.setProgress(Integer.parseInt(bsiList[10]));
//					bsiTv_8.setText(bsiList[11]);
//					progressBar8.setProgress(Integer.parseInt(bsiList[11]));
//					//最后一位包含波束功率，"*"和校验和
//					String last = bsiList[12].substring(0,1);
//					bsiTv_9.setText(last);
//					progressBar9.setProgress(Integer.parseInt(last));
				}
			}
		});
	}

	/**
	 * 关闭串口
	 */
	private void closeSerialClient(){
		if(mSerialClient != null) {
			mSerialClient.close();
			mSerialClient = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//关闭串口
		closeSerialClient();
		//关闭数据库连接
		myDataHander = null;
	}

}
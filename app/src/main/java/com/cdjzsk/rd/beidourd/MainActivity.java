package com.cdjzsk.rd.beidourd;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cdjzsk.rd.beidourd.data.MyDataHander;
import com.cdjzsk.rd.beidourd.data.entity.MessageInfo;
import com.cdjzsk.rd.beidourd.data.entity.User;
import com.cdjzsk.rd.beidourd.utils.Constant;
import com.cdjzsk.rd.beidourd.utils.SerialPortUtils;
import com.cdjzsk.rd.beidourd.utils.multiChildHistogram.MultiGroupHistogramChildData;
import com.cdjzsk.rd.beidourd.utils.multiChildHistogram.MultiGroupHistogramGroupData;
import com.cdjzsk.rd.beidourd.utils.multiChildHistogram.MultiGroupHistogramView;
import com.jzsk.seriallib.SerialClient;
import com.jzsk.seriallib.conn.MessageListener;
import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.util.LogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	private static final String TAG = LogUtils.makeTag(MainActivity.class);
	//记录用户首次点击返回键的时间
	private long firstTime = 0;

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
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;

	/** 水平监听器 */
	private OrientationEventListener orientationEventListener;
	/** 柱状图 */
	private MultiGroupHistogramView multiGroupHistogramView;
	/** 本机卡号 */
	public String mCardId;
	/** 串口操作 */
	public SerialClient mSerialClient;
	/** 串口工具类 */
	public SerialPortUtils serialPortUtils;
	/** 数据库操作类 */
	private MyDataHander myDataHander;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		if (Constant.TEST_UI_MODEL) {
			//初始化串口
			initSerialClient();
		}

		//初始化数据库
		myDataHander = new MyDataHander(this);
		//点击读卡按钮，发送读卡指令，打开波束功率输出
		readButton.setOnClickListener((View view) -> {
			if (Constant.TEST_UI_MODEL) {
				cardId.setText("");
				frequency.setText("");
				//发送读卡指令
				SerialPortUtils.sendControl(Constant.ICA);
				//发送打开波束功率输出指令
				SerialPortUtils.sendControl(Constant.OPEN_BSI);
			}
		});
		//点击短报文按钮跳转到聊天界面
		messageButton.setOnClickListener((View view) -> {
			Intent intent = new Intent(MainActivity.this, ChatActivity.class);
			if (!Constant.TEST_UI_MODEL){
				mCardId = "0412159";
			}
			intent.putExtra("myId", mCardId);
			startActivity(intent);

		});
		messageButton.setClickable(false);
		//点击SOS按钮，暂时不做处理
		sosButton.setOnClickListener((View view) -> {
			Log.d("SOS", "Click SOS Button");
			System.out.println("this is lambda");
			Toast.makeText(this, "SOS功能已触发!",Toast.LENGTH_SHORT);
		});
		sosButton.setClickable(false);

		initHistogram();
		//在测试模式下，查看波束功率的UI
		if (Constant.TEST_UI_MODEL) {
			initMultiGroupHistogramView("$BDBSI,00,00,1,3,2,4,0,3,1,4,2,1*5E");
		}
	}

	@Override
	public void onBackPressed() {
		long secondTime = System.currentTimeMillis();
		if (secondTime - firstTime > 2000) {
			Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
			firstTime = secondTime;
		} else{
			finish();
			System.exit(0);
		}
	}

	/**
	 * 获取柱状图UI界面，设置水平监听器
	 */
	private void initHistogram() {
		multiGroupHistogramView = findViewById(R.id.multiGroupHistogramView);
		initOrientationListener();
	}

	/**
	 * 处理波束功率BSI数据，设置柱状图数据
	 *
	 * @param BSI
	 */
	private void initMultiGroupHistogramView(String BSI) {

		String[] bsiList = BSI.split(",");
		List<MultiGroupHistogramGroupData> groupDataList = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			List<MultiGroupHistogramChildData> childDataList = new ArrayList<>();
			MultiGroupHistogramGroupData groupData = new MultiGroupHistogramGroupData();
			groupData.setGroupName(String.valueOf(i + 1));
			MultiGroupHistogramChildData childData1 = new MultiGroupHistogramChildData();
			childData1.setSuffix("");
			if (i == 9) {
				//最后一位包含波束功率，"*"和校验和
				String last = bsiList[12].substring(0, 1);
				childData1.setValue(Float.valueOf(last));
			} else {
				childData1.setValue(Float.valueOf(bsiList[3 + i]));
			}
			childDataList.add(childData1);
			groupData.setChildDataList(childDataList);
			groupDataList.add(groupData);
		}
		multiGroupHistogramView.setDataList(groupDataList);
		int[] color1 = new int[]{Color.parseColor("#008000"), Color.parseColor("#32CD32")};
		multiGroupHistogramView.setHistogramColor(color1);
	}

	/**
	 * 初始化水平方向的监听器
	 */
	private void initOrientationListener() {
		orientationEventListener = new OrientationEventListener(this) {
			@Override
			public void onOrientationChanged(int orientation) {
				int screenOrientation = getResources().getConfiguration().orientation;
				if (orientation > 315 || orientation < 45 && orientation > 0) {
					if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					}
				} else if (orientation > 45 && orientation < 135) {
					if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
					}
				} else if (orientation > 135 && orientation < 225) {
//                    if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
//                        LogUtil.e("kkkkkkkk: " + orientation);
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
//                    }
				} else if (orientation > 225 && orientation < 315) {
					if (screenOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
						setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
					}
				}
			}
		};
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

	/**
	 * 处理接收到的数据，并且更改UI的显示
	 *
	 * @param msg
	 */
	private void updataUI(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (msg.contains("BDICI")) {
					String[] msgList = msg.split(",");
					mCardId = msgList[1];
					String freqy = msgList[5];
					cardId.setText(mCardId);
					frequency.setText(freqy);
					messageButton.setClickable(true);
					sosButton.setClickable(true);
				}
				if (msg.contains("BDFKI")) {
					String[] result = msg.split(",");
				}
				if (msg.contains("BDTXR")) {
					String[] msgList = msg.split(",");
					MessageInfo messageInfo = new MessageInfo();
					//接收ID
					messageInfo.setReceiveId(mCardId);
					//发送ID
					String userId = msgList[2];
					messageInfo.setSendId(userId);
					//计算长度
					int index = msgList[5].length();
					//去掉末尾的*34/r/n以后的消息内容
					String message = msgList[5].substring(0, (index - 5));
					//消息内容
					messageInfo.setMessage(message);
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
					}
				}
				if (msg.contains("BDBSI")) {
					//处理波束功率数据显示
					initMultiGroupHistogramView(msg);
				}
			}
		});
	}

	/**
	 * 关闭串口
	 */
	private void closeSerialClient() {
		if (mSerialClient != null) {
			mSerialClient.close();
			mSerialClient = null;
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		if (Constant.TEST_UI_MODEL) {
			//重新设置监听器
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

			AlertDialog dialog = new AlertDialog.Builder(this).create();//创建对话框

			try {
				//发送读卡指令
				SerialPortUtils.sendControl(Constant.ICA);
				//发送打开波束功率输出指令
				SerialPortUtils.sendControl(Constant.OPEN_BSI);
			} catch (Exception e) {
				dialog.setIcon(R.mipmap.ic_launcher);//设置对话框icon
				dialog.setTitle("系统提示");//设置对话框标题
				dialog.setMessage("无法连接到北斗短报文模块，程序即将退出！");//设置文字显示内容
				//分别设置三个button
				dialog.setButton(DialogInterface.BUTTON_POSITIVE,"退出", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();//关闭对话框
						finish();
						System.exit(0);
					}
				});
				dialog.show();//显示对话框
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//关闭数据库连接
		myDataHander = null;
		if (Constant.TEST_UI_MODEL) {
			//发送波束功率输出关闭指令
			SerialPortUtils.sendControl(Constant.CLOSE_BSI);
			closeSerialClient();
		}
	}


	@Override
	protected void onPause() {
		super.onPause();
		if (orientationEventListener != null) {
			orientationEventListener.disable();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
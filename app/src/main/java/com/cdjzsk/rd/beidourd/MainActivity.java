package com.cdjzsk.rd.beidourd;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cdjzsk.rd.beidourd.data.MyDataBaseHelper;
import com.jzsk.seriallib.ClientStateCallback;
import com.jzsk.seriallib.SerialClient;
import com.jzsk.seriallib.SupportProtcolVersion;
import com.jzsk.seriallib.conn.MessageListener;
import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.msg.msgv21.Message;
import com.jzsk.seriallib.util.ArrayUtils;
import com.jzsk.seriallib.util.LogUtils;
import com.jzsk.seriallib.util.TimeUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements ClientStateCallback {

	private static final String TAG = LogUtils.makeTag(MainActivity.class);
	@BindView(R.id.textView)
	TextView mTextView;
	@BindView(R.id.btn_regsiter)
	Button mBtnRegsiter;
	@BindView(R.id.btn_connect)
	Button mBtnConnect;
	@BindView(R.id.btn_close)
	Button mBtnClose;

	private SerialClient mSerialClient;
	private MyDataBaseHelper dbHelper;
	private byte[] ICA = "CCICA,0,00".getBytes();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
		//创建一个数据库助手类
		dbHelper = new MyDataBaseHelper(this, "BDStore.db", null, 1); // 执行这句并不会创建数据库文件
		//获取数据库输入对象，对于增删改都可以用db.execSQL(String sql);  来执行sql语句
		//db.execSQL("insert into book(name , author, pages, price) values(\"Android数据库操作指南\", \"panda fang\", 200, 35.5)");
		//遇到字符串要转义 有没有觉得很蛋疼， 用下面的方法就好多了
		//db.execSQL("insert into book(name , author, pages, price) values(?, ? ,? ,? )", new String[]{"Android数据库操作指南", "panda fang", "200", "35.5"});
		SQLiteDatabase db =  dbHelper.getWritableDatabase();
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
				mTextView.append("\n" + TimeUtils.getCurrentTimeSmsform() + " " + msg);
				// 简单滚动到最新一行
				int offset = mTextView.getLineCount() * mTextView.getLineHeight();
				if(offset>mTextView.getHeight()){
					mTextView.scrollTo(0,offset-mTextView.getHeight());
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

	@OnClick({R.id.btn_regsiter,R.id.btn_connect, R.id.btn_close})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.btn_regsiter:
				if(mSerialClient != null && mSerialClient.isConnected()) {
					byte[] sendMsg = ArrayUtils.concatenate(new byte[]{'$'},ICA,new byte[]{'*'},ArrayUtils.bytesToHexString(new byte[]{ArrayUtils.xorCheck(ICA)}).getBytes(),new byte[]{0x0D,0x0A});
					Message msg  = new Message(sendMsg);
					mSerialClient.sendMessage(msg);
					setTxt("写入成功:" + msg.toString());
				}
				break;
			case R.id.btn_connect:
				initSerialClient();
				break;
			case R.id.btn_close:
				closeSerialClient();
				break;
		}
	}
}
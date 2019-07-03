package com.cdjzsk.rd.beidourd.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDataBaseHelper extends SQLiteOpenHelper {

	private Context mContext;
	//创建联系人表，使用卡号做主键，另有用户名
	private static final String CREATE_USERS = "CREATE TABLE user (" +
			"id VARCHAR(20) PRIMARY KEY," +
			"userName VARCHAR(40))";

	//创建消息表，主键id自增长，发送人id为对应联系人表id，收信人id同上，消息内容，操作时间
	private static final String CREATE_MESSAGE = "CREATE TABLE message (" +
			"id INTEGER PRIMARY KEY AUTOINCREMENT," +
			"sendId VARCHAR(20)," +
			"receiveId VARCHAR(20)," +
			"message VARCHAR(400)," +
			"time TEXT)";

	public MyDataBaseHelper(Context context, String name,SQLiteDatabase.CursorFactory factory, int version){
		super(context, name, factory, version);
		mContext = context;
	}

	/**
	 * 数据库已经创建过了， 则不会执行到，如果不存在数据库则会执行
	 * @param sqLiteDatabase
	 */
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		//创建联系人表
		sqLiteDatabase.execSQL(CREATE_USERS);
		//创建消息表
		sqLiteDatabase.execSQL(CREATE_MESSAGE);
		Toast.makeText(mContext, "create succeeded", Toast.LENGTH_SHORT).show();
	}

	/**
	 * 创建数据库时不会执行，增大版本号升级时才会执行到
	 * @param sqLiteDatabase
	 * @param oldVersion
	 * @param newVersion
	 */
	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
		// 在这里面可以把旧的表 drop掉 从新创建新表，
		// 但如果数据比较重要更好的做法还是把旧表的数据迁移到新表上，比如升级qq聊天记录被删掉肯定招骂
		//创建联系人表
		sqLiteDatabase.execSQL(CREATE_USERS);
		//创建消息表
		sqLiteDatabase.execSQL(CREATE_MESSAGE);
		Toast.makeText(mContext, "onUpgrade oldVersion：" + oldVersion + " newVersion:" + newVersion, Toast.LENGTH_SHORT).show();
	}
}

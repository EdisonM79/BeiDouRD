package com.cdjzsk.rd.beidourd.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cdjzsk.rd.beidourd.data.entity.Message;
import com.cdjzsk.rd.beidourd.data.entity.User;

import java.util.ArrayList;
import java.util.List;

public class MyDataHander {

	private static MyDataBaseHelper dbHelper = null;
	private static final int DBVERSION = 1;

	/**
	 * 初始化类直接把数据库初始化了
	 *
	 * @param context
	 */
	public MyDataHander(Context context) {
		MyDataHander.dbHelper = new MyDataBaseHelper(context, "BDStore.db", null, DBVERSION);
	}

	/**
	 * 单例模式，保证每个对象都获取到同一个数据库
	 *
	 * @return
	 */
	public static MyDataBaseHelper getInstance() {
		return dbHelper;
	}

	/**
	 * 查询是否存在用户数据
	 *
	 * @return
	 */
	public boolean isUserExit() {
		int count = 0;

		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			db = dbHelper.getReadableDatabase();
			// select count(Id) from Orders
			cursor = db.rawQuery("SELECT COUNT (*) FROM user", null);
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
			if (count > 0) return true;
		} catch (Exception e) {
			Log.e("db", "", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return false;
	}

	/**
	 * 查询是否存在消息数据
	 *
	 * @return
	 */
	public boolean isMessageExit() {
		int count = 0;

		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			db = dbHelper.getReadableDatabase();
			// select count(Id) from Orders
			cursor = db.rawQuery("SELECT COUNT (*) FROM message", null);
			if (cursor.moveToFirst()) {
				count = cursor.getInt(0);
			}
			if (count > 0) return true;
		} catch (Exception e) {
			Log.e("db", "", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return false;
	}

	/**
	 * 获取所有的联系人
	 *
	 * @return
	 */
	public List<User> getAllUser() {
		List<User> users = new ArrayList<User>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = dbHelper.getReadableDatabase();
			// select * from user
			cursor = db.rawQuery("SELECT * FROM user", null);
			while (cursor.moveToNext()) {
				String userId = cursor.getString(cursor.getColumnIndex("id"));
				String userName = cursor.getString(cursor.getColumnIndex("userName"));
				users.add(new User(userId, userName));
			}
			cursor.close();
			return users;
		} catch (Exception e) {
			Log.e("db", "", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return null;
	}

	/**`
	 * 获取部分信息数据
	 *
	 * @param id
	 * @param offset
	 * @param maxResult
	 * @return
	 */
	public List<Message> getScrollMessageBySendIdOrReceiveId(String id,int offset, int maxResult) {
		List<Message> messages = new ArrayList<Message>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = dbHelper.getReadableDatabase();
			// select * from user
			cursor = db.rawQuery("SELECT * FROM message WHERE sendId=? OR receiveId=? ORDER BY time ASC LIMIT ?,?", new String[]{id,id,String.valueOf(offset), String.valueOf(maxResult)});
			while (cursor.moveToNext()) {
				Integer messageId = cursor.getInt(cursor.getColumnIndex("id"));
				String sendId = cursor.getString(cursor.getColumnIndex("sendId"));
				String receiveId = cursor.getString(cursor.getColumnIndex("receiveId"));
				String message = cursor.getString(cursor.getColumnIndex("message"));
				String time = cursor.getString(cursor.getColumnIndex("time"));
				messages.add(new Message(messageId, sendId, receiveId, message, time));
			}
			cursor.close();
			return messages;
		} catch (Exception e) {
			Log.e("db", "", e);
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (db != null) {
				db.close();
			}
		}
		return null;
	}

	/**
	 * 通过UserId去查询User
	 *
	 * @param id
	 * @return
	 */
	public User findUserByUserId(String id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM user WHERE id = ?",
				new String[]{id});
		//存在数据才返回true
		if (cursor.moveToFirst()) {
			String userId = cursor.getString(cursor.getColumnIndex("id"));
			String userName = cursor.getString(cursor.getColumnIndex("userName"));
			return new User(userId, userName);
		}
		cursor.close();
		return null;
	}

	/**
	 * 新增一名联系人
	 * @param user
	 */
	public void addUser(User user) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(null != user && null != user.getUserId() && null != user.getUserName()) {
			db.execSQL("INSERT INTO user(id,userName) values(?,?)",
					new String[]{user.getUserId(), user.getUserName()});
		}
	}

	/**
	 * 新增一条消息
	 * @param message
	 */
	public void addMessage(Message message) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(null != message) {
			db.execSQL("INSERT INTO message(sendId,receiveId,message,time) VALUES(?,?,?,?)",
					new Object[]{message.getSendId(),message.getReceiveId(),message.getMessage(),message.getTime()});
		}
	}
}
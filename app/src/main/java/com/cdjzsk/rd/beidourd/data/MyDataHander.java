package com.cdjzsk.rd.beidourd.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cdjzsk.rd.beidourd.data.entity.MessageInfo;
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
	public static MyDataBaseHelper getDataBaseHelper() {
		return dbHelper;
	}

	/**
	 * 查询是否存在用户数据
	 *
	 * @return
	 */
	public static boolean isUserExit(String userId) {

		int count = 0;
		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			db = dbHelper.getReadableDatabase();
			// select count(Id) from Orders
			cursor = db.rawQuery("SELECT COUNT (*) FROM user WHERE id = ?", new String[]{userId});
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
	 * 查询是否存在昵称
	 *
	 * @return
	 */
	public static boolean isNameExit(String userName) {
		int count = 0;

		SQLiteDatabase db = null;
		Cursor cursor = null;

		try {
			db = dbHelper.getReadableDatabase();
			// select count(Id) from Orders
			cursor = db.rawQuery("SELECT COUNT (*) FROM user WHERE userName = ?", new String[]{userName});
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
	public static boolean isMessageExit() {
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
	public static List<User> getAllUser() {
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
				Integer image = cursor.getInt(cursor.getColumnIndex("image"));
				users.add(new User(userId, userName, image));
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

	/**
	 * 获取other的聊天记录
	 * @param myCardId
	 * @param otherCardId
	 * @return
	 */
	public static List<MessageInfo> getScrollMessageBySendIdOrReceiveId(String myCardId, String otherCardId ) {
		List<MessageInfo> messages = new ArrayList<MessageInfo>();
		SQLiteDatabase db = null;
		Cursor cursor = null;
		try {
			db = dbHelper.getReadableDatabase();
			// select * from user  DESC按照时间的最新来排序
			cursor = db.rawQuery("SELECT * FROM message WHERE (sendId = ? AND receiveId = ?) OR (sendId = ? AND receiveId = ?)  ORDER BY time DESC",
					new String[]{ myCardId, otherCardId, otherCardId, myCardId});
			while (cursor.moveToNext()) {
				Integer messageId = cursor.getInt(cursor.getColumnIndex("id"));
				String sendId = cursor.getString(cursor.getColumnIndex("sendId"));
				String receiveId = cursor.getString(cursor.getColumnIndex("receiveId"));
				String message = cursor.getString(cursor.getColumnIndex("message"));
				String time = cursor.getString(cursor.getColumnIndex("time"));
				String read = cursor.getString(cursor.getColumnIndex("read"));
				String mySend = cursor.getString(cursor.getColumnIndex("mySend"));
				messages.add(new MessageInfo(messageId, sendId, receiveId, message, time, read, mySend));
			}
			cursor.close();
			return messages;
		} catch (Exception e) {
			Log.e("db", "Get Scroll Message By SendId Or ReceiveId Error", e);
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
	public static User findUserByUserId(String id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM user WHERE id = ?",
				new String[]{id});
		//存在数据才返回true
		if (cursor.moveToFirst()) {
			String userId = cursor.getString(cursor.getColumnIndex("id"));
			String userName = cursor.getString(cursor.getColumnIndex("userName"));
			int image = cursor.getInt(cursor.getColumnIndex("image"));
			return new User(userId, userName, image);
		}
		cursor.close();
		return null;
	}

	/**
	 * 新增一名联系人
	 * @param user
	 */
	public static void addUser(User user) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(null != user && null != user.getUserId() && null != user.getUserName()) {
			db.execSQL("INSERT INTO user(id,userName,image) values(?,?,?)",
					new Object[]{user.getUserId(), user.getUserName(), user.getImage()});
		}
	}

	/**
	 * 新增一条消息
	 * @param message
	 */
	public static void addMessage(MessageInfo message) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(null != message) {
			db.execSQL("INSERT INTO message(sendId,receiveId,message,time,read,mySend) VALUES(?,?,?,?,?,?)",
					new Object[]{message.getSendId(),message.getReceiveId(),message.getMessage(),message.getTime(),message.getRead(),message.getMySend()});
		}
	}

	/**
	 * 获取与某人的最新信息
	 * @return
	 */
	public static MessageInfo getContactShowInfoByCardId(String myCardId, String otherCardId) {

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("SELECT * FROM message WHERE (sendId = ? AND receiveId = ?) OR (sendId = ? AND receiveId = ?)  ORDER BY time DESC LIMIT 1",
				new String[]{ myCardId, otherCardId, otherCardId, myCardId});
		MessageInfo messageInfo = new MessageInfo();
		if(cursor.moveToFirst()) {
			messageInfo.setId(cursor.getInt(cursor.getColumnIndex("id")));
			messageInfo.setMessage(cursor.getString(cursor.getColumnIndex("message")));
			messageInfo.setRead(cursor.getString(cursor.getColumnIndex("read")));
			messageInfo.setReceiveId(cursor.getString(cursor.getColumnIndex("receiveId")));
			messageInfo.setSendId(cursor.getString(cursor.getColumnIndex("sendId")));
			messageInfo.setTime(cursor.getString(cursor.getColumnIndex("time")));
			return messageInfo;
		}
		cursor.close();
		return null;
	}

	/**
	 * 通过消息Id去修改是否未读
	 * @param readState
	 * @param messageId
	 */
	public static void updateReadStateByMessageId(String readState, Integer messageId) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("read",readState);
		db.update("message",values,"id=?",new String[]{messageId.toString()});
	}

	/**
	 * 更新联系人
	 * @param userId
	 * @param userName
	 */
	public static void updateContactById(String userId, String userName) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("userName", userName);
		db.update("user",values,"id=?",new String[]{userId});
	}

	/**
	 * 通过联系人Id去删除联系人
	 * @param userId
	 */
	public static void deleteContactById(String userId) {

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("user","id=?",new String[]{userId});
	}

}

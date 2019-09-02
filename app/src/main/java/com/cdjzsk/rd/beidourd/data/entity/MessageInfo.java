package com.cdjzsk.rd.beidourd.data.entity;

/**
 * 用于数据库进行消息的存取
 */
public class MessageInfo {
	private Integer id;
	private String sendId;
	private String receiveId;
	private String message;
	private String time;
	private String read;
	private String mySend;


	public MessageInfo() {}

	public MessageInfo(Integer id, String sendId, String receiveId, String message, String time, String read, String mySend) {
		this.id = id;
		this.sendId = sendId;
		this.receiveId = receiveId;
		this.message = message;
		this.time = time;
		this.read = read;
		this.mySend = mySend;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getSendId() {
		return sendId;
	}

	public void setSendId(String sendId) {
		this.sendId = sendId;
	}

	public String getReceiveId() {
		return receiveId;
	}

	public void setReceiveId(String receiveId) {
		this.receiveId = receiveId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getRead() {
		return read;
	}

	public void setRead(String read) {
		this.read = read;
	}

	public String getMySend() {
		return mySend;
	}

	public void setMySend(String mySend) {
		this.mySend = mySend;
	}
}

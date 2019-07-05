package com.cdjzsk.rd.beidourd.data.entity;

public class Message {
	private Integer id;
	private String sendId;
	private String receiveId;
	private String message;
	private String time;

	public Message(String sendId, String receiveId, String message, String time) {
		this.sendId = sendId;
		this.receiveId = receiveId;
		this.message = message;
		this.time = time;
	}
	public Message(Integer id,String sendId, String receiveId, String message, String time) {
		this.id = id;
		this.sendId = sendId;
		this.receiveId = receiveId;
		this.message = message;
		this.time = time;
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

	@Override
	public String toString() {
		return "Message{" +
				"id=" + id +
				", sendId='" + sendId + '\'' +
				", receiveId='" + receiveId + '\'' +
				", message='" + message + '\'' +
				", time='" + time + '\'' +
				'}';
	}
}

package com.cdjzsk.rd.beidourd.data.entity;

public class User {
	private String userId;
	private String userName;
	private int image;

	public User (){

	}

	public User(String userId, String userName, Integer image) {
		this.userId = userId;
		this.userName = userName;
		this.image = image;
	}
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getImage() {
		return image;
	}

	public void setImage(int image) {
		this.image = image;
	}
}

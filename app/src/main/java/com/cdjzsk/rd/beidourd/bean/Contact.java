package com.cdjzsk.rd.beidourd.bean;

public class Contact {

	private String cardId;
	private int imageId;
	private String time;
	//未读信息条数
	private String number;

	public Contact(String cardId, int imageId, String time, String number) {
		this.cardId = cardId;
		this.imageId = imageId;
		this.time = time;
		this.number = number;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}

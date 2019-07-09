package com.cdjzsk.rd.beidourd.bean;

public class Contact {

	private String cardId;
	private int imageId;
	private String time;

	public Contact(String cardId, int imageId, String time) {
		this.cardId = cardId;
		this.imageId = imageId;
		this.time = time;
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
}

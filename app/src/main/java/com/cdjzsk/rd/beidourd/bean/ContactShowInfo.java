package com.cdjzsk.rd.beidourd.bean;

public class ContactShowInfo {

    private String cardId;
    private int headImage;
    private String username;
    private String lastMsg;
    private String lastMsgTime;
    private boolean isRead;


    public ContactShowInfo(String cardId, int headImage, String username, String lastMsg, String lastMsgTime, boolean isRead) {
        this.cardId = cardId;
        this.headImage = headImage;
        this.username = username;
        this.lastMsg = lastMsg;
        this.lastMsgTime = lastMsgTime;
        this.isRead = isRead;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public int getHeadImage() {
        return headImage;
    }

    public void setHeadImage(int headImage) {
        this.headImage = headImage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getLastMsgTime() {
        return lastMsgTime;
    }

    public void setLastMsgTime(String lastMsgTime) {
        this.lastMsgTime = lastMsgTime;
    }

}

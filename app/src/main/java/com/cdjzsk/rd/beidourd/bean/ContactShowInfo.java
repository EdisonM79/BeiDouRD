package com.cdjzsk.rd.beidourd.bean;

public class ContactShowInfo {

    private int headImage;
    private String username;
    private String lastMsg;
    private String lastMsgTime;
    private String isRead;

    public ContactShowInfo(int headImage, String username, String lastMsg, String lastMsgTime, String isRead) {
        this.headImage = headImage;
        this.username = username;
        this.lastMsg = lastMsg;
        this.lastMsgTime = lastMsgTime;
        this.isRead = isRead;
    }

    public String isRead() {
        return isRead;
    }

    public void setRead(String read) {
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

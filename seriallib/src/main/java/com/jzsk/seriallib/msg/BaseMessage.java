package com.jzsk.seriallib.msg;

import com.jzsk.seriallib.msg.msgv40.Packet;

import java.io.Serializable;

/**
 * 做一个基类
 */
public abstract class BaseMessage implements Serializable {
    public abstract String getAddr();
    public abstract BasePacket[] getPackets();
}

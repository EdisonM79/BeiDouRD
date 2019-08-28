package com.jzsk.seriallib.msg;

import java.io.Serializable;

/**
 * 做一个基类
 */
public abstract class BasePacket implements Serializable {
    public abstract byte[] getBytes();
}

package com.jzsk.seriallib;

import android.support.annotation.NonNull;

import com.jzsk.seriallib.conn.Connection;
import com.jzsk.seriallib.conn.ConnectionConfiguration;
import com.jzsk.seriallib.conn.ConnectionStateCallback;
import com.jzsk.seriallib.conn.MessageListener;
import com.jzsk.seriallib.msg.msgv21.Message;
import com.jzsk.seriallib.util.LogUtils;

import java.io.IOException;

public class SerialClient implements ConnectionStateCallback {

    private static final String TAG = LogUtils.makeTag(SerialClient.class);

    private Connection mConnection;
    private ConnectionConfiguration mConnectionConfiguration;
    private ClientStateCallback mStateCallback;
    private SupportProtcolVersion mSupportProtcolVersion;

    private boolean isDebugMode = false;

    public SerialClient(){
        mConnection = new Connection();
    }

    public SupportProtcolVersion getSupportProtcolVersion() {
        return mSupportProtcolVersion;
    }

    public void setDebugMode(boolean mockReadData){
        isDebugMode = mockReadData;
    }
    /**
     * 接收消息回调
     * @param messageListener
     * @throws IllegalStateException
     */
    public void setMessageListener(@NonNull MessageListener messageListener) throws IllegalStateException {
        if(mConnection != null) {
            mConnection.setMessageListener(messageListener);
        }else{
            throw new IllegalStateException("current is not connected! ");
        }
    }

    /**
     * 直接发送原始字节
     * @param datas
     */
    public void sendRawMessage(byte[] datas){
        if(mConnection != null && mConnection.isConnected()) {
            try {
                mConnection.getOutput().write(datas);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            throw new IllegalStateException("current is not connected! ");
        }
    }
    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(@NonNull Message message){
        if(mConnection != null && mConnection.isConnected()) {
            mConnection.sendMessage(message);
        }else{
            throw new IllegalStateException("current is not connected! ");
        }
    }
    /**
     * Connect to server
     *
     * @param stateCallback
     */
    public void connect(ClientStateCallback stateCallback, SupportProtcolVersion supportProtcolVersion){
        mStateCallback = stateCallback;
        mSupportProtcolVersion = supportProtcolVersion;
        mConnectionConfiguration = new ConnectionConfiguration(ClientConstants.PREF_DEFAULT_DEVICE, ClientConstants.PREF_DEFAULT_BAUDRATE);
        if(mConnection == null){
            mConnection = new Connection();
        }
        mConnectionConfiguration.setProtcolVersion(mSupportProtcolVersion);
        mConnection.setConfig(mConnectionConfiguration);
        mConnection.setStateCallback(this);
        mConnection.setDebugMode(isDebugMode);
        mConnection.connect();
    }

    public boolean isConnected(){
        return mConnection == null ? false : mConnection.isConnected();
    }

    public boolean isClosed(){
        return mConnection == null ? true : mConnection.isClosed();
    }

    /**
     * close connection and reset other members
     */
    @Override
    public void onClosed(){
        if(mStateCallback != null){
            mStateCallback.connectionClosed();
        }
    }

    @Override
    public void onSuccess() {
        if(mStateCallback != null){
            mStateCallback.connectSuccess();
        }
    }

    @Override
    public void onFail() {
        if(mStateCallback != null){
            mStateCallback.connectFail();
        }
    }

    public void close(){
        if(mConnection != null) {
            mConnection.close();
        }
    }
}
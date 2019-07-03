package com.jzsk.seriallib;

public interface ClientStateCallback {
    /**
     * callback when client connect to server
     */
    void connectSuccess();

    /**
     * callback when client connect to server
     */
    void connectFail();

    /**
     * callback when connection between client and server closed
     */
    void connectionClosed();

}

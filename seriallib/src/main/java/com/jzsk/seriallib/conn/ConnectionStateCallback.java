package com.jzsk.seriallib.conn;

/**
 * @author DeeW   (Find me on ---> https://github.com/D-3)
 * @time 2017/4/2
 */

public interface ConnectionStateCallback {
    /**
     * Called when connect success.
     */
    public void onSuccess();

    /**
     * Called when connect fail.
     */
    public void onFail();
    
    
    /**
     * Called when connect closed.
     */
    public void onClosed();
}

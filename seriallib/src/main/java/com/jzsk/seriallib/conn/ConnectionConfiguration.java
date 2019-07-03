package com.jzsk.seriallib.conn;

import com.jzsk.seriallib.SupportProtcolVersion;

/**
 * Configuration to use while establishing the connection to the server.
 *
 */
public class ConnectionConfiguration {

  private SupportProtcolVersion mSupportProtcolVersion = SupportProtcolVersion.V40; //默认支持4.0

  private String mHost;
  private int    mPort;

  // Flag that indicates if a reconnection should be attempted when abruptly disconnected
  private boolean mReconnectionAllowed = true;

  /**
   * Creates a new ConnectionConfiguration for a connection that will connect to the desired host
   * and port.
   *
   * @param host the host where the JT/T808 server is running
   * @param port the port where the JT/T808 server is listening
   */
  public ConnectionConfiguration(String host, int port) {
    mHost = host;
    mPort = port;
  }
  
  public String getHost() {
    return mHost;
  }

  public int getPort() {
    return mPort;
  }

  /**
   * Returns if the reconnection mechanism is allowed to be used. By default reconnection is
   * allowed. You can disable the reconnection mechanism with
   * {@link #setReconnectionAllowed(boolean)}.
   *
   * @return true, if the reconnection mechanism is enabled
   */
  public boolean isReconnectionAllowed() {
    return mReconnectionAllowed;
  }

  /**
   * Sets if the reconnection mechanism is allowed to be used. By default reconnection is allowed.
   *
   * @param allowed if the reconnection mechanism should be enabled for this connection
   */
  public void setReconnectionAllowed(boolean allowed) {
    mReconnectionAllowed = allowed;
  }

  public SupportProtcolVersion getProtcolVersion() {
    return mSupportProtcolVersion;
  }

  public void setProtcolVersion(SupportProtcolVersion supportProtcolVersion) {
    mSupportProtcolVersion = supportProtcolVersion;
  }
}

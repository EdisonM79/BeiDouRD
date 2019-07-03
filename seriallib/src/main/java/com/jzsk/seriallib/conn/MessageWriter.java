package com.jzsk.seriallib.conn;

import android.util.Log;

import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.msg.BasePacket;
import com.jzsk.seriallib.util.ArrayUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Writes messages to a JT/T808 server.
 * <p>
 * Messages are sent using a dedicated thread. Message interceptors can be
 * registered to dynamically modify message before they're actually sent.
 * Message listeners can be registered to listen for all outgoing messages.
 *
 */
class MessageWriter {

	private static final String TAG = MessageWriter.class.getSimpleName();

	private final BlockingQueue<BasePacket> mQueue;

	private Connection   mConnection;
	private OutputStream mOutput;
	private Thread mWriteThread;
	private Thread mKeepAliveThread;

	private boolean mDone;

	// Timestamp when the last packet was sent to the server. This information
	// is used by the keep
	// alive process to only send heartbeats when the connection has been idle
	private long mLastActive = System.currentTimeMillis();

	/**
	 * Creates a new message writer with the specified connection.
	 *
	 * @param conn
	 *            the connection
	 */
	MessageWriter(Connection conn) {
		mQueue = new ArrayBlockingQueue<>(500, true);
		mConnection = conn;
		init();
	}

	/**
	 * Initializes the writer in order to be used. It is called at the first
	 * connection and also is invoked if the connection is disconnected by an
	 * error.
	 */
	void init() {
		mDone = false;
		mOutput = mConnection.getOutput();

		mWriteThread = new WriteThread();
		// TODO: 10/24/2016 add connection count to the name
		mWriteThread.setName("Serial Message Writer ( )");
		mWriteThread.setDaemon(true);
	}

	/**
	 * Starts the packet write thread. The message writer will continue writing
	 * packets until {@link #shutdown} or an error occurs.
	 */
	public void startup() {
		mWriteThread.start();
	}

	/**
	 * Shuts down the message writer. Once this method has been called, no
	 * further packets will be written to the server.
	 */
	public void shutdown() {
		mDone = true;
		synchronized (mQueue) {
			mQueue.notifyAll();
		}
	}

	/**
	 * Starts the keep alive process. An empty message (aka heartbeat) is going
	 * to be sent to the server every 30 seconds (by default) since the last
	 * packet was sent to the server.
	 */
	void keepAlive() {
		// Schedule a keep-alive task to run if the feature is enabled, will
		// write out a empty
		// message each time it runs to keep the TCP/IP connection open
	}

	/**
	 * Sends the specified message to the server.
	 *
	 * @param msg
	 *            the message to send
	 */
	public void sendMessage(BaseMessage msg) {
		if (!mDone) {
			try {
				for (BasePacket packet : msg.getPackets()) {
					synchronized (mQueue) {
						mQueue.put(packet);//队列为空，阻塞队列，阻塞线程
						mQueue.notifyAll();
					}
				}
			} catch (InterruptedException ie) {
				ie.printStackTrace();
				return;
			}
		}
	}

	private void writePackets() {
		try {
			// Write out packets from the queue
			while (!mDone) {
				BasePacket packet = nextPacket();
				if (packet != null) {
					synchronized (mOutput) {
						byte[] data = packet.getBytes();
						mOutput.write(data);
						mOutput.flush();
						Log.w(TAG,String.format("Serial send=[%s]",new String(data)));
						Log.w(TAG,String.format("Serial send=[%s]",ArrayUtils.bytesToHexString(data)));
						// Keep track of the last time a packet was sent to the
						// server
						mLastActive = System.currentTimeMillis();
					}
				}
			}

			// Flush out the rest of the queue. If the queue is extremely large,
			// it's possible we won't
			// have time to entirely flush it before the socket is forced closed
			// by the shutdown process.
			synchronized (mOutput) {
				while (!mQueue.isEmpty()) {
					BasePacket packet = mQueue.remove();
					mOutput.write(packet.getBytes());
				}
				mOutput.flush();
				mOutput.close();
			}

			// Delete the queue contents (hopefully nothing is left)
			mQueue.clear();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			Log.w(TAG,"serial messagewriter error "+ioe.getMessage());
			// TODO this solution is not good
			mQueue.clear();
			mConnection.shutDown();
		}
	}

	/**
	 * Returns the next available packet from the queue for writing.
	 *
	 * @return the next available for writing
	 */
	private BasePacket nextPacket() {
		BasePacket packet = null;

		// Wait until there's a packet or we're done
		while (!mDone && (packet = mQueue.poll()) == null) {
			try {
				synchronized (mQueue) {
					mQueue.wait();
				}
			} catch (InterruptedException ie) {
				// Do nothing
			}
		}

		return packet;
	}

	private class WriteThread extends Thread {

		@Override
		public void run() {
			super.run();
			writePackets();
		}

	}

}

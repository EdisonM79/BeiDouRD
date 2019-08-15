package com.jzsk.seriallib.conn;

import android.util.Log;

import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.msg.msgv40.Message;
import com.jzsk.seriallib.msg.msgv40.Packet;
import com.jzsk.seriallib.util.ArrayUtils;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Listens for packet traffic from the JT/T808 server and parse it into message
 * objects.
 * <p>
 * The message reader also invokes all message listeners and collectors.
 *
 */
class MessageReader {

	private static final String TAG = MessageReader.class.getSimpleName();
	private Connection mConnection;
	private InputStream mInput;
	private ByteBuffer cache = ByteBuffer.allocate(2 * 1024);
	private Thread mReadThread;
	private ExecutorService mExecutor;
	private MessageListener messageListener;

	private boolean mDone;

	private boolean isDebugMode = true;

	private int mProtcolVersion = 40;// 21 40

	public void setProtcolVersion(int protcolVersion) {
		mProtcolVersion = protcolVersion;
	}

	public void setDebugMode(boolean debugMode){
		isDebugMode = debugMode;
	}

	/**
	 * Creates a new message reader with the specified connection.
	 *
	 * @param conn
	 *            the connection
	 */
	MessageReader(Connection conn) {
		mConnection = conn;
		init();
	}

	/**
	 * Initializes the reader in order to be used. The reader is initialized
	 * during the first connection and when reconnecting due to an abruptly
	 * disconnection.
	 */
	void init() {
		mDone = false;
		mInput = mConnection.getInput();

		mReadThread = new ReadThread();
		// TODO: 10/24/2016 add connection count to the name
		mReadThread.setName("Serial Message Reader ( )");
		//设置为守护线程
		mReadThread.setDaemon(true);

		// Create an executor to deliver incoming messages to listeners. We'll use a single thread with an unbounded queue
		//创建一个执行程序来将传入消息传递给侦听器。我们将使用具有无界队列的单个线程
		//创建一个可以重用的只有单线程的线程池
		mExecutor = Executors.newSingleThreadExecutor(new NameThreadFactory("Serial-read"));
	}
	
	static class NameThreadFactory implements ThreadFactory {

		private final String name;
		private int count;
		
		
		public NameThreadFactory(String name) {
			super();
			this.name = name;
		}

		@Override
		public Thread newThread(Runnable r) {
			Thread thread = new Thread(r);
			thread.setName("T:"+name+"-"+count++);
			return thread;
		}
	}

	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = messageListener;

	}

	/** Starts the packet read thread. */
	public synchronized void startup() {
		mReadThread.start();
	}

	/** Shuts the message reader down. */
	public void shutdown() {
		mDone = true;
	}

	/** Parses packets in order to process them further. */
	private void readPackets() {
		if(isDebugMode){
			Log.w(TAG,"debugMode is opened!");
			while (!mDone){
				try {
					Thread.sleep(10 * 1000);
				} catch (Exception e){
					//ignore
				}
				try {/*$ZJXX...%^&*()0-=.sdfasABCDEFGHI*/
					byte[] buf = ArrayUtils.concatenate("$BDTXR,1,0412159,2,,北斗模块通信测试[混发模式]-!@#%^&*()~1234567890_ABCDEFGHIJKLMNOPQRSTUVWXYZ.*33".getBytes(), new byte[]{0x0D, 0x0A});
					int len = buf.length;
					byte[] data = new byte[len];
					System.arraycopy(buf, 0, data, 0, len);
					int position = cache.limit()>=cache.capacity()?0:cache.limit();
					cache.limit(cache.capacity());
					cache.position(position);
					if((cache.position() + len) >=cache.capacity()) cache.clear();
					cache.put(data);
					switch (mProtcolVersion){ //暂时直接用方法区分
						case 21:
							readDataV21();
							break;
						case 40:
							readDataV40();
							break;
						default:
							Log.e(TAG,"不支持的协议");
							break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		try {
			byte[] buf = new byte[1024];
			int len;
			while (!mDone && (len = mInput.read(buf)) != -1) {
				if (len > 0) {
					//Log.d(TAG,ArrayUtils.bytesToHexString(buf));
					try {
						byte[] data = new byte[len];
						System.arraycopy(buf, 0, data, 0, len);
			        	int position = cache.limit()>=cache.capacity()?0:cache.limit();
						cache.limit(cache.capacity());
						cache.position(position);
						if((cache.position() + len) >=cache.capacity()) cache.clear();
						cache.put(data);
						switch (mProtcolVersion){ //暂时直接用方法区分
							case 21:
								readDataV21();
								break;
							case 40:
								readDataV40();
								break;
							default:
								Log.e(TAG,"不支持的协议");
								break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO this solution is not good
			 mConnection.shutDown();
		} finally {

		}
	}

	private void readDataV40() {
		int pos = 0;
		int mark = 0;
		int newMark = 0;
		cache.flip();
		while (cache.remaining() > 7) {
			cache.mark();
			newMark = mark = cache.position();
			byte tag = cache.get();
			if ((tag == Packet.PREFIX) && cache.remaining() > 0) {
				byte[] addr = new byte[4];
				cache.get(addr);
				short len = cache.getShort();
				if(cache.remaining() >= len  - 7){
						cache.reset();
						byte[] raw = new byte[len];
						cache.get(raw);
						byte[] cacheBytes = new byte[cache.limit()
								- cache.position()];
						cache.get(cacheBytes);
						Log.w(TAG, String.format("serial read=[%s]",new String(raw)));
						Log.w(TAG, String.format("serial read=[%s]",ArrayUtils.bytesToHexString(raw)));
						Message msg = new Message(raw);
						processMessage(msg);
						if (messageListener != null) {
                            messageListener.processMessage(msg);
                        }else{
                            Log.w(TAG, "messageListener == null----------------------");
                        }
						cache.clear();
						cache.put(cacheBytes);
						cache.flip();
				} else {
					cache.reset();
					cache.get();
				}
			}
		}

	}

	private void readDataV21() {
		int pos = 0;
		int mark = 0;
		int newMark = 0;

		/** 将缓存字节数组的指针设置为数组的开始序列即数组下标0 */
		cache.flip();
		/** remaining返回cache剩余的可用长度,循环cache里面的数据*/
		while (cache.remaining() > 0) {
			/** 在此缓冲区的位置设置标记 */
			cache.mark();
			newMark = mark = cache.position();
			byte tag = cache.get();
			/** 寻找协议头$或者!符号，如果不是就抛弃 */
			if ((tag == com.jzsk.seriallib.msg.msgv21.Packet.PREFIX ) && cache.remaining() > 0) {
				/** 获取当前位置数据 */
				tag = cache.get();
				/** 寻找协议尾 0x0D */
				while (tag != com.jzsk.seriallib.msg.msgv21.Packet.SUFFIX) {
					/** 如果cache已经到末尾了 */
					if (cache.remaining() <= 0) {
						/** 将此缓冲区的位置重置为以前标记的位置. */
						cache.reset();
						return;
					}
					tag = cache.get();
					/** 如果在寻找结尾的时候发现了新的开头，就重置开始标志 */
					if((tag == com.jzsk.seriallib.msg.msgv21.Packet.PREFIX )){ //对只跑2.1但含有4.0协议时兼容
						/** 标记协议头的开始位置 */
						newMark = cache.position() - 1;
					}
				}
				/** 上一个是0x0D,这个应该是0x0A */
				tag = cache.get();
				if(tag == com.jzsk.seriallib.msg.msgv21.Packet.SUFFIX1) {
					/** 记录当前的位置，也就是协议末尾 */
					pos = cache.position();
					/** packet的长度 */
					int packetLength = pos - newMark;
					/** 0x0D 0x0A */
					if (packetLength > 2) {
						cache.reset();
						if(newMark - mark > 0) {
							cache.get(new byte[newMark - mark]);
						}
						/** 把需要的数据存放到raw数组里面 */
						byte[] raw = new byte[packetLength];
						// Copy data into array
						cache.get(raw);
						/** 把剩下的数据存放到cacheBytes数组里面 */
						byte[] cacheBytes = new byte[cache.limit() - cache.position()];
						cache.get(cacheBytes);
						Log.w(TAG, String.format("serial read=[%s]",new String(raw)));
						Log.w(TAG, String.format("serial read=[%s]",ArrayUtils.bytesToHexString(raw)));
						com.jzsk.seriallib.msg.msgv21.Message msg = new com.jzsk.seriallib.msg.msgv21.Message(raw);
						processMessage(msg);
						if (messageListener != null) {
							messageListener.processMessage(msg);
						}else{
							Log.w(TAG, "messageListener == null----------------------");
						}
						cache.clear();
						cache.put(cacheBytes);
						cache.flip();
					}
				} else {
					cache.reset();
					cache.get();
				}
			}
		}

	}

	/**
	 * Processes a message after it's been fully parsed by looping through the
	 * installed message collectors and listeners and letting them examine the
	 * message to see if they are a match with the filter.
	 *
	 * @param msg
	 *            the message to process
	 */
	private void processMessage(BaseMessage msg) {
		if (msg == null) {
			return;
		}

		// Loop through all collectors and notify the appropriate ones.
		for (MessageCollector collector : mConnection.getCollectors()) {
			collector.processMessage(msg);
		}

		// Deliver the incoming message to listeners
		mExecutor.submit(new ListenerNotification(msg));
	}

	/** A thread to read packets from the connection. */
	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			readPackets();
		}

	}

	/** A runnable to notify all listeners of a message. */
	private class ListenerNotification implements Runnable {

		private BaseMessage message;

		public ListenerNotification(BaseMessage msg) {
			this.message = msg;
		}

		@Override
		public void run() {
			for (Connection.ListenerWrapper wrapper : mConnection.getRcvListeners().values()) {
				wrapper.notifyListener(this.message);
			}
		}

	}

}

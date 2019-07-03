package com.jzsk.seriallib.conn;

import com.jzsk.seriallib.filter.MessageFilter;
import com.jzsk.seriallib.msg.BaseMessage;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Provides a mechanism to collect messages into a result queue that pass a specified filter. The
 * collector lets you perform blocking and polling operations on the result queue. So, a
 * MessageCollector is more suitable to use than a {@link MessageListener} when you need to wait for
 * a specific result.
 * <p>
 * Each message collector will queue up a configured number of messages for processing before older
 * messages are automatically dropped.
 *
 */
public class MessageCollector {

  private Connection    mConnection;
  private MessageFilter mFilter;
  private BlockingQueue<BaseMessage> mQueue;

  private boolean mCancelled = false;

  /**
   * Creates a new message collector. If the message filter is {@code null}, then all messages will
   * match this collector.
   *
   * @param conn   the connection the collector it tied to
   * @param filter determines which messages will be returned by this collector
   */
  MessageCollector(Connection conn, MessageFilter filter) {
    mConnection = conn;
    mFilter = filter;
    mQueue = new ArrayBlockingQueue<>(500);
  }

  /**
   * Processes a message to see if it meets the criteria for this message collector. If so, the
   * message is added to the result queue.
   *
   * @param msg the message to process
   */
  void processMessage(BaseMessage msg) {
    if (msg == null) {
      return;
    }

    if (mFilter == null || mFilter.accept(msg)) {
      //在队列的尾部插入元素，当该队列已满，offer()会返回false
      while (!mQueue.offer(msg)) {
        // Since we know the queue is full, this poll should never actually block
        //在队列的头部删除并且取出被删除的元素，当该队列为空时，pull()会返回false
        mQueue.poll();
      }
    }
  }

  /**
   * Polls to see if a message is currently available and returns it, or immediately return {@code
   * null} if no messages are currently in the result queue.
   *
   * @return the next message result, or {@code null} if there no more result
   */
  public BaseMessage pollResult() {
    return mQueue.poll();
  }

  /**
   * Returns the next available message. The method call will block (not return) until a message is
   * available.
   *
   * @return the next available message
   */
  public BaseMessage nextResult() {
    try {
      return mQueue.take();
    } catch (InterruptedException ie) {
      throw new RuntimeException(ie);
    }
  }

  /**
   * Returns the next available message. The method call will block (not return) until a message is
   * available or the <tt>timeout</tt> has elapsed. If the time out elapses without a result, {@code
   * null} will be returned.
   *
   * @param timeout the amount of time to wait for the next packet (in milliseconds)
   * @return the next available message
   */
  public BaseMessage nextResult(long timeout) {
    try {
      return mQueue.poll(timeout, TimeUnit.MILLISECONDS);
    } catch (InterruptedException ie) {
      throw new RuntimeException(ie);
    }
  }

  /**
   * Explicitly cancels the message collector so that no more results are queued up. Once a message
   * collector has been cancelled, it cannot be re-enabled. Instead, a new message collector must be
   * created.
   */
  public void cancel() {
    // If the message collector has already been cancelled, do nothing
    if (!mCancelled) {
      mCancelled = true;
      mConnection.removeMessageCollector(this);
    }
  }

  /**
   * Returns the message filter associated with this message collector. The message filter is used
   * to determine what messages are queued as results.
   *
   * @return the message filter
   */
  public MessageFilter getFilter() {
    return mFilter;
  }

}

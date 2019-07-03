package com.jzsk.seriallib.filter;

import com.jzsk.seriallib.msg.BaseMessage;
import com.jzsk.seriallib.msg.msgv21.Message;

/**
 * Filters for messages with a particular message ID.
 *
 */
public class MessageIdFilter implements MessageFilter {

  private String mId; //只做查询地址匹配

  /**
   * Creates a new message ID filter using the specified message ID.
   *
   * @param id the message ID to filter for
   */
  public MessageIdFilter(String id) {
    mId = id;
  }

  @Override
  public boolean accept(BaseMessage msg) {
    return mId.equals(msg.getAddr());
  }

  @Override
  public String toString() {
    return "MessageIdFilter by ID: " + mId;
  }

}

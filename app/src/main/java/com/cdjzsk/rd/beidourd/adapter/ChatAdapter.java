package com.cdjzsk.rd.beidourd.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cdjzsk.rd.beidourd.R;
import com.cdjzsk.rd.beidourd.bean.MsgData;
import com.cdjzsk.rd.beidourd.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by fengshawn on 2017/8/10.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MsgData> listData;
    private Context context;
    private String mId;

    public ChatAdapter(Context context, List<MsgData> listData) {
        this.context = context;
        this.listData = listData;
    }

    public void exchangeListData(List<MsgData> list) {
        this.listData = list;
        this.notifyDataSetChanged();
    }

//    @Override
//    public MsgViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.item_wechat_msg_list, parent, false);
//        return new MsgViewHolder(view);
//    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (i==Integer.parseInt(Constant.MESSAGE_MYSEND)){
            View view = LayoutInflater.from(context).inflate(R.layout.item_wechat_msg_send_layout, viewGroup, false);

            return new MsgViewHolderSend(view);


        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.item_wechat_msg_receive_layout, viewGroup, false);

            return new MsgViewHolderRrceive(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        MsgData msgData = listData.get(i);

        if (viewHolder instanceof MsgViewHolderSend){

            ((MsgViewHolderSend) viewHolder).send_profile.setImageResource(msgData.getProfile_res());
            ((MsgViewHolderSend) viewHolder).sendMsg.setText(msgData.getMsg());
            initTimeStamp(((MsgViewHolderSend) viewHolder).timeStamp, msgData);

        }else{
            ((MsgViewHolderRrceive) viewHolder).receiver_profile.setImageResource(msgData.getProfile_res());
            ((MsgViewHolderRrceive) viewHolder).receiveMsg.setText(msgData.getMsg());
            initTimeStamp(((MsgViewHolderRrceive) viewHolder).timeStamp, msgData);

        }


//        MsgData currentMsgData = listData.get(position);
//        MsgData preMsgData = null;
//        if (position >= 1)
//            preMsgData = listData.get(position - 1);
//        switch (currentMsgData.getMsgType()) {
//            case Constant.TYPE_RECEIVER_MSG:
//                initTimeStamp(holder, currentMsgData, preMsgData);
//                holder.senderLayout.setVisibility(View.GONE);
//                holder.receiverLayout.setVisibility(View.VISIBLE);
//                holder.receiveMsg.setText(currentMsgData.getMsg());
//                holder.receiver_profile.setImageResource(currentMsgData.getProfile_res());
//                break;
//
//
//            case Constant.TYPE_SENDER_MSG:
//                initTimeStamp(holder, currentMsgData, preMsgData);
//                holder.senderLayout.setVisibility(View.VISIBLE);
//                holder.receiverLayout.setVisibility(View.GONE);
//                holder.sendMsg.setText(currentMsgData.getMsg());
//                holder.send_profile.setImageResource(currentMsgData.getProfile_res());
//                break;
//        }
    }



    private void initTimeStamp(TextView view, MsgData currentMsgData) {
        String showTime;
/*        if (preMsgData == null) {
            showTime = HelpUtils.calculateShowTime(HelpUtils.getCurrentMillisTime(), currentMsgData.getTimeStamp());
        } else {
            showTime = HelpUtils.calculateShowTime(currentMsgData.getTimeStamp(), preMsgData.getTimeStamp());
        }*/
        /** 设置为直接显示每条消息的发送时间 */
        SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd E HH:mm");
        showTime = "20"+ format.format(currentMsgData.getTimeStamp());

        if (showTime != null) {
            view.setVisibility(View.VISIBLE);
            view.setText(showTime);
        } else {
            view.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    class MsgViewHolderRrceive extends RecyclerView.ViewHolder {

        ImageView receiver_profile;
        TextView timeStamp, receiveMsg;
        LinearLayout receiverLayout;

        public MsgViewHolderRrceive(View itemView) {
            super(itemView);
            receiver_profile =  itemView.findViewById(R.id.item_wechat_msg_iv_receiver_profile);
            timeStamp =  itemView.findViewById(R.id.item_wechat_msg_iv_time_stamp);
            receiveMsg =  itemView.findViewById(R.id.item_wechat_msg_tv_receiver_msg);
            receiverLayout =  itemView.findViewById(R.id.item_wechat_msg_layout_receiver);
        }
    }

    class MsgViewHolderSend extends RecyclerView.ViewHolder {

        ImageView  send_profile;
        TextView timeStamp,  sendMsg;
        RelativeLayout senderLayout;

        public MsgViewHolderSend(View itemView) {
            super(itemView);
            send_profile =  itemView.findViewById(R.id.item_wechat_msg_iv_sender_profile);
            timeStamp =  itemView.findViewById(R.id.item_wechat_msg_iv_time_stamp);
            sendMsg =  itemView.findViewById(R.id.item_wechat_msg_tv_sender_msg);
            senderLayout =  itemView.findViewById(R.id.item_wechat_msg_layout_sender);
        }
    }


    @Override
    public int getItemViewType(int position) {

        Log.e("getItemViewType", listData.get(position).getMsgType()+"");

        if (Constant.MESSAGE_MYSEND.equals(listData.get(position).getMsgType())){


            return   Integer.parseInt(Constant.MESSAGE_MYSEND);

        }else{
            return   Integer.parseInt(Constant.MESSAGE_NOTMYSEND);
        }


    }
}

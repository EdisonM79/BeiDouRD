package com.cdjzsk.rd.beidourd.adapter;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdjzsk.rd.beidourd.R;
import com.cdjzsk.rd.beidourd.bean.ContactShowInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fengshawn on 2017/8/2.
 */

public class ContactAdapter extends BaseAdapter implements Filterable {

    private List<ContactShowInfo> exchangeInfos;
    private List<ContactShowInfo> contactInfos;
    private Context context;
    private int resource;

    MyFilter mFilter ;


    public ContactAdapter(@NonNull Context context, @LayoutRes int resource, List<ContactShowInfo> contactInfos) {
        this.exchangeInfos = contactInfos;
        this.contactInfos = contactInfos;
        this.context = context;
        this.resource = resource;
    }
    @Override
    public int getCount() {
        return contactInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public long getItemId(int position) {
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        ViewHolder vh;
        ContactShowInfo contactInfo = contactInfos.get(position);

        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(resource, parent, false);
            vh = new ViewHolder(view);
            view.setTag(vh);
        } else {
            view = convertView;
            vh = (ViewHolder) view.getTag();
        }
        //初始化item控件
        vh.headImg.setImageResource(contactInfo.getHeadImage());
        vh.lastMsgTime.setText(contactInfo.getLastMsgTime());
        vh.username.setText(contactInfo.getUsername());
        vh.lastMsg.setText(contactInfo.getLastMsg());
        vh.mute.setVisibility(View.GONE);

        if (contactInfo.isRead()) {
            //isRead为true的时候，不显示红点
            vh.badge.setVisibility(View.GONE);
        } else {
            //isRead为false的时候，显示小红点
            vh.badge.setVisibility(View.VISIBLE);
        }

        return view;
    }

    class ViewHolder {
        private TextView username, lastMsg, lastMsgTime, badge;
        private ImageView headImg, mute;

        public ViewHolder(View v) {
            username = (TextView) v.findViewById(R.id.item_wechat_main_tv_username);
            lastMsg = (TextView) v.findViewById(R.id.item_wechat_main_tv_lastmsg);
            lastMsgTime = (TextView) v.findViewById(R.id.item_wechat_main_tv_time);
            headImg = (ImageView) v.findViewById(R.id.item_wechat_main_iv_headimg);
            mute = (ImageView) v.findViewById(R.id.item_wechat_main_iv_mute);
            badge = (TextView) v.findViewById(R.id.item_wechat_main_iv_unread);
        }
    }

    @Override
    public Filter getFilter() {
        if (mFilter ==null){
            mFilter = new MyFilter();
        }
        return mFilter;
    }

    //我们需要定义一个过滤器的类来定义过滤规则
    class MyFilter extends Filter {
        //我们在performFiltering(CharSequence charSequence)这个方法中定义过滤规则
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            FilterResults result = new FilterResults();
            List<ContactShowInfo> list;
            if (TextUtils.isEmpty(charSequence)) {//当过滤的关键字为空的时候，我们则显示所有的数据
                list = exchangeInfos;
            } else {//否则把符合条件的数据对象添加到集合中
                list = new ArrayList<>();
                for (ContactShowInfo contact : contactInfos) {
                    if (contact.getCardId().contains(charSequence)) { //要匹配的item中的view
                        list.add(contact);
                    }
                    if (contact.getUsername().contains(charSequence)) { //要匹配的item中的view
                        list.add(contact);
                    }
                }
            }
            result.values = list; //将得到的集合保存到FilterResults的value变量中
            result.count = list.size();//将集合的大小保存到FilterResults的count变量中
            return result;
        }

        //在publishResults方法中告诉适配器更新界面
        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            contactInfos = (List<ContactShowInfo>) filterResults.values;
            if (filterResults.count > 0) {
                notifyDataSetChanged();//通知数据发生了改变
            } else {
                notifyDataSetInvalidated();//通知数据失效
            }
        }
    }
}

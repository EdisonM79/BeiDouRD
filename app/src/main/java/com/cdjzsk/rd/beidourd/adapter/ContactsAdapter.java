package com.cdjzsk.rd.beidourd.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cdjzsk.rd.beidourd.R;
import com.cdjzsk.rd.beidourd.bean.Contact;

import java.util.List;


public class ContactsAdapter extends BaseAdapter {
	private Context context;//上下文对象
	private List<Contact> contacts;//ListView显示的数据

	/**
	 * 构造器
	 *
	 * @param context  上下文对象
	 * @param contacts 数据
	 */
	public ContactsAdapter(Context context, List<Contact> contacts) {
		this.context = context;
		this.contacts = contacts;
	}

	public List<Contact> getContacts() {
		return this.contacts;
	}

	@Override
	public int getCount() {
		return contacts == null ? 0 : contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		//判断是否有缓存
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.contact_item, null);
			viewHolder = new ViewHolder(convertView);
			convertView.setTag(viewHolder);
		} else {
			//得到缓存的布局
			viewHolder = (ViewHolder) convertView.getTag();
		}

		//设置图片
		viewHolder.pictureImg.setImageResource(R.mipmap.ic_launcher);
		//设置内容
		viewHolder.contentTv.setText(contacts.get(position).getCardId());
		//设置时间
		viewHolder.contactTime.setText(contacts.get(position).getTime());

		return convertView;
	}

	/**
	 * ViewHolder类
	 */
	private final class ViewHolder {

		ImageView pictureImg;//图片
		TextView contentTv;//内容
		TextView contactTime;//时间

		/**
		 * 构造器
		 *
		 * @param view 视图组件（ListView的子项视图）
		 */
		ViewHolder(View view) {
			pictureImg = (ImageView) view.findViewById(R.id.picture_img);
			contentTv = (TextView) view.findViewById(R.id.contact_name);
			contactTime =(TextView) view.findViewById(R.id.contact_time);
		}
	}

}

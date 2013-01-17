package com.mama100.android.member.widget.adapter;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mama100.android.member.R;
import com.mama100.android.member.bean.MessageItem;

/**
 * @author edwar 2012-11-21
 * 消息界面列表适配器, 这个是从本地数据库获取数据
 * @see MessageListAdapter, 它是从外部服务器获取数据 
 */
public class MessageCursorAdapter extends BaseAdapter {
	
	Activity mActivity;
	
	
	// 从服务器获取的列表。。
	private List<MessageItem> MessageItemList;
	
	//传值从此口进入
	public MessageCursorAdapter(Activity activity, List<MessageItem>  list){
		this.mActivity = activity;
		this.MessageItemList = list;
	}
	
	
	public void clearMemory(){
		mActivity = null;
		MessageItemList.clear();
	}
	
	

	@Override
	public int getCount() {
		return MessageItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return MessageItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if(convertView==null){
			convertView = View.inflate(mActivity, R.layout.msg_list_item, null);
			
			
			holder = new ViewHolder();
			//文本
			holder.title = (TextView) convertView.findViewById(R.id.msg_title);;
			holder.date = (TextView) convertView.findViewById(R.id.msg_date);
			holder.content = (TextView) convertView.findViewById(R.id.msg_content);;
			//图片
			holder.icon = (ImageView) convertView.findViewById(R.id.msg_icon);;
			convertView.setTag(holder); //设置holder进view, 1
		}else{
			holder = (ViewHolder) convertView.getTag(); // 有了1, 才有2 这步。。从view里拿holder
		}
		//设置具体参数值
		holder.title.setText(((MessageItem)getItem(position)).getTitle());
		holder.date.setText(((MessageItem)getItem(position)).getDate());
		holder.content.setText(((MessageItem)getItem(position)).getContent());
		holder.icon.setImageResource(getMsgIconId(((MessageItem)getItem(position)).getIsRead()));
		return convertView;
	}
	
	
	
	

	/******
	 * @param isread,是否已经读过。
	 * 1-已读
	 * 0-未读
	 * @return 对应的图标资源值
	 */
	private int getMsgIconId(String isread) {
	
		int value = Integer.parseInt(isread);
		int resId = 0;
		if(value==1){
			resId = R.drawable.msg_icon_read;
		}else{
			resId = R.drawable.msg_icon_unread;
		}
		return resId;
	}

	public class ViewHolder{
		private TextView title; //标题
		private TextView date; //时间
		private TextView content; //内容
		private ImageView icon; //图标
		
	}
	
	
	
	
}

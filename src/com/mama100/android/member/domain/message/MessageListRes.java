/**
 * 
 */
package com.mama100.android.member.domain.message;
import java.util.List;

import android.test.IsolatedContext;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.bean.MessageItem;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * 
 * @Description: 消息应答
 * 
 * @author Mico
 * 
 * @date 2012-7-20 下午3:39:02
 */
public class MessageListRes extends BaseRes {
	/**
	 * 符合条件的总条数
	 */
	@Expose
	private String count;

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	/**
	 * 消息数据
	 */
	@Expose
	private List<MessageItem> list;

	public List<MessageItem> getList() {
		return list;
	}

	public void setList(List<MessageItem> list) {
		this.list = list;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(".............get MessageList Info START....... \n");
		if(list!=null&&!list.isEmpty()){
			for (int i = 0; i < list.size(); i++) {
				MessageItem item = list.get(i);
				String id = item.getId();
				String title = item.getTitle();
				/**
				 * 是否已读,1-已读, -1 未读
				 */
				String isRead = item.getIsRead();
				int num = Integer.valueOf(isRead);
				sb.append(">>>> message num - " + i +"\n");
				sb.append("id - " + id +"\n");
				sb.append("title - " + title +"\n");
				sb.append("isRead-" + ((num>0)?true:false));
				sb.append("\n");
			}
			
		}
		sb.append(".............get MessageList Info END....... \n");
		return sb.toString();
	}
}

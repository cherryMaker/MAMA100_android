/**
 * 
 */
package com.mama100.android.member.domain.point;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.bean.PointItem;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * @Description: 积分列表应答
 * 
 * @author Mico
 * 
 * @date 2012-7-20 上午9:34:23
 */
public class PointListRes extends BaseRes {

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
	 * 积分余额
	 */
	@Expose
	private String pbalance;
	

	/**
	 * 积分列表数据
	 */
	@Expose
	private List<PointItem> list;

	public List<PointItem> getList() {
		return list;
	}

	public void setList(List<PointItem> list) {
		this.list = list;
	}
	
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(".............get RegPointList Info START....... \n");
		sb.append("....count....." + count);
		for (int i = 0; i < list.size(); i++) {
			PointItem item = list.get(i);
			String id = item.getId();
			String point = item.getPoint();
			String pname = item.getPname();
			String type = (String) getActName(item.getType());
			sb.append(">>>> regpointList No - " + i +"\n");
			sb.append("id - " + id +"\n");
			sb.append("point - " + point +"\n");
			sb.append("pname - " + pname +"\n");
			sb.append("type - " + type +"\n");
			sb.append("\n");
		}
		sb.append(".............get RegPointList Info END....... \n");
		return sb.toString();
	}
	
	/***
	 * @param type 参数，种类值 ：积分-1, 活动-2, 兑换-3
	 * @return 具体某一个名称 
	 */
	private CharSequence getActName(String type) {
		final int REGPOINT = 1;
		final int ACT = 2;
		final int EXG = 3;
		String result = "";
		
		int num = Integer.parseInt(type);
		switch (num) {
		case REGPOINT:
			result =  "购买";
			break;
		case ACT:
			result =  "活动";
			break;
		case EXG:
			result =  "兑换";
			break;
		default:
			break;
		}
		return result;
	}

	public String getPbalance() {
		return pbalance;
	}

	public void setPbalance(String pbalance) {
		this.pbalance = pbalance;
	}


}

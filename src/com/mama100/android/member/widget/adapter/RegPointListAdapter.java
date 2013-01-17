package com.mama100.android.member.widget.adapter;

import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mama100.android.member.R;
import com.mama100.android.member.bean.PointItem;
import com.mama100.android.member.util.StringUtils;

/**
 * @author dell
 *
 */
public class RegPointListAdapter extends BaseAdapter {
	
	Activity mActivity;
	
	
	// 从服务器获取的列表。。
	private List<PointItem> pointItemList;
	
	//传值从此口进入
	public RegPointListAdapter(Activity activity, List<PointItem>  list){
		this.mActivity = activity;
		this.pointItemList = list;
	}
	
	public void clearMemory(){
		mActivity = null;
		if (pointItemList != null) {
			pointItemList.clear();
			pointItemList = null;
		}
		
	}
	

	@Override
	public int getCount() {
		return pointItemList.size();
	}

	@Override
	public Object getItem(int position) {
		return pointItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder holder;
		if(convertView==null){
			convertView = View.inflate(mActivity, R.layout.regpoint_list_item, null);
			
			TextView regpoint = (TextView) convertView.findViewById(R.id.regpoint);
			TextView regdate = (TextView) convertView.findViewById(R.id.regdate);
			TextView regproduct = (TextView) convertView.findViewById(R.id.regproduct);
			TextView regact = (TextView) convertView.findViewById(R.id.regact);
			
			ImageView regicon = (ImageView) convertView.findViewById(R.id.regicon);
			ImageView operatoricon = (ImageView) convertView.findViewById(R.id.operatoricon);
			
			
			holder = new ViewHolder();
			//文本
			holder.point = regpoint;
			holder.date = regdate;
			holder.product = regproduct;
			holder.act = regact;
			//图片
			holder.operatoricon = operatoricon;
			holder.regicon = regicon;
			
			convertView.setTag(holder); //设置holder进view, 1
		}else{
			holder = (ViewHolder) convertView.getTag(); // 有了1, 才有2 这步。。从view里拿holder
		}
		//设置具体参数值
		holder.point.setText(StringUtils.getAbsValue(((PointItem)getItem(position)).getPoint())+"分");
		//point值小于0，就显示蓝色；大于0，显示mama100橙色
		holder.point.setTextColor((StringUtils.isNegativeValue(((PointItem)getItem(position)).getPoint()))?mActivity.getResources().getColor(R.color.mama100Blue):mActivity.getResources().getColor(R.color.mama100Orange));
		holder.date.setText(((PointItem)getItem(position)).getDate());
		holder.product.setText(((PointItem)getItem(position)).getPname());
		holder.act.setText(getActName(((PointItem)getItem(position)).getType()));
		
		holder.operatoricon.setImageResource(getOperatorId(((PointItem)getItem(position)).getPoint()));
		holder.regicon.setImageResource(getActId(((PointItem)getItem(position)).getType()));
		return convertView;
	}
	
	
	
	/***
	 * @param type 参数，种类值 ：积分-1, 活动-2, 兑换-3
	 * @return 具体某一个图片的id 
	 */
	private int getActId(String type) {
		final int REGPOINT = 1;
		final int ACT = 2;
		final int EXG = 3;
		int resId = 0;
		
		int num = Integer.parseInt(type);
		switch (num) {
		case REGPOINT:
			resId = R.drawable.reg_icon_buy;
			break;
		case ACT:
			resId = R.drawable.reg_icon_act;
			break;
		case EXG:
			resId =  R.drawable.reg_icon_exg;
			break;
		default:
			break;
		}
		return resId;
	}


	/******
	 * @param point 具体积分值
	 * @return 对应的操作运算符
	 */
	private int getOperatorId(String point) {
	
		int value = Integer.parseInt(point);
		int resId = 0;
		if(value>=0){
			resId = R.drawable.reg_icon_add;
		}else{
			resId = R.drawable.reg_icon_minus;
		}
		return resId;
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


	public class ViewHolder{
		private TextView point; //积分值
		private TextView date; //时间
		private TextView product; //产品
		private ImageView operatoricon; //操作符号图标
		private ImageView regicon; //积分对应的图标
		private TextView act;//操作
		
	}
	
	
	
	
}

package com.mama100.android.member.widget.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mama100.android.member.R;
import com.mama100.android.member.bean.ShopItem;

/**
* @Description: 用于积分门店列表显示的适配器 
* @author created by liyang  
* @date 2012-11-21 上午10:36:38 
*/
public class ShopListAdapter extends BaseAdapter {
	private Activity activity;
	
	//从服务器获取的总的门店列表
	private List<ShopItem> totalShopItemList = new ArrayList<ShopItem>();
	
	//从服务器获取的附近门店列表
	private List<ShopItem> sideShopItemList = new ArrayList<ShopItem>();
	
	public ShopListAdapter(Activity activity) {
		this.activity = activity ;
	}
	
	
	public void addSide(List<ShopItem> list){
		sideShopItemList.addAll(list);
	}
	
	
	public int getSideSize(){
		return sideShopItemList.size();
	}
	
	
	public void addList(List<ShopItem> list){
		if(list == null) return;
		
		//考虑到每次获取的数据在20条左右
		//此处采用以下方法排除重复项
		for (int i = 0; i < list.size(); i++) {
			ShopItem item = list.get(i);
			if(!totalShopItemList.contains(item)){
				totalShopItemList.add(item);
			}
		}
	}
	
	public void clearMemory(){
		this.activity = null;
		this.totalShopItemList.clear();
		this.sideShopItemList.clear();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return totalShopItemList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return totalShopItemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup group) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if(convertView == null){
			convertView = activity.getLayoutInflater().inflate(R.layout.regpoint_shop_item, null);
			holder = new ViewHolder();
			holder.shopRadio = (RadioButton)convertView.findViewById(R.id.shopCheck);
			holder.shopName = (TextView)convertView.findViewById(R.id.shopName);
			holder.shopAddress = (TextView)convertView.findViewById(R.id.shopAddress);
			holder.shopDistance= (TextView)convertView.findViewById(R.id.shopDistance);
			convertView.setTag(holder);
		}else{
			holder =(ViewHolder)convertView.getTag();
		}
		
		ShopItem shopItem = totalShopItemList.get(position);
		holder.shopName.setText(shopItem.getShopName());
		holder.shopAddress.setText(shopItem.getAddress());
		holder.shopDistance.setText((shopItem.getDistance().intValue())+"m");
		holder.shopRadio.setChecked(shopItem.isChecked());
		
		return convertView;
	}
	
	
	public class ViewHolder{
		public RadioButton shopRadio;
		public TextView shopName;
		public TextView shopAddress;
		public TextView shopDistance;
	}
}

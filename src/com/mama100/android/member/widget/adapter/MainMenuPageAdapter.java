package com.mama100.android.member.widget.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mama100.android.member.R;

//用于主界面的 适配器，显示那一排白色的点。。。
public class MainMenuPageAdapter extends ArrayAdapter
{

  private Map viewMap; //为了实现界面快速显示，暂时放进缓存。。
  private Activity mActivity;

   
	public MainMenuPageAdapter(Activity activity, List<GridItem> itemList) {
		super(activity, 0, itemList);
		this.mActivity = activity;
		this.viewMap = new HashMap();
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {

		if (viewMap != null && viewMap.get(position) != null) {
			return (View) viewMap.get(position);
		} else {
			convertView = mActivity.getLayoutInflater().inflate(
					R.layout.mainmenu_pagegrid_items, null);
			// 获取mList的资源
			GridItem image_text_map = (GridItem) getItem(position);
			//得到该位置如今应该是白点，还是黑点的点的资源id
			int i = image_text_map.getImageId();
			
			//获取白色的点所在的ImageView.
			ImageView localImageView = (ImageView) convertView.findViewById(R.id.item_image);
			localImageView.setBackgroundResource(i);
			
			this.viewMap.put(position, convertView);
			return convertView;
		}
	}
}
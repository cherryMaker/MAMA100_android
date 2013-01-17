package com.mama100.android.member.widget.adapter;

import java.util.List;

import android.content.Intent;

public class GridItem {
	private String title;
	private int imageId;
	private Intent mIntent;
	// 判断是否含有子modules
	private boolean isParent;
	public boolean isParent() {
		return isParent;
	}

	public void setParent(boolean isParent) {
		this.isParent = isParent;
	}

	// 父类modules下的几个子类
	private List<GridItem> childItems;

	public GridItem(String title, int imageId, Intent intent) {
		super();
		this.title = title;
		this.imageId = imageId;
		this.mIntent = intent;
		this.isParent = false;
	}
	public GridItem(String title, int imageId) {
		super();
		this.title = title;
		this.imageId = imageId;
		this.mIntent = null;
		this.isParent = false;
	}

	public GridItem(String title, int imageId, Intent intent, boolean parent) {
		super();
		this.title = title;
		this.imageId = imageId;
		this.mIntent = intent;
		this.isParent = parent;
	}

	public List<GridItem> getChildItems() {
		return childItems;
	}

	public void setChildItems(List<GridItem> childItems) {
		this.childItems = childItems;
	}

	public GridItem() {
		super();
	}

	public Intent getIntent() {
		return mIntent;
	}

	public void setIntent(Intent intent) {
		this.mIntent = intent;
	}

	public String getTitle() {
		return title;
	}

	public int getImageId() {
		return imageId;
	}
}
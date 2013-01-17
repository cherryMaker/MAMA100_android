/**   
 * @Title: PointSideShopRes.java 
 * @Package com.mama100.android.member.domain.point 

 * @version V1.0   
 */

package com.mama100.android.member.domain.point;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.mama100.android.member.bean.ShopItem;
import com.mama100.android.member.domain.base.BaseRes;

/**
 * @Description: 获取关注门店信息的应答类
 * @author created by liyang  
 * @date 2012-11-20 下午5:20:19 
 */
public class PointRelativeShopRes extends BaseRes {

	@Expose
	List<ShopItem> list;

	public List<ShopItem> getList() {
		return list;
	}

	public void setList(List<ShopItem> list) {
		this.list = list;
	}
}

/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mama100.android.member.outwardWeibo;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;


/**
 * A list queue for saving keys and values.
 * Using it to construct http header or get/post parameters.
 * @author ecoo
 * 
 * 
 * modified by edwar  2012-09-27
 * 公共参数类
 */
public class SocialParameters {

	private Bundle mParameters = new Bundle();
	private List<String> mKeys = new ArrayList<String>();
	
	
	public SocialParameters(){
		
	}
	
	
	//增加新的键值
	public void add(String key, String value){
		if(this.mKeys.contains(key)){	
			this.mParameters.putString(key, value);
		}else{
			this.mKeys.add(key);
			this.mParameters.putString(key, value);
		}
	}
	
	
	//删除该键对应的map
	public void remove(String key){
		mKeys.remove(key);
		this.mParameters.remove(key);
	}
	
	//删除该位置对应的map
	public void remove(int i){
		String key = this.mKeys.get(i);
		this.mParameters.remove(key);
		mKeys.remove(key);
	}
	
	
	/**
	 * @param key  要寻找该key的 位置
	 * @return 返回这个key在集合的位置
	 */
	public int getLocation(String key){
		if(this.mKeys.contains(key)){
			return this.mKeys.indexOf(key);
		}
		return -1;
	}
	
	/**
	 * @param location  传入的参数位置
	 * @return 返回该位置的
	 */
	public String getKey(int location){
		if(location >= 0 && location < this.mKeys.size()){
			return this.mKeys.get(location);
		}
		return "";
	}
	
	
	/**
	 * @param key
	 * @return 返回该键对应的键值
	 */
	public String getValue(String key){
		String rlt = this.mParameters.getString(key);
		return rlt;
	}
	
	
	/**
	 * @param location
	 * @return 返回该位置对应的 键值
	 */
	public String getValue(int location){
		String key = this.mKeys.get(location);
		String rlt = this.mParameters.getString(key);
		return rlt;
	}
	
	
	//整个集合的大小
	public int size(){
		return mKeys.size();
	}
	
	//全部复制
	public void addAll(SocialParameters parameters){
		for(int i = 0; i < parameters.size(); i++){
			this.add(parameters.getKey(i), parameters.getValue(i));
		}
		
	}
	
	//清空所有值
	public void clear(){
		this.mKeys.clear();
		this.mParameters.clear();
	}
	
}

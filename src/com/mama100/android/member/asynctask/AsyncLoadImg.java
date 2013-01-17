package com.mama100.android.member.asynctask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.mama100.android.member.util.LogUtils;


import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;

/**
 * 异步加载图片，很适合listview使用。
 * 
 * @author eCo
 */
public class AsyncLoadImg{
	
	private View mView; //显示图片的view
	private String mUrl; //图片url
	private String mViewTagSem;  //view的信号量。View显示当前mUrl所代表的图片之前，如mViewTagSem!=view.getTag(),view将不显示这张图片(已过时)。
	private int mDefaultResId=-1;
	
	protected static final int Map_Max_Size=40;       //Drawable HashMap的最大容量控制在Map_Max_Size左右，建议不超过80
	protected static final int Asyn_Task_Max_NO=80;  //最多同时存在asynTaskMaxSize个加载任务，不允许超过100
	protected static final int Each_Recycle_NO=3; //每次回收drawable的数据，太大影响界面的流畅及缓存效果
	protected static final int Time_Out=10000;        //加载图片超时时间,ms
	
	
	
	protected static HashMap<String, Drawable> DrawableMap=new HashMap<String, Drawable>();	//用于缓存drawable
	
	protected static List<String> keySet=new LinkedList<String>(); //HashMap Key的集合，采用链表结构以在频繁插入及删除操作下保持良好性能
	
	protected static int counter=0;
//	protected static long lastLoadTime=0;
//	protected static long currentTime=0;
//	protected static List<String> urlsTmp=new ArrayList<String>();
	/**
	 * 
	 * @param view 显示图片的view(图片置于背景)
	 * @param imgUrl 图片的url;
	 * @param viewTagSem view tag当前的“信号量”，同样的viewTagSem将得到相同的图片，可以等于imgUrl。根据mUrl下载图片之后，如果此值不等于view tag最新的值(即viewTagSem!=view.getTag())
	 *                   ,view将不显示这张图片。但图片仍然会保存在缓存中，下次相同的viewTagSem会得到这张图片。初始化这个类之前，可以不设置view.setTag(viewTagSem)。
	 * @param mDefaultResId 默认图片的id，如果图片加载失败则用此图片;
	 */
	public AsyncLoadImg(View view,String imgUrl,String viewTagSem,int defaultResId) {
		view.setTag(viewTagSem);
		mView=view;
		mUrl=imgUrl;
		mViewTagSem=viewTagSem;
		mDefaultResId=defaultResId;
	}

	
	public void execute(){
		if(mUrl==null||mUrl.equals("")){
			mView.setBackgroundResource(mDefaultResId);
			return;
		}
		synchronized(AsyncLoadImg.class){
			//Map中已有此图片的drawable
			if(DrawableMap.get(mViewTagSem)!=null){
				mView.setBackgroundDrawable(DrawableMap.get(mViewTagSem));
				//将最近出现的放至链表头部
				keySet.remove(mViewTagSem);
				keySet.add(0, mViewTagSem);
				return;
			}
			
			if(DrawableMap.size()>Map_Max_Size+3){
//				String key=hashMapKeys.remove(0);
//				if(key!=null){
//					Drawable tmp=DrawableMap.remove(key);
//					if(tmp!=null){
//						tmp.setCallback(null);
//						tmp=null;
//					}
//				}
//				key=null;
	
				//每次尝试释放Each_Recycle_NO个drawable
				for(int index=0;index<Each_Recycle_NO;index++){
					//从链表尾部删除数据
					Drawable tmp=DrawableMap.remove(keySet.remove(keySet.size()-1));
					if(tmp!=null){
						tmp.setCallback(null);
						tmp=null;
					}
				}
				System.gc();  
			}
			
			
//			currentTime=System.currentTimeMillis();
//			if(lastLoadTime==0) lastLoadTime=currentTime;
//			//500ms内且累积请求不超过5次，则不发线程
//			if(currentTime-lastLoadTime<=500&&counter<5){
//				urlsTmp.add(mUrl);
//				return;
//			}
//			else{
//				lastLoadTime=currentTime;
//				counter=0;
//				urlsTmp.removeAll(null);
//			}
		}

		pauseOrExecute(new InnerTask());
	}
	
	public class InnerTask extends AsyncTask<Void, Void, Drawable>{
		
		protected Drawable doInBackground(Void... params) {
//			Log.e("doInBackground ", "doInBackground ");
			Drawable drawable = null;
			HttpURLConnection conn = null;
			InputStream stream = null;

			try {
				if (URLUtil.isHttpUrl(mUrl)) {
					// 如果为网络地址。则连接url下载图片
					final URL url = new URL(mUrl);
					conn = (HttpURLConnection) url.openConnection();
					// conn.setDoInput(true);
					conn.setConnectTimeout(Time_Out); // 超时
					conn.connect();
					stream = conn.getInputStream();
					drawable = Drawable.createFromStream(stream, "src");
				} else { // 如果为本地数据，直接解析
					drawable = Drawable.createFromPath(mUrl);
				}
			} catch (Exception e) {
				Log.e("AsyncLoadImg error", e.getMessage()!=null?e.getMessage():"AsyncLoadImg error");
				return null;
			} finally {
				if (stream != null) {
					try {
						stream.close();
						stream=null;
					} catch (IOException e) {
						Log.e("AsyncLoadImg IOException error", e.getMessage()!=null?e.getMessage():"AsyncLoadImg error");
					}
				}
				if (conn != null) {
					conn.disconnect();
					conn=null;
				}
			}
			return drawable;
		}

		protected void onPostExecute(Drawable drawable) {
//			Log.e("onPostExecute ", "onPostExecute ");
			if(drawable==null){
				if(((String)mView.getTag()).equals(mViewTagSem)&&mDefaultResId!=-1){
					mView.setBackgroundResource(mDefaultResId);
				}
				synchronized (AsyncLoadImg.class) {
					counter--;
					LogUtils.logd(getClass(), "after load img,count:"+counter);
				}
				return;
			}
			
			//一段网络延迟之后，这个异步请求有可能已过时(如mView请求显示另外一张图片)
			//如果信号相等，则直接显示
			if(((String)mView.getTag()).equals(mViewTagSem)){
				mView.setBackgroundDrawable(drawable);
			}

			synchronized (AsyncLoadImg.class) {
				//将最近的放到链表头部
				keySet.add(0, mViewTagSem);
				DrawableMap.put(mViewTagSem, drawable);
				
				counter--;
				LogUtils.logd(getClass(), "after load img,count:"+counter);
			}


		}

	}
	
	
	/**This method can prevent AsyncTask from RejectedExecutionException by controlling the max number of AsyncTask
	 * @param loader the image loader task to execute
	 * @return true,execute now;false,wait to execute
	 */
	protected synchronized static boolean pauseOrExecute(final InnerTask loader){
//		LogUtils.logd("check", "before load img,count:"+counter);
		if(counter>Asyn_Task_Max_NO){
			final Thread wait=new Thread(new Runnable() {
				
				@Override
				public void run() {
					while(counter>Asyn_Task_Max_NO){
						try {
							Thread.sleep(Time_Out/5);
						} catch (InterruptedException e) {
							e.printStackTrace();
							return;
						}
					}
					counter++;
					loader.execute();
				}
			});
			wait.start();
			return false;
		}
		else{
			counter++;
			loader.execute();
			return true;
		}
	}
}
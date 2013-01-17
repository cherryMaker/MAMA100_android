package com.mama100.android.member.asynctask;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.mama100.android.member.R;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.util.PictureUtil;
import com.mama100.android.member.util.SDCardUtil;

/**
 * 图片异步加载，用于加载图片，
 * @author edwar 2012-08-15
 */
public class AsyncBitmapTask extends AsyncTask<View, Void, Bitmap> {

	private String LOG_TAG = this.getClass().getSimpleName();
	
	private View mView; //用于本地获取传进来的参数view
	
	private Context context;
	
	
	/***********************************
	 * 用于释放内存的
	 **********************************/
	private SoftReference<Bitmap> softBitmap = null;
	private List<Bitmap> bitmapList = new ArrayList<Bitmap>();//这里用不到，因为图片这里不能回收，外部要显示。
	private boolean isNeedToSave = true; //默认是要保存，用于第三方登录在个人信息界面，不用急着保存第三方头像，除非用户自己提交，且成功了。
	private boolean isNeedToStoreInTempCropingPhotoPath = false; //默认不需要，只有在个人信息界面加载的图片才是true;目的是将用户自拍与加载第三方的路径一致
	
	
	public AsyncBitmapTask(Context context) {
		context = context;
	}
	
	
	/**
	 * 
	 * @param context
	 * @param b  //要不要保存进本地
	 */
	public AsyncBitmapTask(Context context, boolean b) {
		this(context);
		isNeedToSave = b;
		
	}
	
	/**
	 * 
	 * @param context
	 * @param b  //要不要保存进本地
	 * @param isStoreTemp    //保存的路径：true,保存进tempCropPath;false,保存进SaveStorePath
	 */
	public AsyncBitmapTask(Context context, boolean b, boolean isStoreTemp) {
		this(context);
		isNeedToSave = b;
		isNeedToStoreInTempCropingPhotoPath = isStoreTemp;
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}
	
	

	protected Bitmap doInBackground(View... views) {

		Bitmap bitmap = null;
		View view = views[0];
		this.mView = view;
		if (view.getTag() != null) {

			HttpURLConnection conn = null;
			InputStream stream = null;
			
			try {

				if (URLUtil.isHttpUrl(view.getTag().toString())) {

					// 如果为网络地址。则连接url下载图片

					URL url = new URL(view.getTag().toString());

					conn = (HttpURLConnection) url.openConnection();
					//conn.setDoInput(true);
					conn.setConnectTimeout(10 * 1000); // 10秒超时
					conn.connect();	
					
					stream = conn.getInputStream();
					bitmap = BitmapFactory.decodeStream(stream);					

				} else { // 如果为本地数据，直接解析

//					bitmap = Drawable.createFromPath(view.getTag().toString());

				}
			} catch (Exception e) {
				//Log.v(LOG_TAG, e.getMessage());
				return null;
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						//Log.e(LOG_TAG, e.getMessage());
					}
				}
				if (conn != null) {
					conn.disconnect();
				}
			}

		}

		

		return bitmap;
	}

	protected void onPostExecute(Bitmap bitmap) {
	     /**
	      * 判断本地保留参数view的mView
	      */
		/**
		 *  一，如果是相对布局，则是活动界面
		 */
		if(mView instanceof RelativeLayout){
			//隐藏圆形滚动栏
		this.mView.findViewById(R.id.front_progressbar).setVisibility(View.GONE);
		
		if (bitmap != null) {
			//保存本地
			saveIntoLocalActPicture(bitmap);
			((ImageView)this.mView.findViewById(R.id.back_imageview)).setImageBitmap(bitmap);
		}
		//网络不好的情况下，从本地拿图片
		else{
			// 本地默认存放临时图片
				String filename = SDCardUtil.getPictureStorePath()
						+ AppConstants.TEMP_STORE_ACT_NAME + (BasicApplication.getInstance().getActPictureCount()) + ".jpg";
				softBitmap = new SoftReference<Bitmap>(PictureUtil.getPictureFromSDcardByPath(filename));
			//如果从本地获取bitmap成功
			if(softBitmap!=null){
				((ImageView)this.mView.findViewById(R.id.back_imageview)).setImageBitmap(softBitmap.get());
			softBitmap = null;}
			else
				((ImageView)this.mView.findViewById(R.id.back_imageview)).setBackgroundResource(R.drawable.default_act);
		}
		this.mView = null;
	} else
		/**
		 * 二，如果是ImageView，则是头像
		 */
		if(mView instanceof ImageView){
			if (bitmap != null) {
//				LogUtils.logi("AsyncViewTask", "set avatar 44 - : "
//						+ new Timestamp(System.currentTimeMillis()).toString());
				//保存本地
				if(isNeedToSave)
				saveIntoLocalAvatarBitmap(bitmap, isNeedToStoreInTempCropingPhotoPath);
//				LogUtils.loge("HomepageActivity", "get avatar - " + 4 +" - async - server - drawable" );
//				Toast.makeText(context,  "get avatar - " + 4 +" - async - server - drawable", Toast.LENGTH_SHORT).show();
				((ImageView) this.mView).setImageBitmap(null);
				((ImageView) this.mView).setBackgroundDrawable(null);
				((ImageView) this.mView).setImageBitmap(bitmap);
//				
//				LogUtils.logi("AsyncViewTask", "set avatar 66 - : "
//						+ new Timestamp(System.currentTimeMillis()).toString());
			}
			//网络不好的情况下，从本地拿图片
			else{
				//如果本地保留了上次用户的头像，就用上传头像
				String filename = SDCardUtil.getPictureStorePath()+AppConstants.TEMP_STORE_PICTURE_NAME;
				 softBitmap = new SoftReference<Bitmap>(PictureUtil.getPictureFromSDcardByPath(filename));
				//如果从本地获取bitmap成功
				if(softBitmap!=null){
//					LogUtils.loge("HomepageActivity", "get avatar - " + 5 + " - from sdcard TEMP_STORE_PICTURE_NAME  " );
//					Toast.makeText(context,  "get avatar - "  + 5 + " - from sdcard TEMP_STORE_PICTURE_NAME  ", Toast.LENGTH_SHORT).show();
					((ImageView)this.mView).setImageBitmap(softBitmap.get());
					BasicApplication.getInstance().setAvatarBitmap(softBitmap.get());
				}
				else{
//					LogUtils.loge("HomepageActivity", "get avatar - " + 6 );
					
//					Toast.makeText(context, "get avatar - " + 6 , Toast.LENGTH_SHORT).show();
					
					//modified by edwar, 2012-11-06 直接在xml里赋值未登录的默认头像，所以这里没有必要再赋值，精简赋值
//				((ImageView)this.mView).setBackgroundResource(R.drawable.default_avatar);
//				softBitmap =new SoftReference<Bitmap>(BitmapFactory.decodeResource(BasicApplication.getInstance().getResources(), R.drawable.default_avatar));
					//modified end
				}
				softBitmap = null;
			}
			this.mView = null;
		}
		
}
	
	
	
	/**
	 * 将drawable转换成bitmap,再保存进本体sd卡
	 * @param drawable 获取的drawable
	 */
	private void saveIntoLocalActPicture(Bitmap bitmap) {
		try {
			String file  = SDCardUtil.getPictureStorePath() + AppConstants.TEMP_STORE_ACT_NAME+(BasicApplication.getInstance().getActPictureCount())+".jpg";
			PictureUtil.storePicture(file,bitmap,AppConstants.BITMAP_COMPRESS);
			bitmap = null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 将drawable转换成bitmap,再保存进本体内存
	 * @param drawable 获取的drawable
	 */
	private void saveIntoLocalAvatarBitmap(Bitmap bitmap, boolean isNeedToStoreInTempCrop) {
//		Bitmap bitmap = BitmapFactory.decodeStream(stream);	
		 
//		 LogUtils.logi("AsyncViewTask", "set avatar 55.1 - : "
//					+ new Timestamp(System.currentTimeMillis()).toString());
		BasicApplication.getInstance().setAvatarBitmap(bitmap);
		//同时保存图片进sd卡。
		try {
			String file = "";
			if(isNeedToStoreInTempCrop){
				 file = SDCardUtil.getTempCropingPhotoPath();
			}else{
				 file = SDCardUtil.getPictureStorePath() +  AppConstants.TEMP_STORE_PICTURE_NAME;
			}
			PictureUtil.storePicture(file, bitmap, AppConstants.BITMAP_COMPRESS);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//之前的方法是将图片保存在sd卡里，现在不用了
//		PictureUtil.storePicture(SDCardUtil.getPictureStorePath(), bitmap, AppConstants.TEMP_STORE_PICTURE_NAME, 100);
//		Uri uri = Uri.parse(SDCardUtil.getPictureStorePath()+AppConstants.TEMP_STORE_PICTURE_NAME);
//		BasicApplication.getInstance().setStoreAvatarUri(uri);
		
	}
	
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		softBitmap = null;
		
	}

}
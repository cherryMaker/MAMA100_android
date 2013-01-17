package com.mama100.android.member.asynctask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.mama100.android.member.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;

/**
 * 产品图片异步加载类
 * 
 * @author edwar 2012-08-15
 * @modified by liyang 2012-11-30
 */
public class AsyncProductPicTask extends AsyncTask<View, Void, Bitmap> {

	private View mView; // 用于本地获取传进来的参数view

	/***********************************
	 * 用于释放内存的
	 **********************************/

	public AsyncProductPicTask(Context context) {
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
					// conn.setDoInput(true);
					conn.setConnectTimeout(10 * 1000); // 10秒超时
					conn.connect();

					stream = conn.getInputStream();
					bitmap = BitmapFactory.decodeStream(stream);

				} else { // 如果为本地数据，直接解析

					// bitmap =
					// Drawable.createFromPath(view.getTag().toString());

				}
			} catch (Exception e) {
				// Log.v(LOG_TAG, e.getMessage());
				return null;
			} finally {
				if (stream != null) {
					try {
						stream.close();
					} catch (IOException e) {
						// Log.e(LOG_TAG, e.getMessage());
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
		if (mView instanceof ImageView) {
			if (bitmap != null) {
				((ImageView) this.mView).setImageBitmap(bitmap);
			}
		}
		this.mView = null;
	}
}
package com.mama100.android.member.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class PictureUtil {

	private static final float STANDARD_WIDTH = 240;// <moto_Me811照相标准宽度 3264>
													// /<我们的宽度 250> = 13, 缩放比率
													// (1/13)^2 = 1/169
	private static final float STANDARD_HEIGHT = 320;
	private static final int STANDARD_SCALE_RATIO = 16;// 缩放比率最好是 2
														// 的整倍数，执行最快。@see
														// Options.inSampleSize
	// 不用16是因为16倍压缩图片太小，之间250宽度时，倍数是 3264/250 = 13
	private static final int FIRST_COMPRESS = 25;// 获取系统相机图片时，首次压缩，图片质量压缩比率是 25%
	private static final int SECOND_COMPRESS = 25;// 保存到本地sdcard时，二次压缩，图片质量压缩比率是
													// 25%
	private static String TAG = "pictureUtil";

	/**
	 * @param path
	 *            路径
	 * @return bitmap
	 */
	public static Bitmap getPictureFromSDcardByPath(String path) {
		if (SDCardUtil.isSDCardExist()) {
			if (!FileUtil.isFileExist(path)) {
				return null;
			} else {
				return BitmapFactory.decodeFile(path);
			}
		} else {
			LogUtils.loge(TAG, "getPicture name: " + path
					+ "  failed, SDcard is not Exist - ");
			return null;
		}
	}
	/**
	 * 
	 * @param mBitmap
	 *            图片
	 * @param fName文件名称
	 * @return 本地生成图片size: 320 * 240
	 * @throws IOException
	 */
	public static String storePicture(String path, Bitmap mBitmap)
			throws IOException {
		return storePicture(path, mBitmap, SECOND_COMPRESS);
	}

	/**
	 * 
	 * @param mBitmap
	 *            图片
	 * @param fName文件名称
	 * @return 本地生成图片size: 320 * 240
	 * @throws IOException
	 */
	public static String storePicture(String path, Bitmap mBitmap,
			int compressRate) throws IOException {
		if (SDCardUtil.isSDCardExist()) {
			File f = new File(path);
			boolean flag = f.createNewFile();
			LogUtils.logi(TAG, "isExist - " + flag);
			FileOutputStream fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, compressRate, fOut);
			fOut.flush();
			fOut.close();
			return f.getPath();
		} else {
			LogUtils.loge(TAG, "storePicture name: " + path
					+ "failed, SDcard is not Exist - ");
			return "";

		}
	}

	/**
	 * 删除SDCARD上的照片
	 * 
	 * @param fName
	 * @return
	 */
	public static boolean deletePicture(String fName) {
		if (SDCardUtil.isSDCardExist()) {
			File f = new File(fName);
			if (f.exists()) {
				if (f.delete()) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else {
			LogUtils.loge(TAG, "deletePicture name: " + fName
					+ "failed, SDcard is not Exist - ");
			return false;

		}
	}

	// Stream转换成Byte
	static byte[] streamToBytes(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		try {
			while ((len = is.read(buffer)) >= 0) {
				os.write(buffer, 0, len);
			}
		} catch (java.io.IOException e) {

		}
		return os.toByteArray();
	}

	/**
	 * 1,从sdcard获取的图片首先进行压缩，否则容易OOM; 压缩比例用了//
	 * inSampleSize=（outHeight/Height+outWidth/Width）/2，据说这个压缩处理起来最快 2,再进行质量压缩。
	 * 
	 * @param fName
	 * @return
	 * 
	 */
	public static Bitmap sdcardPictureToBitmap(String fName) {

		if (SDCardUtil.isSDCardExist()) {

			LogUtils.logi(TAG, "1--" + System.currentTimeMillis());

			BitmapFactory.Options options = new BitmapFactory.Options();
			// 06-25 21:03:35.981: E/dalvikvm-heap(12440): Out of memory on a
			// 8392808-byte allocation.
			// options.inSampleSize = STANDARD_SCALE_RATIO; //先进行1/256压缩，
			// 否则容易OOM。
			// added by edwar 2012-06-25

			// 1， 先生成 bitmap
			// FileInputStream fileInputStream = null;
			// try {
			// fileInputStream = new FileInputStream(fName);
			// } catch (FileNotFoundException e) {
			// e.printStackTrace();
			// }
			// LogUtils.logi(TAG, "1.1--"+System.currentTimeMillis());
			//
			// //比调用系统路径拍照多了这一步，时间多耗时0.4秒左右 @see
			// getScaledBitmapFromSystemCameraBundle()
			// Bitmap mBitmap =
			// BitmapFactory.decodeStream(fileInputStream,null,options);

			options.inJustDecodeBounds = true;// 不加载bitmap到内存中
			BitmapFactory.decodeFile(fName, options);
			int outWidth = options.outWidth;
			int outHeight = options.outHeight;
			Log.i(TAG, "width - " + outWidth + ", height - " + outHeight);
			options.inDither = false;
			options.inPreferredConfig = Bitmap.Config.ARGB_8888;
			options.inSampleSize = 1;

			if (outWidth != 0 && outHeight != 0 && STANDARD_WIDTH != 0
					&& STANDARD_HEIGHT != 0) {
				// 这就是用来做缩放比的。这里有个技巧：
				// inSampleSize=（outHeight/Height+outWidth/Width）/2
				int sampleSize = (int) ((outWidth / STANDARD_WIDTH + outHeight
						/ STANDARD_HEIGHT) / 2);
				Log.d(TAG, "sampleSize = " + sampleSize);
				options.inSampleSize = sampleSize;
			}
			options.inJustDecodeBounds = false;
			// 1,这种方法比上面被注释掉的方法省时
			Bitmap mBitmap = BitmapFactory.decodeFile(fName, options);

			// 2, 在压缩图片质量
			LogUtils.logi(TAG, "1.2--" + System.currentTimeMillis());

			if (mBitmap == null) {
				LogUtils.logi(TAG, "bm is null");
			}
			int w = mBitmap.getWidth();
			int h = mBitmap.getHeight();
			int height = h;
			int width = w;
			LogUtils.logi(TAG, "width is - " + w + ", height is - " + h + "\n "
					+ "before scaled, size is - : " + (h * w));

			// 2,这里做了压缩处理，Bitmap2Bytes()调用Bitmap.compress()方法，唯一的压缩方法
			// 注意，存储的时候，也做了压缩处理，@see PictureUtil.storePicture(mBitmap, fName);
			// 所以，之前mkt360一共用了两次压缩。
			byte[] mdata = PictureUtil.Bitmap2Bytes(mBitmap);
			LogUtils.logi(TAG, "1.3--" + System.currentTimeMillis());

			// 3, 这里再通过decodeByteArray将之前的bytes转换成bitmap
			// 获取宽度缩放比例
			// BitmapFactory.Options mOptions = new Options();
			// int scale_ratio = (int) (width / (float) STANDARD_WIDTH);
			// LogUtils.logi(TAG, "ratio is - " + scale_ratio);
			// if (scale_ratio <= 0)
			// scale_ratio = 1;
			// mOptions.inSampleSize = scale_ratio;
			options.inJustDecodeBounds = false;
			options.inSampleSize = 1;

			mBitmap = BitmapFactory.decodeByteArray(mdata, 0, mdata.length,
					options);
			LogUtils.logi(TAG, "1.4--" + System.currentTimeMillis());
			if (mBitmap == null) {
				LogUtils.logi(TAG, "bm is null");
			}
			LogUtils.logi(TAG, "2--" + System.currentTimeMillis());
			return mBitmap;
		} else {
			LogUtils.loge(TAG, "deletePicture name: " + fName
					+ "failed, SDcard is not Exist - ");
			return null;
		}
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = 10; // width，hight设为原来的十分一

		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	// 1、Drawable → Bitmap
	public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap
				.createBitmap(
						drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(),
						Bitmap.Config.ARGB_8888);
//						drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//								: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		// canvas.setBitmap(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;
	}

	// 3、Bitmap → byte[]
	/**
	 * @param bm
	 *            欲转换的图片
	 * @return 2进制。。 注意： 这里默认压缩率是 FIRST_COMPRESS， 25%
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm) {
		return Bitmap2Bytes(bm, FIRST_COMPRESS);
	}

	/**
	 * @param bm
	 * @param compressrate
	 *            压缩率，传参进来。
	 * @return
	 */
	public static byte[] Bitmap2Bytes(Bitmap bm, int compressrate) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, compressrate, baos);
		return baos.toByteArray();
	}

	// 4、 byte[] → Bitmap
	public static Bitmap Bytes2Bimap(byte[] b) {
		if (b.length != 0) {
			return BitmapFactory.decodeByteArray(b, 0, b.length);
		} else {
			return null;
		}
	}

	/**
	 * @param data
	 *            Android系统相机返回来的包含 bitmap的intent
	 * @return 压缩过的图片，压缩比例 <moto标准宽度：w3264*1224h> ,我们的目标：
	 */
	public static Bitmap getScaledBitmapFromSystemCameraBundle(Intent data) {

		LogUtils.logi(TAG, "1^--" + System.currentTimeMillis());

		// 1，这里通过获取到的data,直接获取bitmap图片
		Bitmap mBitmap = (Bitmap) data.getExtras().get("data");

		if (mBitmap == null) {
			LogUtils.logi(TAG, "bm is null");
			return null;
		}
		int w = mBitmap.getWidth();
		int h = mBitmap.getHeight();
		int height = h;
		int width = w;
		LogUtils.logi(TAG, "width is - " + w + ", height is - " + h + "\n "
				+ "before scaled, size is - : " + (h * w));

		// 2,这里做了压缩处理.调用Bitmap.compress()方法，唯一的压缩方法
		// 注意，存储的时候，也做了压缩处理，@see PictureUtil.storePicture(mBitmap, fName);
		// 所以，之前mkt360一共用了两次压缩。
		byte[] mdata = PictureUtil.Bitmap2Bytes(mBitmap);

		// 获取宽度缩放比例
		BitmapFactory.Options mOptions = new Options();
		mOptions.inJustDecodeBounds = true;// 当inJustDecodeBounds设成true时，bitmap并不加载到内存
		int scale_ratio = (int) (width / (float) STANDARD_WIDTH);
		LogUtils.logi(TAG, "ratio is - " + scale_ratio);
		if (scale_ratio <= 0)
			scale_ratio = 1;
		mOptions.inSampleSize = scale_ratio;
		mOptions.inJustDecodeBounds = false;

		// 3,这里再通过decodeByteArray将之前的bytes转换成bitmap
		mBitmap = BitmapFactory.decodeByteArray(mdata, 0, mdata.length,
				mOptions);
		if (mBitmap == null) {
			LogUtils.logi(TAG, "bm is null");
			return null;
		}
		LogUtils.logi(TAG, "2^--" + System.currentTimeMillis());
		return mBitmap;
	}

	/**
	 * 获取SD卡中最新图片路径
	 * 
	 * @return
	 */
	public static String getLatestImage(Context context) {
		if (SDCardUtil.isSDCardExist()) {

			// 获取当前时间： 格式： 类似：1341298323
			long current_time = System.currentTimeMillis() / 1000; // 单位： 秒
			// 往前推10分钟差额
			long offset = 10 * 60;
			// 比较时间点
			long compare_time = current_time - offset;

			String latestImage = null;
			// 修改时间大于 某个时间点
			String selection = MediaStore.Images.Media.DATE_ADDED + " > ?";
			String[] selectionArgs = { "" + compare_time + "" };
			String[] items = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA };

			ContentResolver cr = context.getContentResolver();
			Cursor cursor = cr.query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items,
					selection, selectionArgs, MediaStore.Images.Media._ID
							+ " desc  limit 1");
			// Cursor cursor =
			// cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			// null, null, null, MediaStore.Images.Media._ID +
			// " desc  limit 1");

			if (cursor != null && cursor.getCount() > 0) {
				LogUtils.logi(TAG, "查询系统数据库，满足条件的照片个数：" + cursor.getCount());
				cursor.moveToFirst();
				// 判断cursor的位置是不是在lastRow之后，这里api文档里的last应该理解为最近的一个，就是最顶部的一个。
				// 如果在最顶部一个后面，证明所取的row还不是最顶一个，还需要继续move，不过因为是降序排列，所以第一个就是最近一个
				// for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
				// .moveToNext()) {
				
				
				/******************************************************************
				 * 好久不碰，差点就忘了。。 这里cursor.getString(0)就是id值。。 则最后一张图片的Uri路径就是
				 * MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/" + id , 类似于下面这种：
				 * content://media/external/images/media/4
				 * 
				 * 经过debug查值：
				 * content://media/external/images/media  就是   MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				 * 
				 * 后面这个cursor.getString(1) 是真实文件存放的路径 ， 类似于 下面这种：
				 *  /mnt/sdcard/dcim/Camera/2012-09-10_10-47-50_658.jpg
				 */
				latestImage = cursor.getString(0) + ":" + cursor.getString(1);
				Log.e(TAG, "latestImage is ： " + latestImage);

				// for (int i = 0; i < 100; i++) {
				// Log.e(TAG, "第" +i+ "column, 值： "+cursor.getString(i));
				// }
				// break;
				// }
			}

			return latestImage;

		} else {
			LogUtils.loge(TAG,
					"get LatestPicture in Camera folder failed, SDcard is not Exist - ");
			return "";

		}
	}
	
	
	/**
	 * 获取SD卡中最新图片路径
	 * 
	 * @return
	 */
	public static Uri getLatestImageUri(Context context) {
		if (SDCardUtil.isSDCardExist()) {

			// 获取当前时间： 格式： 类似：1341298323
			long current_time = System.currentTimeMillis() / 1000; // 单位： 秒
			// 往前推1分钟差额
			long offset = 1 * 60;
			// 比较时间点
			long compare_time = current_time - offset;

			String latestImage = null;
			// 修改时间大于 某个时间点
			String selection = MediaStore.Images.Media.DATE_ADDED + " > ?";
			String[] selectionArgs = { "" + compare_time + "" };
			String[] items = { MediaStore.Images.Media._ID,
					MediaStore.Images.Media.DATA };

			ContentResolver cr = context.getContentResolver();
			Cursor cursor = cr.query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, items,
					selection, selectionArgs, MediaStore.Images.Media._ID
							+ " desc  limit 1");
			// Cursor cursor =
			// cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
			// null, null, null, MediaStore.Images.Media._ID +
			// " desc  limit 1");

			if (cursor != null && cursor.getCount() > 0) {
				LogUtils.logi(TAG, "查询系统数据库，满足条件的照片个数：" + cursor.getCount());
				cursor.moveToFirst();
				// 判断cursor的位置是不是在lastRow之后，这里api文档里的last应该理解为最近的一个，就是最顶部的一个。
				// 如果在最顶部一个后面，证明所取的row还不是最顶一个，还需要继续move，不过因为是降序排列，所以第一个就是最近一个
				// for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
				// .moveToNext()) {
				
				
				/******************************************************************
				 * 好久不碰，差点就忘了。。 这里cursor.getString(0)就是id值。。 则最后一张图片的Uri路径就是
				 * MediaStore.Images.Media.EXTERNAL_CONTENT_URI + "/" + id , 类似于下面这种：
				 * content://media/external/images/media/4
				 * 
				 * 经过debug查值：
				 * content://media/external/images/media  就是   MediaStore.Images.Media.EXTERNAL_CONTENT_URI
				 * 
				 * 后面这个cursor.getString(1) 是真实文件存放的路径 ， 类似于 下面这种：
				 *  /mnt/sdcard/dcim/Camera/2012-09-10_10-47-50_658.jpg
				 */
				latestImage = MediaStore.Images.Media.EXTERNAL_CONTENT_URI+"/"+cursor.getString(0);;
				Log.e(TAG, "latestImage Uri is ： " + latestImage);

				// for (int i = 0; i < 100; i++) {
				// Log.e(TAG, "第" +i+ "column, 值： "+cursor.getString(i));
				// }
				// break;
				// }
			}
			return Uri.parse(latestImage);
		} else {
			LogUtils.loge(TAG,
					"get LatestPicture in Camera folder failed, SDcard is not Exist - ");
			return null;

		}
	}
	
	/****************************** 将路径转换成 媒体 Uri ****************************************/
	public static  Uri pathToMediaUri(Context context,String path) {
		Uri mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		
		Cursor cursor = context.getContentResolver().query(mediaUri,
				null,
				MediaStore.Images.Media.DISPLAY_NAME + "=" + path.substring(path.lastIndexOf("/") + 1),
				null,
				null);
		cursor.moveToFirst();
		
		Uri uri = ContentUris.withAppendedId(mediaUri, cursor.getLong(0));
		return uri;
	}


}

package com.mama100.android.member.activities;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.mama100.android.member.activities.photo.cameracrop.CropUtil;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.PictureUtil;
import com.mama100.android.member.util.SDCardUtil;

/**
 * <p>
 * Description: PhotoActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-8-16 公用父类，拍照 2012-09-06 更新
 * 
 *         个人体会：
 *         在裁剪的时候，为了适配所有手机，只有自己定义一个裁剪界面。。为了获取高保真的图片，只有通过传入uri进intent方式，自定义保存照片路径
 *         。 然后在裁剪的时候，将这个uri带进裁剪界面，让界面通过uri获取图片。。
 * 
 *         data.getParcelableData("data"),这种方式获取图片，图片容易失真。。如果用这种data
 *         带进裁剪界面，获得的图片会很模糊。 所以，这里裁剪都是通过uri 方式获取图片。
 * 
 *         之前的教训： 调用*系统自定义的*裁剪ACTION,直接通过uri传一个地址进裁剪界面。。结果,金立手机会不能进入， Moto XT910
 *         也会出现裁剪后图片流不能得到。。
 *         解决方法，参考这里的cropPhoto(Bitmap),需要在建立一个新的File，用这个新的File的路径
 *         。。之前的路径传进去不能用（没时间深入研究原因）
 * 
 *         待解决的问题： 有些手机进入裁剪界面，明明竖着拍的，却容易打横。。 以后解决
 * 
 * 
 * 
 * 
 */

public abstract class PhotoActivity extends ThirdPartyLoginActivity {

	/****************************************************
	 * 其它变量
	 *******************************************************/
	private Bitmap photo = null;
	private AlertDialog dialog = null;
	public static final int TAKE_PHOTO = 109990; // 照相专用id

	/*************************************************
	 * 自己拍照时 用到的变量
	 *************************************************/
	private static final int CAMERA_WITH_DATA = 0x30;
	private static Uri takingPhotoStoreUri; // 自己拍照时，定义的存储路径

	/**************************************************
	 * 从相册中选择图片用到的变量
	 **************************************************/
	private static final int PHOTO_PICKED_WITH_DATA = 0x10;

	/***************************************************
	 * 裁剪图片用到的变量
	 ***************************************************/
	private static final int PHOTO_CROP_DATA = 0x11;
	private int rate = 1; // 长 宽比
	private int aspectX = 20000;
	private int aspectY = aspectX / rate;
	private int cropWidth = 300;
	private int cropHeight = cropWidth / rate;
	private static Uri cropImageStoreUri; // 裁剪照片时，定义的存储裁剪后照片的路径

	/***********************************************
	 * 用于内存释放的变量
	 **********************************************/
	private String[] items = null;
	private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
	private String TAG = this.getClass().getSimpleName();
	private String SOME_SPECIAL_DEVICE = "GN10000000000000000000000"; //现在用不到 2012-11-09 edwar

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * call before 子类的 doTakePhoto() method
	 * 
	 * @param width
	 *            the image width after crop
	 * @param height
	 *            the image height after crop
	 */
	protected void setImageCropParams(int width, int height) {
		//图片不能太大， 太大手机内存根本不够分配给图片的
		if(width>=400){
			cropWidth = 400;
		}else{
			cropWidth = 200;
		}
		
		if(height>=400){
			cropHeight = 400;
		}else{
			cropHeight = 200;
		}
	}

	/**
	 * 取得选择框
	 * 
	 * @return
	 */
	public AlertDialog getDialog() {
		if (dialog == null) {
			items = new String[] { "拍照", "从相册选择" };
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.select_dialog_item, items);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("选择照片");
			builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					if (item == 0) {
						takeYourPic();
					} else {
						pickPhotoFromGallery();
					}
				}
			});

			dialog = builder.create();
			dialog.setCanceledOnTouchOutside(true);
		}
		return dialog;
	}

	/***************************************************************
	 * 两种 获取图片的方法 事件定义 - 自己拍照 或者 从图库相册里选-START
	 ***************************************************************/

	// 自己拍照，传uri进入intent
	protected void takeYourPic() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		// 建立要拍照保存的图片路径
		//为金立手机单独打造 GIONEE -  GN105
		if (BasicApplication.getInstance().getModel().startsWith(SOME_SPECIAL_DEVICE )) {
			intent.putExtra("return-data", true);
		} else {
			// 设定false,不返回in-line
			// data.就不能通过data.getParcelableData("data")获取图片流数据
			intent.putExtra("return-data", false);
			if (AppConstants.TAKE_PHOTO_IS_USE_LOCAL_URI) {
				if (createTakingPhotoStoreFile() == null)
					return;
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
						takingPhotoStoreUri);
			}
		}
			startActivityForResult(intent, CAMERA_WITH_DATA);
	}

	/**
	 * 取得新拍照文件的保存路径
	 * 
	 * @return
	 */
	private File createTakingPhotoStoreFile() {
		LogUtils.loge(TAG, "before create taking photo" + new Timestamp(System.currentTimeMillis()));
		String path = SDCardUtil.getTempTakingPhotoPath();
		LogUtils.loge("SD卡文件路径", path);
		if (!path.startsWith(SDCardUtil.SDCARD_IS_UNMOUTED)) {
			LogUtils.loge(TAG, "进入创建拍照文件");
			try {
//				// 拍照的存储路径
				File f = new File(path);
				f.createNewFile();
				takingPhotoStoreUri = Uri.fromFile(f);
				LogUtils.loge(TAG, "after create taking photo" + new Timestamp(System.currentTimeMillis()));
				return f;
			} catch (IOException e) {
				Toast.makeText(this, "读取SD卡失败，无法创建临时文件,\n请检查SD卡",
						Toast.LENGTH_LONG).show();
				return null;
			}
		}else{
		return null;
		}
	}

	/**
	 * 取得裁剪文件的保存路径
	 * 
	 * @return
	 */
	private File createCropingPhotoStoreFile() {
		String path = SDCardUtil.getTempCropingPhotoPath();
		LogUtils.loge("SD卡文件路径", path);
		if (!path.startsWith(SDCardUtil.SDCARD_IS_UNMOUTED)) {
			LogUtils.loge(TAG, "进入创建裁剪文件");
			try {
				// 拍照的存储路径
				File f = new File(path);
				f.createNewFile();
				cropImageStoreUri = Uri.fromFile(f);
				BasicApplication.getInstance().setCropImageStoreUri(cropImageStoreUri);
				return f;
			} catch (IOException e) {
				Toast.makeText(this, "读取SD卡失败，无法创建临时文件,\n请检查SD卡",
						Toast.LENGTH_LONG).show();
				return null;
			}
		}else{
			return null;
		}
	}

	// 从相册中选择图片
	private void pickPhotoFromGallery() {

		/**
		 * 方法一，ACTION_PICK,缺点，无法获得图片uri
		 */
		// Intent intent = new Intent(Intent.ACTION_PICK, null);
		// /**
		// * 下面这句话，与其它方式写是一样的效果，如果：
		// * intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		// * intent.setType(""image/*");设置数据类型
		// * 如果朋友们要限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
		// * 这个地方小马有个疑问，希望高手解答下：就是这个数据URI与类型为什么要分两种形式来写呀？有什么区别？
		// */
		// intent.setDataAndType(
		// MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		// "image/*");
		// startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);

		/***
		 * 方法二，用ACTION_GET_CONTENT
		 */

		Intent intent = new Intent();
		/* Type设定为image */
		intent.setType("image/*");
		/**
		 * 下面这个太具体了，只能选择jpg
		 */
		// intent.setType("image/jpeg");

		/* 使用Intent.ACTION_GET_CONTENT这个Action */
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		/** 参考方法 **/

		// startActivityForResult(Intent.createChooser(
		// intent, "Complete action using"),
		// PHOTO_PICKED_WITH_DATA);

	}

	/***************************************************************
	 * 两种 获取图片的方法 事件定义 - 自己拍照 或者 从图库相册里选-END
	 ***************************************************************/

	/******************************************************************
	 * Activity返回结果 处理 -- START
	 ******************************************************************/

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 如果用户取消操作或者一些意外取消发生，直接返回
		if (resultCode != RESULT_OK)
			return;

		try {
			switch (requestCode) {

			case CAMERA_WITH_DATA://调用系统相机
				if (BasicApplication.getInstance().getModel().startsWith(SOME_SPECIAL_DEVICE)) {
					Bitmap bitmap = data.getParcelableExtra("data");
					bitmapList.add(bitmap);
					BasicApplication.getInstance().setFromGallery(false);
					cropPhoto(bitmap);
				} else {

		/****************************************
		 * added by edwar 2012-11-22
         * 从系统相册数据库(非SD卡路径)获取最近的一张图片的路径，传给裁剪Activity --START
         ****************************************/
					
					if (AppConstants.TAKE_PHOTO_IS_USE_LOCAL_URI) {
						/** 有些手机,到这里时,imagetCaptureUri会为空，只好再赋值 **/
						if (takingPhotoStoreUri == null)
							createTakingPhotoStoreFile();
					} else {
						// LogUtils.loge(TAG, " before seek Uri"+new
						// Timestamp(System.currentTimeMillis()));
						takingPhotoStoreUri = PictureUtil
								.getLatestImageUri(getApplicationContext());
						// LogUtils.loge(TAG, " after seek Uri"+new
						// Timestamp(System.currentTimeMillis()));
						LogUtils.loge(TAG, "latestImage uri- "
								+ takingPhotoStoreUri);
					}
					BasicApplication.getInstance().setFromGallery(false);
					cropPhoto(takingPhotoStoreUri);
					/****************************************
					 * added by edwar 2012-11-22
			         * 从系统相册数据库获取最近的一张图片的路径，传给裁剪Activity --END
			         ****************************************/			
					
				}
				break;

			case PHOTO_PICKED_WITH_DATA: // 调用媒体库

				if (BasicApplication.getInstance().getModel().startsWith(SOME_SPECIAL_DEVICE)) {
					Bitmap bitmap = data.getParcelableExtra("data");
					bitmapList.add(bitmap);
					cropPhoto(bitmap);
				} else {
				
				/*** 参考方法，没空研究优缺点 - START **/
				// try {
				// Uri uri = data.getData();
				// photo = BitmapFactory.decodeStream(cr.openInputStream(uri));
				// } catch (FileNotFoundException e) {
				// e.printStackTrace();
				// }

				/*** 参考方法，没空研究优缺点 - END **/

				// photo = data.getParcelableExtra("data"); //这里这个也是有值的，图片应用里支持
					
					/****************************************
					 * added by edwar 2012-11-22
			         * 从相册库返回的意图里获取图片的路径，传给裁剪Activity --START
			         ****************************************/		
				/** 获取所选图片的 uri **/
				Uri uri = data.getData();
				/****************************************
				 * added by edwar 2012-11-22
		         * 从相册库返回的意图里获取图片的路径，传给裁剪Activity --START
		         ****************************************/		

				try {
					if (uri != null) {
						BasicApplication.getInstance().setFromGallery(true);
						cropPhoto(uri);

						/*** 参考方法，没空研究优缺点 **/
						// ContentResolver resolver = this.getContentResolver();
						// photo = MediaStore.Images.Media.getBitmap(resolver,
						// uri); //显得到bitmap图片
						// photo =
						// BitmapFactory.decodeStream(cr.openInputStream(uri));

						/*** 参考方法,获取图片的 绝对路径 -START **/
						// String[] proj = {MediaStore.Images.Media.DATA};
						// //好像是android多媒体数据库的封装接口，具体的看Android文档
						//
						// Cursor cursor = managedQuery(uri, proj, null, null,
						// null);
						//
						// //按我个人理解 这个是获得用户选择的图片的索引值
						// int column_index =
						// cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
						// //将光标移至开头 ，这个很重要，不小心很容易引起越界
						// cursor.moveToFirst();
						// //最后根据索引值获取图片路径
						// String path = cursor.getString(column_index);
						/*** 参考方法,获取图片的 绝对路径 -END **/

					}

				} catch (Exception e) {
				}
				}

				break;
			case PHOTO_CROP_DATA: // 调用图片剪切程序返回数据
//				Uri uri2 = data.getData();
//				LogUtils.loge(TAG , "裁剪图片保存的路径uri  -  " + uri2);
//				photo = data.getParcelableExtra("data"); // @see CropImage，
//				BasicApplication.getInstance().setAvatarBitmap(photo);
				
//				photo = PictureUtil.getPictureFromSDcardByPath(SDCardUtil.getTempCropingPhotoPath());
//				
//				Matrix matrix = new Matrix();
//				matrix.reset();  
//				matrix.setRotate(RotateBitmap.getInitialRotation()); //XT910是0度，所以图片也不用旋转；而me811是顺90度，所以也旋转顺90度
//                 photo = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(),  
//                		 photo.getHeight(), matrix, true); 
//                 BasicApplication.getInstance().setAvatarBitmap(photo);
				setPhotoIntoAvatar();
				return;
			}
		} catch (Exception e) {
			LogUtils.loge("PhotoActivity", LogUtils.getStackTrace(e));
		}
	}

	/******************************************************************
	 * Activity返回结果 处理 -- END
	 ******************************************************************/

	/*********************************************************************
	 * 进入裁剪页面 -- START
	 *********************************************************************/

	/**
	 * 压缩图片并缓存到存储卡 暂时没有用到
	 * 
	 * @param photo
	 */
	private void cropPhoto(Bitmap photo) {
		
		if(createCropingPhotoStoreFile() == null)
			return;
		
		// 将选择的图片等比例压缩后缓存到存储卡根目录，并返回图片文件
		File f = CropUtil.makeTempFile(photo, "TEMPIMG.png");

		// File f = new File(takingPhotoStoreUri.getPath());

		// 调用CropImage类对图片进行剪切
		Intent intent = new Intent(
				this,
				com.mama100.android.member.activities.photo.cameracrop.CropImage.class);
		Bundle extras = new Bundle();
		extras.putBoolean("circleCrop", false);
		extras.putInt("aspectX", aspectX);
		extras.putInt("aspectY", aspectY);
		intent.putExtra("outputX", cropWidth);
		intent.putExtra("outputY", cropHeight);
		intent.setDataAndType(Uri.fromFile(f), "image/jpeg");
		intent.putExtras(extras);
		startActivityForResult(intent, PHOTO_CROP_DATA);
	}

	/**
	 * 压缩图片并缓存到存储卡
	 * 
	 * @param photo
	 */
	public void cropPhoto(Uri uri) {
		if(createCropingPhotoStoreFile() == null)
			return;
		
		Intent intent = new Intent(
				this,
				com.mama100.android.member.activities.photo.cameracrop.CropImage.class);
		intent.setDataAndType(uri, "image/jpeg");
		// 调用CropImage类对图片进行剪切
		Bundle extras = new Bundle();
		extras.putBoolean("circleCrop", false);
		extras.putInt("aspectX", aspectX);
		extras.putInt("aspectY", aspectY);
		intent.putExtra("outputX", cropWidth);
		intent.putExtra("outputY", cropHeight);

		intent.putExtra("return-data", true);// 设置可以通过
												// data.getParcelableExtra("data");获得图片流.@see
												// 该类Line307

		/********** 被裁剪的图片 额外将被保存何处的路径 **************/
//		intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageStoreUri);
//		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

		intent.putExtras(extras);
		startActivityForResult(intent, PHOTO_CROP_DATA);
	}

	/*********************************************************************
	 * 进入裁剪页面 -- END
	 *********************************************************************/

	// 留给子类的接口
	public abstract void setPhotoIntoAvatar();

	@Override
	protected void onDestroy() {
		
		items = null;

		// 可能子类被引用，咱不回收，交给子类自己回收
		// if(photo!=null)
		// photo.recycle();
		super.onDestroy();
		if(bitmapList!=null&&bitmapList.size()>0){
			for (int i = 0; i < bitmapList.size(); i++) {
				Bitmap bitmap = bitmapList.get(i);
				if(bitmap!=null&&!bitmap.isRecycled()){
					bitmap.recycle();
					bitmap = null;
				}
			}
		}
		finish();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case TAKE_PHOTO:
			getDialog().show();
			break;
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}


}
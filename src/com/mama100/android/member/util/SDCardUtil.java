package com.mama100.android.member.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;

import android.os.Environment;

import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;

/**
 * @author aihua.yan 2012-04-01 SD卡工具类，处理一切与sd卡相关的操作
 * @see isSDCardExist(), 查看是否存在sd卡。
 * 
 */
public class SDCardUtil {
	
	public static final String ROOT_FOLDER_PATH = SDCardUtil.getSdcardUrl()
			+ AppConstants.ROOT_FOLDER_PATH;

	// public static  String PICTURE_TEMP_PATH = SDCardUtil.getSdcardUrl()
	// + AppConstants.PICTURE_TEMP_PATH;
	// public static  String PICTURE_STORE_PATH = SDCardUtil.getSdcardUrl()
	// + AppConstants.PICTURE_STORE_PATH;
	// public static  String APK_FOLDER_PATH = SDCardUtil.getSdcardUrl()
	// + AppConstants.APK_FOLDER_PATH;

	// SD卡不存在
	public static  String SDCARD_IS_UNMOUTED = "sdcard is not exist";

	// 判断sdcard是否存在,true为存在，false为不存在
	public static boolean isSDCardExist() {
		String status = Environment.getExternalStorageState();
		boolean flag =  status.equals(
				android.os.Environment.MEDIA_MOUNTED);
		 LogUtils.logd("SDCardUtil", "检查SD卡结果： "+ ""+((flag)?"可读  ":"不可读  ")+"\n"+"SDCard Status - " + status+" "+"\n" 
					+ ".....................! \n"+ " current time is: "
					+ new Timestamp(System.currentTimeMillis()).toString());
		 return flag;

	}
	
	
	// 判断sdcard的状态，并告知用户
	public static String checkAndReturnSDCardStatus() {
		String status = Environment.getExternalStorageState();
		if(status!=null){
		//SD已经挂载,可以使用
		if (status.equals(android.os.Environment.MEDIA_MOUNTED)) {
			return "1";
		} else if (status.equals(android.os.Environment.MEDIA_REMOVED)) {
			//SD卡已经已经移除
			return "SD卡已经移除或不存在";

		} else if (status.equals(android.os.Environment.MEDIA_SHARED)) {
			//SD卡正在使用中
			return "SD卡正在使用中";

		} else if (status.equals(android.os.Environment.MEDIA_MOUNTED_READ_ONLY)) {
			//SD卡只能读，不能写
			return "SD卡只能读，不能写";
		} else {
			//SD卡的其它情况
			return "SD卡不能使用或不存在";
		}
		} else {
			//SD卡的其它情况
			return "SD卡不能使用或不存在";
		}
	}

	// 获取sdcard路径
	public static String getSdcardUrl() {
		File sdDir = null;
		if (isSDCardExist()) {
			sdDir = Environment.getExternalStorageDirectory().getAbsoluteFile();// 获取跟目录
//			LogUtils.loge("sd卡路径", sdDir.toString());
			return sdDir.toString();
		} else {
			return SDCARD_IS_UNMOUTED;
		}
	}

	/************************************************************************
	 * 获取各种路径 的 方法
	 ************************************************************************/

	// 获取本地临时数据的根路径，要及时删除里面内容
	public static String getRootPath() {
		if (isSDCardExist()) {
			// 分两部检测 /mama100_data/storepic/ 这两个目录的建立情况
			String root = ROOT_FOLDER_PATH;
			File dir = new File(root);
			if (!dir.exists()) {
				dir.mkdir();
			}
			return root;
		} else {
			return SDCARD_IS_UNMOUTED;
		}
	}

	// 获取本地临时存放照片的路径，要及时删除里面内容
	public static String getPictureTempPath() {
		if (isSDCardExist()) {
			
			//不知道为什么在金立手机中多次调用BasicApplication.getInstance().getTempPicPath()
			//和BasicApplication.getInstance().getUserFolderPath()方法的时侯获取不到值，导致程序在运行的时候报错
			//所以此处采用如下方式代替，经测试金立手机可以正常运行。
			String mid = BasicApplication.getInstance().getMid();
			String userFolderPath = ROOT_FOLDER_PATH + mid;
			String tempPicPath = userFolderPath + AppConstants.PICTURE_TEMP_PATH;
			// 分两部检测 /mama100_data/storepic/ 这两个目录的建立情况
//			String saveDir[] = { ROOT_FOLDER_PATH, BasicApplication.getInstance().getUserFolderPath(),
//					BasicApplication.getInstance().getTempPicPath() };
			String saveDir[] = { ROOT_FOLDER_PATH, userFolderPath,tempPicPath};
			for (int i = 0; i < saveDir.length; i++) {
				File dir = new File(saveDir[i]);
				if (!dir.exists()) {
					dir.mkdir();
				}
			}
			// return saveDir[1];
			int sum = saveDir.length;
//			LogUtils.loge("sd卡temppath - ", saveDir[sum - 1]);
			return saveDir[sum - 1];
		} else {
			return SDCARD_IS_UNMOUTED;
		}
	}
	
	
	
	// 获取本地自己拍照时照片的路径，要及时删除里面内容
	public static String getTempTakingPhotoPath() {
		return getPictureTempPath() + AppConstants.TEMP_BIG_PICTURE_NAME;
	}
	
	// 获取本地裁剪照片后保存照片的路径，要及时删除里面内容
	public static String getTempCropingPhotoPath() {
		return getPictureTempPath() + AppConstants.TEMP_CROP_PICTURE_NAME;
	}
	
	
	
	
	

	// 获取本地apk存放路径
	public static String getApkPath() {
		if (isSDCardExist()) {
			// 分两部检测 /mama100_data/storepic/ 这两个目录的建立情况
			String saveDir = ROOT_FOLDER_PATH;
				File dir = new File(saveDir);
				if (!StringUtils.isBlank(dir.getPath())&&!dir.exists()) {
					dir.mkdir();
			}
			LogUtils.loge("getApkPath", " - "+saveDir);
			return saveDir;
		} else {
			return SDCARD_IS_UNMOUTED;
		}
	}

	// 获取本地被提交的用户头像的路径，要及时删除里面内容
	public static String getPictureStorePath() {
		if (isSDCardExist()) {
			// 分两部检测 /mama100_data/storepic/ 这两个目录的建立情况
			String saveDir[] = { ROOT_FOLDER_PATH, BasicApplication.getInstance().getUserFolderPath(),
					BasicApplication.getInstance().getTempStorePath() };
			for (int i = 0; i < saveDir.length; i++) {
				File dir = new File(saveDir[i]);
				if (!dir.exists()) {
					dir.mkdir();
				}
			}
			// return saveDir[1];
			int sum = saveDir.length;
//			LogUtils.loge("sd卡temp store path - ", saveDir[sum - 1]);
			return saveDir[sum - 1];
		} else {
			return SDCARD_IS_UNMOUTED;
		}
	}

	/******************************************************************************
	 * 删除用到的 方法
	 ******************************************************************************/

	/**
	 * 删除SDCARD上的照片
	 * 
	 * @param fName
	 *            文件名
	 * @return
	 */
	public static boolean deletePicture(String fName) {
		if (isSDCardExist()) {
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
			return false;
		}
	}

	/**
	 * 删除SDCARD上的文件夹
	 * 
	 * @param fName
	 *            文件夹名
	 * @return
	 */
	public static boolean deleteFolder(String folderName) {
		if (isSDCardExist()) {
			return FileUtil.DeleteFolder(folderName);
			// File f = new File(folderName);
			// if (f.exists()) {
			// if (f.delete()) {
			// return true;
			// } else {
			// return false;
			// }
			// } else {
			// return false;
			// }
		} else {
			return false;
		}
	}

	/*************************************************************
	 * 拷贝一个文件 从afile 到 b file
	 *************************************************************/

	/**
	 * 复制文件a 到文件 b
	 */
	public static boolean CopyFile(String afile, String bfolder,
			String newFileName) {
		if (isSDCardExist()) {
			File srcFile = new File(afile);
			File destDir = new File(bfolder);
			long copySizes = 0;
			if (!srcFile.exists()) {
				System.out.println("源文件不存在");
				copySizes = -1;
				return false;
			} else if (!destDir.exists()) {
				System.out.println("目标目录不存在");
				copySizes = -1;
				return false;
			} else if (newFileName == null) {
				System.out.println("文件名为null");
				copySizes = -1;
				return false;
			} else {
				try {
					FileChannel fcin = new FileInputStream(srcFile)
							.getChannel();
					FileChannel fcout = new FileOutputStream(new File(destDir,
							newFileName)).getChannel();
					ByteBuffer buff = ByteBuffer.allocate(1024);
					int b = 0, i = 0;
					// long t1 = System.currentTimeMillis();
					/*
					 * while(fcin.read(buff) != -1){ buff.flip();
					 * fcout.write(buff); buff.clear(); i++; }
					 */
					long size = fcin.size();
					fcin.transferTo(0, fcin.size(), fcout);
					// fcout.transferFrom(fcin,0,fcin.size());
					// 一定要分清哪个文件有数据，那个文件没有数据，数据只能从有数据的流向
					// 没有数据的文件
					// long t2 = System.currentTimeMillis();
					fcin.close();
					fcout.close();
					copySizes = size;
					// long t = t2-t1;
					// System.out.println("复制了" + i + "个字节\n" + "时间" + t);
					// System.out.println("复制了" + size + "个字节\n" + "时间" + t);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					return false;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;

		} else {
			return false;
		}
	}
	
	/****************************************
         * 未登录情况下： 是没有Mid的，所以大家共用一个路径
         ****************************************/
	
	// 获取本地裁剪照片后保存照片的路径，要及时删除里面内容
		public static String getTempCropingPhotoPathForUnlogin() {
			return getPictureTempPathForUnlogin() + AppConstants.TEMP_CROP_PICTURE_NAME;
		}
		
		// 获取本地临时存放照片的路径，要及时删除里面内容
		public static String getPictureTempPathForUnlogin() {
			if (isSDCardExist()) {
				
				//不知道为什么在金立手机中多次调用BasicApplication.getInstance().getTempPicPath()
				//和BasicApplication.getInstance().getUserFolderPath()方法的时侯获取不到值，导致程序在运行的时候报错
				//所以此处采用如下方式代替，经测试金立手机可以正常运行。
				String mid = BasicApplication.getInstance().getMid();
//				String userFolderPath = ROOT_FOLDER_PATH + mid;
				String userFolderPath = ROOT_FOLDER_PATH;
				String tempPicPath = userFolderPath + AppConstants.PICTURE_TEMP_PATH;
				// 分两部检测 /mama100_data/storepic/ 这两个目录的建立情况
//				String saveDir[] = { ROOT_FOLDER_PATH, BasicApplication.getInstance().getUserFolderPath(),
//						BasicApplication.getInstance().getTempPicPath() };
				String saveDir[] = { ROOT_FOLDER_PATH, userFolderPath,tempPicPath};
				for (int i = 0; i < saveDir.length; i++) {
					File dir = new File(saveDir[i]);
					if (!dir.exists()) {
						dir.mkdir();
					}
				}
				// return saveDir[1];
				int sum = saveDir.length;
//				LogUtils.loge("sd卡temppath - ", saveDir[sum - 1]);
				return saveDir[sum - 1];
			} else {
				return SDCARD_IS_UNMOUTED;
			}
		}
		
		

}

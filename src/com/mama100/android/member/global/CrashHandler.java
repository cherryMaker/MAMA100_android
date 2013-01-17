package com.mama100.android.member.global;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import com.mama100.android.member.util.FileUtil;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.SDCardUtil;
import com.mama100.android.member.util.StorageUtils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 
 * @author liuhe688 csdn 作者  
 * 
 * 致敬：原作者：http://blog.csdn.net/liuhe688/article/details/6584143#comments
 * 
 */
public class CrashHandler implements UncaughtExceptionHandler {
	
	public static final String TAG = "CrashHandler";
	
	//系统默认的UncaughtException处理类 
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	//CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	//程序的Context对象
	private Context mContext;
	
	
	//用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();
	
	/************************************************************
	 * http://blog.csdn.net/liangguohuan/article/details/6022419 改进的
	 ************************************************************/
	/** 使用Properties来保存设备的信息和错误堆栈信息*/  
    private Properties mDeviceCrashInfo = new Properties();
    
	

	//用于格式化日期,作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	
	 /** 错误报告文件的扩展名 */  
    private static final String CRASH_REPORTER_EXTENSION = ".txt";  

	/** 保证只有一个CrashHandler实例 */
	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		return INSTANCE;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public void init(Context context) {
		mContext = context;
		//获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		//设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			//如果用户没有处理则让系统默认的异常处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				Log.e(TAG, "error : ", e);
			}
			//退出程序
			android.os.Process.killProcess(android.os.Process.myPid());//杀掉进程
			System.exit(1);//连虚拟机都要杀掉
		}
	}

	/**
	 * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
	 * 
	 * @param ex
	 * @return true:如果处理了该异常信息;否则返回false.
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		//使用Toast来显示异常信息
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(mContext, "很抱歉,程序出现异常,即将退出.", Toast.LENGTH_LONG).show();
				Looper.loop();
			}
		}.start();
		//收集设备参数信息 
		collectDeviceInfo(mContext);
		//保存日志文件 
		saveCrashInfo2File(ex);
		//清理
		BasicApplication.getInstance().clearALLFolderPath();
		BasicApplication.getInstance().clearMemory();
		//清空 sd卡里拍照留下的图片
		SDCardUtil.deleteFolder(SDCardUtil.getPictureTempPath());
		BasicApplication.getInstance().clearNormalInfo();
		BackStackManager.getInstance().closeAllActivity();
		//退出程序
		android.os.Process.killProcess(android.os.Process.myPid());//杀掉进程
		return true;
	}
	
	/**
	 * 收集设备参数信息
	 * @param ctx
	 */
	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
				
				/**************************************************
				 *  mDeviceCrashInfo.put(VERSION_NAME,  
                        pi.versionName == null ? "not set" : pi.versionName);  
                mDeviceCrashInfo.put(VERSION_CODE, pi.versionCode);  
				 **************************************************/
				
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				/*******************************************************
				 *  mDeviceCrashInfo.put(field.getName(), field.get(null));  
				 *******************************************************/
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	/**
	 * 保存错误信息到文件中
	 * 
	 * @param ex
	 * @return	返回文件名称,便于将文件传送到服务器
	 */
	private String saveCrashInfo2File(Throwable ex) {
		
		StringBuffer sb = new StringBuffer();
		long timestamp = System.currentTimeMillis();
		String time = formatter.format(new Date());
		long current = System.currentTimeMillis();
		StorageUtils.setShareValue(mContext, "logcreatetime", String.valueOf(current));
		
		sb.append("\n");
		sb.append("crash time -  " + time );
		sb.append("\n");
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		LogUtils.loge(TAG, result+"");
		/************************************
		 *  mDeviceCrashInfo.put(STACK_TRACE, result); 
		 ************************************/
		
		try {
//			String fileName = "crash-" + time + "-" + timestamp + CRASH_REPORTER_EXTENSION;
			String fileName = "log" + CRASH_REPORTER_EXTENSION;
			String path = "/sdcard/mama100_data/";
			String file = path + fileName;
			if (SDCardUtil.isSDCardExist()) {
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				/******** added by edwar 2012-09-09 start **************/
//				FileUtil.writeFile(dir, sb.toString(), true, "UTF-8");
				/******** added by edwar 2012-09-09 end **************/
				
				FileOutputStream fos = new FileOutputStream(file,true);
				fos.write(sb.toString().getBytes());
				/**************************************
				 * mDeviceCrashInfo.store(fos, "");  
                 				fos.flush();
				 **************************************/
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}
		return null;
	}
	
	
	 /** 
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告 
     */  
    public void sendPreviousReportsToServer() {  
        sendCrashReportsToServer(mContext);  
    }  
  
    /** 
     * 把错误报告发送给服务器,包含新产生的和以前没发送的. 
     *  
     * @param ctx 
     */  
    private void sendCrashReportsToServer(Context ctx) {  
        String[] crFiles = getCrashReportFiles(ctx);  
        if (crFiles != null && crFiles.length > 0) {  
            TreeSet<String> sortedFiles = new TreeSet<String>();  
            sortedFiles.addAll(Arrays.asList(crFiles));  
  
            for (String fileName : sortedFiles) {  
                File cr = new File(ctx.getFilesDir(), fileName);  
                postReport(cr);  
                cr.delete();// 删除已发送的报告  
            }  
        }  
    }  
  
    private void postReport(File file) {  
        // TODO 使用HTTP Post 发送错误报告到服务器  
        // 这里不再详述,开发者可以根据OPhoneSDN上的其他网络操作  
        // 教程来提交错误报告  
    }  
  
    /** 
     * 获取错误报告文件名 
     * @param ctx 
     * @return 
     */  
    private String[] getCrashReportFiles(Context ctx) {  
        File filesDir = ctx.getFilesDir();  
        FilenameFilter filter = new FilenameFilter() {  
            public boolean accept(File dir, String name) {  
                return name.endsWith(CRASH_REPORTER_EXTENSION);  
            }  
        };  
        return filesDir.list(filter);  
    }  
	
}

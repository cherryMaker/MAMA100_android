package com.mama100.android.member.zxing;

import java.io.IOException;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.regpoint.RegPointHelpActivity;
import com.mama100.android.member.activities.regpoint.RegPointHomeActivity;
import com.mama100.android.member.activities.regpoint.RegPointInputActivity;
import com.mama100.android.member.activities.regpoint.RegPointProductActivity;
import com.mama100.android.member.businesslayer.PointProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.point.DiyPointProductReq;
import com.mama100.android.member.domain.point.DiyPointProductRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.MobValidateUtils;
import com.mama100.android.member.zxing.camera.CameraManager;
import com.mama100.android.member.zxing.decode.CaptureActivityHandler;
import com.mama100.android.member.zxing.decode.InactivityTimer;
import com.mama100.android.member.zxing.view.ViewfinderView;

public class CaptureActivity extends Activity implements Callback {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private Toast toast;
	private String code;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.barcode_scanner_page);
		// ��ʼ�� CameraManager
		CameraManager.init(getApplication());

		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);
		
		final Button openandcloseflashlightBtn = (Button)findViewById(R.id.openandcloseflashlight);
		openandcloseflashlightBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(CameraManager.get().isFlashLightOpen()){
					CameraManager.get().closeFlashLight();
					openandcloseflashlightBtn.setBackgroundResource(R.drawable.selector_regpoint_scan_open);
				}else{
					CameraManager.get().openFlashLight();
					openandcloseflashlightBtn.setBackgroundResource(R.drawable.selector_regpoint_scan_close);
				}
			}
		});
		
		
		findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		
		
		findViewById(R.id.help).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(CaptureActivity.this,RegPointHelpActivity.class);
				intent.putExtra("isOperationByScan", true);
				intent.putExtra("isOperationByShop", false);
				startActivity(intent);
			}
		});
		
		
		findViewById(R.id.regpointInput).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(CaptureActivity.this,RegPointInputActivity.class));
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		LogUtils.loge("CaptureActivity", "onPause");
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		
		Intent intent = new Intent(this,RegPointHomeActivity.class);
		startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		LogUtils.loge("CaptureActivity", "onDestory");
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
			//设置提示显示的位置
			Rect frame = CameraManager.get().getFramingRect();
			RelativeLayout.LayoutParams  params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			params.setMargins(frame.left,frame.bottom+10,frame.left, 0);
			findViewById(R.id.tips).setLayoutParams(params);
		} catch (Exception ioe) {
			makeText("您的设备有问题,无法使用扫描功能,请点击手动输入");
			return;
		} 
		
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats,
					characterSet);
		}
		
		
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();

		// commentted by edwar 2012-09-04
		// viewfinderView.drawResultBitmap(barcode);
		// playBeepSoundAndVibrate();
		code = obj.getText();
		//code = "1021174316178620";
		
		if(!MobValidateUtils.checkInputAntiFake(code)){
			makeText("防伪码格式不正确,请重新扫描");
			//无论失败或者是成功，最后都需要关闭当前扫描页面
			finish();
			
			startActivity(new Intent(this, CaptureActivity.class));
		}else{
			//提交服务器，开始验证
			// 构建request对象,将code设入req中
			DiyPointProductReq req = new DiyPointProductReq();
			req.setSecurity(code);
			
			// 执行异步线程验证防伪码是否存在
			CustomAsyncTask task = new CustomAsyncTask(this);
			task.displayProgressDialog(R.string.regpoint_input_verify);
			task.execute(req);
		}
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	
	/**
	 * 这样实现主要是为了防止用户一直点击一直弹出新的toast
	 * @param msg
	 */
	protected void makeText(String msg) {
		if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();   
	}
	
	
	
	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			if(request instanceof DiyPointProductReq){
				return PointProvider.getInstance(CaptureActivity.this).diyPointVerifyByScan((DiyPointProductReq)request);
			}
			return null;
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			LogUtils.logd(getClass(), "SPECIAL_CODE1 = " +response.getCode() );
			//验证成功或者失败都跳转到result页面
			//if(SPECIAL_CODE1.equals(response.getCode())){
			if("100".equals(response.getCode())){
				DiyPointProductRes res = (DiyPointProductRes)response;
				
				Intent intent = new Intent(CaptureActivity.this,RegPointProductActivity.class);
				intent.putExtra("productId", res.getProductId());
				intent.putExtra("productSerial", res.getSerial());
				intent.putExtra("productPoint", res.getPoint());
				intent.putExtra("productImgUrl", res.getProductImgUrl());
				intent.putExtra("productName", res.getProductName());
				intent.putExtra("productCode", code);
				intent.putExtra("isOperationByScan",true);
				
				startActivity(intent);
				
				//关闭当前页面
				finish();
			}else{
				makeText(response.getDesc());
				//关闭当前页面
				finish();
				
				startActivity(new Intent(CaptureActivity.this, CaptureActivity.class));
			}
		}
	}
}
/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// originally from AOSP Camera code. modified to only do cropping and return 
// data to caller. Removed saving to file, MediaManager, unneeded options, etc.
package com.mama100.android.member.activities.photo.cameracrop;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.PictureUtil;
import com.mama100.android.member.util.SDCardUtil;

/**
 * The activity can crop specific region of interest from an image.
 */
public class CropImage extends MonitoredActivity {

    // private static final String TAG = "CropImage";

    private static final boolean RECYCLE_INPUT = true;

    private int mAspectX, mAspectY;
    private final Handler mHandler = new Handler();

    // These options specifiy the output image size and whether we should
    // scale the output to fit it (or just crop it).
    private int mOutputX, mOutputY;
    private boolean mScale;
    private boolean mScaleUp = true;
    private boolean mCircleCrop = false;

    boolean mSaving; // Whether the "save" button is already clicked.

    private CropImageView mImageView;

    private Bitmap mBitmap;
    HighlightView mCrop;
    
    /****************************************************************
     * 为了裁剪的时候，传进来的uri，将裁剪的区域保存进该uri 用到的变量
     ****************************************************************/
 // These are various options can be specified in the intent.
    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG; 
    private Uri mSaveUri = null;
    private ContentResolver mContentResolver;

	private int IMAGE_SAMPLE_SIZE = 4 ;  //缩小为原图1/16,不做这个操作，常常爆出VM can't allocate  15M 空间给你的图片
    
    /**************************************************************
     * 用于内存回收的变量
     *************************************************************/
    public List<Bitmap> bitmapList = new ArrayList<Bitmap>();

	private int FIRST_CLASS_SIZE = 200000; //200k
	private int SECOND_CLASS_SIZE = 1000000; //1000k
	private int THIRD_CLASS_SIZE = 2000000; //2000k
	private int FOURTH_CLASS_SIZE = 10000000; //10000k - 10M
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cropimage);

        mImageView = (CropImageView) findViewById(R.id.image);
        mImageView.mContext = this;
        
        /** add  **/
        mContentResolver = getContentResolver();
        /** end **/

        // MenuHelper.showStorageToast(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mBitmap = (Bitmap) extras.getParcelable("data");
            mAspectX = extras.getInt("aspectX");
            mAspectY = extras.getInt("aspectY");
            mOutputX = extras.getInt("outputX");
            mOutputY = extras.getInt("outputY");
            mScale = extras.getBoolean("scale", true);
            mScaleUp = extras.getBoolean("scaleUpIfNeeded", true);
        }

        
        /****************************************
         * added by edwar 2012-11-22
         * 根据传进来的相册路径从相册数据库获取图片大小，根据大小设定压缩比，形成压缩的图片 -START
         ****************************************/
        if (mBitmap == null) {
            InputStream is = null;
            try {
                Uri target = intent.getData();//传进来的 bitmap 保存路径。
                ContentResolver cr = getContentResolver();
                is = cr.openInputStream(target);
                int estimatedSize = is.available();
                
                LogUtils.loge(TAG, "Estimated Size - " + estimatedSize);
//                mBitmap = BitmapFactory.decodeStream(is);
            	BitmapFactory.Options options = new BitmapFactory.Options();
//            	options.inJustDecodeBounds = true;// 作用：bitmap不加载到内存中 。。 这个如果不注释，那么界面看不到图了
            	
            	IMAGE_SAMPLE_SIZE = buildDownsampleValueBySize(estimatedSize);
            	LogUtils.loge(TAG, "downsample size - " + IMAGE_SAMPLE_SIZE);
            	options.inSampleSize = IMAGE_SAMPLE_SIZE ;
                mBitmap = BitmapFactory.decodeStream(is, null, options);
                
                
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }

        if (mBitmap == null) {
            finish();
            return;
        }
        
        /****************************************
         * added by edwar 2012-11-22
         * 根据传进来的相册路径从相册数据库获取图片大小，根据大小设定压缩比，形成压缩的图片 -END
         ****************************************/

        // Make UI fullscreen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.discard).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
        
//        findViewById(R.id.rotate).setOnClickListener(
//        		new View.OnClickListener() {
//        			public void onClick(View v) {
//        				doLeftRotate();
//        			}
//        		});

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onSaveClicked();
            }
        });

        startFaceDetection(); //虽然我们这里不需要面相识别技术，但是代码不能被注释，否则界面无图像
    }


    
    protected void doLeftRotate() {
    	
    	 float angle = 90.0f; 
    	    Matrix matrix = new Matrix();
    	  matrix.reset(); 
          matrix.setRotate(angle); //设置旋转  
           
          //按照matrix的旋转构建新的Bitmap  
          mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
          startFaceDetection();
		
	}



	//根据图片的大小 生成适当的缩小值
	private int buildDownsampleValueBySize(int value) {
		//首先获取绝对值，杜绝负值。
		int estimatedSize = Math.abs(value);
		
		// 范围：(0,200k]
		if ((estimatedSize > 0) && (estimatedSize <= FIRST_CLASS_SIZE)) {
			return 1;
		}
		// (200k - 1000k]
		else if ((estimatedSize > FIRST_CLASS_SIZE)
				&& (estimatedSize <= SECOND_CLASS_SIZE)) {
			return 2;
		}
		// (1000k - 2000k)
		else if ((estimatedSize > SECOND_CLASS_SIZE)
				&& (estimatedSize <= THIRD_CLASS_SIZE)) {
			return 4;
		}
		// (2000k - 10M)
		else if ((estimatedSize > THIRD_CLASS_SIZE)
				&& (estimatedSize <= FOURTH_CLASS_SIZE)) {
			return 6;
		}
		//(10M - &)
		else if(estimatedSize>FOURTH_CLASS_SIZE){
			return 10;
		}
		else {
			return 2;//默认2
		}

	}


	//面相识别
    private void startFaceDetection() {
        if (isFinishing()) {
            return;
        }
        

        //我们的图片为什么进去就是横着的。。 这里就是图片设置的操作
        mImageView.setImageBitmapResetBase(mBitmap, true);

        startBackgroundJob(this, null, "正在处理...", new Runnable() {
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);
                final Bitmap b = mBitmap;
                mHandler.post(new Runnable() {
                    public void run() {
                        if (b != mBitmap && b != null) {
                            mImageView.setImageBitmapResetBase(b, true);
                            mBitmap.recycle();
                            mBitmap = b;
                        }
                        if (mImageView.getScale() == 1F) {
                            mImageView.center(true, true);
                        }
                        latch.countDown();
                    }
                });
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                mRunFaceDetection.run();
            }
        }, mHandler);
    }

    private static class BackgroundJob extends
            MonitoredActivity.LifeCycleAdapter implements Runnable {

        private final MonitoredActivity mActivity;
        private final ProgressDialog mDialog;
        private final Runnable mJob;
        private final Handler mHandler;
        private final Runnable mCleanupRunner = new Runnable() {
            public void run() {
                mActivity.removeLifeCycleListener(BackgroundJob.this);
                if (mDialog.getWindow() != null)
                    mDialog.dismiss();
            }
        };

        public BackgroundJob(MonitoredActivity activity, Runnable job,
                ProgressDialog dialog, Handler handler) {
            mActivity = activity;
            mDialog = dialog;
            mJob = job;
            mActivity.addLifeCycleListener(this);
            mHandler = handler;
        }

        public void run() {
            try {
                mJob.run();
            } finally {
                mHandler.post(mCleanupRunner);
            }
        }

        @Override
        public void onActivityDestroyed(MonitoredActivity activity) {
            // We get here only when the onDestroyed being called before
            // the mCleanupRunner. So, run it now and remove it from the queue
            mCleanupRunner.run();
            mHandler.removeCallbacks(mCleanupRunner);
        }

        @Override
        public void onActivityStopped(MonitoredActivity activity) {
            mDialog.hide();
        }

        @Override
        public void onActivityStarted(MonitoredActivity activity) {
            mDialog.show();
        }
    }

    private static void startBackgroundJob(MonitoredActivity activity,
            String title, String message, Runnable job, Handler handler) {
        // Make the progress dialog uncancelable, so that we can gurantee
        // the thread will be done before the activity getting destroyed.
        ProgressDialog dialog = ProgressDialog.show(activity, title, message,
                true, false);
        new Thread(new BackgroundJob(activity, job, dialog, handler)).start();
    }

    
    //面相识别
    Runnable mRunFaceDetection = new Runnable() {
        float mScale = 1F;
        Matrix mImageMatrix;

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {
            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // make the default size about 4/5 of the width or height
            int cropWidth = Math.min(width, height) * 4 / 5;
            int cropHeight = cropWidth;

            if (mAspectX != 0 && mAspectY != 0) {
                if (mAspectX > mAspectY) {
                    cropHeight = cropWidth * mAspectY / mAspectX;
                } else {
                    cropWidth = cropHeight * mAspectX / mAspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop,
                    mAspectX != 0 && mAspectY != 0);
            mImageView.add(hv);
        }

        public void run() {
            mImageMatrix = mImageView.getImageMatrix();

            mScale = 1.0F / mScale;
            mHandler.post(new Runnable() {
                public void run() {
                    makeDefault();

                    mImageView.invalidate();
                    if (mImageView.HighlightViews.size() == 1) {
                        mCrop = mImageView.HighlightViews.get(0);
                        mCrop.setFocus(true);
                    }
                }
            });
        }
    };

	private String TAG = "CropImage";

    private void onSaveClicked() {
        // TODO this code needs to change to use the decode/crop/encode single
        // step api so that we don't require that the whole (possibly large)
        // bitmap doesn't have to be read into memory
        if (mCrop == null) {
            return;
        }

        if (mSaving)
            return;
        mSaving = true;

        Bitmap croppedImage;

        // If the output is required to a specific size, create an new image
        // with the cropped image in the center and the extra space filled.
        if (mOutputX != 0 && mOutputY != 0 && !mScale) {
            // Don't scale the image but instead fill it so it's the
            // required dimension
            croppedImage = Bitmap.createBitmap(mOutputX, mOutputY,
                    Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(croppedImage);

            Rect srcRect = mCrop.getCropRect();
            Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

            int dx = (srcRect.width() - dstRect.width()) / 2;
            int dy = (srcRect.height() - dstRect.height()) / 2;

            // If the srcRect is too big, use the center part of it.
            srcRect.inset(Math.max(0, dx), Math.max(0, dy));

            // If the dstRect is too big, use the center part of it.
            dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

            // Draw the cropped bitmap in the center
            canvas.drawBitmap(mBitmap, srcRect, dstRect, null);

            // Release bitmap memory as soon as possible
            mImageView.clear();
            mBitmap.recycle();
        } else {
            Rect r = mCrop.getCropRect();

            int width = r.width();
            int height = r.height();

            //added by liyang  
            //增加原因 排除用户选择异常照片时系统报错的可能
            if(width<=0 || height<=0){
            	Toast.makeText(getApplicationContext(), "您选择的照片出现异常,无法分享，请重新选择其他照片", Toast.LENGTH_LONG).show();
            	finish();
            	return;
            }
            // If we are circle cropping, we want alpha channel, which is the
            // third param here.
            croppedImage = Bitmap.createBitmap(width, height,
                    Bitmap.Config.RGB_565);

            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(mBitmap, r, dstRect, null);

            // Release bitmap memory as soon as possible
            mImageView.clear();
            mBitmap.recycle();

            // If the required dimension is specified, scale the image.
            if (mOutputX != 0 && mOutputY != 0 && mScale) {
                croppedImage = transform(new Matrix(), croppedImage, mOutputX,
                        mOutputY, mScaleUp, RECYCLE_INPUT);
            }
        }

        bitmapList.add(croppedImage);
        
        mImageView.setImageBitmapResetBase(croppedImage, true);
        mImageView.center(true, true);
        mImageView.HighlightViews.clear();

        
        
        //为了解决部分手机 在主界面onActivityResult不能获取 setResult返回响应
        
        String filepath = SDCardUtil.getTempCropingPhotoPath() ;
    	try {
    		
    		File f = new File(filepath);
    		f.createNewFile();
    		//只有从拍照进来的，才要旋转
    		if(!BasicApplication.getInstance().isFromGallery()){
    		Matrix matrix = new Matrix();
			matrix.reset();  
			matrix.setRotate(RotateBitmap.getInitialRotation()); //XT910是0度，所以图片也不用旋转；而me811是顺90度，所以也旋转顺90度
			croppedImage = Bitmap.createBitmap(croppedImage, 0, 0, croppedImage.getWidth(),  
					croppedImage.getHeight(), matrix, true); 
    		}
			PictureUtil.storePicture(filepath, croppedImage, AppConstants.BITMAP_COMPRESS);
			
			mSaveUri = Uri.fromFile(f);
			BasicApplication.getInstance().setCropImageStoreUri(mSaveUri);
		} catch (IOException e) {
			LogUtils.loge("CropImage", LogUtils.getStackTrace(e));
		}
    	
        
        Bundle extras = new Bundle();
//        extras.putParcelable("data", croppedImage);
        
        
//        croppedImage = null;
//        ComponentName a = getCallingActivity();
//        LogUtils.loge(TAG, "getCallingActivity - " + a.getClassName());
        //XT910 在homepageActivity界面，不能执行onActivityResult
//        setResult(RESULT_OK
//        		, getIntent()
//        		.setAction("inline-data")
//        		.putExtras(extras)
//                );
        
        
        
        setResult(RESULT_OK
//        		, new Intent(mSaveUri.toString()).putExtras(extras)
        		);
//        
//        setResult(RESULT_OK, (new Intent()).setAction("inline-data").putExtras(
//        		extras));
        finish();
    }

    private static Bitmap transform(Matrix scaler, Bitmap source,
            int targetWidth, int targetHeight, boolean scaleUp, boolean recycle) {
        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
             * In this case the bitmap is smaller, at least in one dimension,
             * than the target. Transform it by placing as much of the image as
             * possible into the target and leaving the top/bottom or left/right
             * (or both) black.
             */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
                    Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
                    + Math.min(targetWidth, source.getWidth()), deltaYHalf
                    + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight
                    - dstY);
            c.drawBitmap(source, src, dst, null);
            if (recycle) {
                source.recycle();
            }
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source
                    .getHeight(), scaler, true);
        } else {
            b1 = source;
        }

        if (recycle && b1 != source) {
            source.recycle();
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth,
                targetHeight);
        if (b2 != b1) {
            if (recycle || b1 != source) {
                b1.recycle();
            }
        }

        return b2;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	StatService.onResume(this);//百度统计
//		MobileProbe.onResume(this);//CNZZ统计
    }

    @Override
    protected void onPause() {
        super.onPause();
    	StatService.onPause(this);//百度统计
//		MobileProbe.onPause(this);//CNZZ统计
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBitmap!=null&&!mBitmap.isRecycled()){
        	mBitmap.recycle();
        	mBitmap= null;
        }
        
        if( mImageView!=null){
        	 mImageView.clear();
        	 mImageView = null;
        }
        
        if(mCrop!=null){
        	mCrop= null;
        }
        
        if(bitmapList!=null&&bitmapList.size()>0){
        	for (Bitmap bitmap : bitmapList) {
        		if(bitmap!=null&&!bitmap.isRecycled()){
        			bitmap.recycle();
        			bitmap = null;
        		}
			}
        	bitmapList.clear();
        	bitmapList = null;
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    	LogUtils.loge("CropImage", "onConfigurationChanged");
    }

}



class CropImageView extends ImageViewTouchBase {
    ArrayList<HighlightView> HighlightViews = new ArrayList<HighlightView>();
    HighlightView mMotionHighlightView = null;
    float mLastX, mLastY;
    int mMotionEdge;

    Context mContext;
    
    
    public void clear(){
    	super.clear();
//    	if(HighlightViews!=null&&HighlightViews.size()>0){
//    		for (HighlightView item : HighlightViews) {
//    			item = null;
//			}
//    		HighlightViews.clear();
//    		HighlightViews = null;
//    	}
    	
    	mMotionHighlightView = null;
    	
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mBitmapDisplayed.getBitmap() != null) {
            for (HighlightView hv : HighlightViews) {
                hv.mMatrix.set(getImageMatrix());
                hv.invalidate();
                if (hv.mIsFocused) {
                    centerBasedOnHighlightView(hv);
                }
            }
        }
    }

    public CropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void zoomTo(float scale, float centerX, float centerY) {
        super.zoomTo(scale, centerX, centerY);
        for (HighlightView hv : HighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomIn() {
        super.zoomIn();
        for (HighlightView hv : HighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void zoomOut() {
        super.zoomOut();
        for (HighlightView hv : HighlightViews) {
            hv.mMatrix.set(getImageMatrix());
            hv.invalidate();
        }
    }

    @Override
    protected void postTranslate(float deltaX, float deltaY) {
        super.postTranslate(deltaX, deltaY);
        for (int i = 0; i < HighlightViews.size(); i++) {
            HighlightView hv = HighlightViews.get(i);
            hv.mMatrix.postTranslate(deltaX, deltaY);
            hv.invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        CropImage cropImage = (CropImage) mContext;
        if (cropImage.mSaving) {
            return false;
        }

        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            for (int i = 0; i < HighlightViews.size(); i++) {
                HighlightView hv = HighlightViews.get(i);
                int edge = hv.getHit(event.getX(), event.getY());
                if (edge != HighlightView.GROW_NONE) {
                    mMotionEdge = edge;
                    mMotionHighlightView = hv;
                    mLastX = event.getX();
                    mLastY = event.getY();
                    mMotionHighlightView
                            .setMode((edge == HighlightView.MOVE) ? HighlightView.ModifyMode.Move
                                    : HighlightView.ModifyMode.Grow);
                    break;
                }
            }
            break;
        case MotionEvent.ACTION_UP:
            if (mMotionHighlightView != null) {
                centerBasedOnHighlightView(mMotionHighlightView);
                mMotionHighlightView.setMode(HighlightView.ModifyMode.None);
            }
            mMotionHighlightView = null;
            break;
        case MotionEvent.ACTION_MOVE:
            if (mMotionHighlightView != null) {
                mMotionHighlightView.handleMotion(mMotionEdge, event.getX()
                        - mLastX, event.getY() - mLastY);
                mLastX = event.getX();
                mLastY = event.getY();

                if (true) {
                    // This section of code is optional. It has some user
                    // benefit in that moving the crop rectangle against
                    // the edge of the screen causes scrolling but it means
                    // that the crop rectangle is no longer fixed under
                    // the user's finger.
                    ensureVisible(mMotionHighlightView);
                }
            }
            break;
        }

        switch (event.getAction()) {
        case MotionEvent.ACTION_UP:
            center(true, true);
            break;
        case MotionEvent.ACTION_MOVE:
            // if we're not zoomed then there's no point in even allowing
            // the user to move the image around. This call to center puts
            // it back to the normalized location (with false meaning don't
            // animate).
            if (getScale() == 1F) {
                center(true, true);
            }
            break;
        }

        return true;
    }

    // Pan the displayed image to make sure the cropping rectangle is visible.
    private void ensureVisible(HighlightView hv) {
        Rect r = hv.mDrawRect;

        int panDeltaX1 = Math.max(0, getLeft() - r.left);
        int panDeltaX2 = Math.min(0, getRight() - r.right);

        int panDeltaY1 = Math.max(0, getTop() - r.top);
        int panDeltaY2 = Math.min(0, getBottom() - r.bottom);

        int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
        int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

        if (panDeltaX != 0 || panDeltaY != 0) {
            panBy(panDeltaX, panDeltaY);
        }
    }

    // If the cropping rectangle's size changed significantly, change the
    // view's center and scale according to the cropping rectangle.
    private void centerBasedOnHighlightView(HighlightView hv) {
        Rect drawRect = hv.mDrawRect;

        float width = drawRect.width();
        float height = drawRect.height();

        float thisWidth = getWidth();
        float thisHeight = getHeight();

        float z1 = thisWidth / width * .6F;
        float z2 = thisHeight / height * .6F;

        float zoom = Math.min(z1, z2);
        zoom = zoom * this.getScale();
        zoom = Math.max(1F, zoom);

        if ((Math.abs(zoom - getScale()) / zoom) > .1) {
            float[] coordinates = new float[] { hv.mCropRect.centerX(),
                    hv.mCropRect.centerY() };
            getImageMatrix().mapPoints(coordinates);
            zoomTo(zoom, coordinates[0], coordinates[1], 300F);
        }

        ensureVisible(hv);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < HighlightViews.size(); i++) {
            HighlightViews.get(i).draw(canvas);
        }
    }

    public void add(HighlightView hv) {
        HighlightViews.add(hv);
        invalidate();
    }

 


}

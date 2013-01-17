package com.mama100.android.member.activities.photo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.PhotoActivity;
import com.mama100.android.member.activities.ThirdPartyWebViewActivity;
import com.mama100.android.member.activities.regpoint.RegPointHistoryActivity;
import com.mama100.android.member.asynctask.AsyncBitmapTask;
import com.mama100.android.member.bean.ThirdPartyUser;
import com.mama100.android.member.bean.WeiboShareContent;
import com.mama100.android.member.bean.thirdparty.CommonBean;
import com.mama100.android.member.bean.thirdparty.QQLoginBean;
import com.mama100.android.member.bean.thirdparty.SinaWeiboBean;
import com.mama100.android.member.bean.thirdparty.TencentWeiboBean;
import com.mama100.android.member.businesslayer.UserProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.user.LoginByThirdPartyUserReq;
import com.mama100.android.member.domain.user.ShareNotificationReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.outwardWeibo.XWeibo;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.PictureUtil;
import com.mama100.android.member.util.SDCardUtil;
import com.mama100.android.member.util.StringUtils;
/**
 * <p>
 * Description: TakePhotoActivity.java
 * </p>
 * @author aihua.yan
 * 2012-7-16
 * 拍乐秀
 */
public class TakePhotoActivity extends PhotoActivity{
	
	/****************************
	 * 底部拍照的变量
	 *****************************/
	public static final String INIT_PHOTO_URI="initPhotoUri"; //初始化照片Uri
	private ImageView avatar;
	private ImageView photo;
	private ImageView take_photo;
	private ImageView photo_hollow;
	private EditText content;
	private TextView tv_count;
	private TextView tv_title;
	private View proBar_updating;
	private int totalCount=0;
	private boolean has_take_photo=false;
	private boolean is_txt_len_illlegal=false;
	private boolean has_try_share=false;
	private Uri curPhotoUri=null;
	private String TAG = this.getClass().getSimpleName();
	
	/*********************************
	 * 用于及时释放内存的变量
	 *********************************/
	private List<Bitmap> bitmapList = new ArrayList<Bitmap>();
	private File picFile = null;
	private Bitmap bitmap = null;//要分享的图片
	
	
	/*******************************************************
	 * 与微博相关的变量
	 ******************************************************/
	private SinaWeiboBean bean_sinaWeibo;
	
	
	private AsyncBitmapTask imageViewTask = null; //用于从非第三方登录进来后下载头像
	private boolean isAfterOnActivityResult = false;//用于判断onResume是在onActivityResult被执行后执行，过滤那种back-onReume的情况。

	private CustomAsyncTask task; //绑定微博时， 先发送请求到mama100服务器
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.photo_home);
		bean_sinaWeibo = getBean_SinaWeibo();
		setTopBar();
		setupViews();
		setBackgroundPicture(R.drawable.bg_wall);
		resetPageValues();
		
	}
	@Override
	public void onResume(){
		super.onResume();
		StatService.onResume(this);//百度统计
//		MobileProbe.onResume(this);//CNZZ统计
		
//		//头像先抢焦点，以免让输入框获得焦点，从而避免进入界面就打开键盘
//		avatar.requestFocusFromTouch();
//		avatar.requestFocus();
		
		if(isUnlogin()){
			return;
		}

		// 如果之前的状态是未登录，而今成为已登录，则要刷新页面
		if (isPreviousUnLoginStatus()) {
			resetPageValues();
		}

	}
	
	private void resetPageValues() {
		//如果不是从第三方登录，且onActivity也被调用， 则证明是从其他登录直接进来，而非back->OnResume进来。。则需要重新加载信息。
//				if(isAfterOnActivityResult){
//					BasicApplication application = BasicApplication.getInstance();
//					setAvatarIntoViews(R.id.photo_avatar,application.getLastAvatarUrl(), TakePhotoActivity.this);
//				}else{
//				setAvatar();
//				}
				//modified by edwar 2012-11-07 
				//修改之前复杂的逻辑为， 如果没有头像，就去mama100服务器系统拿，如果内存有，就直接取
				avatar.setScaleType(ScaleType.FIT_CENTER);
				Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
				if(bitmap!=null){
				setAvatar(bitmap);
				}else{
					BasicApplication application = BasicApplication.getInstance();
					setAvatarIntoViews(
							application.getLastAvatarUrl(), TakePhotoActivity.this);
				}
				//modified end
		
		
	}
	
	
	private void setAvatar(Bitmap bitmap) {
		avatar.setImageBitmap(bitmap);
	}
	
	//用于从非第三方登录入口进来，要获取头像
		private void setAvatarIntoViews(String imgUrl, Activity mActivity) {
			
			if (StringUtils.isBlank(imgUrl)) {
				// TODO 用户初次登录， 服务器没有用户的个人头像信息的情况
				// 方法一，用drawable，问题：图片容易变形
				// Drawable drawable = getResources().getDrawable(
				// R.drawable.testavatar);
				// imageView.setImageDrawable(drawable);

				// 方法二，用uri,因为现在直接用bitmap而放弃
				// Uri path = Uri.parse(
				// "android.resource://com.mama100.android.member/drawable/testavatar");
				// avatar.setImageURI(path);
				// 保存默认头像
				// BasicApplication.getInstance().setStoreAvatarUri(path);

				// 方法三，直接用bitmap

				// LogUtils.loge(TAG, "get avatar - " + 1 );
				// Toast.makeText(HomePageActivity.this, "get avatar - " + 1 ,
				// Toast.LENGTH_SHORT).show();
				// 如果本地保留了上次用户的头像，就用上传头像
				String filename = SDCardUtil.getPictureStorePath()
						+ AppConstants.TEMP_STORE_PICTURE_NAME;
				Bitmap bitmap = PictureUtil.getPictureFromSDcardByPath(filename);
				// 如果从本地获取bitmap为空
				if (bitmap == null) {
					// LogUtils.loge(TAG, "get avatar - " + 2 +
					// " from local sdcard  TEMP_STORE_PICTURE_NAME" );
					// Toast.makeText(HomePageActivity.this, "get avatar - " + 2 +
					// " from local sdcard  TEMP_STORE_PICTURE_NAME" ,
					// Toast.LENGTH_SHORT).show();
					
					//modified by edwar, 2012-11-06 直接在xml里赋值未登录的默认头像，所以这里没有必要再赋值，精简赋值
//					bitmap = BitmapFactory.decodeResource(BasicApplication
//							.getInstance().getResources(), R.drawable.default_avatar);
				}
//				BasicApplication.getInstance().setAvatarBitmap(bitmap);
				//modified end

			} else {
				// LogUtils.logi(TAG, "set avatar 1 - : "
				// + new Timestamp(System.currentTimeMillis()).toString());

				// 异步加载图片
				avatar.setTag(imgUrl);
				imageViewTask = new AsyncBitmapTask(mActivity);
				imageViewTask.execute(avatar);
			}

			// 2,修改头像
			// 设置头像为本地默认的头像,用于在个人信息界面修改头像之后回到首页的头像更新
			// 对于异步头像获取，在获取过程中，先从本地加载头像， 然后更新服务器最新的。。

			// LogUtils.logi(TAG, "set avatar 2 - : "
			// + new Timestamp(System.currentTimeMillis()).toString());
			avatar.setScaleType(ScaleType.FIT_CENTER);

			//modified by edwar 2012-11-06
			Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
			if(bitmap!=null)
			avatar.setImageBitmap(bitmap);
			//modified end
			// LogUtils.logi(TAG, "set avatar 3 - : "
			// + new Timestamp(System.currentTimeMillis()).toString());

		}
		
		
	
	// 设置头像
	private void setAvatar() {
		avatar = (ImageView) findViewById(R.id.photo_avatar);
		avatar.setImageBitmap(BasicApplication.getInstance().getAvatarBitmap());
	}
	
	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);//百度统计
//		MobileProbe.onPause(this);//CNZZ统计
		//1.内存测试 start
				free = Runtime.getRuntime().totalMemory();
				LogUtils.loge(TAG, "用掉的内存 - " + (total-free) + "字节");
	}

	private void setTopBar() {
		setLeftButtonImage(R.drawable.selector_back);
		setRightButtonImage(R.drawable.selector_share);	
		setTopLabel(R.string.photo_title);
	}

	private void setupViews() {
		//分享标题
		
		
		avatar = (ImageView) findViewById(R.id.photo_avatar);
		String html_title = "<font color=#000000>分享到</font> "+ "<img src='"+R.drawable.weibo_logo_sina+"'/>"+"<font color=#000000>微博</font>";
		
		WeiboShareContent shareCon=BasicApplication.getInstance().getWeiboShareContent();
		String html_content;
		
		if(shareCon==null||(shareCon.getAtPerson()==null
				&&shareCon.getContent()==null
				&&shareCon.getTheme()==null))
			//默认分享的内容
			html_content="<font color=#ff6600>#拍乐秀#@妈妈100官方 </font>"+"<font color=#000000>我现在正使用妈妈100分享宝宝搞怪萌照，你也来一起参加吧！</font>";
		else{
			StringBuffer strb=new StringBuffer();
			strb.append("<font color=#ff6600>");
			//主题
			strb.append(shareCon.getTheme()!=null?shareCon.getTheme():"");
			//@人
			if(shareCon.getAtPerson()!=null){
				int size=shareCon.getAtPerson().size();
				for(int i=0;i<size;i++){
					strb.append(shareCon.getAtPerson().get(i));
					//最后@someone必需在其后加上空格
					if(i==size-1)
						strb.append(" ");
				}
			}
			strb.append("</font><font color=#000000>");
			strb.append(shareCon.getContent()!=null?shareCon.getContent():"");
			strb.append("</font>");
			html_content=strb.toString();
		}
		
		tv_count=(TextView)findViewById(R.id.tv_count);
		tv_title = (TextView)findViewById(R.id.label);
		proBar_updating=findViewById(R.id.proBar_updating);
		tv_title.append(Html.fromHtml(html_title,imageGetter,null));
		
		content=((EditText)findViewById(R.id.content));
		content.addTextChangedListener(new InnerWatch());
		content.append(Html.fromHtml(html_content));
		content.requestFocus();
		content.setFilters(new InputFilter[] { new InputFilter() {
			 @Override
			 public CharSequence filter(CharSequence source, int start,
			  int end, Spanned dest, int dstart, int dend) {
				 //source 用户输入的字符，只有在输入时才有值，删除时值为空
				 LogUtils.loge("E", "source = "+source);
				 
				 //
				 LogUtils.loge("E", "start = "+start);
				 //
				 LogUtils.loge("E", "end = "+end);
				 //准备操作的目标字符串
				 LogUtils.loge("E", "dest = "+dest);
				 //对字符串进行操作前光标的位置
				 LogUtils.loge("E", "dstart = "+dstart);
				 //对字符串进行操作后光标的位置
				 LogUtils.loge("E", "dend = "+dend);
				 //获取上一步操作的目标字符
				 LogUtils.loge("E", "char = "+dest.toString().substring(dstart, dend));

				 String str = "#拍乐秀#@妈妈100官方 ";
				 String ch = dest.toString().substring(dstart, dend);
				 
				 Pattern pattern = Pattern.compile(ch);
				 Matcher matcher = pattern.matcher(str);
				 
				 //如果输入字符串长度>0 而且光标>=标签长度的时候才允许输入
				 if(source.length()>0 && dstart >=str.length()){
					 return source;
				 }
				 //如果光标<=标签长度 且 操作目标字符存在标签字符中 返回该操作字符
				 else if(matcher.find()&& dstart<=str.length()){
					 return ch;
				 }
				 
				 //返回空 给用户效果是不能输入
				 else{
					 return "";
				 }
			 }
			} });
		
		//分享图片
		photo = (ImageView) findViewById(R.id.photo);
		photo_hollow = (ImageView) findViewById(R.id.hollow_frame);
		
		//avatar获得本地头像
//		avatar.setImageURI(BasicApplication.getInstance().getStoreAvatarUri());
		setAvatar();		
//		photo.setLayoutParams(new LayoutParams(480
//				,widthPixels-DisplayUtil.dip2px(this, 20)));
		
		

		try {
			curPhotoUri=(Uri) getIntent().getParcelableExtra(INIT_PHOTO_URI);
			bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), 
					(Uri) getIntent().getParcelableExtra(INIT_PHOTO_URI));
			
//			Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
			
//			bitmap = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
//			photo.setBackgroundDrawable(null);
			
//			photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE); 
			photo.setImageBitmap(bitmap);
			bitmapList.add(bitmap);
			
			has_take_photo=true;
		}catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, R.string.init_photo_failed, Toast.LENGTH_SHORT)
			.show();
		}

		//点击拍照
		take_photo = ((ImageButton)findViewById(R.id.btn_take_photo));
		setImageCropParams(widthPixels-20,widthPixels-20);
		take_photo.setId(TAKE_PHOTO);
		take_photo.setOnClickListener(this);
	}

	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		if(has_try_share){
			TakePhotoActivity.this.finish();
			return;
		}
		showmemberDialog(R.string.not_share_comfirm, new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				closeDialog();
				TakePhotoActivity.this.finish();
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		doClickLeftBtn();
	}
	@Override
	public void doClickRightBtn() {
		super.doClickRightBtn();
		if(isUnlogin()){
			goToLoginPage(AppConstants.REQUEST_CODE_UNLOGIN_INTO_TAKE_PHOTO_SHARE_PAGE);
			return;
		}
		
    	if(!bean_sinaWeibo.isAccessTokenValid()){
        	Toast.makeText(TakePhotoActivity.this,"请绑定微博后，再分享",Toast.LENGTH_LONG)
        	.show();
        	author2Method(SinaWeiboBean.REQ_AUTHOR);
    		return;
    	}
    	final String conStr=content.getText().toString();
    	if(conStr==null||conStr.equals("")){
        	Toast.makeText(TakePhotoActivity.this,"内容为空",Toast.LENGTH_SHORT)
        	.show();
    		return;
    	}
    	if(is_txt_len_illlegal){
    		content.setError("不能超过140个汉字喔");
    		return;
    	}
    	if(!has_take_photo){
        	Toast.makeText(TakePhotoActivity.this,"请选择照片",Toast.LENGTH_SHORT)
        	.show();
        	return;
    	}
    	if(curPhotoUri==null){
            Toast.makeText(TakePhotoActivity.this, "照片无效，请重新选择", Toast.LENGTH_SHORT)
            .show();
            return;
    	}
    	picFile=new File(curPhotoUri.getPath());
        if (!picFile.exists()) {
            Toast.makeText(TakePhotoActivity.this, "照片不存在", Toast.LENGTH_SHORT)
                    .show();
            return;
        }   
    	
    	
//        XWeibo.getInstance().share2weibo(conStr, picFile,new MyShareWeiboLis());
    	
    	
//    	if(bitmap==null||bitmap.isRecycled()){
//      Toast.makeText(TakePhotoActivity.this, "照片无效，请重新选择", Toast.LENGTH_SHORT)
//      .show();
//      return;
//	}
    	
//    	String filepath = SDCardUtil.getTempCropingPhotoPath() ;
//    	try {
//			PictureUtil.storePicture(filepath, bitmap, AppConstants.BITMAP_COMPRESS);
//			bitmapList.add(bitmap);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//    	
//    	File file = new File(filepath);
        
        proBar_updating.setVisibility(View.VISIBLE);
        setRightButtonVisibility(View.INVISIBLE);
        take_photo.setVisibility(View.INVISIBLE);
        
        
        
        XWeibo.getInstance().share2weibo(conStr, picFile ,new MyShareWeiboLis());
	}

	 ImageGetter imageGetter = new ImageGetter() {  
	        @Override
	       public Drawable getDrawable(String source) {
	       int id = Integer.parseInt(source);

	      //根据id从资源文件中获取图片对象
	       Drawable d = getResources().getDrawable(id);
	       d.setBounds(0, 0, d.getIntrinsicWidth(),d.getIntrinsicHeight());
	        return d;
	       }
	        };

	@Override
	public void setPhotoIntoAvatar() {
		
		//清除上一张图的内存
		if(bitmap!=null&&bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
			
		}
		
//		    bitmap = newBitmap;
		
		
		
		
		try {
			has_take_photo=true;
			curPhotoUri=BasicApplication.getInstance().getCropImageStoreUri();
			bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), curPhotoUri);
			photo.setBackgroundDrawable(null);
//			photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE); 
			photo.setImageBitmap(bitmap);
			bitmapList.add(bitmap);
			
			//头像率先抢掉焦点，以免让输入框获得焦点，从而避免进入界面就打开键盘
			avatar.requestFocusFromTouch();
			avatar.requestFocus();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		
//			has_take_photo=true;
//			photo_hollow.setBackgroundResource(R.drawable.homepage_avatar_hollow);
//			photo.setBackgroundDrawable(null);
////			photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//			photo.setImageBitmap(null);
//			photo.invalidate();
//			bitmapList.add(bitmap);
//			photo.setImageBitmap(bitmap);
			
			//头像率先抢掉焦点，以免让输入框获得焦点，从而避免进入界面就打开键盘
//			avatar.requestFocusFromTouch();
//			avatar.requestFocus();
	}     

	@Override
	protected void onDestroy() {
		imageGetter = null;
		//释放 Html.fromHtml引起的内存
		tv_title = null; 
		tv_count = null;
		
		
		if(bitmapList!=null&&bitmapList.size()>0){
			for (int i = 0; i < bitmapList.size(); i++) {
				Bitmap bitmap = bitmapList.get(i);
				if(bitmap!=null&&!bitmap.isRecycled()){
					bitmap.recycle();
					bitmap = null;
				}
			}
		}
		bitmapList.clear();
		bitmapList = null;
		picFile = null;
		clearImageViewMomery(avatar);
		clearImageViewMomery(photo);
		clearImageViewMomery(photo_hollow);
		
		if (imageViewTask != null && imageViewTask.isCancelled() == false) {
			imageViewTask.cancel(true);
			imageViewTask = null;
		}
		
		if (task != null && task.isCancelled() == false) {
			task.cancel(true);
			task = null;
		}
		
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		// 这里沿用第三方登录的请求对象，其实这里不是第三方登录，而是向服务器提交绑定或解绑定请求
		LoginByThirdPartyUserReq request = new LoginByThirdPartyUserReq();
		// 新浪微博用户授权回调处理
		if (resultCode == CommonBean.RES_SUCCESS) {
			if (requestCode == SinaWeiboBean.REQ_AUTHOR) {

					// 更新bean
					updateBeanContent();
//					setSinaWeiboBindStatus(true);

					SinaWeiboBean bean = getBean_SinaWeibo();
					request.setUserType(ThirdPartyUser.type_sina);
					request.setUid(bean.getUid());
					request.setAccess_token(bean.getAccessToken());
					request.setToken_expire_date(bean.getExpiresIn());

					task = new CustomAsyncTask(this);
					task.displayProgressDialog(R.string.doing_req_message);
					task.execute(request);
			} else if (requestCode == QQLoginBean.REQ_AUTHOR) {

			}

			else if (requestCode == TencentWeiboBean.REQ_AUTHOR) {

			}

		}

		//TODO 从登录界面返回
		if(resultCode == RESULT_OK && requestCode == AppConstants.REQUEST_CODE_UNLOGIN_INTO_TAKE_PHOTO_SHARE_PAGE){
			
			isAfterOnActivityResult = true;
			is_from_third_party = data.getBooleanExtra(IS_FROM_THIRD_PARTY,
					false);
			
			setUnlogin(false);
			BasicApplication.getInstance().setAutoLogin(true);
			//涉及到输入的情况下，就不主动点击;以免造成错误。
//			doClickRightBtn();
		}
	}
	
	protected void updateBeanContent() {
		bean_sinaWeibo = getBean_SinaWeibo();
	}

	class InnerWatch implements TextWatcher{

		@Override
		public void afterTextChanged(Editable s) {
			//Sina微博最多允许输入140个汉字
			int tmpCount=0;
			if(totalCount%2==0){
				tmpCount=totalCount/2;
			}
			else{
				tmpCount=(totalCount+1)/2;
			}
			if(tmpCount>140){
				tv_count.setTextColor(getResources().getColor(R.color.red));
				is_txt_len_illlegal=true;
			}
			else{
				tv_count.setTextColor(getResources().getColor(R.color.edittext_hint_color));
				is_txt_len_illlegal=false;
			}
			tv_count.setText(tmpCount+"/140");
			
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			if(count>after&&count>0){
				totalCount-=StringUtils.countEx(s.subSequence(start+after, start+count));
			}

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if(count>before&&count>0){
				totalCount+=StringUtils.countEx(s.subSequence(start+before, start+count));
			}
		}
	}
   
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		LogUtils.logd(TAG, "configration changed");
	}
	
	class shareNotificationTask extends AsyncReqTask{
		public shareNotificationTask(Context context) {
			super(context);
		}
		@Override
		protected BaseRes doRequest(BaseReq request) {
			return UserProvider.getInstance(mContext).shareNotification(
					(ShareNotificationReq)request);
		}
		@Override
		protected void handleResponse(BaseRes response) {
			String res=response.getCode();
			LogUtils.logd(getClass(), "response.getCode().....:"+response.getCode());
		}
	}

	@Override
	protected void updateFollowMaMa100CheckBoxStatus(boolean b) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void shareWeiboComplete(boolean b) {
		if(b){
			proBar_updating.setVisibility(View.GONE);
            setRightButtonVisibility(View.VISIBLE);
            take_photo.setVisibility(View.VISIBLE);
			has_try_share=true;
			ShareNotificationReq request=new ShareNotificationReq();
			
			request.setContent(content.getText().toString());
			request.setType(ThirdPartyUser.type_sina); //2.1版本前，暂时写为sina
			new shareNotificationTask(getApplicationContext())
				.execute(request);
			
			showmemberDialog(R.string.send_weibo_sucess, 0, 
				new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					closeDialog();
	        		final Intent intent=new Intent(getApplicationContext(), ThirdPartyWebViewActivity.class)
	        		.putExtra(ThirdPartyWebViewActivity.KEY_URL, XWeibo.URL_VIEW_M_USER_WEIBO +"/"+bean_sinaWeibo.getUid())
	        		.putExtra(ThirdPartyWebViewActivity.KEY_ACTION, ThirdPartyWebViewActivity.REQ_XUSER_WEIBO);
	        		startActivity(intent);
	        		TakePhotoActivity.this.finish();

				}
			}, new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					closeDialog();
					TakePhotoActivity.this.finish();
				}
			});
			setDialogBtnTips(R.string.btn_check,R.string.btn_back);
//            Toast.makeText(TakePhotoActivity.this, R.string.send_weibo_sucess, Toast.LENGTH_LONG).show();
        
			
		}else{
        	proBar_updating.setVisibility(View.GONE);
            take_photo.setVisibility(View.VISIBLE);
            has_try_share=true;
            setRightButtonVisibility(View.VISIBLE);
		}
	}
	@Override
	protected void doSSoLogin(boolean b, int requestCode) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	/****************************************
	 * 绑定或者解绑 用到的 方法
	 ****************************************/

	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
				return UserProvider.getInstance(getApplicationContext())
						.bindAction((LoginByThirdPartyUserReq) request);
		}

		@Override
		protected void handleResponse(BaseRes response) {
			
			//TODO  response.getCode = 101 , 已经被别人绑定，就不能绑定
			closeProgressDialog();
			makeText(response.getDesc());
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				if(response.getCode().equals(DeviceResponseCode.WEIBO_IS_BOUNDED_BY_OTHER))
					//让webview不记住当前登陆用户
					CookieSyncManager.createInstance(getApplicationContext());   
					CookieManager cookieManager = CookieManager.getInstance();  
					cookieManager.removeAllCookie();
					//如果发现微博已经被别人绑过，这样用户就不能绑定该微博
					bean_sinaWeibo.unbindUserToDeleteValueInSharedPreference();
					updateBeanContent();
			}else{
				//绑定成功后，提示分享
				showmemberDialog(R.string.auth_succeed_toshare, new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						//更新bean
						updateBeanContent();
						
						doClickRightBtn();
						closeDialog();
					}
				});
				setDialogBtnTips(R.string.btn_share,R.string.btn_cancel);
			}
		}
	}
	
}

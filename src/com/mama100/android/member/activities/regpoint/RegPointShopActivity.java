package com.mama100.android.member.activities.regpoint;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.LocationListener;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.activities.babyshop.MapBasicActivity;
import com.mama100.android.member.bean.ShopItem;
import com.mama100.android.member.businesslayer.PointProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.point.PointRelativeShopReq;
import com.mama100.android.member.domain.point.PointRelativeShopRes;
import com.mama100.android.member.domain.point.PointSideShopReq;
import com.mama100.android.member.domain.point.PointSideShopRes;
import com.mama100.android.member.domain.point.PointSubmitReq;
import com.mama100.android.member.domain.point.PointSubmitRes;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.widget.adapter.ShopListAdapter;
import com.mama100.android.member.zxing.CaptureActivity;

/**
 * 
 * @Description:显示门店信息，用户可以选择门店进行积分操作
 * @author liyang
 * @date 2012-11-20 下午2:34:04 
 */
public class RegPointShopActivity extends BaseActivity {
	
	//上一次点击的位置
	public ShopItem lastClickItem = null;
	
	//附近门店列表的 footer
	private View footer ;
	
	//附近门店列表适配器
	private ShopListAdapter adapter;
	
	//附近门店列表
	private ListView listview;
	
	//判断当前请求类型
	private static boolean isSubmit = false ;
	
	
	private LocationListener mLocationListener;
	private BMapManager mapManager;
	private double lat = 720.0d;// 纬度不能初始化为0,可能存在纬度为0
	private double lon = 720.0d;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.regpoint_shop);

		// 设置顶部标题、按钮图片、页面背景
		setTopLabel(R.string.regpoint_shop_title);
		setLeftButtonImage(R.drawable.selector_back);
		setRightButtonImage(R.drawable.selector_submit);
		setBackgroundPicture(R.drawable.bg_wall);
		
		//设置帮助提示的样式和点击事件
		((TextView)findViewById(R.id.help)).getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		((TextView)findViewById(R.id.help)).getPaint().setAntiAlias(true);
		((TextView)findViewById(R.id.help)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(RegPointShopActivity.this,RegPointHelpActivity.class);
				intent.putExtra("isOperationByScan", false);
				intent.putExtra("isOperationByShop", true);
				startActivity(intent);
			}
		});
		
		mapManager = new BMapManager(getApplication());
		mapManager.init(MapBasicActivity.mStrKey,null);
		
		loadShopSelectView();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mapManager != null)
			mapManager.getLocationManager().removeUpdates(mLocationListener);
		if (mapManager != null)
			mapManager.stop();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		// 注册定位事件
		if (mapManager != null)
			mapManager.getLocationManager().requestLocationUpdates(mLocationListener);
		if (mapManager!= null)
			mapManager.start();		
	}
	
	
	/**
	 * 页面销毁时进行内存清理操作
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if(adapter!=null){
			adapter.clearMemory();
			adapter = null;
		}
		
		if (mapManager != null)
			mapManager.getLocationManager().removeUpdates(mLocationListener);
		if (mapManager != null)
			mapManager.destroy();
	}

	@Override
	public void doClickLeftBtn() {
		// TODO Auto-generated method stub
		super.doClickLeftBtn();
		onBackPressed();
	}

	@Override
	public void doClickRightBtn() {
		// TODO Auto-generated method stub
		super.doClickRightBtn();
		
		//如果未选择门店 点击提交
		if(lastClickItem == null){
			makeText(R.string.regpoint_shop_tips_7);
		}else{
			submit();
		}
	}
	
	
	/**
	 * 提示是否确认提交
	 */
	public void submit(){
		showmemberDialog(R.string.regpoint_shop_tips_10,new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				isSubmit = true;
				//产品序列号
				String productSerial = getIntent().getStringExtra("productSerial");
				//产品防伪码
				String productCode = getIntent().getStringExtra("productCode");
				//产品门店
				String productTerminal = lastClickItem.getTerminalCode();
				
				PointSubmitReq request = new PointSubmitReq();
				request.setTerminal(productTerminal);
				request.setSerial(productSerial);
				request.setSecurity(productCode);
				
				LogUtils.logd(getClass(), "TerminalCode = " + 17970);
				LogUtils.logd(getClass(), "productSerial = " + productSerial);
				LogUtils.logd(getClass(), "productCode = " + productCode);
				
				CustomAsyncTask task = new CustomAsyncTask(RegPointShopActivity.this);
				task.displayProgressDialog(R.string.doing_req_message);
				task.execute(request);
			}
		});
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		if (MapBasicActivity.mBMapMan != null)
			MapBasicActivity.mBMapMan.stop();
		
		finish();
	}
	
	
	/**
	 * 已登录  已开通积分通情况
	 * 
	 * 显示页面在已登录和已开通积分通的情况下的界面布局
	 */
	void loadShopSelectView(){
		//显示附近更多门店的视图
		footer = getLayoutInflater().inflate(R.layout.regpoint_shop_footer,null);
		footer.setOnClickListener(footerClickListener);
		
		adapter = new ShopListAdapter(this);
		
		//初始化listview
		listview = (ListView)findViewById(R.id.sideShopList);
		listview.addFooterView(footer);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new ItemClickListener());
		
		//设置状态为正在加载
		setFooterOfSideToLoading();
		
		mLocationListener = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					lat = location.getLatitude();
					lon = location.getLongitude();
					
					MapBasicActivity.curLat=(int)(lat*1e6);
					MapBasicActivity.curLon=(int)(lon*1e6);
					
					//默认加载第一页门店
					loadRelativeShop();
					// 停止定位服务
					mapManager.getLocationManager().removeUpdates(mLocationListener);
				}else{
					new CheckLocateTask().execute();
				}
			}
		};
		mapManager.getLocationManager().requestLocationUpdates(mLocationListener);
	}
	
	/**
	 * 检查8秒内是否能获得当前位置，如果不能则提示稍候重试
	 *
	 */
	class CheckLocateTask extends AsyncTask<Void, Void, Void>{
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				Thread.sleep(15000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(lat>361&&lon>361){
				//TODO 此处改成友好提示
				setFooterOfSideToOverload();
			}
		}
	}
	
	
	/**
	 * 选择门店时执行
	 * @author liyang
	 *
	 */
	private class ItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> aView, View view, int i,long l) {
			// TODO Auto-generated method stub
			Object object = aView.getItemAtPosition(i);
			if(object instanceof ShopItem ){
				//判断是否已经选择过门店
				if(lastClickItem != null){
					//取消选择过的门店					
					lastClickItem.setChecked(false);
				}
				
				ShopItem shopItem = (ShopItem)object;
				shopItem.setChecked(true);
				
				//将选择过的门店设置为本次选择的门店
				lastClickItem = shopItem;
				
				//通知适配器更新数据
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	/**
	 * 点击获取更多门店是执行
	 * @author liyang
	 *
	 */
	private OnClickListener footerClickListener =new OnClickListener(){
		//定义初始页码和每页显示的个数
		private int pageNo = 0;
		private int pageSize = 20;
		
		@Override
		public void onClick(View view) {
			//页码+1
			pageNo ++;
			//加载下一页数据
			loadSideShop(pageNo, pageSize);
		}
	};
	
	
	/**
	 * 加载附近门店
	 */
	void loadSideShop(int pageNo,int pageSize){
		PointSideShopReq request =new PointSideShopReq();
		request.setPageNo(pageNo);
		request.setPageSize(pageSize);
		request.setLatitude(String.valueOf(lat));
		request.setLongitude(String.valueOf(lon));
		//判断是否已经加载到最后一页
		CustomAsyncTask task = new CustomAsyncTask(this);
		task.execute(request);
	}
	
	/**
	 * 加载关注门店
	 */
	void loadRelativeShop(){
		PointRelativeShopReq request = new PointRelativeShopReq();
		request.setLatitude(String.valueOf(lat));
		request.setLongitude(String.valueOf(lon));
		
		LogUtils.logd(getClass(), "Latitude="+lat);
		LogUtils.logd(getClass(), "Longitude="+lon);
		
		
		CustomAsyncTask task = new CustomAsyncTask(this);
		task.execute(request);
	}
	
	
	/**
	 * 设置附近门店footer显示为正在加载的视图
	 */
	void setFooterOfSideToLoading(){
		footer.findViewById(R.id.footer_progress).setVisibility(View.VISIBLE);
		((TextView)footer.findViewById(R.id.footer_text)).setText(getString(R.string.regpoint_shop_btn_2));
		footer.setClickable(false);
	}
	
	/**
	 * 设置附近门店footer显示为未加载的视图
	 */
	void setFooterOfSideToUnLoading(){
		footer.findViewById(R.id.footer_progress).setVisibility(View.GONE);
		((TextView)footer.findViewById(R.id.footer_text)).setText(getString(R.string.regpoint_shop_btn_1));
		footer.setClickable(true);
	}
	
	
	/**
	 * 设置附近门店加载出错的视图
	 */
	void setFooterOfSideToError(){
		footer.findViewById(R.id.footer_progress).setVisibility(View.GONE);
		((TextView)footer.findViewById(R.id.footer_text)).setText(getString(R.string.regpoint_shop_error_3));
		footer.setClickable(false);
	}
	
	

	/**
	 * 设置附近门店加载失败，请重新加载的视图
	 */
	void setFooterOfSideToOverload(){
		footer.findViewById(R.id.footer_progress).setVisibility(View.GONE);
		((TextView)footer.findViewById(R.id.footer_text)).setText(getString(R.string.regpoint_shop_error_4));
		footer.setClickable(true);
	}
	
	
	
	/**
	 * 设置附近门店已经加载完毕
	 */
	void setFooterOfSideToOver(){
		footer.findViewById(R.id.footer_progress).setVisibility(View.GONE);
		((TextView)footer.findViewById(R.id.footer_text)).setText(getString(R.string.regpoint_shop_over_1));
		footer.setClickable(false);
	}
	
	/**
	 * 隐藏右上角提交按钮
	 */
	void setBtnOfSubmitHidden(){
		setRightButtonVisibility(View.GONE);
	}
	
	/**
	 * 继续积分监听器
	 * 点击按照上一次成功积分方式进行积分
	 */
	private OnClickListener  pos_listener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(getIntent().getBooleanExtra("isOperationByScan", false)){
				startActivity(new Intent(RegPointShopActivity.this,CaptureActivity.class));
			}else{
				startActivity(new Intent(RegPointShopActivity.this,RegPointInputActivity.class));
			}
			
			finish();	//关闭当前页面
		}
	}; 
	
	/**
	 * 退出积分监听器
	 * 点击返回自助积分首页
	 */
	private OnClickListener  cancel_listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivity(new Intent(RegPointShopActivity.this, RegPointHomeActivity.class));
			finish();
		}
	};
	
	
	/**
	 *显示操作成功时候的结果
	 */
	void displayResultOfSuccess(String point,String pbalance){
		String msg = "<font color=#000000>恭喜你，</font>"
				   + "<font color=#ff6600>成功积分"+ point+"分！</font>"
				   + "<font color=#000000>现在帐户可用积分余额为"+pbalance+"分。</font>" ;
		
		//设置积分用于刷新自助积分首页余额
		String  sBalance = BasicApplication.getInstance().getLastRegpointBalance();
		Integer lBalance = Integer.valueOf(sBalance) + Integer.valueOf(point);
		BasicApplication.getInstance().setLastRegpointBalance(String.valueOf(lBalance));
		
		//用于清空输入框的历史记录
		RegPointInputActivity.isRegpointSuccess = true;
		
		showmemberDialog(msg, 0, cancel_listener,pos_listener );
	}
	
	
	/**
	 *显示操作失败时候的结果
	 */
	void displayResultOfFailure(String desc){
		//如果是特殊码则跳转到人工积分界面
		//否则直接采用Toast提示
		Intent intent = new Intent(this, RegPointFailureActivity.class);
		intent.putExtra("desc", desc);
		intent.putExtra("productPoint", getIntent().getStringExtra("productPoint"));
		startActivity(intent);
	}
	
	/**
	 * 
	 * 获取门店列表的异步请求类
	 * 
	 */
	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			if(!isSubmit)
				setFooterOfSideToLoading();
				
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			if(request instanceof PointSideShopReq){
				return PointProvider.getInstance(RegPointShopActivity.this).getSideShop((PointSideShopReq)request);
			}else if(request instanceof PointRelativeShopReq){
				return PointProvider.getInstance(RegPointShopActivity.this).getRelativeShop((PointRelativeShopReq)request);
			}else if(request instanceof PointSubmitReq){
				return PointProvider.getInstance(RegPointShopActivity.this).diyPointSubmit((PointSubmitReq)request);
			}
			return null;
		}

		@Override
		protected void handleResponse(BaseRes response) {
			closeProgressDialog();
			LogUtils.logd(getClass(), "code = " + response.getCode());
			//验证成功
			if("100".equals(response.getCode())){
				if(adapter == null) return;
				if(response instanceof PointSideShopRes){
					PointSideShopRes res = (PointSideShopRes)response;
					adapter.addSide(res.getList());
					
					adapter.addList(res.getList());
					//通知更新Listview数据
					adapter.notifyDataSetChanged();
					
					
					LogUtils.logd(getClass(), "AllRowsNo="+res.getAllRowsNo());
					LogUtils.logd(getClass(), "SideSize="+adapter.getSideSize());
					
					if(res.getAllRowsNo() == 0){
						//设置底部为加载失败的状态
						setFooterOfSideToError();
					}else if(adapter.getSideSize() == res.getAllRowsNo()){
						//设置底部为加载完的状态
						setFooterOfSideToOver();
					}else{
						//设置底部为默认状态
						setFooterOfSideToUnLoading();
					}
					
					//门店为0，隐藏右上角提交按钮
					if(adapter.getCount() == 0){
						setBtnOfSubmitHidden();
					}
				}else if(response instanceof PointRelativeShopRes){
					PointRelativeShopRes res = (PointRelativeShopRes)response;
					//显示关注门店无数据的视图
					if(!res.getList().isEmpty()){
						adapter.addList(res.getList());
						//通知更新Listview数据
						adapter.notifyDataSetChanged();
						
						setFooterOfSideToUnLoading();
					}else{
						//如果没有关注的门店则默认加载附近门店的第一页
						footerClickListener.onClick(footer);
					}
				}else if(response instanceof PointSubmitRes){
					PointSubmitRes res = (PointSubmitRes)response;
					displayResultOfSuccess(res.getPoint(),res.getPbalance());
				}
			}else{
 				//加载失败时显示				
				if(response instanceof PointSubmitRes){
					displayResultOfFailure(response.getDesc());
				}else if(response instanceof PointSideShopRes){
					setFooterOfSideToError();
				}
			}
		}
	}
}

/**
 * 
 */
package com.mama100.android.member.activities.regpoint;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.asynctask.AsyncBitmapTask;
import com.mama100.android.member.bean.PointItem;
import com.mama100.android.member.businesslayer.PointProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.point.PointListReq;
import com.mama100.android.member.domain.point.PointListRes;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.util.PictureUtil;
import com.mama100.android.member.util.SDCardUtil;
import com.mama100.android.member.util.StringUtils;
import com.mama100.android.member.widget.adapter.RegPointListAdapter;
import com.mama100.android.member.widget.listview.PulldownToRefreshListView;
import com.mama100.android.member.widget.listview.PulldownToRefreshListView.OnRefreshListener;

/**
 * <p>
 * Description: RegPointActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 积分模块主界面
 */
public class RegPointHistoryActivity extends BaseActivity {
	
	public String TAG = this.getClass().getSimpleName();
	private ListView listview;
	private RegPointListAdapter mAdapter;
	private List<PointItem> regpointList = new ArrayList<PointItem>();
	private int nextpage = 1; // 默认第一页
	private String PAGE_SIZE = "10";
	protected int REGPOINT_DIY = 10000;// 自助积分请求

	/*******************************************************************
	 * 从服务器下拉获取消息 用到的变量
	 *********************************************************************/
	// 符合条件的总记录数
	private int totalCount = 0;
	public boolean canLoad = true; // 还可以继续下载
	/************************************************************************
	 * 显示无记录
	 ***********************************************************************/
	private TextView no_record;//没有任何记录时显示
	
	/***************************************************************
	 * 释放内存的变量
	 ***********************************************************/
	private CustomAsyncTask task = null;
	private TextView regpoint_balance;
	private TextView regpoint_account;
	
	private ImageView avatar;
	private AsyncBitmapTask imageViewTask = null; // 用于从非第三方登录进来后下载头像

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.regpoint_history);
		setBackgroundPicture(R.drawable.bg_wall);
		setupTopBar();
		setupViews();
		setupAdatpers();
		resetPageValues();
		// 设置积分余额
		getDataFromServer();
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);//百度统计
//		MobileProbe.onResume(this);//CNZZ统计
		
		// 如果未登录，直接离开
				if (isUnlogin()) {
					return;
				}

				// 如果之前的状态是未登录，而今成为已登录，则要刷新页面
				if (isPreviousUnLoginStatus()) {
					resetPageValues();
				}

	}
	
	
	private void resetPageValues() {
		//modified by edwar 2012-11-07 
		//修改之前复杂的逻辑为， 如果没有头像，就去mama100服务器系统拿，如果内存有，就直接取
		avatar.setScaleType(ScaleType.FIT_CENTER);
		Bitmap bitmap = BasicApplication.getInstance().getAvatarBitmap();
		if(bitmap!=null){
		setAvatar(bitmap);
		}else{
			BasicApplication application = BasicApplication.getInstance();
			setAvatarIntoViews(
					application.getLastAvatarUrl(), RegPointHistoryActivity.this);
		}
		//modified end
		setUsername();
		setPointBalance(BasicApplication.getInstance().getLastRegpointBalance());

	}
	
	// 用于从非第三方登录入口进来，要获取头像
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
//							.getInstance().getResources(),
//							R.drawable.default_avatar);
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
					setAvatar(bitmap);
					//modified end
					
			// LogUtils.logi(TAG, "set avatar 3 - : "
			// + new Timestamp(System.currentTimeMillis()).toString());

		}

	
	
	private void setAvatar(Bitmap bitmap) {
		avatar.setImageBitmap(bitmap);
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

	private void setupTopBar() {
		setLeftButtonImage(R.drawable.selector_back);
		setTopLabel(R.string.reg_history);
	}

	private void getDataFromServer() {
		PointListReq request = new PointListReq();
		nextpage = getCurrent4DisplayPageNo();
		LogUtils.logi(AppConstants.PROGRESS_TAG,
				"nextpage ------------------------------------- " + nextpage);
		request.setPageNo(String.valueOf(nextpage));
		request.setPageSize(PAGE_SIZE);

		task = new CustomAsyncTask(this);
		// task.displayProgressDialog(R.string.doing_req_message);
		task.execute(request);
	}

	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			return PointProvider.getInstance(getApplicationContext())
					.getPointList((PointListReq) request);
		}

		@Override
		protected void handleResponse(BaseRes response) {

			// closeProgressDialog();
			if (canLoad) {
				if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
					makeText(response.getDesc());
					return;
				}

				PointListRes res = ((PointListRes) response);
				LogUtils.logi(AppConstants.PROGRESS_TAG, res.toString());


				if(!res.getPbalance().equals("-1")){
					//获取最新积分
					setPointBalance(res.getPbalance());
					BasicApplication.getInstance().setLastRegpointBalance(res.getPbalance());
				}

				// 第一次查询，获取总条数
				String sum = res.getCount();
				Integer total = Integer.parseInt(sum);
				if(total ==0){
//					makeText("您还没有任何记录");
					displayNoRecordText();
					return;
				}
				// 总条数第一次全部返回，以后查询都返回-1.
				if (total != null && total != -1) {
					totalCount = total;
					LogUtils.logi(AppConstants.PROGRESS_TAG,
							"总个数 ----------------------------------------------------"
									+ totalCount);
				}
				// msgList = res.getList();
				createTestData(res);
				if (regpointList.size() > 0) {
					LogUtils.logi(AppConstants.PROGRESS_TAG,
							"regpointList 个数---------------------------------"
									+ regpointList.size() + "    ,   "
									+ " 总个数 --------- " + totalCount);
					// 只有小于才能继续下载，等于的话也不能下载
					if (regpointList.size() < totalCount) {
						canLoad = true;
					} else {
						canLoad = false;
						((PulldownToRefreshListView) listview)
								.setDownloadAll(true);
					}
				}

			}
		}

		@Override
		public void onPostExecute(BaseRes response) {
			super.onPostExecute(response);
			// 调用 onRefreshComplete 当列表刷新完毕的 时候
			((PulldownToRefreshListView) listview).onRefreshComplete();
		}

	}

	public void createTestData() {
		if (regpointList == null)
			regpointList = new ArrayList<PointItem>();
		for (int i = 1; i <= 3; i++) {
			PointItem item = new PointItem();
			item.setDate("2012-03-04 14:23");
			item.setPname("合生元金装一阶段奶粉");
			if (i / 2 == 0) {
				item.setPoint("2000");
			} else {
				item.setPoint("-1000");
			}
			item.setType("" + i);
			regpointList.add(item);
			mAdapter.notifyDataSetChanged();
		}
	}

	public void createTestData(PointListRes res) {
		List<PointItem> list = res.getList();
		for (int i = 0; i < list.size(); i++) {
			PointItem item = list.get(i);
			regpointList.add(item);
			mAdapter.notifyDataSetChanged();
		}

		// regpointList = res.getList();
		// mAdapter.notifyDataSetChanged();
	}

	private void setupAdatpers() {
		this.listview = (ListView) findViewById(R.id.listview_regpoint);

		// 注册一个监听当列表要被松开时
		((PulldownToRefreshListView) listview)
				.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						getDataFromServer();
					}
				});

		mAdapter = new RegPointListAdapter(this, regpointList);
		listview.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();

	}

	private void setupViews() {
		
		avatar = ((ImageView) findViewById(R.id.regpoint_avatar));
		//设置提示文本
		no_record = (TextView) findViewById(R.id.no_record);
		hideNoRecordText();

		// 设置自助积分点击事件
		((ImageButton) findViewById(R.id.reg_diy))
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// startActivity(new Intent(getApplicationContext(),
						// RegPointYourselfActivity.class));

						startActivityForResult(new Intent(
								getApplicationContext(),
								RegPointYourselfActivity.class), REGPOINT_DIY);

					}
				});

	}

	// 设置积分余额
	private void setPointBalance(String balance) {
		regpoint_balance = (TextView) findViewById(R.id.regbalance); 
		String balance_html = "<font color=#808080>积分余额:</font> "
				+ "<font color=#ff6600>" + balance + "</font>" +"<font color=#808080>分</font> ";
		regpoint_balance.setText(Html.fromHtml(balance_html));
	}

	// 设置用户名
	private void setUsername() {
		regpoint_account = (TextView) findViewById(R.id.account);
//		String username = StorageUtils
//				.getLastLoginAccount(getApplicationContext());
		String mobile = BasicApplication.getInstance().getMobile();
		String username_html = "<font color=#808080>账户:</font> "
				+ "<font color=#000000>" + mobile + "</font>";
		regpoint_account.setText(Html.fromHtml(username_html));
	}

	// 控制，只有刚进来 或者 自助积分成功了， 才需要重新刷新列表。。节省不必要的刷新
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REGPOINT_DIY) {
			if (resultCode == RESULT_OK) {
				// 更新积分余额总值
				String balance = data.getStringExtra("sum");
				setPointBalance(balance);

				// 积分多了一条记录，需要重新刷新列表
				if (regpointList != null)
					regpointList.clear();
				canLoad = true;
				((PulldownToRefreshListView) listview).setDownloadAll(false);
				// 改变文本的值
				((PulldownToRefreshListView) listview).resetRefreshTextValue();
				getDataFromServer();
			}
		}
	}

	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		doClickLeftBtn();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		regpoint_balance = null;//释放Html.fromHtml引起的内存释放
		regpoint_account = null;//释放Html.fromHtml引起的内存释放
		if (regpointList != null) {
			regpointList.clear();
			regpointList = null;
			
		}
		
		if(task!=null&&task.isCancelled()==false){
			task.cancel(true);
			task = null;
			}
		
		
		if(mAdapter!=null){
			mAdapter.clearMemory();
			mAdapter = null;
		}
		
		listview = null;
		clearImageViewMomery(((ImageView) findViewById(R.id.regpoint_avatar)));

	}

	/************************************************************************
	 * 用于 判断 分页 的 方法
	 *************************************************************************/

	/**
	 * 获取正要准备显示的页号（马上要加载的下一页）
	 * 
	 * @return
	 */
	protected int getCurrent4DisplayPageNo() {

		int sizeOfItems = regpointList.size();
		if (sizeOfItems == 0) {
			return 1;
		}

		int currentPage = 0;
		int pageSize = Integer.valueOf(PAGE_SIZE);
		int visibleCount = sizeOfItems;

		if (visibleCount < pageSize && visibleCount % pageSize == 0) {
			currentPage = visibleCount / pageSize;
		} else {
			currentPage = visibleCount / pageSize + 1;
		}
		return currentPage;
	}

	/**
	 * 获取总页数
	 * 
	 * @return
	 */
	protected int getTotalPageCount() {

		int totalPage = 0;

		int pageSize = Integer.valueOf(PAGE_SIZE);

		if (totalCount % pageSize == 0) {
			totalPage = totalCount / pageSize;
		} else {
			totalPage = totalCount / pageSize + 1;
		}

		return totalPage;
	}
	
	
	private void hideNoRecordText() {
		if(no_record!=null)
		no_record.setVisibility(View.GONE);
	}
	
	private void displayNoRecordText() {
		if(no_record!=null){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					no_record.setVisibility(View.VISIBLE);
					no_record.setText(R.string.regpoint_history_record_none);
				}
			});
		}
	}

}

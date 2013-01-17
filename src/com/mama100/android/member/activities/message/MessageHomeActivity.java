/**
 * 
 */
package com.mama100.android.member.activities.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.mama100.android.member.R;
import com.mama100.android.member.activities.AsyncReqTask;
import com.mama100.android.member.activities.BaseActivity;
import com.mama100.android.member.activities.WebViewActivity;
import com.mama100.android.member.bean.MessageItem;
import com.mama100.android.member.businesslayer.MessageProvider;
import com.mama100.android.member.domain.base.BaseReq;
import com.mama100.android.member.domain.base.BaseRes;
import com.mama100.android.member.domain.message.MessageListReq;
import com.mama100.android.member.domain.message.MessageListRes;
import com.mama100.android.member.domain.sys.HomeReq;
import com.mama100.android.member.global.AppConstants;
import com.mama100.android.member.global.BasicApplication;
import com.mama100.android.member.global.DeviceResponseCode;
import com.mama100.android.member.util.DateHelper;
import com.mama100.android.member.util.LogUtils;
import com.mama100.android.member.widget.adapter.MessageListAdapter;
import com.mama100.android.member.widget.listview.PulldownAndupToRefreshListView;
import com.mama100.android.member.widget.listview.PulldownAndupToRefreshListView.OnRefreshListener;

/**
 * <p>
 * Description: MessageActivity.java
 * </p>
 * 
 * @author aihua.yan 2012-7-16 消息主界面
 */
public class MessageHomeActivity extends BaseActivity {
	private MessageListAdapter mAdapter;
	private List<MessageItem> pullMsgList = new ArrayList<MessageItem>();
	private List<MessageItem> pushMsgList = new ArrayList<MessageItem>();
	private List<MessageItem> totalMsgList = new ArrayList<MessageItem>();
	private int nextpage = 1; // 默认第一页
	private String PAGE_SIZE = "10";
	
	private PulldownAndupToRefreshListView pullListView;

	/*****************************************************************
	 * 查看消息详情
	 *****************************************************************/
	 final private int GET_DETAILS = 100; // 点击某一项，请求查看详情的code
	 private View selectedView; // 当前被点击的那一项的view
	 private MessageItem selectedItem; // 当前被点击的那一项的view
	/*******************************************************************
	 * 从服务器下拉获取消息 用到的变量
	 *********************************************************************/
	// 上拉符合条件的总记录数
	private int totalPullCount = 0;
	// 下拉符合条件的总记录数
	private int totalPushCount = 0;
		
	public boolean canLoad = true; // 还可以继续下载
	
	//进入页面的时间
	private Date date = new Date();
	
	//保存用户上一次刷新数据的时间
	private String startTime = getSpecTime();

	/********************************************************************
	 * 消息通知栏 如果仅有一条消息时，用到的变量
	 *******************************************************************/
	String messageId="";
	
	/***************************************************************
	 * 释放内存的变量
	 ***********************************************************/
	private CustomAsyncTask task = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.msg_home);
		setBackgroundPicture(R.drawable.bg_wall);
		setupTopBar();
		setupAdatpers();
		getDataFromServer();
		
		
		/**
		 * 用于之前的消息通知-->消息列表-->自动进入(消息详情)情况 -START
		 */
//		// 获取消息的id,获取pendingIntent传过来的requestCode
//		messageId = getIntent().getStringExtra("id");
//		int count = BasicApplication.getInstance().getUnreadMsgSum();
//		if(count ==1&& messageId!=null&&messageId!=""){
//		forwardToMessageDetails(messageId);	
//		}
		/**
		 * 用于之前的消息通知-->消息列表-->自动进入(消息详情)情况 -END
		 */
		
		// 设置消息列表页已经打开
		BasicApplication.getInstance().setMessageListAlreadyStart(true);
		
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
	}

	//消息列表-->点击进入(消息详情)情况
	private void forwardToMessageDetails(String id) {
		// 传id给WebViewActivity
		Intent toWebAct = new Intent(getApplicationContext(),WebViewActivity.class).putExtra(WebViewActivity.ID,id);
		startActivityForResult(toWebAct, 100);
	}

	@Override
	public void onResume() {
		super.onResume();
		StatService.onResume(this);//百度统计
//		MobileProbe.onResume(this);//CNZZ统计
		
	}
	
	@Override
	public void onPause() {
		super.onPause();
		StatService.onPause(this);//百度统计
//		MobileProbe.onPause(this);//CNZZ统计
	}
	
	
	
	/**
	 * 下拉刷新开始时间节点
	 * @return
	 */
	public String getStartTime() {
		return startTime;
	}
	
	/**
	 * 下拉刷新结束时间节点
	 * @return
	 */
	public String getEndTime() {
		//永远为获取当前时间
		return DateHelper.dateToStr(new Date(), "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 进入页面时间
	 * @return
	 */
	public String getSpecTime(){
		return DateHelper.dateToStr(date, "yyyy-MM-dd HH:mm:ss");
	}


	private void setupAdatpers() {

		pullListView = (PulldownAndupToRefreshListView) findViewById(R.id.listview_msg);

		// 注册一个监听当列表要被松开时
		pullListView.setOnRefreshListener(new OnRefreshListener() {
					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						// new GetDataTask().execute();
						getDataFromServer();
					}
				});

		mAdapter = new MessageListAdapter(this, totalMsgList);
		pullListView.setAdapter(mAdapter);
		pullListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				//当前选中的对象
				selectedItem = (MessageItem)parent.getItemAtPosition(position);
				//当前选中的view				
				selectedView = view;
				
				//跳转到消息详细页面
				forwardToMessageDetails(selectedItem.getId());
			}

		});
		
		mAdapter.notifyDataSetChanged();
	}


	private void setupTopBar() {
		setLeftButtonImage(R.drawable.selector_back);
		setTopLabel(R.string.message_title);

	}

	// 从服务器获取数据
	private void getDataFromServer() {

			MessageListReq request = new MessageListReq();
			
			//如果是下拉
			if(pullListView.isPullDown()){
				nextpage = getCurrent4DisplayPageNoByPull();
				request.setSpecTimeStr(getSpecTime());
			}else{
				nextpage = getCurrent4DisplayPageNoByPush();
				request.setStartTimeStr(getStartTime());
				request.setEndTimeStr(getEndTime());
			}
			
			request.setPageNo(String.valueOf(nextpage));
			request.setPageSize(PAGE_SIZE);

			task = new CustomAsyncTask(this);
			task.execute(request);
	}

	class CustomAsyncTask extends AsyncReqTask {
		public CustomAsyncTask(Context context) {
			super(context);
		}

		@Override
		protected BaseRes doRequest(BaseReq request) {
			return MessageProvider.getInstance(getApplicationContext())
					.getMessageList((MessageListReq) request);
		}

		@Override
		protected void handleResponse(BaseRes response) {
//			closeProgressDialog();
			if (!response.getCode().equals(DeviceResponseCode.SUCCESS)) {
				makeText(response.getDesc());
				return;
			}

			MessageListRes res = ((MessageListRes) response);
			LogUtils.logi(AppConstants.PROGRESS_TAG, res.toString());
			
			
			// 第一次查询，获取总条数
			if(pullListView.isPullDown()){
				String sum = res.getCount();
				Integer total = Integer.parseInt(sum);
				if(total ==0){
					pullListView.setHasRecord(false);
					return;
				}
				
				// 总条数第一次全部返回，以后查询都返回-1.
				if (total != null && total != -1) {
					totalPullCount = total;
				}
				createTestData(res);
				pullListView.setPullDownloadAll(pullMsgList.size() == totalPullCount);
			}else if(pullListView.isPushDown()){
				String sum = res.getCount();
				Integer total = Integer.parseInt(sum);
				
				LogUtils.logd("D", "total = " + total);
				
				if(total ==0){
					Toast.makeText(getApplicationContext(), "没有新的消息", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if (total != null && total != -1) {
					totalPushCount = total;
				}
				
				//上拉刷新完成的时候 把结束时间设成开始时间
				if(totalPushCount == pushMsgList.size()){
					Toast.makeText(getApplicationContext(), "没有新的消息", Toast.LENGTH_SHORT).show();
					
					//pushMsgList.clear();
					
					startTime = getEndTime();
					
					return;
				}
				createTestData(res);
			}
		}

		@Override
		public void onPostExecute(BaseRes response) {
			super.onPostExecute(response);
			// 调用 onRefreshComplete 当列表刷新完毕的 时候
				pullListView.onRefreshComplete();
		}

	}

	@Override
	public void doClickLeftBtn() {
		super.doClickLeftBtn();
		if(BasicApplication.getInstance().isHomepageAlreadyStart()){
			finish();
		}else{
			//消息通知栏进来的情况
			HomeReq request = new HomeReq();
			HomepageAsyncTask task = new HomepageAsyncTask(this);
			task.displayProgressDialog(R.string.doing_req_message);
			task.execute(request);
		}
	}

	public void createTestData(MessageListRes res) {
		List<MessageItem> list = res.getList();
		
		if(pullListView.isPullDown()){
			for (int i = 0; i < list.size(); i++) {
				MessageItem item = list.get(i);
				pullMsgList.add(item);
				totalMsgList.add(item);
			}
		}else if(pullListView.isPushDown()){
			for (int i = 0; i < list.size(); i++) {
				MessageItem item = list.get(i);
				pushMsgList.add(item);
				totalMsgList.add(i, item);
			}
		}
		
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		doClickLeftBtn();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		//清除上拉列表
		if (pullMsgList != null) {
			pullMsgList.clear();
			pullMsgList = null;
		}
				
		//清除下拉列表
		if (pushMsgList != null) {
			pushMsgList.clear();
			pushMsgList = null;
		}		
		
		//清除总列表
		if (totalMsgList != null) {
			totalMsgList.clear();
			totalMsgList = null;
		}
		
		if(task!=null&&task.isCancelled()==false){
			task.cancel(true);
			task = null;
		}
		
		if(mAdapter!=null){
			mAdapter.clearMemory();
			mAdapter= null;
		}
		
		selectedItem = null;
		selectedView = null;
		pullListView = null;
		// 设置消息列表页已经关闭
		BasicApplication.getInstance().setMessageListAlreadyStart(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GET_DETAILS) {
			if (resultCode == RESULT_OK) {
				int isRead = Integer.valueOf(selectedItem.getIsRead());
				if (isRead == -1) {
					//更新未读消息
					int count = BasicApplication.getInstance().getUnreadMsgSum();
					BasicApplication.getInstance().setUnreadMsgSum(count-1); 
					
					// 更新被点击项目的图片
					ImageView imageView = (ImageView) selectedView.findViewById(R.id.msg_icon);
					imageView.setImageResource(R.drawable.msg_icon_read);
				}
			}
		}
	}

	
	/************************************************************************
	 * 用于 判断 分页 的 方法
	 *************************************************************************/

	/**
	 * 获取正要准备显示的页号（马上要加载的下一页）
	 * 
	 * @return
	 */
	protected int getCurrent4DisplayPageNoByPull() {

		int sizeOfItems = pullMsgList.size();
		if (sizeOfItems == 0) {
			return 1;
		}

		int currentPage = 0;
		int pageSize = Integer.valueOf(PAGE_SIZE);
		int visibleCount = sizeOfItems;

		if (visibleCount<pageSize&&visibleCount % pageSize == 0) {
			currentPage = visibleCount / pageSize;
		} else {
			currentPage = visibleCount / pageSize + 1;
		}
		return currentPage;
	}

	
	/**
	 * 获取正要准备显示的页号（马上要加载的下一页）
	 * 
	 * @return
	 */
	protected int getCurrent4DisplayPageNoByPush() {

		int sizeOfItems = pushMsgList.size();
		if (sizeOfItems == 0) {
			return 1;
		}

		int currentPage = 0;
		int pageSize = Integer.valueOf(PAGE_SIZE);
		int visibleCount = sizeOfItems;

		if (visibleCount<pageSize&&visibleCount % pageSize == 0) {
			currentPage = visibleCount / pageSize;
		} else {
			currentPage = visibleCount / pageSize + 1;
		}
		return currentPage;
	}
	
}

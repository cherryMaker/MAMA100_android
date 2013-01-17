package com.mama100.android.member.widget.listview;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mama100.android.member.R;
import com.mama100.android.member.util.LogUtils;

/****************************************
 * 总结： 2012-10-12 aihua.yan  
 * 仅限于上拉 刷新 的逻辑。。
 * onScroll()方法里，没有去触发刷新，触发刷新是在onTouch()方法里Action_up时做的。
 * 而onScroll()方法只是根据当前底部刷新栏的位置设置不同的状态，从而为onTouchEvent()的Action_up提供准备。。
 * 
 * 状态TAP_TO_REFRESH 是在刷新完毕，需要设置resetFooter()时，才被设置的。
 * 状态REFRESH只有在刷新时，才会被设置;
 *  
 * 还有，将要执行刷新操作的时候，要判断当前是否已经是刷新状态，如果已经是刷新状态，则不执行刷新。
 * 
 * onScroll()里,系统会根据底部刷新栏是否被拉离底线，来设置状态是RELEASE_TO_REFRESH或者PULL_TO_REFRESH,或者TAP_TO_REFRESH的。
 * 具体解释，@see onScroll()
 * onTouchEvent()里，如果当前状态不是REFRESHING，但是底部栏已经被拉上来，则系统就会触发刷新，且设置状态为REFRESHING.如果底部栏压根
 * 没有冒上来，则系统认为当前状态为TAP_TO_REFRESH.
 * 
 * 
 * 
 * 假定 A = getMeasuredHeight();  当前屏幕高度 , 
 *     B = mRefreshViewHeight;   底部栏自身的高度。。
 *     X = mRefreshView.getTop();底部栏最上端距离屏幕顶部的距离。
 *     20,代码里写死的一个值。
 *     关键点： A-B-20 , A-B   
 * 那么 如果在一个横轴上标记这两个关键点， 则就有三个分区： 
 * (-&, A-B-20], (A-B-20, A-B], (A-B, +&)  注:& 代表无穷大
 * 根据底部刷新栏距离屏幕顶部的距离判断，其X就对应三个分区，有三种位置状态：
 * X在(A-B, +&), X在(A-B-20, A-B], X在 (-&, A-B-20]
 * 则：
 * 整理 onScroll()里状态S变化的逻辑，就是。
 * 1，X在(-&, A-B-20]时，执行 (S!=RELEASE_TO_REFRESH)?RELEASE_TO_REFRESH:TAP_TO_REFRESH;
 * 3，X在(A-B, +&)时，执行 (S!=PULL_TO_REFRESH)?PULL_TO_REFRESH:TAP_TO_REFRESH;
 * 2，X在(A-B-20, A-B]时，执行 
 * (S!=RELEASE_TO_REFRESH)?RELEASE_TO_REFRESH:PULL_TO_REFRESH;
 * 
 * 整理 onTouchEvent()里状态S变化的逻辑，就是。
 * 1，X在(-&, A-B]时，执行 (S==RELEASE_TO_REFRESH)?REFRESHING:S;
 * 3，X在(A-B, +&)时，执行 S = TAP_TO_REFRESH;
 ****************************************/

/****************************************
 * 李阳 后来增补的 下拉刷新的逻辑以后再写进来。
 ****************************************/

public class PulldownAndupToRefreshListView extends ListView implements
		OnScrollListener {

	// 四种状态：点击刷新，下拉刷新，释放刷新，正在刷新
	private static final int TAP_TO_REFRESH = 1;
	private static final int PULL_TO_REFRESH = 2;
	private static final int RELEASE_TO_REFRESH = 3;
	private static final int REFRESHING = 4;

	private OnRefreshListener mOnRefreshListener;

	/**
	 * Listener that will receive notifications every time the list scrolls.
	 */
	private OnScrollListener mOnScrollListener;
	private LayoutInflater mInflater;

	// 上拉使用到的组件
	private RelativeLayout mRefreshFooterView;
	private TextView mRefreshFooterViewText;
	private ImageView mRefreshFooterViewImage;
	private ProgressBar mRefreshFooterViewProgress;
	private TextView mRefreshFooterViewLastUpdated;

	// 下拉使用到的组件
	private RelativeLayout mRefreshHeaderView;
	private TextView mRefreshHeaderViewText;
	private ImageView mRefreshHeaderViewImage;
	private ProgressBar mRefreshHeaderViewProgress;
	private TextView mRefreshHeaderViewLastUpdated;

	//
	private int mCurrentScrollState;
	private int mRefreshState;

	// 用来旋转动画
	private RotateAnimation mFlipAnimation;
	private RotateAnimation mReverseFlipAnimation;

	private int mRefreshHeaderViewHeight;
	private int mRefreshFooterViewHeight;
	private int mRefreshOriginalTopPadding;
	private int mRefreshOriginalBottomPadding;
	private int mLastMotionY;

	/***********************************************
	 * 如果已经向下全部下载完，则禁止出现下拉效果 用到的变量
	 ************************************************/
	private boolean isPulldownAll = false;

	/***********************************************
	 * 标示是否有记录存在，用到的变量
	 ************************************************/
	private boolean hasRecord = true;

	// 判断用户是否在进行上拉、或者是点击footer
	// 上拉或者是点击footer的时候状态都要更新为true
	private boolean isPullDown = true;

	// 判断用户是否在进行下拉、或者是点击header
	// 上拉或者是点击header的时候状态都要更新为true
	private boolean isPullUp = true;

	public boolean isPulldownAll() {
		return isPulldownAll;
	}

	public void setPullDownloadAll(boolean isPulldownAll) {
		this.isPulldownAll = isPulldownAll;
	}

	public boolean isPullDown() {
		return isPullDown;
	}

	public void setPullDown(boolean isPullDown) {
		this.isPullDown = isPullDown;
	}

	public boolean isPushDown() {
		return isPullUp;
	}

	public void setPushDown(boolean isPullUp) {
		this.isPullUp = isPullUp;
	}

	public boolean hasRecord() {
		return hasRecord;
	}

	public void setHasRecord(boolean hasRecord) {
		this.hasRecord = hasRecord;
	}

	/**
	 * 设置正在上拉 或者是正在点击footer
	 */
	public void setPull() {
		setPullDown(true);
		setPushDown(false);
	}

	/**
	 * 设置正在下拉 或者是正在点击header
	 */
	public void setPush() {
		setPushDown(true);
		setPullDown(false);
	}

	/**
	 * 
	 * @param context
	 */
	public PulldownAndupToRefreshListView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 */
	public PulldownAndupToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public PulldownAndupToRefreshListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		// Load all of the animations we need in code rather than through XML
		// 声明所有我们必须的动画在代码里而不是在xml里。
		// 正旋转
		// 从零度逆时针转到180度，旋转中心(x,y)都为相对自身的一半的点的(x,y)（等于以中点为圆心）
		mFlipAnimation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mFlipAnimation.setInterpolator(new LinearInterpolator());// 设置加速曲线
		mFlipAnimation.setDuration(250);// 设置旋转时间
		mFlipAnimation.setFillAfter(true);// 保留在终止位置
		// 反旋转
		mReverseFlipAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
		mReverseFlipAnimation.setDuration(250);
		mReverseFlipAnimation.setFillAfter(true);

		// 视图加载器
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 下拉加载视图
		mRefreshFooterView = (RelativeLayout) mInflater.inflate(
				R.layout.pull_to_refresh_footer, this, false);
		// <string name="pull_to_refresh_pull_label">下拉刷新...</string>
		// <string name="pull_to_refresh_release_label">松手刷新...</string>
		// <string name="pull_to_refresh_refreshing_label">正在下载...</string>
		// <string name="pull_to_refresh_tap_label">点击刷新...</string>
		// 文本，就是所有的文本。如上：
		mRefreshFooterViewText = (TextView) mRefreshFooterView
				.findViewById(R.id.footer_text);
		// 下拉箭头刷新图片
		mRefreshFooterViewImage = (ImageView) mRefreshFooterView
				.findViewById(R.id.footer_image);
		// 下载滚动进度栏
		mRefreshFooterViewProgress = (ProgressBar) mRefreshFooterView
				.findViewById(R.id.footer_progress);
		// 最后更新文本， 目前还没有用
		mRefreshFooterViewLastUpdated = (TextView) mRefreshFooterView
				.findViewById(R.id.footer_updated);

		// 下拉箭头的最小高度50
		mRefreshFooterViewImage.setMinimumHeight(50);
		// 设置点击监听器
		mRefreshFooterView.setOnClickListener(new OnClickRefreshListener());

		// 视图加载器
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// 下拉加载视图
		mRefreshHeaderView = (RelativeLayout) mInflater.inflate(
				R.layout.pull_to_refresh_header, this, false);
		// <string name="pull_to_refresh_pull_label">下拉刷新...</string>
		// <string name="pull_to_refresh_release_label">松手刷新...</string>
		// <string name="pull_to_refresh_refreshing_label">正在下载...</string>
		// <string name="pull_to_refresh_tap_label">点击刷新...</string>
		// 文本，就是所有的文本。如上：
		mRefreshHeaderViewText = (TextView) mRefreshHeaderView
				.findViewById(R.id.header_text);
		// 下拉箭头刷新图片
		mRefreshHeaderViewImage = (ImageView) mRefreshHeaderView
				.findViewById(R.id.header_image);
		// 下载滚动进度栏
		mRefreshHeaderViewProgress = (ProgressBar) mRefreshHeaderView
				.findViewById(R.id.header_progress);
		// 最后更新文本， 目前还没有用
		mRefreshHeaderViewLastUpdated = (TextView) mRefreshHeaderView
				.findViewById(R.id.header_updated);

		// 下拉箭头的最小高度50
		mRefreshHeaderViewImage.setMinimumHeight(50);
		// 设置点击监听器
		mRefreshHeaderView.setOnClickListener(new OnClickRefreshListener());

		// modify by edwar 2012-08-23 start
		/******************
		 * // //初始状态默认为点击刷新 // mRefreshState = TAP_TO_REFRESH;
		 */
		addHeaderView(mRefreshHeaderView);
		// 将下拉栏放在footerView里
		addFooterView(mRefreshFooterView);

		measureView(mRefreshHeaderView);
		measureView(mRefreshFooterView);

		// 获取顶部和底部的padding
		mRefreshOriginalTopPadding = mRefreshHeaderView.getPaddingTop(); // 15px,10dp
		mRefreshOriginalBottomPadding = mRefreshFooterView.getPaddingBottom();// 23px,15dp

		mRefreshFooterViewHeight = mRefreshFooterView.getMeasuredHeight();// 83px
		mRefreshHeaderViewHeight = mRefreshHeaderView.getMeasuredHeight();// 83px

		// 设置滚动监听事件
		super.setOnScrollListener(this);

		prepareFooterForRefresh();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ListView#setAdapter(android.widget.ListAdapter)
	 */
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}

	/**
	 * Set the listener that will receive notifications every time the list
	 * scrolls. 这个目前没用到
	 * 
	 * @param l
	 *            The scroll listener.
	 */
	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mOnScrollListener = l;
	}

	/**
	 * Register a callback to be invoked when this list should be refreshed.
	 * 就是在子类里面设置回调
	 * 
	 * @param onRefreshListener
	 *            The callback to run.
	 */
	public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
		mOnRefreshListener = onRefreshListener;
	}

	/**
	 * Set a text to represent when the list was last updated.
	 * 展现一个文本当列表被最后一次更新时。
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
	public void setFooterLastUpdated(CharSequence lastUpdated) {
		if (lastUpdated == null) {
			mRefreshFooterViewLastUpdated.setVisibility(View.GONE);
		} else if (isPullDown) {
			mRefreshFooterViewLastUpdated.setVisibility(View.VISIBLE);
			mRefreshFooterViewLastUpdated.setText(lastUpdated);
		}
	}

	/**
	 * Set a text to represent when the list was last updated.
	 * 展现一个文本当列表被最后一次更新时。
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
	public void setHeaderLastUpdated(CharSequence lastUpdated) {
		if (lastUpdated == null) {
			mRefreshHeaderViewLastUpdated.setVisibility(View.GONE);
		} else if (isPullDown) {
			mRefreshHeaderViewLastUpdated.setVisibility(View.VISIBLE);
			mRefreshHeaderViewLastUpdated.setText(lastUpdated);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int y = (int) event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:

			if (!isVerticalScrollBarEnabled()) {// 判断是否允许画右边滚条
				setVerticalScrollBarEnabled(true);
			}
			// 获取目前可见范围里最后一个项目的位置，如果等于所获得的列表总数， 且目前不处于刷新中状态时
			if (getLastVisiblePosition() == getAdapter().getCount() - 1
					&& mRefreshState != REFRESHING) {

				if (!isPulldownAll()) {
					if ((mRefreshFooterView.getTop() <= getMeasuredHeight()
							- mRefreshFooterViewHeight)
							&& mRefreshState == RELEASE_TO_REFRESH) {
						// Initiate the refresh
						mRefreshState = REFRESHING;
						// 为新一轮刷新做准备
						prepareFooterForRefresh();
						// 执行子类的 下载代码
						onRefresh();
					} else if (mRefreshFooterView.getTop() > getMeasuredHeight()
							- mRefreshFooterViewHeight) {
						// Abort refresh and scroll down below the refresh view
						resetFooter();
						// setSelection(1);
						if (getFooterViewsCount() > 0) {
							setSelectionFromTop(getAdapter().getCount() - 1,
									(this.getMeasuredHeight()));
						}
					}
				}
			} else if (getFirstVisiblePosition() == 0
					&& mRefreshState != REFRESHING) {
				if (mRefreshHeaderView.getBottom() >= mRefreshHeaderViewHeight
						&& mRefreshState == RELEASE_TO_REFRESH) {

					mRefreshState = REFRESHING;
					prepareHeaderForRefresh();
					onRefresh();
				} else if (mRefreshHeaderView.getBottom() < mRefreshHeaderViewHeight
						&& mRefreshState == PULL_TO_REFRESH) {
					resetHeader();
				}
			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionY = y;
			break;

		case MotionEvent.ACTION_MOVE:
			applyHeaderPadding(event);
			break;
		}
		return super.onTouchEvent(event);
	}

	private void applyHeaderPadding(MotionEvent ev) {
		final int historySize = ev.getHistorySize();// 在ActionMove轨迹上的历史点总数

		// Workaround for getPointerCount() which is unavailable in 1.5
		// (it's always 1 in 1.5)
		int pointerCount = 1;
		try {
			Method method = MotionEvent.class.getMethod("getPointerCount");// java反射
			pointerCount = (Integer) method.invoke(ev);
		} catch (NoSuchMethodException e) {
			pointerCount = 1;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IllegalAccessException e) {
			System.err.println("unexpected " + e);
		} catch (InvocationTargetException e) {
			System.err.println("unexpected " + e);
		}

		Log.i("PullToRefreshListView", "historySize:" + historySize);
		Log.i("PullToRefreshListView", "pointerCount:" + pointerCount);
		Log.i("PullToRefreshListView", "  ");
		Log.i("PullToRefreshListView", "  ");
		Log.i("PullToRefreshListView", "  ");

		for (int h = 0; h < historySize; h++) {
			for (int p = 0; p < pointerCount; p++) {
				if (mRefreshState == RELEASE_TO_REFRESH) {
					if (isVerticalFadingEdgeEnabled()) {
						setVerticalScrollBarEnabled(false);
					}

					int historicalY = 0;
					try {
						// For Android > 2.0
						Method method = MotionEvent.class.getMethod(
								"getHistoricalY", Integer.TYPE, Integer.TYPE);
						historicalY = ((Float) method.invoke(ev, p, h))
								.intValue();
					} catch (NoSuchMethodException e) {
						// For Android < 2.0
						historicalY = (int) (ev.getHistoricalY(h));
					} catch (IllegalArgumentException e) {
						throw e;
					} catch (IllegalAccessException e) {
						System.err.println("unexpected " + e);
					} catch (InvocationTargetException e) {
						System.err.println("unexpected " + e);
					}

					// Calculate the padding to apply, we divide by 1.7 to
					// simulate a more resistant effect during pull.
					int bottomPadding = (int) (((historicalY + mLastMotionY) + mRefreshFooterViewHeight) / 1.7);

					mRefreshFooterView
							.setPadding(mRefreshFooterView.getPaddingLeft(),
									mRefreshFooterView.getPaddingTop(),
									mRefreshFooterView.getPaddingRight(),
									bottomPadding);
				}
			}
		}
	}

	/**
	 * Sets the header padding back to original size. 设回 头的padding
	 */
	private void resetFooterPadding() {
		mRefreshFooterView.setPadding(mRefreshFooterView.getPaddingLeft(),
				mRefreshFooterView.getPaddingTop(),
				mRefreshFooterView.getPaddingRight(),
				mRefreshOriginalBottomPadding);
	}

	/**
	 * Resets the header to the original state. 设置头为最初状态
	 */
	public void resetFooter() {
		// 当状态不是点击刷新时，设置回默认 点击刷新
		if (mRefreshState != TAP_TO_REFRESH) {
			mRefreshState = TAP_TO_REFRESH;

			resetFooterPadding();

			// Set refresh view text to the pull label
			// 设置文本为 点击刷新
			if (hasRecord) {
				if (!isPulldownAll()) {
					mRefreshFooterViewText
							.setText(R.string.pull_to_refresh_tap_label);
				} else {
					mRefreshFooterViewText
							.setText(R.string.pull_to_refresh_tap_label3);
				}
			} else {
				mRefreshFooterViewText.setText(R.string.no_message_record);
			}

			// Replace refresh drawable with arrow drawable
			// 将图片改为 下拉箭头
			mRefreshFooterViewImage
					.setImageResource(R.drawable.ic_pulltorefresh_arrow);
			// Clear the full rotation animation
			// 清除旋转动画
			mRefreshFooterViewImage.clearAnimation();
			// Hide progress bar and arrow.
			// 隐藏滚动进度栏和箭头
			mRefreshFooterViewImage.setVisibility(View.GONE);
			mRefreshFooterViewProgress.setVisibility(View.GONE);
		}
	}

	/**
	 * Sets the header padding back to original size. 设回 头的padding
	 */
	private void resetHeaderPadding() {
		mRefreshHeaderView.setPadding(mRefreshHeaderView.getPaddingLeft(),
				mRefreshOriginalTopPadding,
				mRefreshHeaderView.getPaddingRight(),
				mRefreshHeaderView.getPaddingBottom());
	}

	/**
	 * Resets the header to the original state. 设置头为最初状态
	 */
	public void resetHeader() {

		LogUtils.logd("D", "mRefreshState != TAP_TO_REFRESH"
				+ (mRefreshState != TAP_TO_REFRESH));

		// 当状态不是点击刷新时，设置回默认 点击刷新
		if (mRefreshState != TAP_TO_REFRESH) {
			mRefreshState = TAP_TO_REFRESH;

			resetHeaderPadding();

			// 视图个数大于1的时候才执行此操作
			// 以防抛出异常
			if (getCount() > 1) {
				setSelection(1);
			}

			// Set refresh view text to the pull label
			// 设置文本为 点击刷新
			mRefreshHeaderViewText.setText(R.string.pull_to_refresh_tap_label4);
			// Replace refresh drawable with arrow drawable
			// 将图片改为 下拉箭头
			mRefreshHeaderViewImage
					.setImageResource(R.drawable.ic_pulltorefresh_arrow);
			// Clear the full rotation animation
			// 清除旋转动画
			mRefreshHeaderViewImage.clearAnimation();
			// Hide progress bar and arrow.
			// 隐藏滚动进度栏和箭头
			mRefreshHeaderViewImage.setVisibility(View.GONE);
			mRefreshHeaderViewProgress.setVisibility(View.GONE);
		}
	}

	private void measureView(View child) {
		// 获取子类自身的布局需求
		ViewGroup.LayoutParams p = child.getLayoutParams();// height-wrapcontent:-2,
															// width-fillparent:-1
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		// 跟父类沟通，得到的许可宽度
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);

		// 跟父类沟通，得到的许可高度
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

		// 当下拉栏完全可见时，改变文本为“松手刷新”， 然后滑动下拉箭头
		if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
				&& mRefreshState != REFRESHING) {
			if (firstVisibleItem + visibleItemCount == totalItemCount) {

				setPull();

				if (!isPulldownAll()) {
					mRefreshFooterViewImage.setVisibility(View.VISIBLE);
					if ((// footerview完全显示时

							// footerview完全显示时
							// 补充:2012-10-12 底部栏完全冒上来，在屏幕底线上面的时候。
							// getMeasuredHeight()指当前屏幕高度,
							// mRefreshView.getTop()是指底部栏最上端距离屏幕顶部的距离。
							// mRefreshViewHeight 是指底部栏自身的高度。。

							mRefreshFooterView.getTop() <= getMeasuredHeight()
									- mRefreshFooterViewHeight)// 716top,mesuare751,viewheight
																// 83
							&& mRefreshState != RELEASE_TO_REFRESH) {
						// 设置文本 - 为 松手刷新
						mRefreshFooterViewText
								.setText(R.string.pull_to_refresh_release_label);
						mRefreshFooterViewImage.clearAnimation();
						mRefreshFooterViewImage.startAnimation(mFlipAnimation);
						mRefreshState = RELEASE_TO_REFRESH;
					} else if (// footerview露出 //todo

					// footerview露出 //todo
					// 补充:2012-10-12 底部栏完全冒上来，但还没有冒太远，还没冒出20以外，还在20远以内的时候。
					// getMeasuredHeight()指当前屏幕高度,
					// mRefreshView.getTop()是指底部栏最上端距离屏幕顶部的距离。
					// mRefreshViewHeight 是指底部栏自身的高度。。
					mRefreshFooterView.getTop() > getMeasuredHeight() - 20
							- mRefreshFooterViewHeight
							&& mRefreshState != PULL_TO_REFRESH) {
						// 设置文本 - 为上拉刷新
						mRefreshFooterViewText
								.setText(R.string.pull_to_refresh_pull_label);
						if (mRefreshState != TAP_TO_REFRESH) {
							mRefreshFooterViewImage.clearAnimation();
							mRefreshFooterViewImage
									.startAnimation(mReverseFlipAnimation);
						}
						mRefreshState = PULL_TO_REFRESH;
					}
				}
			} else if (firstVisibleItem == 0) {

				setPush();

				mRefreshHeaderViewImage.setVisibility(View.VISIBLE);
				if (mRefreshHeaderView.getBottom() >= mRefreshHeaderViewHeight
						&& mRefreshState != RELEASE_TO_REFRESH) {
					mRefreshHeaderViewText
							.setText(R.string.pull_to_refresh_release_label);
					mRefreshHeaderViewImage.clearAnimation();
					mRefreshHeaderViewImage.startAnimation(mFlipAnimation);
					mRefreshState = RELEASE_TO_REFRESH;
				} else if (mRefreshHeaderView.getBottom() < mRefreshHeaderViewHeight + 20
						&& mRefreshState != PULL_TO_REFRESH) {
					mRefreshHeaderViewText
							.setText(R.string.pull_to_refresh_pull_label1);
					if (mRefreshState != TAP_TO_REFRESH) {
						mRefreshHeaderViewImage.clearAnimation();
						mRefreshHeaderViewImage
								.startAnimation(mReverseFlipAnimation);
					}
					mRefreshState = PULL_TO_REFRESH;
				}
			}
		} else if (mCurrentScrollState == SCROLL_STATE_FLING
				&& getLastVisiblePosition() >= getAdapter().getCount() - 1
				&& mRefreshState != REFRESHING) {

			if (getFooterViewsCount() > 0) {
				setSelectionFromTop(getAdapter().getCount() - 1,
						(this.getMeasuredHeight()));// 设置最后一项被选中，且距离listview顶部距离751
			}
		}

		if (mOnScrollListener != null) {
			mOnScrollListener.onScroll(view, firstVisibleItem,
					visibleItemCount, totalItemCount);
		}
	}

	// 获取该下拉视图
	public View getLoadBarView() {
		return mRefreshFooterView;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		mCurrentScrollState = scrollState;

		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	// 为新一次刷新做准备
	public void prepareFooterForRefresh() {
		resetFooterPadding();
		// 隐藏下拉箭头
		mRefreshFooterViewImage.setVisibility(View.GONE);
		// We need this hack, otherwise it will keep the previous drawable.
		// 删掉下拉箭头的背景，否则会保存之前的drawable.
		mRefreshFooterViewImage.setImageDrawable(null);
		// 显示滚动进度栏
		mRefreshFooterViewProgress.setVisibility(View.VISIBLE);

		// Set refresh view text to the refreshing label
		// 设置文本为 正在下载
		mRefreshFooterViewText
				.setText(R.string.pull_to_refresh_refreshing_label);

		// 更改状态为 正在刷新
		mRefreshState = REFRESHING;
	}

	// 为新一次刷新做准备
	public void prepareHeaderForRefresh() {

		resetHeaderPadding();
		// 隐藏下拉箭头
		mRefreshHeaderViewImage.setVisibility(View.GONE);
		// We need this hack, otherwise it will keep the previous drawable.
		// 删掉下拉箭头的背景，否则会保存之前的drawable.
		mRefreshHeaderViewImage.setImageDrawable(null);
		// 显示滚动进度栏
		mRefreshHeaderViewProgress.setVisibility(View.VISIBLE);

		// Set refresh view text to the refreshing label
		// 设置文本为 正在下载
		mRefreshHeaderViewText
				.setText(R.string.pull_to_refresh_refreshing_label);

		// 更改状态为 正在刷新
		mRefreshState = REFRESHING;
	}

	// 执行子类的 下载代码
	public void onRefresh() {
		if (mOnRefreshListener != null) {
			mOnRefreshListener.onRefresh();
		}
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 * 
	 * @param lastUpdated
	 *            Last updated at.
	 */
	public void onRefreshComplete(CharSequence lastUpdated) {
		if (isPullDown) {
			if (!isPulldownAll()) {
				setFooterLastUpdated(lastUpdated);
			} else {
				setFooterLastUpdated("已经是最后一条");
			}
		} else {
			setHeaderLastUpdated(lastUpdated);
		}
		onRefreshComplete();
	}

	/**
	 * Resets the list to a normal state after a refresh.
	 */
	public void onRefreshComplete() {

		if (isPullDown) {
			// 如果是上拉操作，则重置底部footer
			resetFooter();
		} else if (isPullUp) {

			// 如果是下拉操作，则重置顶部header
			resetHeader();
		}
	}

	/**
	 * Invoked when the refresh view is clicked on. This is mainly used when
	 * there's only a few items in the list and it's not possible to drag the
	 * list.
	 */
	private class OnClickRefreshListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == mRefreshFooterView) {

				// 设置为点击header的状态
				setPull();

				// 在存在记录且未下载完的情况下可执行
				if (hasRecord && !isPulldownAll()) {
					if (mRefreshState != REFRESHING) {
						prepareFooterForRefresh();
						onRefresh();
					}
				}
			} else if (v == mRefreshHeaderView) {
				if (mRefreshState != REFRESHING) {
					// 设置为点击header的状态
					setPush();

					prepareHeaderForRefresh();

					onRefresh();
				}
			}
		}
	}

	/**
	 * Interface definition for a callback to be invoked when list should be
	 * refreshed.
	 */
	public interface OnRefreshListener {
		/**
		 * Called when the list should be refreshed.
		 * <p/>
		 * A call to
		 * {@link com.PulldownToRefreshListView.view.PullToRefreshListView #onRefreshComplete()}
		 * is expected to indicate that the refresh has completed.
		 */
		public void onRefresh();
	}
}

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

public class PulldownToRefreshListView extends ListView implements OnScrollListener {

	//四种状态：点击刷新，下拉刷新，释放刷新，正在刷新
    private static final int TAP_TO_REFRESH = 1;
    private static final int PULL_TO_REFRESH = 2;
    private static final int RELEASE_TO_REFRESH = 3;
    private static final int REFRESHING = 4;

    private static final String TAG = "PullToRefreshListView";

    private OnRefreshListener mOnRefreshListener;

    /**
     * Listener that will receive notifications every time the list scrolls.
     */
    private OnScrollListener mOnScrollListener;
    private LayoutInflater mInflater;

    private RelativeLayout mRefreshView;
    private TextView mRefreshViewText;
    private ImageView mRefreshViewImage;
    private ProgressBar mRefreshViewProgress;
    private TextView mRefreshViewLastUpdated;

    private int mCurrentScrollState;
    private int mRefreshState;

    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;

    private int mRefreshViewHeight;
    private int mRefreshOriginalTopPadding;
    private int mRefreshOriginalBottomPadding;
    private int mLastMotionY;
    
    /***********************************************
     * 如果已经全部下载完，则禁止出现下拉效果 用到的变量
     ************************************************/
    private boolean isDownloadAll = false;

    public boolean isDownloadAll() {
		return isDownloadAll;
	}

	public void setDownloadAll(boolean isDownloadAll) {
		this.isDownloadAll = isDownloadAll;
	}

	public PulldownToRefreshListView(Context context) {
        super(context);
        init(context);
    }

    public PulldownToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PulldownToRefreshListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        // Load all of the animations we need in code rather than through XML
    	//声明所有我们必须的动画在代码里而不是在xml里。
    	//正旋转
    	//从零度逆时针转到180度，旋转中心(x,y)都为相对自身的一半的点的(x,y)（等于以中点为圆心）
        mFlipAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,  
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());//设置加速曲线
        mFlipAnimation.setDuration(250);//设置旋转时间
        mFlipAnimation.setFillAfter(true);//保留在终止位置 
      //反旋转
        mReverseFlipAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(250);
        mReverseFlipAnimation.setFillAfter(true);

        //视图加载器
        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        //下拉加载视图
        mRefreshView = (RelativeLayout) mInflater.inflate(
                R.layout.pull_to_refresh_footer, this, false);
//        <string name="pull_to_refresh_pull_label">下拉刷新...</string>
//        <string name="pull_to_refresh_release_label">松手刷新...</string>
//        <string name="pull_to_refresh_refreshing_label">正在下载...</string>
//        <string name="pull_to_refresh_tap_label">点击刷新...</string>
        //文本，就是所有的文本。如上：
        mRefreshViewText =
                (TextView) mRefreshView.findViewById(R.id.footer_text);
        //下拉箭头刷新图片
        mRefreshViewImage =
                (ImageView) mRefreshView.findViewById(R.id.footer_image);
        //下载滚动进度栏
        mRefreshViewProgress =
                (ProgressBar) mRefreshView.findViewById(R.id.footer_progress);
        //最后更新文本， 目前还没有用
        mRefreshViewLastUpdated =
                (TextView) mRefreshView.findViewById(R.id.footer_updated);

        //下拉箭头的最小高度50
        mRefreshViewImage.setMinimumHeight(50);
        //设置点击监听器
        mRefreshView.setOnClickListener(new OnClickRefreshListener());
        //获取顶部和底部的padding
        mRefreshOriginalTopPadding = mRefreshView.getPaddingTop(); //15px,10dp
        mRefreshOriginalBottomPadding = mRefreshView.getPaddingBottom();//23px,15dp

        
        //modify by edwar 2012-08-23 start
        /******************
//        //初始状态默认为点击刷新
//        mRefreshState = TAP_TO_REFRESH;
         */
        prepareForRefresh();
      //modify by edwar 2012-08-23 end
        

//        addHeaderView(mRefreshView);
        //将下拉栏放在footerView里
        addFooterView(mRefreshView);

        super.setOnScrollListener(this);

        measureView(mRefreshView);
        mRefreshViewHeight = mRefreshView.getMeasuredHeight();//83px
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        //setSelection(1);
    }

    /**
     * Set the listener that will receive notifications every time the list
     * scrolls.
     * 这个目前没用到
     * @param l The scroll listener.
     */
    @Override
    public void setOnScrollListener(OnScrollListener l) {
        mOnScrollListener = l;
    }

    /**
     * Register a callback to be invoked when this list should be refreshed.
     * 就是在子类里面设置回调
     * @param onRefreshListener The callback to run.
     */
    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        mOnRefreshListener = onRefreshListener;
    }

    /**
     * Set a text to represent when the list was last updated.
     * 展现一个文本当列表被最后一次更新时。
     *
     * @param lastUpdated Last updated at.
     */
    public void setLastUpdated(CharSequence lastUpdated) {
        if (lastUpdated != null) {
            mRefreshViewLastUpdated.setVisibility(View.VISIBLE);
            mRefreshViewLastUpdated.setText(lastUpdated);
        } else {
            mRefreshViewLastUpdated.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	if(!isDownloadAll){
    	Log.i("PullToRefreshListView", "onTouchEvent ");
        final int y = (int) event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                if (!isVerticalScrollBarEnabled()) {//判断是否允许画右边滚条
                    setVerticalScrollBarEnabled(true);
                }
                //获取目前可见范围里最后一个项目的位置，如果等于所获得的列表总数， 且目前不处于刷新中状态时
                if (getLastVisiblePosition() == getAdapter().getCount() - 1
                        && mRefreshState != REFRESHING) {
                    if ((
                            mRefreshView.getTop() <= getMeasuredHeight() - mRefreshViewHeight)
                            && mRefreshState == RELEASE_TO_REFRESH) {
                        // Initiate the refresh
                        mRefreshState = REFRESHING;
                        //为新一轮刷新做准备
                        prepareForRefresh();
                      //执行子类的 下载代码
                        onRefresh();
                    } else if (mRefreshView.getTop() > getMeasuredHeight() - mRefreshViewHeight) {
                        // Abort refresh and scroll down below the refresh view
                        resetFooter();
                        //setSelection(1);
                        if (getFooterViewsCount() > 0) {
                            setSelectionFromTop(getAdapter().getCount() - 1, (this.getMeasuredHeight()));
                        }

//                        if (mRefreshState != RELEASE_TO_REFRESH){
//                            scrollTo(0, mRefreshView.getScrollY() - mRefreshViewHeight);
//                        }
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

    	}
        return super.onTouchEvent(event);
    }

    private void applyHeaderPadding(MotionEvent ev) {
        final int historySize = ev.getHistorySize();//在ActionMove轨迹上的历史点总数

        // Workaround for getPointerCount() which is unavailable in 1.5
        // (it's always 1 in 1.5)
        int pointerCount = 1;
        try {
            Method method = MotionEvent.class.getMethod("getPointerCount");//java反射
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
                        historicalY = ((Float) method.invoke(ev, p, h)).intValue();
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
                    int topPadding = (int) (((historicalY + mLastMotionY)
                            + mRefreshViewHeight) / 1.7);

                    mRefreshView.setPadding(
                            mRefreshView.getPaddingLeft(),
                            mRefreshView.getPaddingTop(),
                            mRefreshView.getPaddingRight(),
                            topPadding);
                }
            }
        }
    }

    /**
     * Sets the header padding back to original size.
     * 设回 头的padding
     */
    private void resetFooterPadding() {
        mRefreshView.setPadding(
                mRefreshView.getPaddingLeft(),
                mRefreshView.getPaddingTop(),
                mRefreshView.getPaddingRight(),
                mRefreshOriginalBottomPadding);
    }

    /**
     * Resets the header to the original state.
     * 设置头为最初状态
     */
    public void resetFooter() {
    	//当状态不是点击刷新时，设置回默认 点击刷新
        if (mRefreshState != TAP_TO_REFRESH) {
            mRefreshState = TAP_TO_REFRESH;

            resetFooterPadding();

            // Set refresh view text to the pull label
            //设置文本为 点击刷新
            if(!isDownloadAll){
            mRefreshViewText.setText(R.string.regpoint_history_tab_label);
            }else{
            	mRefreshViewText.setText(R.string.regpoint_history_record_over);
            	}
            // Replace refresh drawable with arrow drawable
            //将图片改为 下拉箭头
            mRefreshViewImage.setImageResource(R.drawable.ic_pulltorefresh_arrow);
            // Clear the full rotation animation
            //清除旋转动画
            mRefreshViewImage.clearAnimation();
            // Hide progress bar and arrow.
            //隐藏滚动进度栏和箭头
            mRefreshViewImage.setVisibility(View.GONE);
            mRefreshViewProgress.setVisibility(View.GONE);
        }
    }
    
    
    //专门用于 从当前列表activity进入另一个activity， 然后再返回该列表activity时，要重新刷新列表时用。
    //如果没有这个方法，那么当之前列表activity显示“已经是最后一条”文本时，按上面方法再次进来重新刷新列表时，文本会还是“已经是最后一条”
    //情景：这才刚刷新，就显示“已经是最后一条”，容易歧义。。
    public void resetRefreshTextValue(){
    	prepareForRefresh();
    	   // If refresh view is visible when loading completes, scroll down to
        // the next item. 下拉栏会回到视图界面顶部，没有这句话，重新刷新时，按理这时一条记录都没有，下拉栏应该在最上边，可是，下拉栏依然会在最下边。
        if (mRefreshView.getBottom() > 0) {
            invalidateViews();
//            setSelection(1);
        }
    }

    private void measureView(View child) {
        //获取子类自身的布局需求
    	ViewGroup.LayoutParams p = child.getLayoutParams();//height-wrapcontent:-2, width-fillparent:-1
        if (p == null) {
            p = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        //跟父类沟通，得到的许可宽度
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
                0 + 0, p.width);
        
        //跟父类沟通，得到的许可高度
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
    	if(!isDownloadAll){
        Log.i("PullToRefreshListView", "scroll firstVisibleItem:" + firstVisibleItem);
        Log.i("PullToRefreshListView", "scroll visibleItemCount:" + visibleItemCount);
        Log.i("PullToRefreshListView", "scroll totalItemCount:" + totalItemCount);
        Log.i("PullToRefreshListView", "");
        Log.i("PullToRefreshListView", "");
        Log.i("PullToRefreshListView", "");
    	
    	
        // When the refresh view is completely visible, change the text to say
        // "Release to refresh..." and flip the arrow drawable.
    	//当下拉栏完全可见时，改变文本为“松手刷新”， 然后滑动下拉箭头
        if (mCurrentScrollState == SCROLL_STATE_TOUCH_SCROLL
                && mRefreshState != REFRESHING) {
            if (firstVisibleItem + visibleItemCount == totalItemCount) {
            	Log.i("PullToRefreshListView", "1");
                mRefreshViewImage.setVisibility(View.VISIBLE);
                if ((//footerview完全显示时
                        mRefreshView.getTop() <= getMeasuredHeight() - mRefreshViewHeight)//716top,mesuare751,viewheight 83
                        && mRefreshState != RELEASE_TO_REFRESH) {
                	//设置文本 - 为 松手刷新
                    mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
                    mRefreshViewImage.clearAnimation();
                    mRefreshViewImage.startAnimation(mFlipAnimation);
                    mRefreshState = RELEASE_TO_REFRESH;
                } else if (//footerview露出 //todo
                        mRefreshView.getTop() > getMeasuredHeight() - 20 - mRefreshViewHeight
                                && mRefreshState != PULL_TO_REFRESH) {
                	//设置文本 - 为上拉刷新
                    mRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
                    if (mRefreshState != TAP_TO_REFRESH) {
                        mRefreshViewImage.clearAnimation();
                        mRefreshViewImage.startAnimation(mReverseFlipAnimation);
                    }
                    mRefreshState = PULL_TO_REFRESH;
                }
            } else {
            	Log.i("PullToRefreshListView", "2");
                mRefreshViewImage.setVisibility(View.GONE);
                resetFooter();
            }
        } else if (mCurrentScrollState == SCROLL_STATE_FLING
                && getLastVisiblePosition() >= getAdapter().getCount() -1
                && mRefreshState != REFRESHING) {

                if (getFooterViewsCount() > 0) {
                    setSelectionFromTop(getAdapter().getCount() - 1, (this.getMeasuredHeight()));//设置最后一项被选中，且距离listview顶部距离751
                }


        }

        if (mOnScrollListener != null) {
        	Log.i("PullToRefreshListView", "3");
            mOnScrollListener.onScroll(view, firstVisibleItem,
                    visibleItemCount, totalItemCount);
        }
    }

    }

    //获取该下拉视图
    public View getLoadBarView() {
        return mRefreshView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        mCurrentScrollState = scrollState;

        if (mOnScrollListener != null) {
            mOnScrollListener.onScrollStateChanged(view, scrollState);
        }
    }

    
    //为新一次刷新做准备
    public void prepareForRefresh() {
        resetFooterPadding();

        //隐藏下拉箭头
        mRefreshViewImage.setVisibility(View.GONE);
        // We need this hack, otherwise it will keep the previous drawable.
        //删掉下拉箭头的背景，否则会保存之前的drawable.
        mRefreshViewImage.setImageDrawable(null);
        //显示滚动进度栏
        mRefreshViewProgress.setVisibility(View.VISIBLE);

        // Set refresh view text to the refreshing label
        //设置文本为 正在下载
        mRefreshViewText.setText(R.string.pull_to_refresh_refreshing_label);
        //更改状态为 正在刷新
        mRefreshState = REFRESHING;
    }

    //执行子类的 下载代码
    public void onRefresh() {
        Log.d(TAG, "onRefresh");

        if (mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

    /**
     * Resets the list to a normal state after a refresh.
     *
     * @param lastUpdated Last updated at.
     */
    public void onRefreshComplete(CharSequence lastUpdated) {
    	Log.d(TAG, "onRefreshComplete with label");
    	if(!isDownloadAll){
    	setLastUpdated(lastUpdated);
    	}else{
    		setLastUpdated("已经是最后一条");
    	}
        onRefreshComplete();
    }

    /**
     * Resets the list to a normal state after a refresh.
     */
    public void onRefreshComplete() {
        Log.d(TAG, "onRefreshComplete");

        resetFooter();

        // If refresh view is visible when loading completes, scroll down to
        // the next item.
        if (mRefreshView.getBottom() > 0) {
            invalidateViews();
//            setSelection(1);
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
        	if(!isDownloadAll){
            if (mRefreshState != REFRESHING) {
                prepareForRefresh();
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
         * A call to {@link com.PulldownToRefreshListView.view.PullToRefreshListView #onRefreshComplete()} is
         * expected to indicate that the refresh has completed.
         */
        public void onRefresh();
    }
}

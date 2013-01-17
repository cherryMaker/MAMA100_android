package com.mama100.android.member.widget.scrollview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import com.mama100.android.member.util.LogUtils;

/**
 * 
 * 主界面模仿Google Android 源码：Lancher Workspace 写的一个 左右滑动控件。
 * 
 * @author aihua.yan 2012-04-11
 * 
 */
public class MyScrollLayout extends ViewGroup {

	@SuppressWarnings({ "UnusedDeclaration" })
	private static final String TAG = "Mkt.Homepage.MyScrollLayout";
	private VelocityTracker mVelocityTracker; // 用于判断甩动手势
	private static final int SNAP_VELOCITY = 600;
	private Scroller mScroller; // 滑动控制器
	private int mCurScreen;
	private int mDefaultScreen = 0;
	private float mLastMotionX;
	private float mLastMotionY;

	// 接口，用于在主界面更新页面上方的圆点switch切换栏
	private OnViewChangeListener mOnViewChangeListener;

	public MyScrollLayout(Context context) {
		super(context);
		init(context);
	}

	public MyScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public MyScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		mCurScreen = mDefaultScreen;
		mScroller = new Scroller(context);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurScreen * width, 0);
	}

	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// get the valid layout page 获取有效布局页面

		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);

			mCurScreen = whichScreen;
			invalidate(); // Redraw the layout 泼脏水一盆
			if (mOnViewChangeListener != null) {
				mOnViewChangeListener.OnViewChange(mCurScreen);// 子类切换圆点切换栏
			}
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;
	private static final int INVALID_POINTER = -1;
	private int mActivePointerId = INVALID_POINTER;
	// 此值过小，一些设备不能捕捉到点击事件
	private int mTouchSlop = 18;

	private float mSmoothingTime;
	private float mTouchX;
	private static final float NANOTIME_DIV = 1000000000.0f;
	private static final float SMOOTHING_SPEED = 0.75f;
	private static final float SMOOTHING_CONSTANT = (float) (0.016 / Math
			.log(SMOOTHING_SPEED));
	private int mCurrentScreen;
	private int mNextScreen = INVALID_SCREEN;
	private static final int INVALID_SCREEN = -1;
	private boolean mAllowLongPress = true;

	private void releaseVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	private void onSecondaryPointerUp(MotionEvent ev) {
		final int pointerIndex = 0;
//		final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		final int pointerId = ev.getPointerId(pointerIndex);
		if (pointerId == mActivePointerId) {
			// This was our active pointer going up. Choose a new
			// active pointer and adjust accordingly.
			// 这是我们活动点going up，选择一个活动的点然后有根据地调整。
			// TODO: Make this decision more intelligent.
			final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
			mLastMotionX = ev.getX(newPointerIndex);
			mLastMotionY = ev.getY(newPointerIndex);
			mActivePointerId = ev.getPointerId(newPointerIndex);
			if (mVelocityTracker != null) {
				mVelocityTracker.clear();
			}
		}
	}

	private void acquireVelocityTrackerAndAddMovement(MotionEvent ev) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
	}

	/**
	 * 拦截事件——屏幕触摸 1，当屏幕事件是滑动姿势时，该法返回true,从而调用本viewgroup的onTouch()，从而实现屏幕滚动，切换新界面
	 * 2，当屏幕事件属于点击时，该反返回false,从而结束本拦截事件，也不调用viewgroup的onTouch()，从而实现事件向下传递，传给子类，
	 * 调用子类的onTouch
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {

		/*
		 * This method JUST determines whether we want to intercept the motion.
		 * If we return true, onTouchEvent will be called and we do the actual
		 * scrolling there. 该法仅仅用来决定是否我们想要拦截事件。如果返回true,OnTouchEvent将被调用， 然后我们滚动
		 */

		/*
		 * Shortcut the most recurring case: the user is in the dragging state
		 * and he is moving his finger. We want to intercept this motion.
		 * 快捷重复case:用户正拖拉状态，移动手指。我们想拦截事件。
		 */

		final int action = ev.getAction();

		LogUtils.logd(TAG, "onInterceptTouchEvent, action: " + action
				+ " mTouchState: " + mTouchState);

		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			LogUtils.logd(TAG,
					"onInterceptTouchEvent, MotionEvent.ACTION_MOVE mTouchState: "
							+ mTouchState);
			return true;
		}

		acquireVelocityTrackerAndAddMovement(ev);

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_MOVE: {
			LogUtils.logd(TAG,
					"onInterceptTouchEvent.ACTION_MOVE.................");
			/*
			 * mIsBeingDragged == false, otherwise the shortcut would have
			 * caught it. Check whether the user has moved far enough from his
			 * original down touch. mIsBeingDragged=false，否则捷径将捕捉它。
			 * 检查用户是否移动至距离原始的下触摸位置足够远。。
			 */

			/*
			 * Locally do absolute value. mLastMotionX is set to the y value of
			 * the down event.
			 */
			final int pointerIndex = ev.findPointerIndex(mActivePointerId);
			final float x = ev.getX(pointerIndex);
			final float y = ev.getY(pointerIndex);
			final int xDiff = (int) Math.abs(x - mLastMotionX);
			final int yDiff = (int) Math.abs(y - mLastMotionY);

			final int touchSlop = mTouchSlop;

			boolean xMoved = xDiff > touchSlop;
			boolean yMoved = yDiff > touchSlop;

			if (xMoved || yMoved) {

				if (xMoved) {
					LogUtils.logd(TAG,
							"onInterceptTouchEvent.ACTION_MOVE.TOUCH_STATE_SCROLLING................");
					// Scroll if the user moved far enough along the X axis
					mTouchState = TOUCH_STATE_SCROLLING;
					mLastMotionX = x;
					mTouchX = getScrollX();
					mSmoothingTime = System.nanoTime() / NANOTIME_DIV;
					// enableChildrenCache(mCurrentScreen - 1, mCurrentScreen +
					// 1);
				}
				// Either way, cancel any pending longpress
				if (mAllowLongPress) {
					mAllowLongPress = false;
					// Try canceling the long press. It could also have been
					// scheduled
					// by a distant descendant, so use the mAllowLongPress flag
					// to block
					// everything
					// 尝试取消长按，它也可以排期一个子类，所以用mAllowLongPress标签来阻止事情。
					final View currentScreen = getChildAt(mCurrentScreen);
					currentScreen.cancelLongPress();
				}
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			LogUtils.logd(TAG,
					"onInterceptTouchEvent.ACTION_DOWN.................");
			final float x = ev.getX();
			final float y = ev.getY();
			// Remember location of down touch
			mLastMotionX = x;
			mLastMotionY = y;
			mActivePointerId = ev.getPointerId(0);
			mAllowLongPress = true;

			/*
			 * If being flinged and user touches the screen, initiate drag;
			 * otherwise don't. mScroller.isFinished should be false when being
			 * flinged.
			 * 如果被飞屏且用户触摸屏幕，初始化拖动。否则不要。mScroller.isFinished应该为false当被飞屏幕时。
			 */
			LogUtils.logd(TAG, "mScroller.isFinished()................."
					+ mScroller.isFinished());
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			LogUtils.logd(TAG,
					"onInterceptTouchEvent.ACTION_UP.................");

			// Release the drag
			// clearChildrenCache();
			mTouchState = TOUCH_STATE_REST;
			mActivePointerId = INVALID_POINTER;
			mAllowLongPress = false;
			releaseVelocityTracker();
			break;

		case MotionEvent.ACTION_POINTER_UP:
			LogUtils.logd(TAG,
					"onInterceptTouchEvent.ACTION_POINTER_UP.................");
			onSecondaryPointerUp(ev);
			break;
		}

		/*
		 * The only time we want to intercept motion events is if we are in the
		 * drag mode.
		 */
		LogUtils.logd(TAG,
				"onInterceptTouchEvent.mTouchState != TOUCH_STATE_REST................."
						+ (mTouchState != TOUCH_STATE_REST));
		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			LogUtils.logi(TAG, "onTouchEvent  ACTION_DOWN");
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			break;

		case MotionEvent.ACTION_MOVE:
			LogUtils.logi(TAG, "onTouchEvent  ACTION_MOVE");
			int deltaX = (int) (mLastMotionX - x);
			if (IsCanMove(deltaX)) {
				if (mVelocityTracker != null) {
					mVelocityTracker.addMovement(event);
				}
				mLastMotionX = x;
				scrollBy(deltaX, 0);
			}

			break;
		case MotionEvent.ACTION_UP:
			LogUtils.logi(TAG, "onTouchEvent  ACTION_UP");
			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				velocityX = (int) mVelocityTracker.getXVelocity();
			}
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				LogUtils.logi(TAG, "snap left");
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				LogUtils.logi(TAG, "snap right");
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}
			// mTouchState = TOUCH_STATE_REST;
			break;
		}
		return true;
	}

	private boolean IsCanMove(int deltaX) {
		if (getScrollX() <= 0 && deltaX < 0) {
			return false;
		}
		if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
			return false;
		}
		return true;
	}

	public void SetOnViewChangeListener(OnViewChangeListener listener) {
		mOnViewChangeListener = listener;
	}

}
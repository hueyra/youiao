package com.clong.youiao.weiget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.clong.youiao.utils.DensityUtil;

public class YouiaoScrollView extends ScrollView implements View.OnTouchListener
{
	private Context mContext;

	// 下拉超过60dp就出发刷新
	private int mPullSize = 60;

	private float mFirstPosition = 0;
	private Boolean mScaling = false;

	private View dropZoomView;
	private int dropZoomViewWidth;
	private int dropZoomViewHeight;
	private int dropZoomedViewHeight;
	private int dropZoomedViewWidth;

	private PullToRefreshListener mPullToRefreshListener;
	private PullTouchListener mPullTouchListener;

	private OnScrollListener onScrollListener;
	private int lastScrollY;

	public YouiaoScrollView(Context context)
	{
		super(context);
		mContext = context;
	}

	public YouiaoScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
	}

	public YouiaoScrollView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (onScrollListener != null )
		{
			onScrollListener.onScroll(lastScrollY = this.getScrollY());
		}
		if (dropZoomViewWidth <= 0 || dropZoomViewHeight <= 0 )
		{
			dropZoomViewWidth = dropZoomView.getMeasuredWidth();
			dropZoomViewHeight = dropZoomView.getMeasuredHeight();
		}
		switch (event.getAction())
		{
			case MotionEvent.ACTION_UP:
				// 滚动
				handler.sendMessageDelayed(handler.obtainMessage(), 20);
				// 手指离开后恢复图片
				mScaling = false;
				startRefresh();
				break;
			case MotionEvent.ACTION_MOVE:
				if (mPullTouchListener != null )
				{
					mPullTouchListener.onEvent(event);
				}
				if (!mScaling )
				{
					if (getScrollY() == 0 )
					{
						mFirstPosition = event.getY();
					}
					else
					{
						break;
					}
				}
				int distance = (int) ((event.getY() - mFirstPosition) * 0.6);
				if (distance < 0 )
				{
					break;
				}
				mScaling = true;
				setZoom(1 + distance);
				return true;
		}
		return false;
	}

	private void startRefresh()
	{
		if (mPullToRefreshListener != null )
		{
			dropZoomedViewWidth = dropZoomView.getMeasuredWidth();
			dropZoomedViewHeight = dropZoomView.getMeasuredHeight();
			int pullPx = DensityUtil.dip2px(mContext, mPullSize);
			if (dropZoomedViewHeight - dropZoomViewHeight > pullPx )
			{
				mPullToRefreshListener.onRefresh();
				float h = dropZoomViewHeight + pullPx;
				float zoomPullSizeWidth = (h / dropZoomViewHeight) * dropZoomViewWidth;
				// 设置动画
				ValueAnimator anim = ObjectAnimator
						.ofFloat(0.0F, (dropZoomedViewWidth - zoomPullSizeWidth) / dropZoomedViewWidth)
						.setDuration((long) ((dropZoomView.getMeasuredWidth() - dropZoomViewWidth) * 0.3));
				anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
				{
					@Override
					public void onAnimationUpdate(ValueAnimator animation)
					{
						float cVal = (Float) animation.getAnimatedValue();
						ViewGroup.LayoutParams lp = dropZoomView.getLayoutParams();
						lp.width = (int) (dropZoomedViewWidth - (dropZoomedViewWidth * cVal));
						lp.height = (int) (dropZoomViewHeight
								* ((dropZoomedViewWidth - (dropZoomedViewWidth * cVal)) / dropZoomViewWidth));
						dropZoomView.setLayoutParams(lp);

					}
				});
				anim.start();
			}
			else
			{
				refreshComplete();
			}
		}
		else
		{
			refreshComplete();
		}
	}

	// 缩放
	public void setZoom(float s)
	{
		if (dropZoomViewHeight <= 0 || dropZoomViewWidth <= 0 )
		{
			return;
		}
		ViewGroup.LayoutParams lp = dropZoomView.getLayoutParams();
		lp.width = (int) (dropZoomViewWidth + s);
		lp.height = (int) (dropZoomViewHeight * ((dropZoomViewWidth + s) / dropZoomViewWidth));
		dropZoomView.setLayoutParams(lp);
	}

	public void setPullZoomView(View view)
	{

		setOverScrollMode(OVER_SCROLL_NEVER);
		dropZoomView = view;
		setOnTouchListener(this);
	}

	public interface PullToRefreshListener
	{
		void onRefresh();
	}

	public interface PullTouchListener
	{
		void onEvent(MotionEvent event);
	}

	public interface OnScrollListener
	{
		void onScroll(int scrollY);
	}

	public void setPullToRefreshListener(PullToRefreshListener listener)
	{
		mPullToRefreshListener = listener;
	}

	public void setPullTouchListener(PullTouchListener listener)
	{
		mPullTouchListener = listener;
	}

	public void setOnScrollListener(OnScrollListener onScrollListener)
	{
		this.onScrollListener = onScrollListener;
	}

	public void refreshComplete()
	{
		final float distance = dropZoomView.getMeasuredWidth() - dropZoomViewWidth;
		ValueAnimator anim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration((long) (distance * 0.5));
		anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
		{
			@Override
			public void onAnimationUpdate(ValueAnimator animation)
			{
				float cVal = (Float) animation.getAnimatedValue();
				setZoom(distance - ((distance) * cVal));
			}
		});
		anim.start();
	}

	/**
	 * 用于用户手指离开ScrollView的时候获取ScrollView滚动的Y距离，然后回调给onScroll方法中
	 */
	private Handler handler = new Handler()
	{

		public void handleMessage(android.os.Message msg)
		{
			int scrollY = YouiaoScrollView.this.getScrollY();
			// 此时的距离和记录下的距离不相等，在隔5毫秒给handler发送消息
			if (lastScrollY != scrollY )
			{
				lastScrollY = scrollY;
				handler.sendMessageDelayed(handler.obtainMessage(), 5);
			}
			if (onScrollListener != null )
			{
				onScrollListener.onScroll(scrollY);
			}
		};

	};
}

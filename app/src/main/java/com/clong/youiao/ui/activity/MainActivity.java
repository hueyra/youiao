package com.clong.youiao.ui.activity;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.clong.youiao.R;
import com.clong.youiao.ui.BaseActivity;
import com.clong.youiao.weiget.YouiaoScrollView;

import butterknife.BindView;

public class MainActivity extends BaseActivity
		implements YouiaoScrollView.PullToRefreshListener, YouiaoScrollView.OnScrollListener
{
	private AnimationDrawable mAnimationDrawable;
	private int searchLayoutTop;
	private int mTopSearchViewWidth;
	private int mMoveSearchViewWidth;

	@BindView(R.id.drop_zoom_scrollview)
	YouiaoScrollView mYouiaoScrollView;
	@BindView(R.id.imaga_head_view)
	ImageView mHeadImageView;
	@BindView(R.id.header_progressbar)
	ImageView mRefreshProgress;
	@BindView(R.id.v_search_view_bg)
	View mMoveSearchView;
	@BindView(R.id.tv_search_view_text)
	TextView mMoveSearchViewText;
	@BindView(R.id.ll_search_view_top)
	LinearLayout mTopSearchView;
	@BindView(R.id.rl_search_move_layout)
	RelativeLayout mMoveLayout;
	@BindView(R.id.rl_top_search_layout)
	RelativeLayout mTopSearchLayout;
	@BindView(R.id.v_top_bar)
	View mTopBar;

	@Override
	public int getLayoutResID()
	{
		return R.layout.activity_main;
	}

	@Override
	public void init()
	{
		mAnimationDrawable = (AnimationDrawable) mRefreshProgress.getDrawable();
		mYouiaoScrollView.setPullZoomView(mHeadImageView);
		mYouiaoScrollView.setPullToRefreshListener(this);
		mYouiaoScrollView.setOnScrollListener(this);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus )
		{
			searchLayoutTop = mMoveLayout.getTop() - (mTopSearchView.getTop() + mTopSearchLayout.getTop());
			mTopSearchViewWidth = mTopSearchView.getMeasuredWidth();
			mMoveSearchViewWidth = mMoveSearchView.getMeasuredWidth();
		}
	}

	@Override
	public void onRefresh()
	{
		mRefreshProgress.setVisibility(View.VISIBLE);
		mAnimationDrawable.start();
		mRefreshProgress.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				mYouiaoScrollView.refreshComplete();
				mRefreshProgress.setVisibility(View.INVISIBLE);
				mAnimationDrawable.stop();
			}
		}, 2000);
	}

	@Override
	public void onScroll(int scrollY)
	{
		if (scrollY >= searchLayoutTop )
		{
			if (mTopSearchView.getVisibility() != View.VISIBLE )
			{
				mTopSearchView.setVisibility(View.VISIBLE);
				mMoveSearchView.setVisibility(View.INVISIBLE);
				mMoveSearchViewText.setVisibility(View.INVISIBLE);
				mTopBar.setBackgroundColor(Color.parseColor("#3FA1AC"));
				mTopSearchLayout.setBackgroundColor(Color.parseColor("#3FA1AC"));
			}
		}
		else
		{
			if (mTopSearchView.getVisibility() != View.INVISIBLE )
			{
				mTopSearchView.setVisibility(View.INVISIBLE);
				mMoveSearchView.setVisibility(View.VISIBLE);
				mMoveSearchViewText.setVisibility(View.VISIBLE);
				mTopBar.setBackgroundColor(Color.parseColor("#00FFFFFF"));
				mTopSearchLayout.setBackgroundColor(Color.parseColor("#00FFFFFF"));
			}
		}

		if (mTopSearchView.getVisibility() != View.VISIBLE )
		{
			if (mMoveSearchView.getMeasuredWidth() <= mMoveSearchViewWidth )
			{
				int width = (int) (((float) (mMoveSearchViewWidth - mTopSearchViewWidth) / searchLayoutTop)
						* scrollY);
				ViewGroup.LayoutParams lp = mMoveSearchView.getLayoutParams();
				lp.width = mMoveSearchViewWidth - width;
				lp.height = mMoveSearchView.getMeasuredHeight();
				mMoveSearchView.setLayoutParams(lp);
			}
		}

	}
}

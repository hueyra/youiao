package com.clong.youiao.ui;



import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;

/**
 * Created by hueyra on 2017/4/7 0007.
 * 
 */
public abstract class BaseActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// 隐去头部通知栏
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP )
		{
			Window window = getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			window.getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.TRANSPARENT);
		}
		else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
		{
			// 4.4 全透明状态栏
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		}
		setContentView(getLayoutResID());
		ButterKnife.bind(this);
		init();
	}

	/** 获取layout资源文件 */
	public abstract int getLayoutResID();

	/** onCreate的初始化操作 */
	public abstract void init();

}

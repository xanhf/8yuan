package com.netease.nim.uikit.view;

import android.content.Context;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.R;


/**
 * 这个类封装了loading动画
 * 
 */
public class UikitAppPullLoadingLayout extends RelativeLayout {

	/** 旋转图片 */
	private ImageView imgView;
	/** 提示TextView */
	private TextView apptextview;
	/** 旋转的动画 */
	private Animation mRotateAnimation;

	/**
	 * 构造方法
	 *
	 * @param context
	 *            context
	 */
	public UikitAppPullLoadingLayout(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造方法
	 *
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public UikitAppPullLoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}


	protected View createLoadingView(Context context) {
		View container = LayoutInflater.from(context).inflate(
				R.layout.my_layout_pull_progressbar, null);
		return container;
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 *            context
	 */
	private void init(Context context) {
		addView(createLoadingView(context));
		imgView = (ImageView) findViewById(R.id.imgView);
		apptextview = (TextView) findViewById(R.id.apptextview);

		rotationMatrix = new Matrix();
		mRotateAnimation = AnimationUtils.loadAnimation(context,
				R.anim.rotate_loading);
		resetRotation();
		imgView.startAnimation(mRotateAnimation);
	}




	private Matrix rotationMatrix;

	public void onPull(float scale) {
		float angle = scale * 180f; // SUPPRESS CHECKSTYLE
		rotationMatrix.setRotate(angle);
		imgView.setImageMatrix(rotationMatrix);
	}

	/**
	 * 重置动画
	 */
	private void resetRotation() {
		imgView.clearAnimation();
		rotationMatrix.setRotate(0);
		imgView.setImageMatrix(rotationMatrix);
	}
}

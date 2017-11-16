package com.trade.eight.moudle.outterapp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.easylife.ten.lib.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.trade.eight.base.BaseActivity;
import com.trade.eight.config.AppImageLoaderConfig;
import com.trade.eight.task.ImageLoaderTask;
import com.trade.eight.tools.AsyncImageLoader;
import com.trade.eight.tools.Utils;
import com.trade.eight.view.photoview.PhotoViewAttacher;
import com.trade.eight.view.photoview.PhotoViewAttacher.OnMatrixChangedListener;
import com.trade.eight.view.photoview.PhotoViewAttacher.OnPhotoTapListener;

/**
 * url
 *
 * 传入参数url
 *
 */
public class ImageViewAttachActivity extends BaseActivity {

	static final String PHOTO_TAP_TOAST_STRING = "Photo Tap! X: %.2f %% Y:%.2f %%";

	private ImageView mImageView;

	private PhotoViewAttacher mAttacher;

	private Toast mCurrentToast;
	View loadingView = null;

	/**
	 * 别的页面进来需要的参数
	 * @param context
	 * @param url 网络图片地址
	 * @param isLand  是否横屏显示
	 */
	public static void start (Context context, String url, boolean isLand){
		Intent intent = new Intent(context, ImageViewAttachActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("isLand", isLand);
		context.startActivity(intent);
	}

	/**
	 * modify by fangzhu, PhotoViewAttacher must use after bitmap inited
	 *
	 * @param savedInstanceState
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boolean isLand = getIntent().getBooleanExtra("isLand", true);
		if (isLand) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		setContentView(R.layout.activity_full_image);
		loadingView = findViewById(R.id.loading);
		mImageView = (ImageView) findViewById(R.id.fullImage);

		if (getIntent() != null && getIntent().getBooleanExtra("AsyncImageLoader", false)) {
			getBitmap4AsyncImageLoader();
		} else {
			getBitmap4ImageLoader();
		}
	}

	@Override
	public boolean isActivityFitsSystemWindows() {
		return false;
	}
	//	public void back(View view) {
//		finish();
//	}
	
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	/**
	 * keep code review
	 */
	private void getBitmap() {
		// TODO Auto-generated method stub

		Bundle bundle = this.getIntent().getExtras();
		String imageurl = bundle.getString("url");

		if (imageurl == null || "".equals(imageurl))
			return;

		ImageLoaderTask imageLoaderTask = new ImageLoaderTask(this);
		imageLoaderTask.setTackCallBack(new ImageLoaderTask.TackCallBack() {
			@Override
			public void onCallBack() {
				// The MAGIC happens here!
				mAttacher = new PhotoViewAttacher(mImageView);

				// Lets attach some listeners, not required though!
				mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
				mAttacher.setOnPhotoTapListener(new PhotoTapListener());
			}
		});
		imageLoaderTask.showoImage(mImageView, imageurl,
				Utils.getWindowWidth(this), Utils.getWindowHeight(this));

	}

	private void getBitmap4ImageLoader() {
		// TODO Auto-generated method stub

		Bundle bundle = this.getIntent().getExtras();
		String imageurl = bundle.getString("url");

		if (imageurl == null || "".equals(imageurl))
			return;

		ImageLoader.getInstance().displayImage(imageurl, mImageView, AppImageLoaderConfig.getDisplayImageOptions(this), new ImageLoadingListener() {
			@Override
			public void onLoadingStarted(String s, View view) {
				if (loadingView != null)
					loadingView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String s, View view, FailReason failReason) {
				if (loadingView != null)
					loadingView.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String s, View view, Bitmap bitmap) {
				if (loadingView != null)
					loadingView.setVisibility(View.GONE);
				if (bitmap != null) {
					// The MAGIC happens here!
					mAttacher = new PhotoViewAttacher(mImageView);

					// Lets attach some listeners, not required though!
					mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
					mAttacher.setOnPhotoTapListener(new PhotoTapListener());
				}
			}

			@Override
			public void onLoadingCancelled(String s, View view) {
				if (loadingView != null)
					loadingView.setVisibility(View.GONE);
			}
		});
	}

	private void getBitmap4AsyncImageLoader() {
		// TODO Auto-generated method stub

		Bundle bundle = this.getIntent().getExtras();
		String imageurl = bundle.getString("url");

		if (imageurl == null || "".equals(imageurl))

			return;
		AsyncImageLoader asyncImageLoader = AsyncImageLoader.instance();
		Bitmap mbitmap = asyncImageLoader.loadBitmap(imageurl, null, new AsyncImageLoader.ImageCallback1() {
			@Override
			public void imageLoaded(Bitmap bitmap, String imageUrl) {
				if (bitmap != null) {
					mImageView.setImageBitmap(bitmap);

					// The MAGIC happens here!
					mAttacher = new PhotoViewAttacher(mImageView);

					// Lets attach some listeners, not required though!
					mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
					mAttacher.setOnPhotoTapListener(new PhotoTapListener());
				}

			}
		});
		if (mbitmap != null) {
			mImageView.setImageBitmap(mbitmap);

			// The MAGIC happens here!
			mAttacher = new PhotoViewAttacher(mImageView);

			// Lets attach some listeners, not required though!
			mAttacher.setOnMatrixChangeListener(new MatrixChangeListener());
			mAttacher.setOnPhotoTapListener(new PhotoTapListener());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Need to call clean-up
		if (mAttacher != null)
			mAttacher.cleanup();
//		BitMapUtil.destoryFirst();
	}

	private class PhotoTapListener implements OnPhotoTapListener {

		@Override
		public void onPhotoTap(View view, float x, float y) {
			float xPercentage = x * 100f;
			float yPercentage = y * 100f;
			if (null != mCurrentToast) {
				mCurrentToast.cancel();
			}
			finish();
		}
	}

	private class MatrixChangeListener implements OnMatrixChangedListener {

		@Override
		public void onMatrixChanged(RectF rect) {
			// mCurrMatrixTv.setText(rect.toString());
		}
	}

}

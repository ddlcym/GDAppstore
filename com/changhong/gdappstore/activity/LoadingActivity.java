package com.changhong.gdappstore.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.MyApplication;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.base.BaseActivity;
import com.changhong.gdappstore.datacenter.DataCenter;
import com.changhong.gdappstore.net.LoadListener.LoadObjectListener;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.DialogUtil.DialogBtnOnClickListener;
import com.changhong.gdappstore.util.DialogUtil.DialogMessage;
import com.changhong.gdappstore.util.ImageLoadUtil;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.SharedPreferencesUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 应用进入动画页面
 * 
 * @author wangxiufeng
 * 
 */
public class LoadingActivity extends BaseActivity {
	/** 动画执行时间 */
	private final int AnimDuration = 3000;
	private ImageView ivLoading;
	private AlphaAnimation alphaAnimation;
	private static final String TAG = "LoadingActivity";
	private String lastCachedADUri = "";

	private static final int WHAT_JUMPMAIN = 100;
	private static final int WHAT_DOACCESS = 101;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		initView();
		loadData();
		requestBootAdImgData();
		handler.sendEmptyMessageDelayed(WHAT_JUMPMAIN, 3000);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT_JUMPMAIN:
				jumpToMain();
				break;
			case WHAT_DOACCESS:
				doAccess();
				break;
			default:
				break;
			}

			super.handleMessage(msg);
		}

	};

	private void initView() {
		L.d("widthpx==" + context.getResources().getDisplayMetrics().density);
		L.d("widthpx==" + screenWidth + " " + screenHeight);
		ivLoading = findView(R.id.iv_loading);
		ivLoading.setImageResource(0);
		alphaAnimation = new AlphaAnimation(0.4f, 1f);
		alphaAnimation.setDuration(AnimDuration);
		alphaAnimation.setFillAfter(true);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// jumpToMain();
			}
		});

	}

	private void loadData() {
		lastCachedADUri = SharedPreferencesUtil.getJsonCache(context, Config.KEY_BOOTADIMG);
		L.d(TAG + " lastCachedADUri===" + lastCachedADUri + " ");
		if (TextUtils.isEmpty(lastCachedADUri)) {
			ivLoading.setImageResource(R.drawable.lug_img_loading);
			ivLoading.startAnimation(alphaAnimation);
		} else {
			ImageLoadUtil.displayImgByonlyDiscCache(lastCachedADUri, ivLoading, new ImageLoadingListener() {

				@Override
				public void onLoadingStarted(String imageUri, View view) {
					L.d(TAG + " onLoadingStarted url=" + imageUri);
				}

				@Override
				public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
					ivLoading.setImageResource(R.drawable.lug_img_loading);
					ivLoading.startAnimation(alphaAnimation);
					// DialogUtil.showLongToast(context, "图片下载失败，使用默认图片！");
					L.d(TAG + " onLoadingFailed failReason=" + failReason);
				}

				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					ivLoading.startAnimation(alphaAnimation);
					L.d(TAG + " onLoadingComplete url=" + imageUri);
				}

				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					ivLoading.setImageResource(R.drawable.lug_img_loading);
					ivLoading.startAnimation(alphaAnimation);
					// DialogUtil.showLongToast(context, "图片下载取消，使用默认图片！");
					L.d(TAG + " onLoadingCancelled url=" + imageUri);
				}
			});
		}
	}

	private boolean isJumped=false;
	private void jumpToMain() {
		if (SharedPreferencesUtil.getAccessCache(context, false)) {
			startActivity(new Intent(context, IndiaMainActivity.class));
			isJumped=true;
			finish();
		}
	}

	/**
	 * 请求广告图片信息和下载广告图片
	 * 广告图片加载机制：请求广告图片地址；判断是否和上一次地址一样，不一样就下载图片保存本地。下一次启动时候加载上次下载的图片
	 * ，如果本地没有去服务器请求，请求失败显示默认图片
	 */
	private void requestBootAdImgData() {

		DataCenter.getInstance().loadBootADData(context, new LoadObjectListener() {

			@Override
			public void onComplete(Object object) {
				String uri = (String) object;

				handler.sendEmptyMessage(WHAT_DOACCESS);

				if (TextUtils.isEmpty(uri)) {
					L.d(TAG + " loadnextBootAdImg by uri is null");
					return;
				}
				if (!TextUtils.isEmpty(lastCachedADUri) && uri.equals(lastCachedADUri)) {
					L.d(TAG + " loadnextBootAdImg by uri equals lastone" + uri);
					return;// 和上次缓存是同一张图片不再请求
				}

				loadBootAdImg(uri);
			}
		});
	}

	/**
	 * 处理是否拥有全新进入应用
	 */
	private void doAccess() {
		boolean hasAccess = SharedPreferencesUtil.getAccessCache(context, false);
		L.d(TAG + "HAS_ACCESSUSER==" + hasAccess + " inited " + MyApplication.ACCESSUSER_INITED);
		if (hasAccess) {
			jumpToMain();
			return;
		}
//		startService(new Intent(context,SystemDialogService.class));
		String content = context.getResources().getString(R.string.noaccess);
		Dialog dialog = DialogUtil.showMyAlertDialog(context, context.getResources().getString(R.string.tishi),
				content, "OK", "", true, true, false, new DialogBtnOnClickListener() {

					@Override
					public void onSubmit(DialogMessage dialogMessage) {
						if (dialogMessage != null && dialogMessage.dialogInterface != null) {
							dialogMessage.dialogInterface.dismiss();
						}
						System.exit(0);
					}

					@Override
					public void onCancel(DialogMessage dialogMessage) {

					}
				});
	}

	/**
	 * 下载保存图片
	 * 
	 * @param uri
	 */
	private void loadBootAdImg(String uri) {
		DisplayImageOptions options = new DisplayImageOptions.Builder().displayer(new SimpleBitmapDisplayer())
				.bitmapConfig(Bitmap.Config.ARGB_8888).imageScaleType(ImageScaleType.IN_SAMPLE_INT)
				.cacheInMemory(false).cacheOnDisc(true).showImageOnFail(R.drawable.lug_img_loading).build();
		MyApplication.imageLoader.loadImage(uri, options, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				L.d(TAG + " loadnextBootAdImg onLoadingStarted url=" + imageUri);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				L.d(TAG + " loadnextBootAdImg onLoadingFailed url=" + imageUri);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				L.d(TAG + " loadnextBootAdImg onLoadingComplete url=" + imageUri);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				L.d(TAG + " loadnextBootAdImg onLoadingCancelled url=" + imageUri);
			}
		});
	}
}

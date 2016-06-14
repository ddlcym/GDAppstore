package com.changhong.gdappstore.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.text.TextUtils;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.Toast;

import com.changhong.gdappstore.MyApplication;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.model.NativeApp;

public class Util {
	/**
	 * 获取当前显示activity
	 * 
	 * @param context
	 * @return
	 */
	public static String getTopActivity(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.RunningTaskInfo info = manager.getRunningTasks(1).get(0);
		String curClassName = info.topActivity.getClassName(); // 类名
		return curClassName;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dipTopx(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int pxTodip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static float getStarRandomInt() {
		int max = 5;
		int min = 3;
		Random random = new Random();
		// float s = random.nextInt(max) % (max - min + 1) + min;
		float s = random.nextInt(max - min + 1) + min;
		return s;
	}

	/**
	 * 只删除子文件，不删除父文件
	 * 
	 * @param path
	 *            父文件目录
	 */
	public static void deleteFileChildrens(String path) {
		File file = new File(path);
		if (file == null || !file.exists()) {
			return;
		}
		if (file != null && file.isDirectory() && file.listFiles().length > 0) {
			for (int i = 0; i < file.listFiles().length; i++) {
				Util.deleteFile(file.listFiles()[i].getAbsolutePath());
			}
		}
	}

	/**
	 * 递归删除文件及其子文件
	 * 
	 * @param path
	 */
	public static void deleteFile(String path) {
		if (TextUtils.isEmpty(path)) {
			return;
		}
		File file = new File(path);
		if (file == null || !file.exists()) {
			return;
		}
		if (file.isDirectory() && file.listFiles().length > 0) {
			for (int i = 0; i < file.listFiles().length; i++) {
				File childFile = file.listFiles()[i];
				if (childFile.isDirectory() && childFile.listFiles().length > 0) {
					deleteFile(childFile.getAbsolutePath());
				} else {
					childFile.delete();
				}
			}
		} else {
			file.delete();
		}
	}

	/**
	 * 开发文件所有权限
	 * 
	 * @param filepath
	 */
	public static void chrome0777File(String filepath) {
		try {
			Runtime.getRuntime().exec("chmod 0777  " + filepath);
			// Thread.sleep(600);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通过包名启动应用
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean openAppByPackageName(Context context, String packageName) {
		boolean isOk = false;
		if (packageName != null && packageName.equals("com.changhong.gdappstore")) {
			Toast.makeText(context, context.getString(R.string.app_started), Toast.LENGTH_LONG).show();
			return false;
		}
		try {
			System.out.println("packagename--------->" + packageName);
			// 通过包名启动
			PackageManager packageManager = context.getPackageManager();
			Intent intent = packageManager.getLaunchIntentForPackage(packageName);
			if (intent != null) {
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				isOk = true;
			} else {
				System.out.println("getLaunchIntentForPackage is null");
			}
		} catch (Exception e) {
			isOk = false;
		}
		if (!isOk) {
			Toast.makeText(context, context.getString(R.string.appstartfailed_pleasecheck), Toast.LENGTH_LONG).show();
		}
		return isOk;
	}

	/**
	 * 获取本地安装某应用，未安装返回空
	 * 
	 * @param context
	 * @param packageName
	 *            应用包名
	 * @return
	 */
	public static NativeApp getNativeApp(Context context, String packageName) {
		List<NativeApp> objects = getApp(context);
		if (objects == null || objects.size() == 0 || TextUtils.isEmpty(packageName)) {
			return null;
		}
		for (int i = 0; i < objects.size(); i++) {
			NativeApp nativeApp = (NativeApp) objects.get(i);
			if (nativeApp != null && nativeApp.getAppPackage() != null && nativeApp.getAppPackage().equals(packageName)) {
				return nativeApp;
			}
		}
		return null;
	}

	/**
	 * 获取本地安装应用列表
	 * 
	 * @param context
	 * @return
	 */
	public static List<NativeApp> getApp(Context context) {
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		if (context == null || context.getPackageManager() == null) {
			return null;
		}
		// http://blog.csdn.net/qinjuning/article/details/6867806
		List<NativeApp> nativeApps = new ArrayList<NativeApp>();
		List<PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);
		for (int i = 0; i < packages.size(); i++) {
			PackageInfo packageInfo = packages.get(i);
			if ((packageInfo.applicationInfo.flags & packageInfo.applicationInfo.FLAG_SYSTEM) <= 0) {
				// 非系统预装的应用程序

				NativeApp tmpInfo = new NativeApp();
				tmpInfo.appname = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
				tmpInfo.appPackage = packageInfo.packageName;
				tmpInfo.nativeVersionInt = packageInfo.versionCode;
				tmpInfo.checked = false;
				tmpInfo.appIcon = packageInfo.applicationInfo.loadIcon(context.getPackageManager());
				nativeApps.add(tmpInfo);
				tmpInfo.appid = -1;
			}
		}
		return nativeApps;
	}

	/**
	 * int转为str类型
	 * 
	 * @param int
	 * @return String
	 */
	public static String intToStr(int num) {
		if (!com.changhong.gdappstore.Config.IS_ENGLISH_VERSION && MyApplication.IS_ZH_LANGUAGE) {
			if (num < 10000) {
				// 1w以下直接返回
				return num + "";
			} else if (num < 100000) {
				// 1w~10w以下格式:x0000+ x为数字
				return num / 10000 + "万+";
			} else if (num < 1000000) {
				// 10w~100w以下格式:x0万+ x为数字
				return num / 100000 + "0万+";
			} else if (num < 10000000) {
				return num / 1000000 + "00万+";
			} else if (num < 100000000) {
				return num / 10000000 + "000万+";
			} else {
				return num / 100000000 + "亿+";
			}
		} else {
			if (num < 1000) {
				// 1000以下直接返回
				return num + "";
			} else if (num < 10000000) {
				// 千
				return num / 1000 + "thousand+";
			} else if (num < 1000000000) {
				// 百万
				return num / 1000000 + "million+";
			} else {
				return num / 100000000 + "billion+";
			}
		}
	}
	
	public static boolean getLanguageIsZH(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        L.d("language is "+ language);
        if (language.contains("zh"))
            return true;
        else
            return false;
    }

	/**
	 * 判断list是否为空
	 * 
	 * @param int
	 * @return String
	 */
	public static boolean listIsEmpty(List list) {
		if (list == null || list.isEmpty()) {
			return true;
		}
		return false;
	}

	/**
	 * 去除list里面的空值。
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<T> clearListNullItem(List<T> list) {
		if (listIsEmpty(list)) {
			return list;
		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == null) {
				list.remove(i);// 去除空值
				i--;
			}
		}
		return list;
	}

	/**
	 * 
	 * @param all
	 * @param lookingfor
	 * @param position
	 *            从1开始，要想选择第几个？
	 * @return
	 */
	public static int getIndexOfString(String all, Character lookingfor, int position) {
		if (TextUtils.isEmpty(all) || position >= all.length()) {
			return -1;
		}
		int pos = 0;
		for (int i = 0; i < all.length(); i++) {
			if (all.substring(i, i + 1).equals(lookingfor)) {
				pos++;
				if (pos == position) {
					return i;
				}
			}
		}
		return pos;
	}

	// public static Bitmap convertViewToBitmap(View view) {
	// if (view==null) {
	// L.d("view is null ");
	// return null;
	// }
	// L.d("musicpostitemview--convertViewToBitmap "+view.getWidth()+" shandowh "+view.getHeight());
	// view.setDrawingCacheEnabled(true);
	// view.buildDrawingCache();
	// Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());
	// view.setDrawingCacheEnabled(false);
	// return bmp;
	// }
	public static Bitmap convertViewToBitmap(View paramView) {
		paramView.destroyDrawingCache();
		paramView.measure(View.MeasureSpec.makeMeasureSpec(paramView.getWidth(), MeasureSpec.EXACTLY),
				View.MeasureSpec.makeMeasureSpec(paramView.getHeight(), MeasureSpec.EXACTLY));
		// paramView.layout(paramView.getLeft(), paramView.getTop(),
		// paramView.getMeasuredWidth(),
		// paramView.getMeasuredHeight());//会导致位置不对
		paramView.buildDrawingCache();

		return paramView.getDrawingCache();
	}

	public static Bitmap createImages(Context context, Bitmap bitmap, float heightProportion) {
		if (context == null || bitmap == null) {
			L.d("Util createImages---bitmap is null ");
			return null;
		}
		if (heightProportion < 0 || heightProportion > 1) {
			heightProportion = 0.4f;
		}
		// 原图与倒影的间距1px
		// final int gapHeight = 1;
		/* step1 采样方式解析原图并生成倒影 */
		// 解析原图，生成原图Bitmap对象
		Bitmap originalImage = bitmap;
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		// Y轴方向反向，实质就是X轴翻转
		Matrix matrix = new Matrix();
		matrix.setScale(1, -1);
		// 且仅取原图下半部分创建倒影Bitmap对象
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, (int) ((1 - heightProportion) * height), width,
				(int) (heightProportion * height), matrix, false);

		/* step2 绘制 */
		// 创建一个可包含原图+间距+倒影的新图Bitmap对象
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (int) (heightProportion * height), Config.ARGB_8888);
		// 在新图Bitmap对象之上创建画布
		Canvas canvas = new Canvas(bitmapWithReflection);
		// 抗锯齿效果
		canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG));
		// 绘制原图
		// canvas.drawBitmap(originalImage, 0, 0, null);
		// 绘制间距
		Paint gapPaint = new Paint();
		gapPaint.setColor(0xFFCCCCCC);
		// canvas.drawRect(0, height, width, height + gapHeight, gapPaint);
		// 绘制倒影
		canvas.drawBitmap(reflectionImage, 0, 0, null);

		/* step3 渲染 */
		// 创建一个线性渐变的渲染器用于渲染倒影
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, 0, 0, (int) (heightProportion * height), 0x55ffffff, 0x00ffffff,
				TileMode.CLAMP);
		// 设置画笔渲染器
		paint.setShader(shader);
		// 设置图片混合模式
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// 渲染倒影+间距
		canvas.drawRect(0, 0, width, (int) (heightProportion * height), paint);

		// maps.put(resId, bitmapWithReflection);

		/* step4 释放heap */
		originalImage.recycle();
		reflectionImage.recycle();
		return bitmapWithReflection;
	}

	public static Bitmap createReflectedImages(Context context, Bitmap bitmap) {
		final int reflectionGap = 4;// 原图与倒影之间的间隙
		Bitmap originalImage = bitmap; // 获得图片资源
		// 获得图片的长宽
		int width = originalImage.getWidth();
		int height = originalImage.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1); // 实现图片的反转
		Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height / 2, width, height / 2, matrix, false); // 创建反转后的图片Bitmap对象，图片高是原图的一半
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888); // 创建标准的Bitmap对象，宽和原图一致，高是原图的1.5倍

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(originalImage, 0, 0, null); // 创建画布对象，将原图画于画布，起点是原点位置
		Paint paint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, paint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null); // 将反转后的图片画到画布中

		paint = new Paint();
		LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0, bitmapWithReflection.getHeight()
				+ reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.MIRROR);// 创建线性渐变LinearGradient对象
		paint.setShader(shader); // 绘制
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));// 倒影遮罩效果
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint); // 画布画出反转图片大小区域，然后把渐变效果加到其中，就出现了图片的倒影效果

		return reflectionImage;
	}
}

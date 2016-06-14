package com.changhong.gdappstore.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class NativeApp implements Serializable{
	/*** 应用id，小于等于0表示服务端没该应用 */
	public int appid = -1;
	/** 应用名称 */
	public String appname;
	/** 服务端配置版本（不一定是apk里面版本号） */
	public int ServerVersionInt;
	/** 本地数据库保存版本（不一定是apk里面版本号） */
	public int nativeVersionInt;
	/** 应用图标 */
	public Drawable appIcon;
	/** 应用包名 */
	public String appPackage;
	/**多选时候是否选中*/
	public boolean checked;

	public NativeApp() {
	}

	public NativeApp(int appid, String appname, int serverVersionInt, int nativeVersionInt, Drawable appIcon,
			String appPackage) {
		super();
		this.appid = appid;
		this.appname = appname;
		ServerVersionInt = serverVersionInt;
		this.nativeVersionInt = nativeVersionInt;
		this.appIcon = appIcon;
		this.appPackage = appPackage;
	}

	public int getAppid() {
		return appid;
	}

	public void setAppid(int appid) {
		this.appid = appid;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public int getServerVersionInt() {
		return ServerVersionInt;
	}

	public void setServerVersionInt(int serverVersionInt) {
		ServerVersionInt = serverVersionInt;
	}

	public int getNativeVersionInt() {
		return nativeVersionInt;
	}

	public void setNativeVersionInt(int nativeVersionInt) {
		this.nativeVersionInt = nativeVersionInt;
	}

	public Drawable getAppIcon() {
		return appIcon;
	}

	public void setAppIcon(Drawable appIcon) {
		this.appIcon = appIcon;
	}

	public String getAppPackage() {
		return appPackage;
	}

	public void setAppPackage(String appPackage) {
		this.appPackage = appPackage;
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	public String toString() {
		return "NativeApp [appid=" + appid + ", appname=" + appname + ", ServerVersionInt=" + ServerVersionInt
				+ ", nativeVersionInt=" + nativeVersionInt + ", appIcon=" + appIcon + ", appPackage=" + appPackage
				+ "]";
	}

}

package com.changhong.gdappstore.model;

/**
 * 应用详情
 * 
 * @author wangxiufeng
 * 
 */
public class AppDetail extends App {
	public static final String PASSED = "PASSED";
	// 应用apk下载地址
	protected String apkFilePath;
	// 应用描述
	protected String description;
	// 应用路径
	protected String host;
	// 分类id
	protected int categoryId;
	// 应用路径
	protected String updateDate;
	// 状态，除了PASSED外都为下架
	protected String state = PASSED;
	// 是否是VIP应用
	protected boolean isVipApp = false;
	// 是否可以下载
	protected boolean isCanDownload = true;

	public AppDetail() {
		super();
	}

	/**
	 * 是否上架
	 * 
	 * @return true上架，false下架
	 */
	public boolean isOnShelf() {
		return state.equals(PASSED);
	}

	public String getApkFilePath() {
		return apkFilePath;
	}

	public void setApkFilePath(String apkFilePath) {
		this.apkFilePath = apkFilePath;
	}

	public String getApkSize() {
		return apkSize;
	}

	public void setApkSize(String apkSize) {
		this.apkSize = apkSize;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public boolean isVipApp() {
		return isVipApp;
	}

	public void setVipApp(boolean isVipApp) {
		this.isVipApp = isVipApp;
	}

	public boolean isCanDownload() {
		return isCanDownload;
	}

	public void setCanDownload(boolean isCanDownload) {
		this.isCanDownload = isCanDownload;
	}

	@Override
	public String toString() {
		return "AppDetail [apkFilePath=" + apkFilePath + ", description=" + description + ", host=" + host
				+ ", categoryId=" + categoryId + ", updateDate=" + updateDate + ", state=" + state + ", isVipApp="
				+ isVipApp + ", isCanDownload=" + isCanDownload + "]";
	}

}

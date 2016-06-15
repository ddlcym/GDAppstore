package com.changhong.gdappstore.model;
/**
 * 首页推荐位应用信息（比起基础应用信息多了页面序号和页面存放位置参数）
 * @author wangxiufeng
 *
 */
public class PageApp extends App {
	// 页面序号
	protected int pageid;
	// 页面存放位置
	protected int position;

	public PageApp() {
		super();
	}


	public int getPageid() {
		return pageid;
	}

	public void setPageid(int pageid) {
		this.pageid = pageid;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "PageApp ["+super.toString()+"pageid=" + pageid + ", position=" + position + "]";
	}

}

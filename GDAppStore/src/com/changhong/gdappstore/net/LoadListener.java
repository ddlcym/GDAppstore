package com.changhong.gdappstore.net;

import java.util.List;

/**
 * 请求解析完成监听器
 * 
 * @author wangxiufeng
 * 
 */
public class LoadListener {
	/**
	 * 加载完成监听器
	 * 
	 * @author wangxiufeng
	 * 
	 */
	public interface LoadCompleteListener {
		public void onComplete();
	}

	/**
	 * 加载列表完成监听器
	 * 
	 * @author wangxiufeng
	 * 
	 */
	public interface LoadListListener {
		public void onComplete(List<Object> items);
	}
	/**
	 * 加载完成监听器
	 * 
	 * @author wangxiufeng
	 * 
	 */
	public interface LoadObjectListener {
		public void onComplete(Object object);
	}
}

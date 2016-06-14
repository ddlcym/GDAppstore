package com.changhong.gdappstore.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.text.TextUtils;

import com.changhong.gdappstore.Config;
import com.changhong.gdappstore.R;
import com.changhong.gdappstore.util.DialogUtil;
import com.changhong.gdappstore.util.L;
import com.changhong.gdappstore.util.NetworkUtils;

/**
 * 网络请求
 * 
 * @author wangxiufeng
 * 
 */
public class HttpRequestUtil {

	/**
	 * 获取String
	 * 
	 * @param httpEntity
	 * @return
	 */
	public static String getEntityString(HttpEntity httpEntity, Context context) {
		String jsonString = "";
		if (httpEntity == null) {
			L.d("getEntityString return null by httpEntity is null ");
			return null;
		}
		try {
			long contentLenght = httpEntity.getContentLength();
			jsonString = EntityUtils.toString(httpEntity, "UTF-8");
			if (Config.IS_JSONSTRING_DEBUG) {
				L.d("getEntityString-httpentity content lenght is " + contentLenght + " jsonString " + jsonString);
			}
		} catch (Exception e) {
			L.d("getEntityString error " + e);
			DialogUtil.showChildThreadToast(context.getString(R.string.error_parse), context, true);
			e.printStackTrace();
		}
		return jsonString;
	}

	/**
	 * get请求方式
	 * 
	 * @param url
	 *            地址
	 * @return
	 */
	public static HttpEntity doGetRequest(String url, Context context) {
		if (TextUtils.isEmpty(url) || !NetworkUtils.isConnectInternet(context)) {
			L.d("doGetRequest--returned by url= " + url + " netisconnect= " + NetworkUtils.ISNET_CONNECT);
			//DialogUtil.showChildThreadToast(context.getString(R.string.error_net_notconnect), context, true);
			return null;
		}
		url = url.replaceAll(" ", "%20");// 替换空格
		L.d("doGetRequest--url is " + url);
		try {
			// URLEncoder.encode(url,"UTF-8");
			HttpGet httpGet = new HttpGet(url);
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Config.CONNECTION_TIMEOUT);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, Config.CONNECTION_TIMEOUT);
			httpClient.getConnectionManager().closeExpiredConnections();
			HttpResponse httpResponse = httpClient.execute(httpGet);
			if (httpResponse == null || httpResponse.getStatusLine() == null) {
				L.e("doGetRequest-The Http Response is null or statusline is null");
			}
			if (200 == httpResponse.getStatusLine().getStatusCode()) {
				HttpEntity httpEntity = httpResponse.getEntity();
				return httpEntity;
			}
		} catch (Exception e) {
			L.e("doGetRequest- connect error");
			DialogUtil
					.showChildThreadToast(context.getString(R.string.error_netconnect_please_checknet), context, true);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * post请求方式
	 * 
	 * @param url
	 *            地址
	 * @param paramList
	 *            参数
	 * @return
	 */
	public static HttpEntity doPostRequest(String url, final List<NameValuePair> paramList, Context context) {
		if (TextUtils.isEmpty(url) || !NetworkUtils.isConnectInternet(context)) {
			L.d("doPostRequest--returned by url= " + url + " netisconnect= " + NetworkUtils.ISNET_CONNECT);
			//DialogUtil.showChildThreadToast(context.getString(R.string.error_net_notconnect), context, true);
			return null;
		}
		url = url.replaceAll(" ", "%20");// 替换空格
		L.d("doPostRequest--url is " + url);
		HttpPost httpPost = null;
		try {
			httpPost = new HttpPost(url);
			if (paramList != null) {
				httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
			}
			DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, Config.CONNECTION_TIMEOUT);
			httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, Config.CONNECTION_TIMEOUT);
			httpClient.getConnectionManager().closeExpiredConnections();
			HttpResponse httpResponse = httpClient.execute(httpPost);
			if (httpResponse == null || httpResponse.getStatusLine() == null) {
				L.e("doPostRequest-The Http Response is null or statusline is null");
			}
			if (200 == httpResponse.getStatusLine().getStatusCode()) {
				HttpEntity httpEntity = httpResponse.getEntity();
				// long contentLenght = httpEntity.getContentLength();
				// jsonString = EntityUtils.toString(httpEntity, "UTF-8");
				// L.d("doGetRequest-httpentity content lenght is " +
				// contentLenght + " jsonString " + jsonString);
				return httpEntity;
			}
		} catch (Exception e) {
			L.e("doPostRequest--connect error ");
			DialogUtil
					.showChildThreadToast(context.getString(R.string.error_netconnect_please_checknet), context, true);
			e.printStackTrace();
		}
		return null;
	}

	public static void writetofile(byte[] bytes, File file) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 将HTTP请求实体转换为数组
	 * 
	 * @param httpEntity
	 * @return
	 * @throws IOException
	 */
	public static byte[] toByteArray(HttpEntity httpEntity) throws IOException {
		if (httpEntity == null) {
			L.w("HttpEntityUtils HTTP entity is null");
		}
		InputStream in = checkGzippedContent(httpEntity);
		if (in == null) {
			L.w("HttpEntityUtils The inputStream is null");
			return null;
		}
		int i = (int) httpEntity.getContentLength();
		if (i > Integer.MAX_VALUE) {
			L.w("HttpEntityUtils HTTP entity too large to be buffered in memory");
		}
		if (i < 0) {
			i = 4096;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream(i);
		try {
			byte[] arrayOfByte = new byte[4096];
			while (true) {
				int j = in.read(arrayOfByte);
				if (j == -1) {
					break;
				}
				baos.write(arrayOfByte, 0, j);
			}
		} finally {
			in.close();
		}
		return baos.toByteArray();
	}

	private static InputStream checkGzippedContent(HttpEntity httpEntity) throws IOException {
		// 此方法验证OK,反编译和源码一摸一样
		InputStream responseStream = httpEntity.getContent();
		if (responseStream == null) {
			return responseStream;
		}
		Header header = httpEntity.getContentEncoding();
		if (header != null) {
			String contentEncoding = header.getValue();
			if (contentEncoding != null) {
				if (contentEncoding.contains("gzip")) {
					L.d("HttpEntityUtils The response stream is gzipped");
					return new GZIPInputStream(responseStream);
				}
			}
		}
		return responseStream;
	}

}

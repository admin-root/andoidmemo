package com.phodev.android.tools.conn;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.phodev.android.tools.conn.ConnectionHelper.RequestMethod;
import com.phodev.android.tools.conn.ConnectionHelper.RequestReceiver;

/**
 * 请求实例
 * 
 * @author skg
 * 
 */
public class RequestEntity {
	private String url;
	private HttpEntity postEntity;
	private RequestReceiver requestReceiver;
	private String rawResponse;
	private RequestMethod requestMethod;
	private int resultCode;
	private int requestId;
	private Object mTag;

	private RequestEntity() {

	}

	public RequestEntity(String url, RequestMethod method) {
		this.url = url;
		this.requestMethod = method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Post需要发送的数据
	 * 
	 * @return
	 */
	public HttpEntity getPostEntitiy() {
		return postEntity;
	}

	/**
	 * Post需要发送的数据
	 * 
	 * @return
	 */
	public void setPostEntitiy(HttpEntity entity) {
		postEntity = entity;
	}

	public void setPostEntitiy(List<NameValuePair> postValues) {
		try {
			postEntity = new UrlEncodedFormEntity(postValues);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 带文件上传的POST
	 * 
	 * <pre>
	 * RequestMethod必须是{@link ConnectionHelper.RequestMethod#POST_WITH_FILE}}模式
	 * </pre>
	 * 
	 * @param postValues
	 * @param files
	 */
	public void setPostEntitiy(List<NameValuePair> postValues,
			Map<String, File> files) {
		MultipartEntity entity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		postEntity = entity;
		if (postValues != null) {
			for (NameValuePair v : postValues) {
				try {
					entity.addPart(v.getName(), new StringBody(v.getValue()));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		if (files != null) {
			Iterator<Entry<String, File>> iterator = files.entrySet()
					.iterator();
			while (iterator.hasNext()) {
				Entry<String, File> entry = iterator.next();
				entity.addPart(entry.getKey(), new FileBody(entry.getValue()));
			}
		}
	}

	public void setPostEntitiy(String querryString) {
		try {
			postEntity = new StringEntity(querryString);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	public RequestReceiver getRequestReceiver() {
		return requestReceiver;
	}

	public void setRequestReceiver(RequestReceiver receiver) {
		this.requestReceiver = receiver;
	}

	public String getRawResponse() {
		return rawResponse;
	}

	protected void setRawResponse(String rawResponse) {
		this.rawResponse = rawResponse;
	}

	/**
	 * Get,Post
	 * 
	 * @return
	 */
	public RequestMethod getMethod() {
		return requestMethod;
	}

	/**
	 * Get,Post
	 * 
	 * @return
	 */
	public void setMethod(RequestMethod method) {
		this.requestMethod = method;
	}

	public int getResultCode() {
		return resultCode;
	}

	protected void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public int getRequestId() {
		return requestId;
	}

	protected void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	public void setTag(Object tag) {
		mTag = tag;
	}

	public Object getTag() {
		return mTag;
	}

	private final static List<RequestEntity> recyleList = new ArrayList<RequestEntity>();

	public static RequestEntity obtain() {
		if (recyleList.size() <= 0) {
			return new RequestEntity();
		} else {
			return recyleList.remove(0);
		}
	}

	public void recycle() {
		url = null;
		postEntity = null;
		requestReceiver = null;
		rawResponse = null;
		requestMethod = null;
		resultCode = 0;
		if (recyleList.size() < 6) {
			recyleList.add(this);
		}
	}
}

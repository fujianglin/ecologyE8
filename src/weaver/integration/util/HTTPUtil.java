/**
 *
 * Copyright (c) 2001-2017 泛微软件.
 * 泛微协同商务系统,版权所有.
 * 
 */
package weaver.integration.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import weaver.general.FWHttpConnectionManager;
import weaver.integration.logging.Logger;
import weaver.integration.logging.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @version 1.0
 * @author lv HTTP工具类
 */
public class HTTPUtil {
    /**
     * 集成日志
     */
	private static Logger newlog = LoggerFactory.getLogger(weaver.integration.util.HTTPUtil.class);
	
	/**
     * 编码方式：UTF-8
     */
	private static String HTTP_UTF8 = "UTF-8";
	
	/**
	 * 发送GET请求
	 * @param url GET请求地址
	 * @return
	 */
	public static String doGet(String url) {
		return doGet(url, null, null, 0, null);
	}
	
	/**
	 * 发送GET请求
	 * @param url		GET请求地址
	 * @param charset	返回内容的编码
	 * @return
	 */
	public static String doGet(String url, String charset) {
		return doGet(url, null, null, 0, charset);
	}
	
	/**
	 * 发送GET请求
	 * @param url		GET请求地址
	 * @param headerMap GET请求头
	 * @return
	 */
	public static String doGet(String url, Map headerMap) {
		return doGet(url, headerMap, null, 0, null);
	}
	
	/**
	 * 发送GET请求
	 * @param url		GET请求地址
	 * @param headerMap	GET请求头
	 * @param charset	返回内容的编码
	 * @return
	 */
	public static String doGet(String url, Map headerMap, String charset) {
		return doGet(url, headerMap, null, 0, charset);
	}
	
	/**
	 * 发送GET请求
	 * @param url		GET请求地址
	 * @param proxyUrl  代理服务器地址
	 * @param proxyPort 代理服务器端口
	 * @return
	 */
	public static String doGet(String url, String proxyUrl, int proxyPort) {
		return doGet(url, null, proxyUrl, proxyPort, null);
	}
	
	/**
	 * 发送GET请求
	 * @param url		GET请求地址，必填
	 * @param headerMap GET请求头，没有可以填null
	 * @param proxyUrl  代理服务器地址，没有可以填null
	 * @param proxyPort 代理服务器端口，没有可以填0
	 * @param charset	返回内容的编码，默认为UTF-8
	 * @return 响应内容
	 */
	public static String doGet(String url, Map headerMap, String proxyUrl, int proxyPort, String charset) {
		//byte[] content = null;
		String result = null;
		// HttpClient httpClient = new HttpClient();
		HttpClient httpClient = FWHttpConnectionManager.getHttpClient();
		newlog.error("GET请求地址=" + url);
		GetMethod getMethod = new GetMethod(url);
		
		// 请求头
		if (headerMap != null) {
			Iterator iter = headerMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				String headerName = (String) entry.getKey();
				String headerValue = (String) entry.getValue();
				newlog.error("头部请求信息：headerName=" + headerName + "，headerValue=" + headerValue);
				getMethod.addRequestHeader(headerName, headerValue);
			}
		}
		
		if (StringUtils.isNotBlank(proxyUrl)) {
			newlog.error("代理服务器地址=" + proxyUrl + "，代理服务器端口=" + proxyPort);
			httpClient.getHostConfiguration().setProxy(proxyUrl, proxyPort);
		}
		
		if (charset == null || "".equals(charset)) {
			charset = HTTP_UTF8;
		}
		
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(100000000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(100000000);
		
		InputStream inputStream = null;
		try {
			int status = httpClient.executeMethod(getMethod);
			newlog.error("status=" + status);
			if (status == HttpStatus.SC_OK) {
				inputStream = getMethod.getResponseBodyAsStream();
				//content = IOUtils.toByteArray(inputStream);
				result = IOUtils.toString(inputStream, charset);
			} else {
				newlog.error("Method failed : " + getMethod.getStatusLine());
			}
		} catch (IOException e) {
			newlog.error("GET请求异常：", e);
		} finally {
			IOUtils.closeQuietly(inputStream);
			getMethod.releaseConnection();
		}
		
		return result;
	}
	
	
	
	/**
	 * 发送POST请求
	 * @param url			POST请求地址
	 * @param parameterMap	POST请求参数
	 * @return
	 */
	public static String doPost(String url, Map parameterMap) {
		return doPost(url, null, parameterMap, null, null, 0, null);
	}
	
	/**
	 * 发送POST请求
	 * @param url			POST请求地址
	 * @param parameterMap	POST请求参数
	 * @param paramCharset	参数编码
	 * @return
	 */
	public static String doPost(String url, Map parameterMap,
			String paramCharset) {
		return doPost(url, null, parameterMap, paramCharset, null, 0, null);
	}
	
	/**
	 * 发送POST请求
	 * @param url			POST请求地址
	 * @param parameterMap	POST请求参数
	 * @param paramCharset	参数编码
	 * @param charset		返回内容的编码
	 * @return
	 */
	public static String doPost(String url, Map parameterMap,
			String paramCharset, String charset) {
		return doPost(url, null, parameterMap, paramCharset, null, 0, charset);
	}
	
	/**
	 * 发送POST请求
	 * @param url			POST请求地址
	 * @param headerMap		POST请求头
	 * @param parameterMap	POST请求参数
	 * @param paramCharset	参数编码
	 * @return
	 */
	public static String doPost(String url, Map headerMap, Map parameterMap,
			String paramCharset) {
		return doPost(url, headerMap, parameterMap, paramCharset, null, 0, null);
	}
	
	/**
	 * 发送POST请求
	 * @param url			POST请求地址
	 * @param headerMap		POST请求头
	 * @param parameterMap	POST请求参数
	 * @param paramCharset	参数编码
	 * @param charset		返回内容的编码
	 * @return
	 */
	public static String doPost(String url, Map headerMap, Map parameterMap,
			String paramCharset, String charset) {
		return doPost(url, headerMap, parameterMap, paramCharset, null, 0, charset);
	}
	
	/**
	 * 发送POST请求
	 * @param url			POST请求地址
	 * @param parameterMap	POST请求参数
	 * @param paramCharset	参数编码
	 * @param proxyUrl		代理服务器地址
	 * @param proxyPort		代理服务器端口
	 * @return
	 */
	public static String doPost(String url, Map parameterMap,
			String paramCharset, String proxyUrl, int proxyPort) {
		return doPost(url, null, parameterMap, paramCharset, proxyUrl, proxyPort, null);
	}
	
	/**
	 * 发送POST请求
	 * @param url			POST请求地址
	 * @param parameterMap	POST请求参数
	 * @param paramCharset	参数编码
	 * @param proxyUrl		代理服务器地址
	 * @param proxyPort		代理服务器端口
	 * @param charset		返回内容的编码
	 * @return
	 */
	public static String doPost(String url, Map parameterMap,
			String paramCharset, String proxyUrl, int proxyPort, String charset) {
		return doPost(url, null, parameterMap, paramCharset, proxyUrl, proxyPort, charset);
	}
	
	/**
	 * 发送POST请求
	 * @param url			POST请求地址，必填
	 * @param headerMap		POST请求头，没有可以填null
	 * @param parameterMap	POST请求参数
	 * @param paramCharset	参数编码，没有可以填null
	 * @param proxyUrl		代理服务器地址，没有可以填null
	 * @param proxyPort		代理服务器端口，没有可以填0
	 * @param charset		返回内容的编码，默认为UTF-8
	 * @return 响应内容
	 */
	public static String doPost(String url, Map headerMap, Map parameterMap,
			String paramCharset, String proxyUrl, int proxyPort, String charset) {
		//byte[] content = null;
		String result = null;
		// HttpClient httpClient = new HttpClient();
		HttpClient httpClient = FWHttpConnectionManager.getHttpClient();
		newlog.error("POST请求地址=" + url);
		PostMethod postMethod = new PostMethod(url);
		
		if (StringUtils.isNotBlank(paramCharset)) {
			postMethod.getParams().setContentCharset(paramCharset);
			postMethod.getParams().setHttpElementCharset(paramCharset);
		}
		
		// 请求头
		if (headerMap != null) {
			Iterator iter = headerMap.entrySet().iterator();
			while (iter.hasNext()) {
				Entry entry = (Entry) iter.next();
				String headerName = (String) entry.getKey();
				String headerValue = (String) entry.getValue();
				newlog.error("头部请求信息：headerName=" + headerName + "，headerValue=" + headerValue);
				postMethod.addRequestHeader(headerName, headerValue);
			}
		}
		
		// 请求参数
		Iterator iterator = parameterMap.keySet().iterator();
		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			String value = (String) parameterMap.get(key);
			postMethod.addParameter(key, value);
		}
		
		if (StringUtils.isNotBlank(proxyUrl)) {
			newlog.error("代理服务器地址=" + proxyUrl + "，代理服务器端口=" + proxyPort);
			httpClient.getHostConfiguration().setProxy(proxyUrl, proxyPort);
		}
		
		if (charset == null || "".equals(charset)) {
			charset = HTTP_UTF8;
		}
		
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(10000);
		
		InputStream inputStream = null;
		try {
			int status = httpClient.executeMethod(postMethod);
			newlog.error("status=" + status);
			if (status == HttpStatus.SC_OK) {
				inputStream = postMethod.getResponseBodyAsStream();
				//content = IOUtils.toByteArray(inputStream);
				result = IOUtils.toString(inputStream, charset);
			} else {
				newlog.error("Method failed : " + postMethod.getStatusLine());
			}
		} catch (IOException e) {
			newlog.error("POST请求异常：", e);
		} finally {
			IOUtils.closeQuietly(inputStream);
			postMethod.releaseConnection();
		}
		
		return result;
	}
	
	/**
	 * 发送POST请求，传输文件
	 * @param url			请求地址
	 * @param contentType	MIME类型
	 * @param filePath		文件路径
	 * @return
	 */
	public static String doPostFile(String url, String contentType, String filePath) {
		return doPostFile(url, contentType, filePath, null);
	}
	
	/**
	 * 发送POST请求，传输文件
	 * @param url			请求地址
	 * @param contentType	MIME类型
	 * @param filePath		文件路径
	 * @param charset		返回内容的编码，默认为UTF-8
	 * @return
	 */
	public static String doPostFile(String url, String contentType, String filePath, String charset) {
		StringBuffer result = new StringBuffer();
		String line = "";
		
		HttpClient httpClient = FWHttpConnectionManager.getHttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
		httpClient.getHttpConnectionManager().getParams().setSoTimeout(10000);
		
		newlog.error("POST请求地址=" + url);
		PostMethod postMethod = new PostMethod(url);
		postMethod.setRequestHeader("Content-Type", contentType);
		
		if (charset == null || "".equals(charset)) {
			charset = HTTP_UTF8;
		}
		
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			InputStreamRequestEntity entity = new InputStreamRequestEntity(fis);
			postMethod.setRequestEntity((RequestEntity) entity);
			
			InputStream inputStream = null;
			int status = httpClient.executeMethod(postMethod);
			newlog.error("status=" + status);
			if (status == HttpStatus.SC_OK) {
				inputStream = postMethod.getResponseBodyAsStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, charset));
				while ((line = br.readLine()) != null) {
					result.append(line);
				}
				br.close();
			} else {
				newlog.error("Method failed : " + postMethod.getStatusLine());
			}
		} catch (FileNotFoundException e) {
			newlog.error("FileNotFoundException:", e);
		} catch (IOException e) {
			newlog.error("IOException:", e);
		}
		
		return result.toString();
	}
	
	/**
	 * 发送POST请求，传递数据
	 * @param urlStr		请求地址
	 * @param contentType	MIME类型
	 * @param data			传入的数据
	 * @return 响应内容		
	 */
	public static String doPostData(String urlStr, String contentType, String data) {
		return doPostData(urlStr, contentType, data, null);
	}
	
	/**
	 * 发送POST请求，传递数据
	 * @param urlStr		请求地址
	 * @param contentType	MIME类型
	 * @param data			传入的数据
	 * @param charset		返回内容的编码，默认为UTF-8
	 * @return 响应内容		
	 */
	public static String doPostData(String urlStr, String contentType, String data, String charset) {
		StringBuffer result = new StringBuffer();
		String line = "";
		try {
			URL url = new URL(urlStr);
			URLConnection con = url.openConnection();
			con.setDoInput(true);// 从服务器获取数据
			con.setDoOutput(true);// 向服务器写入数据
			con.setConnectTimeout(5000);
			con.setRequestProperty("Pragma:", "no-cache");
			con.setRequestProperty("Cache-Control", "no-cache");
			con.setRequestProperty("Content-Type", contentType);
			
			if (charset == null || "".equals(charset)) {
				charset = HTTP_UTF8;
			}
			
			OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream(), charset);
			String temp = data;
			newlog.error("post data=" + temp);
			out.write(temp);
			out.flush();
			out.close();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), charset));
			while ((line = br.readLine()) != null) {
				result.append(line);
			}
		} catch (MalformedURLException e) {
			newlog.error("MalformedURLException:", e);
		} catch (IOException e) {
			newlog.error("IOException:", e);
		}
		
		return result.toString();
	}
/**
 * basic 认证（get）
 * @param url 认证地址
 * @param username 用户名
 * @param password 密码
 * @return
 */
	public static String doGetWithBasicAuth(String url, String username,String password) {
		Map headparas=new HashMap();
		String nameAndpwd=username+":"+password;
		String base64=new String (weaver.general.Base64.encode(nameAndpwd.getBytes()));
		headparas.put("Authorization","Basic "+base64);
		return doGet(url,headparas);
		
	}
	
	
	
}

package msdev.jhswyy.sap.util;

import java.io.IOException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import weaver.general.BaseBean;

/**
 * @version 1.0
 * @author  joshua.cheng
 * SAP连接工具类
 */
public class sapInterfaceUtil extends BaseBean {

	/**
	 * 根据地址/用户名/密码获取token
	 * @param url 认证地址
	 * @param username 用户名
	 * @param password 密码
	 * @return
	 */
	public static String doGetTokenWithBasicAuth(String url, String username,String password) {
		BaseBean bb = new BaseBean();
		String token = "";
		String nameAndpwd = username + ":" + password;
		String base64 = new String(weaver.general.Base64.encode(nameAndpwd.getBytes()));
		String code = "Basic " + base64;

		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");
		httpget.setHeader("Authorization", code);
		httpget.setHeader("x-csrf-token", "fetch");
		Header headers[] = httpget.getAllHeaders();
		HttpClient httpclient = new DefaultHttpClient();
		HttpResponse res;
		try {
			res = httpclient.execute(httpget);
			headers = res.getAllHeaders();
			for (Header h : headers) {
				if (h.getName().equals("x-csrf-token")) {
					token = h.getValue();
				}
			}
		} catch (ClientProtocolException e) {
			bb.writeLog(e);
		} catch (IOException e) {
			bb.writeLog(e);
		}
		return token;
	}
}

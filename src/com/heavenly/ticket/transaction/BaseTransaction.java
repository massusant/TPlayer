package com.heavenly.ticket.transaction;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;


public abstract class BaseTransaction {

//	public boolean login(String name, String password, String verifycode) {
//		String loginUrl = "";
//		try {
//			HttpURLConnection urlConnecton = RpcHelper.doInvokeRpcForConnection(loginUrl, null);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
	
	public abstract BaseResponse doAction();
	
	public abstract List<NameValuePair> getParamList();
	
	protected HashMap<String, String> obtainRequestHeader() {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("Accept", "application/json, text/javascript, */*");
		map.put("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
		map.put("Accept-Encoding", "gzip,deflate,sdch");
		map.put("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4");
		map.put("Connection", "keep-alive");
		map.put("Content-Type", "application/x-www-form-urlencoded");
		map.put("Host", "dynamic.12306.cn");
		map.put("Origin", "https://dynamic.12306.cn");
		map.put("X-Requested-With", "XMLHttpRequest");
		map.put("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.17 (KHTML, like Gecko) Chrome/24.0.1312.52 Safari/537.17");

//		map.put("Referer",
//				"https://dynamic.12306.cn/otsweb/order/loginAction.do?method=login");
		return map;
	}
}
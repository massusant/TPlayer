package com.heavenly.ticket.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;
import android.util.Log;

import com.heavenly.ticket.util.RpcHelper;

public class LoginTransaction extends BaseTransaction {
	
	private final String TAG = "LoginService";

	private final String URL_FOR_LOGIN_SAND = "https://dynamic.12306.cn/otsweb/loginAction.do?method=loginAysnSuggest";
	private final String URL_FOR_REAL_LOGIN = "https://dynamic.12306.cn/otsweb/loginAction.do?method=login";
	
	private String mUserName;
	private String mPassword;
	private String mVerifyCode;
	
	private String loginRand;
	
	private BaseResponse mRespons;
	private String mainResponsContent;
	private String mShowName;
	private String mShowGender;
	
	public void setUserName(String user) {
		mUserName = user;
	}
	
	public void setPassword(String pass) {
		mPassword = pass;
	}
	
	public void setVerfiyCode(String code) {
		mVerifyCode = code;
	}
	
	public String getUserShowName() {
		return mShowName;
	}
	
	public String getUserShowGender() {
		return mShowGender;
	}
	
	@Override
	public BaseResponse doAction() {
		mRespons = new BaseResponse();
		mRespons.success = doLogin();
		return mRespons;
	}

	@Override
	public List<NameValuePair> getParamList() {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("refundLogin","N"));
		params.add(new BasicNameValuePair("refundFlag", "Y"));
		params.add(new BasicNameValuePair("loginUser.user_name", mUserName));
		params.add(new BasicNameValuePair("user.password", mPassword));
		params.add(new BasicNameValuePair("randCode", mVerifyCode));
		params.add(new BasicNameValuePair("nameErrorFocus", ""));
		params.add(new BasicNameValuePair("passwordErrorFocus", ""));
		params.add(new BasicNameValuePair("randErrorFocus", ""));
		params.add(new BasicNameValuePair("loginRand", loginRand));
		return params;
	}
	
	private boolean doLogin() {
		if (mRespons == null) {
			mRespons = new BaseResponse();
		}
		if (!doActionForLoginSand()) {
			Log.d(TAG, "登陆随机数获取失败。。。");
			mRespons.msg = "登陆随机数获取失败。。。";
			return false;
		}
		if (!doActionForLogin()) {
			Log.d(TAG, "登陆失败");
			mRespons.msg = "登陆失败";
			return false;
		}
		Log.d(TAG, "登陆成功");
		mRespons.msg = "登陆成功";
		return true;
	}
	
	private boolean doActionForLoginSand() {
		int retry_times = 10;
		String sand = null;
		for (int i = 0; i < retry_times && sand == null; i++) {
			Map<String, String> header = obtainRequestHeader();
			String sandJsonString = RpcHelper.doInvokeRpc(
					URL_FOR_LOGIN_SAND, header, null);
			try {
				JSONObject sandJson = new JSONObject(sandJsonString);
				sand = sandJson.getString("loginRand");
			} catch (JSONException e) {
				e.printStackTrace();
				sand = null;
			}
		}
		loginRand = sand;
		return !TextUtils.isEmpty(loginRand);
	}
	
	private boolean doActionForLogin() {
		Map<String, String> header = obtainRequestHeader();
		String content = RpcHelper.doInvokeRpcByPost(URL_FOR_REAL_LOGIN,
				header, getParamList());
		if (TextUtils.isEmpty(content)) {
			return false;
		}
		if (content.contains(FLAG_CONTENT_SUCCESS)) {
			mainResponsContent = content;
			mShowName = parseShowName(content);
			mShowGender = parseGender(content);
			return true;
		} else if (content.contains(FLAG_CONTENT_MAINTAIN)) {
			Log.d(TAG, FLAG_CONTENT_MAINTAIN);
		}
		return false; 
	}
	
	private String parseStringByTag(String content, String startTag, char endTag) {
		if (TextUtils.isEmpty(content) || TextUtils.isEmpty(startTag)
				|| TextUtils.isEmpty(endTag + "")) {
			return "unknown";
		}
		int index = content.indexOf(startTag);
		for (int i = index + startTag.length(); i < index + 100; i++) {
			char c = content.charAt(i);
			if (c == endTag) {
				return content.substring(index + startTag.length(), i);
			}
		}
		return "unknown";
	}
	
	private String parseShowName(String content) {
		return parseStringByTag(content, "var u_name = '", '\'');
	}
	
	private String parseGender(String content) {
		return parseStringByTag(content, "var hello = '" + mShowName, ' ');
	}
	
	

	@Override
	protected HashMap<String, String> obtainRequestHeader() {
		HashMap<String, String> header = super.obtainRequestHeader();
		header.put("Referer", "https://dynamic.12306.cn/otsweb/loginAction.do?method=init");
		return header;
	}
	
	private final String FLAG_CONTENT_SUCCESS = "欢迎您登录中国铁路客户服务中心网站";
	private final String FLAG_CONTENT_MAINTAIN = "系统维护中，维护时间为23:00-07:00，在此期间，如需在互联网购票、改签或退票，请到铁路车站窗口办理，谢谢！";
	
}

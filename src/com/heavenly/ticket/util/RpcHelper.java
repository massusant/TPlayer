package com.heavenly.ticket.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.NameValuePair;

import android.content.Context;
import android.util.Log;

/**
 * Used for convenience of RPC. USAGE: derive the RpcObserver to set callbacks,
 * and call invokRpc.
 * 
 * @author zhipeng.zhangzp
 * 
 */
public class RpcHelper {

	public static final int RETRY_LIMIT = 5;

	/*
	 * used for invoke RPC BEWARE: it should be executed async.
	 */
	public static String invokeRpc(final String rpcUrl,
			final Map<String, String> header,
			final List<NameValuePair> paramList) {
		for (int i = 0; i < RETRY_LIMIT; i++) {
			String result = doInvokeRpc(rpcUrl, header, paramList);
			if (result != null)
				return result;
			try {
				TimeUnit.MILLISECONDS.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String doInvokeRpc(final String rpcUrl,
			final Map<String, String> header,
			final List<NameValuePair> paramList) {
		try {
			InputStreamReader isr;
			isr = new InputStreamReader(doInvokeRpcForStringStream(rpcUrl,
					header, paramList));
			StringBuilder sb = new StringBuilder();
			CharBuffer cb = CharBuffer.allocate(BUFFER_SIZE);
			while (isr.read(cb) > 0) {
				cb.flip();
				sb.append(cb.toString());
				cb.clear();
			}
			printDebug(sb.toString());
			return sb.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static InputStream doInvokeRpcForStringStream(final String rpcUrl,
			final Map<String, String> header,
			final List<NameValuePair> paramList) throws IOException {
		InputStream is = null;
		HttpURLConnection urlConnection = makeURLConnection(rpcUrl, paramList);
		if (header != null && !header.isEmpty()) {
			Set<Entry<String, String>> set = header.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while(it.hasNext()) {
				Entry<String, String> entry = it.next();
				urlConnection.setRequestProperty(entry.getKey(),
						entry.getValue());
			}
		}
		urlConnection.setRequestProperty("Cookie",
				getCookie(urlConnection.getURL(), HTTP_CONTEXT_ID_DEFAULT));
		urlConnection.connect();
		try {
			is = urlConnection.getInputStream();
			if ("gzip".equalsIgnoreCase(urlConnection.getContentEncoding())) {
				is = new GZIPInputStream(is);
			}
		} catch (FileNotFoundException e) {
			is = urlConnection.getErrorStream();
		}
		try {
			sHttpContext.get(HTTP_CONTEXT_ID_DEFAULT).put(
					urlConnection.getURL().toURI(),
					urlConnection.getHeaderFields());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return is;
	}
	
	public static InputStream doInvokeRpcForStream(final String rpcUrl,
			final Map<String, String> header,
			final List<NameValuePair> paramList) throws IOException {
		HttpURLConnection urlConnection = makeURLConnection(rpcUrl, paramList);
		if (header != null && !header.isEmpty()) {
			Set<Entry<String, String>> set = header.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while(it.hasNext()) {
				Entry<String, String> entry = it.next();
				urlConnection.setRequestProperty(entry.getKey(),
						entry.getValue());
			}
		}
		urlConnection.setRequestProperty("Cookie",
				getCookie(urlConnection.getURL(), HTTP_CONTEXT_ID_DEFAULT));
		urlConnection.connect();
		InputStream is = urlConnection.getInputStream();
		try {
			sHttpContext.get(HTTP_CONTEXT_ID_DEFAULT).put(
					urlConnection.getURL().toURI(),
					urlConnection.getHeaderFields());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return is; 
	}
	
	public static HttpURLConnection makeURLConnection(final String rpcUrl,
			final List<NameValuePair> paramList) throws IOException {
		URL url = buildUrl(rpcUrl, paramList);
		HttpURLConnection urlConnection;
		if (url.getProtocol().toLowerCase(Locale.ENGLISH).equalsIgnoreCase("https")) {
			trustAllHosts();
			HttpsURLConnection https = (HttpsURLConnection) url
					.openConnection();
			https.setHostnameVerifier(DO_NOT_VERIFY);
			urlConnection = https;
		} else {
			urlConnection = (HttpURLConnection) url.openConnection();
		}
		urlConnection.setConnectTimeout(CONNECTION_TIME_OUT);
		urlConnection.setReadTimeout(READ_TIME_OUT);
//		urlConnection.setInstanceFollowRedirects(sFollowRedirect);
		return urlConnection;
	}
	
	public static HttpURLConnection makeURLPostConnection(final String rpcUrl,
			final Map<String, String> header,
			final List<NameValuePair> paramList) throws IOException {
		HttpURLConnection urlConnection = makeURLConnection(rpcUrl, paramList);
		urlConnection.setRequestMethod("POST");
		urlConnection.setDoOutput(true);
		if (header != null && !header.isEmpty()) {
			Set<Entry<String, String>> set = header.entrySet();
			Iterator<Entry<String, String>> it = set.iterator();
			while(it.hasNext()) {
				Entry<String, String> entry = it.next();
				urlConnection.setRequestProperty(entry.getKey(),
						entry.getValue());
			}
		}
		return urlConnection;
	}
	
	private static InputStream doInvokeRpcByPostForStream(final String rpcUrl,
			final Map<String, String> header,
			final List<NameValuePair> paramList) throws IOException {
		HttpURLConnection urlConnection = makeURLPostConnection(rpcUrl, header, null);
		urlConnection.setRequestProperty("Cookie",
				getCookie(urlConnection.getURL(), HTTP_CONTEXT_ID_DEFAULT));
//		urlConnection.connect();
		OutputStream os = urlConnection.getOutputStream();
		if (paramList != null && !paramList.isEmpty()) {
			os.write(createParamsString(paramList).getBytes("UTF-8"));
			os.flush();
			os.close();
		}
		
		InputStream is = urlConnection.getInputStream();
		if ("gzip".equalsIgnoreCase(urlConnection.getContentEncoding())) {
			is = new GZIPInputStream(is);
		}
		try {
			sHttpContext.get(HTTP_CONTEXT_ID_DEFAULT).put(
					urlConnection.getURL().toURI(),
					urlConnection.getHeaderFields());
			Log.d(TAG, "" + urlConnection.getResponseMessage());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return is;
	}
	
	public static String doInvokeRpcByPost(final String rpcUrl,
			final Map<String, String> header,
			final List<NameValuePair> paramList) {
		String ret = null;
		try {
			InputStream is = doInvokeRpcByPostForStream(rpcUrl, header, paramList);
			if (is == null) {
				return "";
			}
			StringBuilder sb = new StringBuilder();
			InputStreamReader isr = new InputStreamReader(is);
			CharBuffer cb = CharBuffer.allocate(BUFFER_SIZE);
			while (isr.read(cb) > 0) {
				cb.flip();
				sb.append(cb.toString());
				cb.clear();
			}
			ret = sb.toString();
			printDebug(ret);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
	
	public static String invokeRpcByPost(final String rpcUrl,
			final Map<String, String> header,
			final List<NameValuePair> paramList) {
		for (int i = 0; i < RETRY_LIMIT; i++) {
			String result = doInvokeRpcByPost(rpcUrl, header, paramList);
			if (result != null)
				return result;
			try {
				TimeUnit.MILLISECONDS.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * Trust every server - dont check for any certificate
	 */
	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				printDebug(authType);
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
				printDebug(authType);
			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static URL buildUrl(final String url,
			final List<NameValuePair> paramList) throws MalformedURLException,
			UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		if (paramList != null && !paramList.isEmpty()) {
			sb.append("?");
			sb.append(createParamsString(paramList));
		}
		printDebug(sb.toString());
		return new URL(sb.toString());
	}
	
	private static String createParamsString(List<NameValuePair> paramList)
			throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		for (NameValuePair nvp : paramList) {
			if (nvp.getName() != null && nvp.getValue() != null) {
				sb.append(nvp.getName());
				sb.append("=");
				sb.append(URLEncoder.encode(nvp.getValue(), "UTF-8"));
				sb.append("&");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
//		Log.d(TAG, "param >> " + sb);
		return sb.toString();
	}

	@SuppressWarnings("unused")
	private static void printError(String str) {
		//Log.e(TAG, str);
	}

	private static void printDebug(String str) {
		Log.d(TAG, str);
	}
	
	public static void init(Context context) {
		sHttpContext = new HashMap<String, CookieManager>();
		sHttpContext.put(HTTP_CONTEXT_ID_DEFAULT, new CookieManager(null,
				CookiePolicy.ACCEPT_ALL));
	}
	
	public static String getCookie(URL url, String id) {
		if (sHttpContext == null) {
			sHttpContext = new HashMap<String, CookieManager>();
			sHttpContext.put(HTTP_CONTEXT_ID_DEFAULT, new CookieManager(null,
					CookiePolicy.ACCEPT_ALL));
			return "";
		}
		if (!sHttpContext.containsKey(id)) {
			return "";
		}
		CookieManager cookieMgr = sHttpContext.get(id);
		try {
			CookieStore cookieStore = cookieMgr.getCookieStore();
			if (cookieStore == null) {
				return "";
			}
			List<HttpCookie> list = cookieStore.get(url.toURI());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < list.size(); i++) {
				sb.append(list.get(i)).append("; ");
			}
			Log.d(TAG, "Cookie: " + sb);
			return sb.toString().trim();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void resetHttpContext() {
		sHttpContext.clear();
	}
	
	public void resetHttpContextDefaultOnly() {
		resetHttpContext(HTTP_CONTEXT_ID_DEFAULT);
	}
	
	public void resetHttpContext(String id) {
		sHttpContext.remove(id);
	}
	
	public static void setFollowRedirect(boolean follow) {
		sFollowRedirect = follow;
	}
	
	private static final String HTTP_CONTEXT_ID_DEFAULT = "http.context.default";
	
//	@SuppressWarnings("unused")
	private static boolean sFollowRedirect = true;
	private static final String TAG = "RpcHelper";
	private static final int READ_TIME_OUT = 20000;
	private static final int CONNECTION_TIME_OUT = 15000;
	private static final int BUFFER_SIZE = 1024;
	private static HashMap<String, CookieManager> sHttpContext;
	// always verify the host - dont check for certificate
	final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
		public boolean verify(String hostname, SSLSession session) {
			return true;
		}
	};

}

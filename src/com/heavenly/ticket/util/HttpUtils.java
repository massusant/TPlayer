package com.heavenly.ticket.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;


public class HttpUtils {
//	private static String tempfilepath = "/httptemp";
	private static Map<String, DefaultHttpClient> _threadHttpClient = new HashMap<String, DefaultHttpClient>();
	private static Map<String, HttpContext> _threadHttpContext = new HashMap<String, HttpContext>();

	/**
	 * 打印Cookies信息
	 * 
	 * @param httpContext
	 */
	public static void printCookies(HttpContext httpContext) {
//		System.out.print("pring cooo");
//		 CookieStore cookieStore = (CookieStore)
//		 httpContext.getAttribute(ClientContext.COOKIE_STORE);
//		 List<Cookie> cookies = cookieStore.getCookies();
//		 if (cookies.isEmpty()) {
//		 System.out.println("None");
//		 } else {
//		 for (int i = 0; i < cookies.size(); i++) {
//		 System.out.println("- " + cookies.get(i).toString());
//		 }
//		 }
	}

	/**
	 * 获取请求URL的Client
	 */
	public static HttpClient createHttpClient(String userName) {
		DefaultHttpClient httpclient = _threadHttpClient.get(userName);
		if (httpclient != null) {
			return httpclient;
		}
		httpclient = new DefaultHttpClient();

		_threadHttpClient.put(userName, httpclient);
		try {
			TrustManager easyTrustManager = new X509TrustManager() {
				public void checkClientTrusted(
						java.security.cert.X509Certificate[] x509Certificates,
						String s)

				throws java.security.cert.CertificateException {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] x509Certificates,
						String s)

				throws java.security.cert.CertificateException {
				}

				public java.security.cert.X509Certificate[] getAcceptedIssuers() {
					return new java.security.cert.X509Certificate[0];
				}
			};
			SSLContext sslcontext = SSLContext.getInstance("TLS");
			sslcontext
					.init(null, new TrustManager[] { easyTrustManager }, null);
//			final SocketFactory sf = sslcontext.getSocketFactory();
//			SSLSocketFactory sf = new SSLSocketFactory(sslcontext);
//			Scheme sch = new Scheme("https", 443, sf);
//			Scheme sch = new Scheme("https", sf, 443);
//			httpclient.getConnectionManager().getSchemeRegistry().register(sch);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return httpclient;
	}
	/**
	 * 获取请求URL的
	 */
	private static HttpContext getHttpContext(String urlHost, String cookies,String userName) {
		HttpContext httpContext = _threadHttpContext.get(userName);

		if (httpContext != null) {
			printCookies(httpContext);
			return httpContext;
		}
		httpContext = new BasicHttpContext();
		CookieStore cookieStore = createCookieStore(urlHost, cookies);
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		_threadHttpContext.put(userName, httpContext);
		printCookies(httpContext);
		return httpContext;
	}

	public static void shutdownHttpClient(String userName) {
		_threadHttpContext.remove(userName);
		DefaultHttpClient httpclient = _threadHttpClient.get(userName);
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
		_threadHttpClient.remove(userName);
	}

	public static CookieStore createCookieStore(String urlHost, String cookieStr) {
		// Create a local instance of cookie store
		CookieStore cookieStore = new BasicCookieStore();
		if (cookieStr == null || "".equals(cookieStr))
			return cookieStore;
		String domain = urlHost.substring(urlHost.indexOf("//") + 2);
		if (null != cookieStr && !cookieStr.trim().equals("")) {
			String[] cookies = cookieStr.split(";");
			// userCookieList = new AttributeList();
			for (int i = 0; i < cookies.length; i++) {
				int _i = cookies[i].indexOf("=");
				if (_i != -1) {
					String name = cookies[i].substring(0, _i);
					String value = cookies[i].substring(_i + 1);
					BasicClientCookie _cookie = new BasicClientCookie(name,
							value);
					_cookie.setDomain(domain);
					cookieStore.addCookie(_cookie);
				}
			}
		}
		return cookieStore;
	}

	public static List<NameValuePair> createNameValuePair(String params) {
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		if (null != params && !params.trim().equals("")) {
			String[] _params = params.split("&");
			// userCookieList = new AttributeList();
			for (int i = 0; i < _params.length; i++) {
				int _i = _params[i].indexOf("=");
				if (_i != -1) {
					String name = _params[i].substring(0, _i);
					String value = _params[i].substring(_i + 1);
					nvps.add(new BasicNameValuePair(name, value));
				}
			}
		}
		return nvps;
	}

	@SuppressWarnings("deprecation")
	public static String doGetBody(String url, String cookieStr,String userName) {
		url = url.replaceAll("###(.*)", "");
		try {
			String urlEx = url.substring(0, url.lastIndexOf("/"));
			String urlHost = url;
			try {
				urlHost = url.substring(0, url.indexOf("/", 9));
			} catch (Exception e) {
			}

			HttpClient httpclient = createHttpClient(userName);
			HttpContext localContext = getHttpContext(urlHost, cookieStr,userName);
			String resultBody = null;
			int _count = 0;
			String loadUrl = url;
			HttpGet httpget = null;
			while (_count++ < 5) {
				try {
					localContext
							.removeAttribute("http.protocol.redirect-locations");
					httpget = new HttpGet(loadUrl);
//					httpget.setHeader("Cookie", cookieStr);
					HttpResponse response = httpclient.execute(httpget,
							localContext);
					String locationUrl = checkLocation(response);
					if (locationUrl != null) {
						loadUrl = locationUrl;
						if (!loadUrl.startsWith("/")
								&& loadUrl.indexOf("://") == -1)
							loadUrl = urlEx + loadUrl;
						else if (loadUrl.indexOf("://") == -1) {
							loadUrl = urlHost + loadUrl;
						}
						continue;
					}
					if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
						continue;
					}
					HttpEntity entity = response.getEntity();
					// Consume response content
					if (entity != null) {
						resultBody = EntityUtils.toString(entity);
						entity.consumeContent();
						locationUrl = checkLocation(resultBody);
						if (resultBody == null) {
						} else {
							locationUrl = checkLocation(resultBody);
							if (locationUrl != null) {
								loadUrl = locationUrl;
								if (!loadUrl.startsWith("/"))
									loadUrl = urlEx + loadUrl;
								else if (loadUrl.indexOf("://") == -1) {
									loadUrl = urlHost + loadUrl;
								}
							} else
								break;
						}
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (httpget != null)
						httpget.abort();
				}
			}
			return resultBody;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public static File doGetFile(String url, String fileName,String userName) {
		url = url.replaceAll("###(.*)", "");
		String urlHost = url;
		try {
			urlHost = url.substring(0, url.indexOf("/", 9));
		} catch (Exception e) {
		}

		HttpClient httpclient = createHttpClient(userName);
		HttpContext localContext = getHttpContext(urlHost, "",userName);
		HttpGet httpget = new HttpGet(url);

		HttpResponse response;
		try {
			response = httpclient.execute(httpget, localContext);
			if (response.getStatusLine().getStatusCode() != 200) {
				httpget.abort();
				return null;
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedInputStream bis = new BufferedInputStream(
						entity.getContent());
				File file = new File(fileName);
				FileOutputStream fs = new FileOutputStream(file);

				byte[] buf = new byte[1024];
				int len = bis.read(buf);
				if (len == -1 || len == 0) {
					file.delete();
					file = null;
					entity.consumeContent();
					return file;
				}
				while (len != -1) {
					fs.write(buf, 0, len);
					len = bis.read(buf);
				}
				fs.close();

				entity.consumeContent();
				return file;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void doPost(Void ni) {
		HttpURLConnection conn = null;
	}

	@SuppressWarnings("deprecation")
	public static String doPostForOrder(String params,String userName) {
		HttpClient httpclient = createHttpClient(userName);
		HttpContext localContext = getHttpContext("", "",userName);
		HttpPost httpost = null;
		String resultBody = null;
		httpost = new HttpPost(
				"https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=confirmSingleForQueueOrder");
		httpost.setHeader("Accept", "application/json, text/javascript, */*");
		httpost.setHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.3");
		httpost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpost.setHeader("Host", "dynamic.12306.cn");
		httpost.setHeader("Origin", "https://dynamic.12306.cn");
		httpost.setHeader("Referer",
				"https://dynamic.12306.cn/otsweb/order/confirmPassengerAction.do?method=init");
		httpost.setHeader("X-Requested-With", "XMLHttpRequest");
		List<NameValuePair> nvps = createNameValuePair(params);
		try {
			httpost.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
			httpost.setHeader("Content-Type",
					"application/x-www-form-urlencoded");
			httpost.setHeader(
					"User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.19 (KHTML, like Gecko) Chrome/25.0.1323.1 Safari/537.19");
			HttpResponse response = httpclient.execute(httpost, localContext);
			response.getParams();
			HttpEntity entity = response.getEntity();
			// Consume response content
			if (entity != null) {
				resultBody = EntityUtils.toString(entity);
				entity.consumeContent();
			}
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpost != null)
				httpost.abort();
		}
		return resultBody;
	}

	@SuppressWarnings("deprecation")
	public static String doPostBody(String url, String params,
			String cookieStr, String encode, boolean redirt,String userName) {
		url = url.replaceAll("###(.*)", "");
		String urlEx = url.substring(0, url.lastIndexOf("/"));
		String urlHost = url;
		try {
			urlHost = url.substring(0, url.indexOf("/", 9));
		} catch (Exception e) {
		}

		HttpClient httpclient = createHttpClient(userName);
		HttpContext localContext = getHttpContext(urlHost, cookieStr,userName);
		int _count = 0;
		String loadUrl = null;
		HttpPost httpost = null;
		String resultBody = null;

		while (_count++ < 5) {
			try {
				httpost = new HttpPost(url);
				if (encode == null) {
					StringEntity stringEntity = new StringEntity(params);
					httpost.setEntity(stringEntity);
					httpost.setHeader("Content-Type",
							"application/x-www-form-urlencoded");
				} else {
					List<NameValuePair> nvps = createNameValuePair(params);
					try {
						httpost.setEntity(new UrlEncodedFormEntity(nvps, encode));
						httpost.setHeader("Content-Type",
								"application/x-www-form-urlencoded");
						httpost.setHeader(
								"User-Agent",
								"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.19 (KHTML, like Gecko) Chrome/25.0.1323.1 Safari/537.19");
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			
				HttpResponse response = httpclient.execute(httpost,
						localContext);
				String locationUrl = checkLocation(response);
				if (locationUrl != null) {
					loadUrl = locationUrl;
					if (!loadUrl.startsWith("/")
							&& loadUrl.indexOf("://") == -1)
						loadUrl = urlEx + loadUrl;
					else if (loadUrl.indexOf("://") == -1) {
						loadUrl = urlHost + loadUrl;
					}
					break;
				}
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					continue;
				}
				HttpEntity entity = response.getEntity();
				// Consume response content
				if (entity != null) {
					resultBody = EntityUtils.toString(entity);
					entity.consumeContent();
					locationUrl = checkLocation(resultBody);
					if (resultBody == null) {
					} else {
						locationUrl = checkLocation(resultBody);
						if (locationUrl != null) {
							loadUrl = locationUrl;
							if (!loadUrl.startsWith("/"))
								loadUrl = urlEx + loadUrl;
							else if (loadUrl.indexOf("://") == -1) {
								loadUrl = urlHost + loadUrl;
							}
						} else
							break;
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (httpost != null)
					httpost.abort();
			}
		}
		if (loadUrl != null && redirt)
			resultBody = doGetBody(loadUrl, null,userName);
		return resultBody;
	}

	@SuppressWarnings("deprecation")
	public static String doPostBody(String url, byte[] content,
			Header[] headers, String cookieStr, String encode, boolean redirt ,String userName) {
		if (encode == null)
			encode = HTTP.UTF_8;

		String urlEx = url.substring(0, url.lastIndexOf("/"));
		String urlHost = url;
		try {
			urlHost = url.substring(0, url.indexOf("/", 9));
		} catch (Exception e) {
		}
		HttpClient httpclient = createHttpClient(userName);
		HttpContext localContext = getHttpContext(urlHost, cookieStr,userName);
		int _count = 0;
		String loadUrl = null;
		HttpPost httpost = null;
		String resultBody = null;

		while (_count++ < 5) {
			try {
				httpost = new HttpPost(url);

				ByteArrayEntity byteArrayEntity = new ByteArrayEntity(content);
				httpost.setEntity(byteArrayEntity);

				httpost.setHeaders(headers);

				HttpResponse response = httpclient.execute(httpost,
						localContext);

				String locationUrl = checkLocation(response);
				if (locationUrl != null) {
					loadUrl = locationUrl;
					if (!loadUrl.startsWith("/")
							&& loadUrl.indexOf("://") == -1)
						loadUrl = urlEx + loadUrl;
					else if (loadUrl.indexOf("://") == -1) {
						loadUrl = urlHost + loadUrl;
					}
					break;
				}
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					continue;
				}
				HttpEntity entity = response.getEntity();
				// Consume response content
				if (entity != null) {
					resultBody = EntityUtils.toString(entity);
					entity.consumeContent();
					locationUrl = checkLocation(resultBody);
					if (resultBody == null) {
					} else {
						locationUrl = checkLocation(resultBody);
						if (locationUrl != null) {
							loadUrl = locationUrl;
							if (!loadUrl.startsWith("/"))
								loadUrl = urlEx + loadUrl;
							else if (loadUrl.indexOf("://") == -1) {
								loadUrl = urlHost + loadUrl;
							}
						} else
							break;
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (httpost != null)
					httpost.abort();
			}
		}
		if (loadUrl != null && redirt)
			resultBody = doGetBody(loadUrl, null,userName);
		return resultBody;
	}

	@SuppressWarnings("deprecation")
	public static String doPostBody(String url, Map<String, String> stringBody,
			Map<String, File> fileBody, Header[] headers, String cookieStr,
			String encode, boolean redirt,String userName) {
		if (encode == null)
			encode = HTTP.UTF_8;

		String urlEx = url.substring(0, url.lastIndexOf("/"));
		String urlHost = url;
		try {
			urlHost = url.substring(0, url.indexOf("/", 9));
		} catch (Exception e) {
		}
		HttpClient httpclient = createHttpClient(userName);
		HttpContext localContext = getHttpContext(urlHost, cookieStr,userName);
		int _count = 0;
		String loadUrl = null;
		HttpPost httpost = null;
		String resultBody = null;

		while (_count++ < 5) {
			try {
				httpost = new HttpPost(url);
				httpost.setHeaders(headers);

//				MultipartEntity reqEntity = new MultipartEntity();
//				for (Iterator<Entry<String, File>> it = fileBody.entrySet()
//						.iterator(); it.hasNext();) {
//					Entry<String, File> fileEntry = it.next();
//					FileBody file = new FileBody(fileEntry.getValue());
//					reqEntity.addPart(fileEntry.getKey(), file);
//				}
//
//				for (Iterator<Entry<String, String>> it = stringBody.entrySet()
//						.iterator(); it.hasNext();) {
//					Entry<String, String> stringEntry = it.next();
//					StringBody str = new StringBody(stringEntry.getValue());
//					reqEntity.addPart(stringEntry.getKey(), str);
//				}
				// 设置请求
//				httpost.setEntity(reqEntity);

				HttpResponse response = httpclient.execute(httpost,
						localContext);

				String locationUrl = checkLocation(response);
				if (locationUrl != null) {
					loadUrl = locationUrl;
					if (!loadUrl.startsWith("/")
							&& loadUrl.indexOf("://") == -1)
						loadUrl = urlEx + loadUrl;
					else if (loadUrl.indexOf("://") == -1) {
						loadUrl = urlHost + loadUrl;
					}
					break;
				}
				if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					continue;
				}
				HttpEntity entity = response.getEntity();
				// Consume response content
				if (entity != null) {
					resultBody = EntityUtils.toString(entity);
					entity.consumeContent();
					locationUrl = checkLocation(resultBody);
					if (resultBody == null) {
					} else {
						locationUrl = checkLocation(resultBody);
						if (locationUrl != null) {
							loadUrl = locationUrl;
							if (!loadUrl.startsWith("/"))
								loadUrl = urlEx + loadUrl;
							else if (loadUrl.indexOf("://") == -1) {
								loadUrl = urlHost + loadUrl;
							}
						} else
							break;
					}
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (httpost != null)
					httpost.abort();
			}
		}
		if (loadUrl != null && redirt)
			resultBody = doGetBody(loadUrl, null,userName);
		return resultBody;
	}

	/**
	 * 检查是否包含链接转向，3种方法<br>
	 * <ol>
	 * <li>头部包含“location:”或“content-location:”，返回代号302</li>
	 * <li>内容部分包含“meta http-equiv=refresh content="2;URL=..."”</li>
	 * <li>js脚本刷新，正则为：
	 * "(?s)<script.{0,50}?>\\s*((document)|(window)|(this))\\.location(\\.href)?\\s*="
	 * </li>
	 * </ol>
	 */
	private static String checkLocation(HttpResponse response) {
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			if (header.getName().equalsIgnoreCase("location")
					|| header.getName().equalsIgnoreCase("content-location")) {
				return header.getValue();
			}
		}
		return null;
	}

	/**
	 * 检查是否包含链接转向，3种方法<br>
	 * <ol>
	 * <li>内容部分包含“meta http-equiv=refresh content="2;URL=..."”</li>
	 * <li>js脚本刷新，正则为：
	 * "(?s)<script.{0,50}?>\\s*((document)|(window)|(this))\\.location(\\.href)?\\s*="
	 * </li>
	 * </ol>
	 */
	private static String checkLocation(String httpBody) {
		String locationUrl = null;
		// 2.
		String bodyLocationStr = "";
		if (httpBody.length() > 5120) {
			bodyLocationStr = httpBody.substring(0, 5120);// 太长则截取部分内容
		} else {
			bodyLocationStr = httpBody;
		}
		bodyLocationStr = bodyLocationStr.replaceAll("<!--(?s).*?-->", "")
				.replaceAll("['\"]", "");// 去除注释和引号部分

		int metaLocation = -1;
		metaLocation = bodyLocationStr.toLowerCase().indexOf(
				"http-equiv=refresh");
		if (metaLocation != -1) {
			String locationPart = bodyLocationStr.substring(metaLocation,
					bodyLocationStr.indexOf(">", metaLocation));
			metaLocation = locationPart.toLowerCase().indexOf("url");
			if (metaLocation != -1) {
				// 假定url=...是在 > 之前最后的部分
				locationUrl = locationPart.substring(metaLocation + 4,
						locationPart.length()).replaceAll("\\s+[^>]*", "");
				return locationUrl;
			}
		}
		// 3.
		Matcher locationMath = Pattern
				.compile(
						"(?s)<script.{0,50}?>\\s*((document)|(window)|(this))\\.location(\\.href)?\\s*=")
				.matcher(httpBody.toLowerCase());
		if (locationMath.find()) {
			String[] cs = httpBody.substring(locationMath.end()).trim()
					.split("[> ;<]");
			locationUrl = cs[0];
			cs = null;
			return locationUrl;
		}
		// 没有转向
		return null;
	}

	public static File doGetCheckFile(String url,String savePath) {
		return getImageFile(url/*
										 * getImageSrcFromWebPage(getWebPageAsString
										 * (url))
										 */, savePath);
	}

	/**
	 * 从服务器下载验证码图片
	 * 
	 * @param urlImageName
	 * @param savePath
	 */
	private static File getImageFile(String url, String savePath) {
//		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
//		GetMethod getMethod = null;
//		getMethod = new GetMethod(url);
//		try {
//			// 执行getMethod
//			int statusCode = httpClient.executeMethod(getMethod);
//			if (statusCode != HttpStatus.SC_OK) {
//				System.err.println("Method failed: "
//						+ getMethod.getStatusLine());
//			}
//			// 读取内容
//			InputStream inputStream = getMethod.getResponseBodyAsStream();
//			OutputStream outStream = new FileOutputStream(savePath);
//			IOUtils.copy(inputStream, outStream);
//			outStream.close();
//			return new File(savePath);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			getMethod.releaseConnection();
//		}
		return null;
	}

	/**
	 * 从网页内容中提取出验证码图片 名称
	 * 
	 * @param text
	 * @return
	 */
	static String getImageSrcFromWebPage(String text) {
		String src = "src=\"passCodeAction.do?.*\"";
		Pattern pa = Pattern.compile(src);
		Matcher ma = pa.matcher(text);
		if (ma.find()) {
			System.out.println(ma.group());
			return StringUtils.split(ma.group(), "?")[1].split("\"")[0];
		} else {
			return null;
		}
	}

	public static String getWebPageAsString(String url) throws HttpException,
			IOException {
		String text = null;
//		org.apache.commons.httpclient.HttpClient httpClient = new org.apache.commons.httpclient.HttpClient();
//		PostMethod post = new PostMethod(url);
//		httpClient.executeMethod(post);
//		String text = post.getResponseBodyAsString();
//		post.releaseConnection();
		return text;
	}
}
package com.heavenly.ticket.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {

	public static Bitmap getFromURL(final String rpcUrl,
			final Map<String, String> header,
			final List<NameValuePair> paramList) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeStream(RpcHelper.doInvokeRpcForStream(
					rpcUrl, header, paramList));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bm;
	}
	
	public static Bitmap getFromURL(final String rpcUrl,
			final List<NameValuePair> paramList) {
		return getFromURL(rpcUrl, null, paramList);
	}
	
}
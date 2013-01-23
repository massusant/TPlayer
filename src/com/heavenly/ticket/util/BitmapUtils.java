package com.heavenly.ticket.util;

import java.io.IOException;
import java.util.List;

import org.apache.http.NameValuePair;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitmapUtils {

	public static Bitmap getFromURL(final String rpcUrl,
			final List<NameValuePair> paramList) {
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeStream(RpcHelper.doInvokeRpcForStream(
					rpcUrl, null, paramList));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bm;
	}
}
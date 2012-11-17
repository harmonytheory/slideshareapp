package net.harmonytheory.android.slideshare.common;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SlideShareAsyncHttpClient extends SlideShareHttpClient {
	private static AsyncHttpClient client = new AsyncHttpClient();
	static {
		// デフォルトだとbot扱いされてしまうためデフォルトのUSERAGENTを設定する。
		client.setUserAgent((String) new DefaultHttpClient().getParams().getParameter(CoreProtocolPNames.USER_AGENT));
		// リダイレクトを無効
		client.getHttpClient().getParams().setBooleanParameter(ClientPNames.HANDLE_REDIRECTS, false);
	}
	public static void get(Context context, String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		Log.e("get image", "url=" + getAbsoluteUrl(url) + ", params=" + addApiKey(params));
		client.get(context, getAbsoluteUrl(url), addApiKey(params), responseHandler);
	}
}

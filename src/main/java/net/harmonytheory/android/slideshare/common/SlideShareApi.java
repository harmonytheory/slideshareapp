package net.harmonytheory.android.slideshare.common;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.harmonytheory.android.slideshare.data.MetaJs;
import net.harmonytheory.android.slideshare.data.Oembed;
import net.harmonytheory.android.slideshare.jpp.JppHttpMessageConverter;
import net.harmonytheory.android.slideshare.jpp.JppHttpMessageConverterReadFix;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.util.Log;

public class SlideShareApi extends SlideShareHttpClient {
	// Create a new RestTemplate instance
	private static RestTemplate restTemplate = new RestTemplate(new SimpleClientHttpRequestFactory(){
		@Override
		protected void prepareConnection(HttpURLConnection connection,
				String httpMethod) throws IOException {
			super.prepareConnection(connection, httpMethod);
			// SlideShareがUser-Agentが不正だと404になるため設定する。
			connection.addRequestProperty("User-Agent", (String) new DefaultHttpClient().getParams().getParameter(CoreProtocolPNames.USER_AGENT));
		}
	});
	static {
		restTemplate.getMessageConverters().add(new JppHttpMessageConverter());
		restTemplate.getMessageConverters().add(new JppHttpMessageConverterReadFix());
		restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());
	}

	public static String getRedirectUrl(String url) {
		URI location = new RestTemplate().headForHeaders(url).getLocation();
		if (location == null) {
			return null;
		}
		return location.toString();
	}
	
	public static Oembed getOembed(String slideUrl) {
		final Map<String, String> param = new HashMap<String, String>();
		param.put("url", slideUrl);
		param.put("format", "json");
		Oembed oembed = execute(Oembed.class, ApiUrl.oembed, param);
		MetaJs metaJs = getMetaJs(oembed.getMetaJsUrl());
		oembed.fixData(metaJs.getAvailableSizes());
		return oembed;
	}
	
	public static MetaJs getMetaJs(String metaJsUrl) {
		return execute(MetaJs.class, metaJsUrl, null);
	}
	
	private static <T> T execute(Class<T> clazz, String url, Map<String, String> param) {
		if (param == null) {
			param = Collections.emptyMap();
		}
		Log.e("get image", "url=" + appendQuery(url, param) + ", params=" + param);
		ResponseEntity<T> response = restTemplate.getForEntity(appendQuery(url, param), clazz, param);
		return response.getBody();
	}
}

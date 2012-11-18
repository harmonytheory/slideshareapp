package net.harmonytheory.android.slideshare.common;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.loopj.android.http.RequestParams;


abstract class SlideShareHttpClient {
	private static final String API_KEY = "";
	private static final String SHARED_SECRET = "";
	
	protected static String getAbsoluteUrl(String relativeUrl) {
		if (URI.create(relativeUrl).isAbsolute()) {
			return relativeUrl;
		}
		return ApiUrl.BASE + relativeUrl;
	}
	protected static RequestParams addApiKey(RequestParams params) {
		if (params == null) {
			params = new RequestParams();
		}
		params.put("api_key", API_KEY);
        String ts = Long.toString(System.currentTimeMillis() / 1000);
        params.put("ts", ts);
        params.put("hash", Util.sha1Hex(SHARED_SECRET + ts));
		
		return params;
	}
	protected static Map<String, String> addApiKey(Map<String, String> param) {
		if (param == null) {
			param = new HashMap<String, String>();
		}
		param.put("api_key", API_KEY);
        String ts = Long.toString(System.currentTimeMillis() / 1000);
        param.put("ts", ts);
        param.put("hash", Util.sha1Hex(SHARED_SECRET + ts));
		
		return param;
	}
	
	protected static String appendQuery(String base, Map<String, String> param) {
		if (param == null || param.isEmpty()) {
			return base;
		}
		// Make the HTTP GET request, marshaling the response to a String
		StringBuilder query = new StringBuilder();
		for (String key : param.keySet()) {
			query.append(key).append("={").append(key).append("}&");
		}
		StringBuilder url = new StringBuilder(base);
		if (query.length() > 0) {
			url.append("?").append(query.substring(0, query.length() - 1));
		}
		return url.toString();
	}
}

package net.harmonytheory.android.slideshare.jpp;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自動生成クラスコンテナ。
 * @author SPEEDY
 */
public class JppGenContainer {
	public static class JppBeanGenerater<T> {
		protected Method getMethodByInputStream;
		protected Method getMethodByString;
		public JppBeanGenerater(Class<?> clazz) throws NoSuchMethodException {
			getMethodByInputStream = clazz.getMethod("get", InputStream.class);
			getMethodByString = clazz.getMethod("get", String.class);
		}
		@SuppressWarnings("unchecked")
		public T get(InputStream is) throws Exception {
			return (T) getMethodByInputStream.invoke(null, is);
		}
		@SuppressWarnings("unchecked")
		public T get(String str) throws Exception {
			return (T) getMethodByString.invoke(null, str);
		}
	}
	/** サポートクラス。*/
	private static final Map<Class<?>, JppBeanGenerater> supportClassMap = new HashMap<Class<?>, JppBeanGenerater>();
	/** 未サポートクラス。*/
	private static final List<Class<?>> unsupportClassList = new ArrayList<Class<?>>();
	/** apt自動生成クラス接尾辞。*/
	private static String generatedPostfix;
	
	private JppGenContainer() {
	}
	
	public static void setPostfix(final String postfix) {
		generatedPostfix = postfix;
	}
	
	public static <T> JppBeanGenerater<T> get(Class<T> clazz) {
		if (supports(clazz)) {
			return supportClassMap.get(clazz);
		}
		return null;
	}
	public static <T> boolean supports(Class<T> clazz) {
		// サポートクラスチェック
		if (supportClassMap.containsKey(clazz)) {
			return true;
		}
		// 未サポートクラスチェック
		if (unsupportClassList.contains(clazz)) {
			return false;
		}

		try {
			// 自動生成クラスを生成
			Class<?> forName = Class.forName(clazz.getName().concat(generatedPostfix));
			// 生成出来た場合はサポートクラスとして追加する
			supportClassMap.put(clazz, new JppBeanGenerater<T>(forName));
		} catch (Exception e) {
			// 生成時にエラーが発生した場合は未サポートクラスとする
			unsupportClassList.add(clazz);
			return false;
		}
		return true;
	}
}

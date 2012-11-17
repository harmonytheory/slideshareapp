package net.harmonytheory.android.slideshare.jpp;

import java.io.IOException;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

/**
 * JsonPullParser用HttpMessageConverter
 * @author SPEEDY
 */
public class JppHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
	/**
	 * コンストラクタ。
	 */
	public JppHttpMessageConverter() {
		this("Gen");
	}
	/**
	 * コンストラクタ。
	 * @param genPostfix 自動生成クラス接尾辞
	 */
	public JppHttpMessageConverter(MediaType... mediaTypes) {
		super(mediaTypes);
		JppGenContainer.setPostfix("Gen");
	}
	/**
	 * コンストラクタ。
	 * @param genPostfix 自動生成クラス接尾辞
	 */
	public JppHttpMessageConverter(String genPostfix) {
		this(genPostfix, MediaType.APPLICATION_JSON);
	}
	/**
	 * コンストラクタ。
	 * @param genPostfix 自動生成クラス接尾辞
	 */
	public JppHttpMessageConverter(String genPostfix, MediaType... mediaTypes) {
		super(mediaTypes);
		JppGenContainer.setPostfix(genPostfix);
	}
	@Override
	protected boolean supports(Class<?> clazz) {
		// サポートクラスチェック
		return JppGenContainer.supports(clazz);
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		try {
			return JppGenContainer.get(clazz).get(inputMessage.getBody());
		} catch (Exception e) {
			throw new HttpMessageNotReadableException("Could not read [" + clazz + "]", e);
		}
	}

	@Override
	protected void writeInternal(Object o, HttpOutputMessage outputMessage) 
			throws IOException, HttpMessageNotWritableException {
		throw new UnsupportedOperationException("Unsupported method [writeInternal].");
	}
}

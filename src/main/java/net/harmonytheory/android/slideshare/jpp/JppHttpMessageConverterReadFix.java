package net.harmonytheory.android.slideshare.jpp;

import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.FileCopyUtils;

/**
 * JsonPullParser用HttpMessageConverter
 * @author SPEEDY
 */
public class JppHttpMessageConverterReadFix extends JppHttpMessageConverter {
	/**
	 * コンストラクタ。
	 * @param genPostfix 自動生成クラス接尾辞
	 */
	public JppHttpMessageConverterReadFix() {
		super(MediaType.ALL);
	}

	@Override
	protected Object readInternal(Class<? extends Object> clazz,
			HttpInputMessage inputMessage) throws IOException,
			HttpMessageNotReadableException {
		String body = FileCopyUtils.copyToString(new InputStreamReader(inputMessage.getBody()));
		try {
			// jsが返却されるため、パラメータ内のjson部分だけ取得して返却する。
			return JppGenContainer.get(clazz).get(body.substring(body.indexOf("{"), body.lastIndexOf("}")+1));
		} catch (Exception e) {
			throw new HttpMessageNotReadableException("Could not read [" + clazz + "]", e);
		}
	}
}

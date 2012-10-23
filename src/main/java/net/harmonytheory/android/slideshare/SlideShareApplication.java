package net.harmonytheory.android.slideshare;

import android.app.Application;

public class SlideShareApplication extends Application {
	/** シングルトンインスタンス。*/
	protected static SlideShareApplication instance;
	
	/**
	 * 起動時処理。
	 * 自身をシングルトンインスタンスとして保持する。
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if (instance == null) {
			instance = this;
		}
	}
	
	/**
	 * シングルトンインスタンス返却処理。
	 * @return シングルトンインスタンス
	 */
	public static SlideShareApplication getInstance() {
		return instance;
	}
}

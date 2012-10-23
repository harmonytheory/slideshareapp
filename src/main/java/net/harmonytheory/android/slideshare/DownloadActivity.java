package net.harmonytheory.android.slideshare;

import java.io.File;

import net.harmonytheory.android.slideshare.common.Util;
import net.harmonytheory.android.slideshare.data.Oembed;
import android.support.v4.app.FragmentActivity;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.googlecode.androidannotations.annotations.AfterViews;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NoTitle;
import com.googlecode.androidannotations.annotations.UiThread;
import com.googlecode.androidannotations.annotations.ViewById;

@EActivity(R.layout.download)
@NoTitle
public class DownloadActivity extends FragmentActivity {
	/** スライドID取得キー。*/
	public static final String INTENT_KEY_OEMBED = "INTENT_KEY_OEMBED";
	@ViewById
	ProgressBar pbDownload;
	@ViewById
	TextView tvDownloadStatus;

	@AfterViews
	protected void setting() {
		Oembed oembed = getIntent().getParcelableExtra(INTENT_KEY_OEMBED);
		if (oembed == null) {
			finish();
		}
		tvDownloadStatus.setText(getString(R.string.progress_status, 0, oembed.getTotalSlides()));
		pbDownload.setMax(oembed.getTotalSlides());
		download(oembed);
	}

	/**
	 * 全スライド取得メニュー。
	 * @param slideId2 
	 * @param urlList ダウンロードURLリスト
	 */
	@Background
	protected void download(Oembed oembed) {
		for (int slideNo = 1, l = oembed.getTotalSlides(); slideNo <= l; slideNo++) {
			// セカンダリ値更新
			addDownloadSecondaryProgress();
			
			Util.getSlideImage(getApplicationContext(), oembed, slideNo, new Util.SlideImageHandler() {
				@Override
				public void onSuccess(byte[] bytes) {
					// プログレスバー更新
					addDownloadProgress();
				}
				public void onFailure(Throwable e) {
					// プログレスバー更新
					addDownloadProgress();
				}
				@Override
				public void hasCache(File cacheFile) {
					// プログレスバー更新
					addDownloadProgress();
				}
			});
		}
	}

	@UiThread
	void addDownloadProgress() {
		// プログレスバー更新
		pbDownload.setProgress(pbDownload.getProgress() + 1);
		// ステータス更新
		tvDownloadStatus.setText(getString(R.string.progress_status, pbDownload.getProgress(), pbDownload.getMax()));
		// ダウンロード終了
		if (pbDownload.getMax() == pbDownload.getProgress()) {
			Toast.makeText(this, R.string.finish_download, Toast.LENGTH_LONG).show();
			finish();
		}
	}
	@UiThread
	void addDownloadSecondaryProgress() {
		pbDownload.setSecondaryProgress(pbDownload.getSecondaryProgress() + 1);
	}
}

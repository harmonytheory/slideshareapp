package net.harmonytheory.android.slideshare;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.harmonytheory.android.slideshare.common.SlideShareApi;
import net.harmonytheory.android.slideshare.common.Util;
import net.harmonytheory.android.slideshare.data.Oembed;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@EActivity(R.layout.main)
@OptionsMenu(R.menu.menu_index)
public class SlideShareViewerActivity extends FragmentActivity {
	@ViewById
	ViewPager viewpager;
	@ViewById
	TextView tvPage;
	@ViewById
	LinearLayout lCommand;
	
	/** 取得データ。*/
	Oembed oembed;

	@AfterViews
	protected void setting() {
		Intent intent = getIntent();
		String action = intent.getAction();
		if (Intent.ACTION_VIEW.equals(action)) {
			requestSlide(intent.getData());
		} else {
			menuOpen();
		}
	}
	
	/**
	 * SlideShareの情報を取得する。
	 * @param uri スライドショーのURL
	 */
	@Background
	protected void requestSlide(Uri uri) {
		// 短縮URLのため一旦HEADリクエスト
		if ("slidesha.re".equals(uri.getHost())) {
			uri = Uri.parse(SlideShareApi.getRedirectUrl(uri.toString()));
		}
		oembed = SlideShareApi.getOembed(uri.toString());
//		new OembedSqliteHelper().insert(oembed);
		try {
			// 取得結果書き込み
			Util.writeSlideInfo(getApplicationContext(), oembed);
			// 画面表示
			settingViewer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * リクエスト結果からViewPagerの生成を行う。
	 * @param oembed SlideShare取得結果
	 */
	@UiThread
	protected void settingViewer() {
		// 下のボタン等を表示状態へ
		lCommand.setVisibility(View.VISIBLE);
		// タイトル
		setTitle(oembed.getTitle());
		
		viewpager.setAdapter(new ImageFragmentPagerAdapter());
		viewpager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int pageNo) {
				tvPage.setText(getString(R.string.progress_status, pageNo + 1, viewpager.getAdapter().getCount()));
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		tvPage.setText(getString(R.string.progress_status, 1, viewpager.getAdapter().getCount()));
	}

	@Click(R.id.btnStart)
	protected void clickBtnStart() {
		viewpager.setCurrentItem(0);
	}
	@Click(R.id.btnPrev)
	protected void clickBtnPrev() {
		viewpager.arrowScroll(View.FOCUS_LEFT);
	}
	@Click(R.id.btnNext)
	protected void clickBtnNext() {
		viewpager.arrowScroll(View.FOCUS_RIGHT);
	}
	@Click(R.id.btnEnd)
	protected void clickBtnEnd() {
		viewpager.setCurrentItem(viewpager.getAdapter().getCount() - 1);
	}
	
	/**
	 * 全スライド取得メニュー。
	 */
	@OptionsItem(R.id.menu_download)
	protected void menuDownload() {
		if (oembed == null) {
			return;
		}
		new AlertDialog.Builder(this)
		.setTitle(R.string.title_download_slide)
		.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(SlideShareViewerActivity.this, DownloadActivity_.class);
				intent.putExtra(DownloadActivity.INTENT_KEY_OEMBED, oembed);
				startActivity(intent);
			}
		})
		.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}
	
	/**
	 * キャッシュ削除メニュー。
	 */
	@OptionsItem(R.id.menu_delete_cache)
	protected void menuDeleteCache() {
		final File[] dirs = Util.getCachedDirs(getApplicationContext());

		final List<Integer> slideIdList = new ArrayList<Integer>();
		List<String> titleList = new ArrayList<String>();
		for (File dir : dirs) {
			Oembed cacheOembed = Util.getSlideInfo(dir);
			if (cacheOembed == null) {
				slideIdList.add(Integer.parseInt(dir.getName()));
				titleList.add(dir.getName());
			} else {
				// 現在表示中は削除対象としない
				if (oembed != null && (cacheOembed.getSlideshowId() == oembed.getSlideshowId())) {
					continue;
				}
				slideIdList.add(cacheOembed.getSlideshowId());
				titleList.add(cacheOembed.getTitle());
			}
		}
		final boolean[] checkList = new boolean[slideIdList.size()];
		
		new AlertDialog.Builder(this)
			.setTitle("削除対象")
			.setMultiChoiceItems(titleList.toArray(new String[]{}), checkList, new OnMultiChoiceClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					checkList[which] = isChecked;
				}
			})
			.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					for (int i = 0, l = slideIdList.size(); i < l; i++) {
						if (checkList[i]) {
							Util.deleteCachedDir(getApplicationContext(), slideIdList.get(i));
						}
					}
				}
			})
			.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
	}

	/**
	 * スライド表示メニュー。
	 */
	@OptionsItem(R.id.menu_open)
	protected void menuOpen() {
		final EditText editText = new EditText(this);
		final AlertDialog alertDialog = new AlertDialog.Builder(this)
			.setTitle(R.string.title_open).setView(editText)
			.setPositiveButton(R.string.btn_ok, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String url = editText.getText().toString();
					if (url.startsWith("http://") 
							&& (url.contains("slidesha.re") || url.contains("slideshare.net"))) {
						requestSlide(Uri.parse(url));
					} else {
						Toast.makeText(SlideShareViewerActivity.this, "not slideshare url", Toast.LENGTH_LONG).show();
					}
				}
			})
			.setNegativeButton(R.string.btn_cancel, null)
			.create();

		editText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// フォーカス時にキーボードを出す
					alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
				}
			}
		});
		alertDialog.show();
	}
	

    public class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
		public ImageFragmentPagerAdapter() {
			super(getSupportFragmentManager());
		}
		@Override
		public Fragment getItem(int position) {
	        Bundle args = new Bundle();
	        args.putParcelable(ImageFragment.KEY_BUNDLE_OEMBED, oembed);
	        args.putInt(ImageFragment.KEY_BUNDLE_SLIDENO, position + 1);
	        return Fragment.instantiate(getApplicationContext(), ImageFragment.class.getName(), args);
		}
		@Override
		public int getCount() {
			return oembed.getTotalSlides();
		}
	}
}

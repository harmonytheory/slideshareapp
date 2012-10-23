package net.harmonytheory.android.slideshare.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import net.harmonytheory.android.slideshare.data.Oembed;
import net.harmonytheory.android.slideshare.data.OembedGen;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.BinaryHttpResponseHandler;

public class Util {
	private static final String OEMBED_FILE = "oembed.json";
	public interface SlideImageHandler {
		void hasCache(File cacheFile);
		void onSuccess(byte[] bytes);
		void onFailure(Throwable e);
	}
	
	/**
	 * キャッシュ済みスライドディレクトリ取得
	 * @param context
	 * @return
	 */
	public static File[] getCachedDirs(final Context context) {
		File[] cacheDirs = context.getFilesDir().listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		
		return cacheDirs;
	}

	/**
	 * キャッシュディレクトリ取得。
	 * @param context コンテキスト
	 * @param slideId スライドID
	 * @return キャッシュディレクトリ
	 */
	public static File getCachedDir(final Context context, final int slideId) {
		return new File(context.getFilesDir(), Integer.toString(slideId));
	}
	/**
	 * キャッシュディレクトリ削除。
	 * @param context コンテキスト
	 * @param slideId スライドID
	 */
	public static void deleteCachedDir(final Context context, final int slideId) {
		File cachedDir = getCachedDir(context, slideId);
		for (File file : cachedDir.listFiles()) {
			file.delete();
		}
		cachedDir.delete();
	}
	
	/**
	 * キャッシュスライド画像取得。
	 * @param context コンテキスト
	 * @param oembed スライド情報
	 * @param slideNo スライド番号
	 * @return
	 */
	public static File getSlideImageCacheFile(final Context context, final Oembed oembed, final int slideNo) {
		String filePath = new StringBuilder(context.getFilesDir().getAbsolutePath()).append(File.separator)
							.append(oembed.getSlideshowId()).append(File.separator)
							.append(oembed.getSlideshowId()).append("_").append(slideNo)
							.append(oembed.getExt()).toString();
		Log.e("CACHE FiLE", filePath);
		return new File(filePath);
	}

	/**
	 * スライド画像取得。
	 * @param context コンテキスト
	 * @param oembed スライド情報
	 * @param slideNo スライド番号
	 * @param handler スライド画像取得ハンドラ
	 * @return
	 */
	public static void getSlideImage(final Context context, final Oembed oembed, final int slideNo, final SlideImageHandler handler) {
		// ローカル保持ファイル
		final File cacheFile = getSlideImageCacheFile(context, oembed, slideNo);
		// ローカルにファイルがある場合はそれを使用して、ない場合は取得を行う
		if (cacheFile.exists()) {
			handler.hasCache(cacheFile);
		} else {
			Log.d("SLIDESHARE", "file not exists!");
			// 画像を非同期で取得する。
			SlideShareAsyncHttpClient.get(context, oembed.getSlideImageUrl(slideNo), null, new BinaryHttpResponseHandler() {
				@Override
				public void onSuccess(byte[] bytes) {
					// ディスクキャッシュ書き込み
					saveDisk(cacheFile, bytes);
					handler.onSuccess(bytes);
				}
				@Override
				public void onFailure(Throwable e, byte[] arg1) {
					handler.onFailure(e);
				}
			});
		}
	}

	/**
	 * キャッシュスライド画像情報書き込み。
	 * @param context コンテキスト
	 * @param oembed スライド情報
	 * @throws Exception
	 */
	public static void writeSlideInfo(final Context context, final Oembed oembed) throws Exception {
		File cachedDir = getCachedDir(context, oembed.getSlideshowId());
		cachedDir.mkdirs();
		OembedGen.encode(new FileWriter(new File(cachedDir, OEMBED_FILE)), oembed);
	}
	/**
	 * キャッシュスライド画像情報取得。
	 * @param dir キャッシュディレクトリ
	 * @return スライド画像情報
	 */
	public static Oembed getSlideInfo(File dir) {
		File infoFile = new File(dir, OEMBED_FILE);
		if (!infoFile.exists()) {
			return null;
		}
		try {
			return OembedGen.get(new FileInputStream(infoFile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void checkFileDir(final Context context) {
		Log.e("FILE", "===============================================");
		for (File dir : context.getFilesDir().listFiles()) {
			if (dir.isDirectory()) {
				String dirName = dir.getName();
				for (File file : dir.listFiles()) {
					Log.e("FILE", dirName + "/" + file.getName());
				}
			}
		}
		Log.e("FILE", "===============================================");
	}
	
	public static void saveDisk(File saveFile, byte[] bytes) {
		saveFile.getParentFile().mkdirs();
		// キャッシュとしてディスクに書き込み
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(saveFile, false);
			fos.write(bytes);
			fos.flush();
			Log.d("SLIDESHARE", "file write!");
		} catch (Exception e) {
			Log.d("SLIDESHARE", "file write error!");
			e.printStackTrace();
			saveFile.delete();
			return;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	/**
	 * 文字列を圧縮する。
	 * @param data 圧縮対象文字列
	 * @return 圧縮バイト文字列
	 */
	public static byte[] compress(String data) {
		byte[] bResponse = data.getBytes();
		Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION);
		compresser.setInput(bResponse); // 圧縮するデータを設定
		compresser.finish(); // 圧縮データを確定
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bResponse.length);
		byte[] buf = new byte[1024];
		int count;
		while (!compresser.finished()) {
			count = compresser.deflate(buf);
			baos.write(buf, 0, count);
		}
		return baos.toByteArray();
	}

	/**
	 * 文字列を解凍する。
	 * @param data 圧縮バイト文字列
	 * @return 圧縮対象文字列
	 * @throws DataFormatException dataがzipでない場合
	 */
	public static String decompress(byte[] data) throws DataFormatException {
		// 解凍
		Inflater zipDecompresser = new Inflater();
		zipDecompresser.setInput(data); // 解凍する対象を設定
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			int size;
			byte[] buf = new byte[1024];
			while (true) {
				size = zipDecompresser.inflate(buf); // 解凍
				baos.write(buf, 0, size);
				if (zipDecompresser.finished())
					break;
			}
			return new String(baos.toByteArray());
		} finally {
			try {
				baos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}
	}

	public static String sha1Hex(String str) {
		String sha1hex = null;
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-1");
			byte[] input = sha.digest(str.getBytes("UTF-8"));
			sha1hex = convertToHex(input);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sha1hex;
	}

	private static String convertToHex(byte[] data) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}
}

package net.harmonytheory.android.slideshare.db;

import net.harmonytheory.android.slideshare.SlideShareApplication;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class SlideShareDatabaseHelper extends SQLiteOpenHelper {
	private static final int DB_VERSION = 1;
	private static final String DB_NAME = "slideshare.db";

	public SlideShareDatabaseHelper() {
		super(SlideShareApplication.getInstance().getApplicationContext(), DB_NAME, null, DB_VERSION);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}

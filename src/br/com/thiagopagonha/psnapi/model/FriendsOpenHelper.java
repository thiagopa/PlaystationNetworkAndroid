package br.com.thiagopagonha.psnapi.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FriendsOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "dictionary.db";
	private static final int DATABASE_VERSION = 1;

	public static final String TABLE_NAME = "friends";
	private static final String TABLE_CREATE = "CREATE TABLE " + TABLE_NAME
			+ " (" + "PSN_ID TEXT, " + "PLAYING TEXT," + "AVATAR_SMALL TEXT,"
			+ "UPDATED DATETIME);";

	FriendsOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public void onCreate(SQLiteDatabase db) {
		db.execSQL(TABLE_CREATE);
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(FriendsOpenHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public SQLiteDatabase getdb() {
		return this.getWritableDatabase();

	}
}

package com.c.card;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBAdapter {
	private static final String DB_NAME = "card.db";
	private static final String DB_TABLE = "cardInfo";
	private static final int DB_VERSION = 1;

	private static final String KEY_ID = "_id";
	private static final String KEY_LOGO = "logo";
	private static final String KEY_HEAD = "head";
	private static final String KEY_NAME = "name";
	private static final String KEY_TITLE = "title";
	private static final String KEY_ADDRESS = "address";
	private static final String KEY_POSTCODE = "postcode";
	private static final String KEY_PHONE = "phone";
	private static final String KEY_MAILBOX = "mailbox";
	private static final String KEY_AUTOGRAPH = "autograph";
	private static final String KEY_HOMEPAHE = "homepahe";

	private SQLiteDatabase db;
	private final Context context;
	private DBOpenHelper dbOpenHelper;

	public DBAdapter(Context _context) {//获取类
		context = _context;
	}

	public void open() throws SQLiteException {
		dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, DB_VERSION);//信息
		try {//抛出异常
			db = dbOpenHelper.getWritableDatabase();
		} catch (SQLiteException ex) {
			db = dbOpenHelper.getReadableDatabase();
		}
	}

	public void close() {//关闭数据库
		if (db != null) {
			db.close();
			db = null;
		}
	}

	public class DBOpenHelper extends SQLiteOpenHelper {

		public DBOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {//创建数据文件
			// TODO Auto-generated method stub
			String DB_CREATE = "create table " + DB_TABLE + " (" + KEY_ID
					+ " integer primary key autoincrement, " + KEY_NAME
					+ " text UNIQUE NOT NULL, " + KEY_TITLE + " text, "
					+ KEY_ADDRESS + " text, " + KEY_POSTCODE + " text, "
					+ KEY_PHONE + " text, " + KEY_MAILBOX + " text, "
					+ KEY_AUTOGRAPH + " text, " + KEY_HOMEPAHE + " text, "
					+ KEY_LOGO + " text, " + KEY_HEAD + " text);";
			db.execSQL(DB_CREATE);

		}

		@Override //升级了数据库版本执行
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS" + DB_TABLE);
			onCreate(db);
		}
	}

	public long insert(People people) {//增加数据
		ContentValues newValues = new ContentValues();
		newValues.put(KEY_NAME, people.Name);
		newValues.put(KEY_TITLE, people.Title);
		newValues.put(KEY_ADDRESS, people.Address);
		newValues.put(KEY_POSTCODE, people.Postcode);
		newValues.put(KEY_PHONE, people.Phone);
		newValues.put(KEY_MAILBOX, people.Mailbox);
		newValues.put(KEY_AUTOGRAPH, people.Autograph);
		newValues.put(KEY_HOMEPAHE, people.Homepahe);
		newValues.put(KEY_LOGO, people.Logo);
		newValues.put(KEY_HEAD, people.Head);

		return db.insert(DB_TABLE, null, newValues);

	}

	public long update(String peopleName, People people) {//更新数据
		ContentValues updateValues = new ContentValues();
		updateValues.put(KEY_TITLE, people.Title);
		updateValues.put(KEY_ADDRESS, people.Address);
		updateValues.put(KEY_POSTCODE, people.Postcode);
		updateValues.put(KEY_PHONE, people.Phone);
		updateValues.put(KEY_MAILBOX, people.Mailbox);
		updateValues.put(KEY_AUTOGRAPH, people.Autograph);
		updateValues.put(KEY_HOMEPAHE, people.Homepahe);
		updateValues.put(KEY_LOGO, people.Logo);
		updateValues.put(KEY_HEAD, people.Head);
		return db.update(DB_TABLE, updateValues, KEY_NAME + "=?",
				new String[] { peopleName });

	}

//	private People[] ConvertToPeople(Cursor cursor) {
//		int resultCounts = cursor.getCount();
//		if (resultCounts == 0 || !cursor.moveToFirst()) {
//			return null;
//		}
//		People[] peoples = new People[resultCounts];
//		for (int i = 0; i < resultCounts; i++) {
//			peoples[i] = new People();
//			peoples[i].ID = cursor.getInt(0);
//			peoples[i].Name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
//			peoples[i].Title = cursor.getString(cursor
//					.getColumnIndex(KEY_TITLE));
//			peoples[i].Address = cursor.getString(cursor
//					.getColumnIndex(KEY_ADDRESS));
//			peoples[i].Postcode = cursor.getString(cursor
//					.getColumnIndex(KEY_POSTCODE));
//			peoples[i].Phone = cursor.getString(cursor
//					.getColumnIndex(KEY_PHONE));
//			peoples[i].Mailbox = cursor.getString(cursor
//					.getColumnIndex(KEY_MAILBOX));
//			peoples[i].Autograph = cursor.getString(cursor
//					.getColumnIndex(KEY_AUTOGRAPH));
//			peoples[i].Homepahe = cursor.getString(cursor
//					.getColumnIndex(KEY_HOMEPAHE));
//			peoples[i].Logo = cursor.getString(cursor.getColumnIndex(KEY_LOGO));
//			peoples[i].Head = cursor.getString(cursor.getColumnIndex(KEY_HEAD));
//			cursor.moveToNext();
//		}
//		return peoples;
//	}

	public Cursor queryAll() {//查询数据
		dbOpenHelper = new DBOpenHelper(context, DB_NAME, null, 1);
		 Cursor cursor = dbOpenHelper.getWritableDatabase().rawQuery(
		 "select " + KEY_ID + "," + KEY_NAME + "," + KEY_TITLE + ","
		 + KEY_ADDRESS + "," + KEY_POSTCODE + "," + KEY_PHONE
		 + "," + KEY_MAILBOX + "," + KEY_AUTOGRAPH + ","
		 + KEY_HOMEPAHE + "," + KEY_LOGO + "," + KEY_HEAD
		 + " from " + DB_TABLE, null);
		return cursor;
	}

//	public People[] getAllData() {
//		// TODO Auto-generated method stub
//		Cursor results = db.query(DB_TABLE, new String[] { KEY_ID, KEY_NAME,
//				KEY_TITLE, KEY_ADDRESS, KEY_POSTCODE, KEY_PHONE, KEY_MAILBOX,
//				KEY_AUTOGRAPH, KEY_HOMEPAHE, KEY_LOGO, KEY_HEAD }, null, null,
//				null, null, null);
//		return ConvertToPeople(results);
//	}

	public void delectAll() {//删除所有数据
		dbOpenHelper=new DBOpenHelper(context, DB_NAME, null, 1);
		try{
			dbOpenHelper.getWritableDatabase().delete(DB_TABLE, null, null);
		}finally{
			dbOpenHelper.close();
		}


	}

	public long delectOne(String name) {//删除单一数据
		return db.delete(DB_TABLE, KEY_NAME + "=?", new String[] { name });

	}
}

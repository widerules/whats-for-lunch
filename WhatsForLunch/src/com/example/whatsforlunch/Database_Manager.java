package com.example.whatsforlunch;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Database_Manager {
	
	private SQLiteDatabase db; // reference to database manager class
	private final String DB_NAME = "foods";
	private final int DB_VERSION = 1; // version
	
	
	//column names
	private final String TABLE_NAME = "database_table";
	private final String TABLE_ROW_ID = "id";
	private final String TABLE_ROW_ONE = "banana";
	private final String TABLE_ROW_TWO = "spinach";
	
	
	//TODO: write constructor and methods for this class
	
		//the beginnings our SQLiteOpenHelper class
	private class CustomSQLiteOpenHelper extends SQLiteOpenHelper{

		@Override
		public void onCreate (SQLiteDatabase db){
			
			//the SQLite query string that will create our 3 column database table
			String newTableQueryString = "create table " +
					TABLE_NAME +
					" (" +
					TABLE_ROW_ID + " integer primary key autoincrement not null," +
					TABLE_ROW_ONE + " text," +
					TABLE_ROW_TWO + " text" +
					");";
			
			//execute the query string to the database.
			db.execSQL(newTableQueryString);
		}
		public CustomSQLiteOpenHelper (Context context){
			super(context, DB_NAME, null, DB_VERSION);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			// NOTHING TO DO HERE. THIS IS THE ORIGINAL DATABASE VERSION.
			// OTHERWISE, YOU WOULD SPECIFIY HOW TO UPGRADE THE DATABASE
			// FROM OLDER VERSIONS.
		}
		
	}
}

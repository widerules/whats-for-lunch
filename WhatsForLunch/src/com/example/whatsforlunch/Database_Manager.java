package com.example.whatsforlunch;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
	public void addRow(String rowStringOne, String rowStringTwo)
	{
		// this is a key value pair holder used by android's SQLite functions
		ContentValues values = new ContentValues();
	 
		// this is how you add a value to a ContentValues object
		// we are passing in a key string and a value string each time
		values.put(TABLE_ROW_ONE, rowStringOne);
		values.put(TABLE_ROW_TWO, rowStringTwo);
	 
		// ask the database object to insert the new data 
		try
		{
			db.insert(TABLE_NAME, null, values);
		}
		catch(Exception e)
		{
			Log.e("DB ERROR", e.toString()); // prints the error message to the log
			e.printStackTrace(); // prints the stack trace to the log
		}
	}
	
	public void deleteRow(long rowID)
	{
		// ask the database manager to delete the row of given id
		try
		{
		    db.delete(TABLE_NAME, TABLE_ROW_ID + "=" + rowID, null);
	    }
		catch (Exception e)
		{
			Log.e("DB ERROR", e.toString());
			e.printStackTrace();
		}
	}
	
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

package com.example.whatsforlunch;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database_Manager {//test comment
       
        // the activity or application that is creating an object from this class
        Context context;
       
        // a reference to the database used by this application/object
        private SQLiteDatabase db; // reference to database manager class
       
        //these constants are specific to the DB. change to
        // fit WHATS FOR LUNCH
        private final String DB_NAME = "foods";
        private final int DB_VERSION = 1; // version
        public static final String default_pendingIntent = "pending not set";
       
        //column names, change to suit WHATS FOR LUNCH
        private final String TABLE_NAME = "food_table";
        private final String TABLE_ROW_ID = "id";
        private final String TABLE_ROW_ONE = "itemname";
        private final String TABLE_ROW_TWO = "condition";
        private final String TABLE_ROW_THREE = "tripname";
        private final String TABLE_ROW_FOUR = "tripdate";
        private final String TABLE_ROW_FIVE = "expdate";
        private final String TABLE_ROW_SIX = "json";
       
       
       
       
        public Database_Manager(Context context){
                this.context = context;
                
                Log.d("DB Ref Creation", "Creating database from: " + context);
                // create or open the database
                CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
                this.db = helper.getWritableDatabase();
        }
       
        //edit method as needed, adds row to a database table
        //the key is automatically assigned by the database
        //@param rowStringOne the value for the row's first column
        //@param rowStringTwo the value for the row's second column
       
        public void addRow(String name, String condition, String tripname, String tripdate, String expdate, String json)
        {
                // this is a key value pair holder used by android's SQLite functions
                ContentValues values = new ContentValues();
         
                // this is how you add a value to a ContentValues object
                // we are passing in a key string and a value string each time
                values.put(TABLE_ROW_ONE, name);
                values.put(TABLE_ROW_TWO, condition);
                values.put(TABLE_ROW_THREE, tripname);
                values.put(TABLE_ROW_FOUR, tripdate);
                values.put(TABLE_ROW_FIVE, expdate);
                values.put(TABLE_ROW_SIX, json);
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
       
       
        /**********************************************************************
         * DELETING A ROW FROM THE DATABASE TABLE
         *
         * This is an example of how to delete a row from a database table
         * using this class. this method probably does
         * not need to be rewritten.
         *
         * @param rowID the SQLite database identifier for the row to delete.
         */
       
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
                }//TODO update alarms
        }
        
        public void deleteTrip(String tripName)
        {
                // ask the database manager to delete the trip 
                try
                {
                	
                    db.delete(TABLE_NAME, TABLE_ROW_TWO + "=?", new String[] {tripName});
            }
                catch (Exception e)
                {
                        Log.e("DB ERROR", e.toString());
                        e.printStackTrace();
                }//TODO update alarms
        }
       
        /**********************************************************************
         * UPDATING A ROW IN THE DATABASE TABLE
         *
         * This is an example of how to update a row in the database table
         * using this class. edit this method to fit WHATS FOR LUNCH.
         *
         * @param rowID the SQLite database identifier for the row to update.
         * @param rowStringOne the new value for the row's first column
         * @param rowStringTwo the new value for the row's second column
         */
       
        public void updateRow(long rowID, String name, String condition, String tripname, String tripdate, String expdate, String json)
        {
                // this is a key value pair holder used by android's SQLite functions
                ContentValues values = new ContentValues();
                values.put(TABLE_ROW_ONE, name);
                values.put(TABLE_ROW_TWO, condition);
                values.put(TABLE_ROW_THREE, tripname);
                values.put(TABLE_ROW_FOUR, tripdate);
                values.put(TABLE_ROW_FIVE, expdate);
                values.put(TABLE_ROW_SIX, json);
         
                // ask the database object to update the database row of given rowID
                try {db.update(TABLE_NAME, values, TABLE_ROW_ID + "=" + rowID, null);}
                catch (Exception e)
                {
                        Log.e("DB Error", e.toString());
                        e.printStackTrace();
                }//TODO update alarms
        }
        
        /**********************************************************************
         * UPDATING A ROW IN THE DATABASE TABLE
         *
         * This is an example of how to update a row in the database table
         * using this class. edit this method to fit WHATS FOR LUNCH.
         *
         * @param rowID the SQLite database identifier for the row to update.
         * @param rowStringOne the new value for the row's first column
         * @param rowStringTwo the new value for the row's second column
         */
       
        public void updateRow(long rowID, String name, String condition, String tripname, String tripdate, String expdate)
        {
                // this is a key value pair holder used by android's SQLite functions
                ContentValues values = new ContentValues();
                values.put(TABLE_ROW_ONE, name);
                values.put(TABLE_ROW_TWO, condition);
                values.put(TABLE_ROW_THREE, tripname);
                values.put(TABLE_ROW_FOUR, tripdate);
                values.put(TABLE_ROW_FIVE, expdate);
         
                // ask the database object to update the database row of given rowID
                try {db.update(TABLE_NAME, values, TABLE_ROW_ID + "=" + rowID, null);}
                catch (Exception e)
                {
                        Log.e("DB Error", e.toString());
                        e.printStackTrace();
                }//TODO update alarms
        }
       
        /**********************************************************************
         * RETRIEVING ALL ROWS FROM THE DATABASE TABLE
         *
         * This is an example of how to retrieve all data from a database
         * table using this class. edit this method to suit WHATS FOR LUNCH
         *
         * the key is automatically assigned by the database
         */
        public ArrayList<ArrayList<Object>> getAllRowsAsArrays()
        {
                // create an ArrayList that will hold all of the data collected from
                // the database.
                //CREATE ARRAYLIST of FOOD OBJECTS?
                ArrayList<ArrayList<Object>> dataArrays =
                        new ArrayList<ArrayList<Object>>();
         
                // this is a database call that creates a "cursor" object.
                // the cursor object store the information collected from the
                // database and is used to iterate through the data.
                Cursor cursor;
         
                try
                {
                        // ask the database object to create the cursor.
                        cursor = db.query(
                                        TABLE_NAME,
                                        new String[]{TABLE_ROW_ID, TABLE_ROW_ONE, TABLE_ROW_TWO, TABLE_ROW_THREE,
                                        		TABLE_ROW_FOUR, TABLE_ROW_FIVE, TABLE_ROW_SIX},
                                        null, null, null, null, null
                        );
                      
                        // move the cursor's pointer to position zero.
                        cursor.moveToFirst();
         
                        // if there is data after the current cursor position, add it
                        // to the ArrayList.
                        if (!cursor.isAfterLast())
                        {
                                do
                                {
                                        ArrayList<Object> dataList = new ArrayList<Object>();
         
                                        dataList.add(cursor.getLong(0));
                                        dataList.add(cursor.getString(1));
                                        dataList.add(cursor.getString(2));
                                        dataList.add(cursor.getString(3));
                                        dataList.add(cursor.getString(4));
                                        dataList.add(cursor.getString(5));
                                        dataList.add(cursor.getString(6));
                                        dataArrays.add(dataList);
                                }
                                // move the cursor's pointer up one position.
                                while (cursor.moveToNext());
                        }
                }
                catch (SQLException e)
                {
                        Log.e("DB Error", e.toString());
                        e.printStackTrace();
                }
         
                // return the ArrayList that holds the data collected from
                // the database.
                return dataArrays;
        }
       
        /**********************************************************************
         * RETRIEVING A ROW FROM THE DATABASE TABLE
         *
         * This is an example of how to retrieve a row from a database table
         * using this class. edit this method to suit WHATS FOR LUNCH.
         *
         * @param rowID the id of the row to retrieve
         * @return an array containing the data from the row
         */
        public ArrayList<Object> getRowAsArray_Food_Trip(String name, String trip)
        {
                // create an array list to store data from the database row.
               
               
                //CREATE ARRAYLIST 
                ArrayList<Object> rowArray = new ArrayList<Object>();
                Cursor cursor;
         
                try
                {
                        // this is a database call that creates a "cursor" object.
                        // the cursor object store the information collected from the
                        // database and is used to iterate through the data.
                        cursor = db.query
                        (
                                        TABLE_NAME,
                                        new String[]{TABLE_ROW_ID, TABLE_ROW_ONE, TABLE_ROW_TWO, TABLE_ROW_THREE,
                                        		TABLE_ROW_FOUR, TABLE_ROW_FIVE, TABLE_ROW_SIX},     
                                        TABLE_ROW_ONE + "=?" + " AND " + TABLE_ROW_THREE + "=?",
                                        new String[] {name, trip}, null, null, null, null
                        );
         
                        // move the pointer to position zero in the cursor.
                        cursor.moveToFirst();
         
                        // if there is data available after the cursor's pointer, add
                        // it to the ArrayList that will be returned by the method.
                        if (!cursor.isAfterLast())
                        {
                                do
                                {
                                        rowArray.add(cursor.getLong(0));
                                        rowArray.add(cursor.getString(1));
                                        rowArray.add(cursor.getString(2));
                                        rowArray.add(cursor.getString(3));
                                        rowArray.add(cursor.getString(4));
                                        rowArray.add(cursor.getString(5));
                                        rowArray.add(cursor.getString(6));
                                }
                                while (cursor.moveToNext());
                        }
         
                        // let java know done with with the cursor.
                        cursor.close();
                }
                catch (SQLException e)
                {
                        Log.e("DB ERROR", e.toString());
                        e.printStackTrace();
                }
         
                // return the ArrayList containing the given row from the database.
                return rowArray;
        }
        
        public ArrayList<ArrayList<Object>> getRowAsArray_Trip(String trip)
        {
                // create an array list to store data from the database row.
                
               
                //CREATE ARRAYLIST
        	ArrayList<ArrayList<Object>> tripArrays =
                    new ArrayList<ArrayList<Object>>();
                Cursor cursor;
         
                try
                {
                        // this is a database call that creates a "cursor" object.
                        // the cursor object store the information collected from the
                        // database and is used to iterate through the data.
                        cursor = db.query
                        (
                                        TABLE_NAME,
                                        new String[]{TABLE_ROW_ID, TABLE_ROW_ONE, TABLE_ROW_TWO, TABLE_ROW_THREE,
                                        		TABLE_ROW_FOUR, TABLE_ROW_FIVE, TABLE_ROW_SIX},     
                                         TABLE_ROW_THREE + "=?",
                                        new String[] {trip}, null, null, null, null
                        );
         
                        // move the pointer to position zero in the cursor.
                        cursor.moveToFirst();
         
                        // if there is data available after the cursor's pointer, add
                        // it to the ArrayList that will be returned by the method.
                        if (!cursor.isAfterLast())
                        {
                                do
                                {
                                	 ArrayList<Object> rowArray = new ArrayList<Object>();
                                	
                                        rowArray.add(cursor.getLong(0));
                                        rowArray.add(cursor.getString(1));
                                        rowArray.add(cursor.getString(2));
                                        rowArray.add(cursor.getString(3));
                                        rowArray.add(cursor.getString(4));
                                        rowArray.add(cursor.getString(5));
                                        rowArray.add(cursor.getString(6));
                                        tripArrays.add(rowArray);
                                }
                                while (cursor.moveToNext());
                        }
         
                        // let java know that you are through with the cursor.
                        cursor.close();
                }
                catch (SQLException e)
                {
                        Log.e("DB ERROR", e.toString());
                        e.printStackTrace();
                }
         
                // return the ArrayList containing the given row from the database.
                return tripArrays;
        }

        
        
        public ArrayList<Object> getRowAsArray_ID(long id)
        {
                // create an array list to store data from the database row.
               
               
                //CREATE ARRAYLIST 
                ArrayList<Object> rowArray = new ArrayList<Object>();
                Cursor cursor;
         
                try
                {
                        // this is a database call that creates a "cursor" object.
                        // the cursor object store the information collected from the
                        // database and is used to iterate through the data.
                        cursor = db.query
                        (
                                        TABLE_NAME,
                                        new String[]{TABLE_ROW_ID, TABLE_ROW_ONE, TABLE_ROW_TWO, TABLE_ROW_THREE,
                                        		TABLE_ROW_FOUR, TABLE_ROW_FIVE, TABLE_ROW_SIX},     
                                        TABLE_ROW_ID + "=" + id,
                                        null, null, null, null, null
                        );
         
                        // move the pointer to position zero in the cursor.
                        cursor.moveToFirst();
         
                        // if there is data available after the cursor's pointer, add
                        // it to the ArrayList that will be returned by the method.
                        if (!cursor.isAfterLast())
                        {
                                do
                                {
                                        rowArray.add(cursor.getLong(0));
                                        rowArray.add(cursor.getString(1));
                                        rowArray.add(cursor.getString(2));
                                        rowArray.add(cursor.getString(3));
                                        rowArray.add(cursor.getString(4));
                                        rowArray.add(cursor.getString(5));
                                        rowArray.add(cursor.getString(6));
                                }
                                while (cursor.moveToNext());
                        }
         
                        // let java know done with with the cursor.
                        cursor.close();
                }
                catch (SQLException e)
                {
                        Log.e("DB ERROR", e.toString());
                        e.printStackTrace();
                }
         
                // return the ArrayList containing the given row from the database.
                return rowArray;
        }

        
        
        
        
        /**********************************************************************
         * RETRIEVING A ROW FROM THE DATABASE TABLE
         *
         * This is an example of how to retrieve a row from a database table
         * using this class. edit this method to suit WHATS FOR LUNCH.
         *
         * @param rowID the id of the row to retrieve
         * @return an array containing the data from the row
         */
        public ArrayList<Object> getRowAsArray(long rowID)
        {
                // create an array list to store data from the database row.
                // I would recommend creating a JavaBean compliant object
                // to store this data instead.  That way you can ensure
                // data types are correct.
               
                //CREATE ARRAYLIST OF FOOD OBJECTS?
                ArrayList<Object> rowArray = new ArrayList<Object>();
                Cursor cursor;
         
                try
                {
                        // this is a database call that creates a "cursor" object.
                        // the cursor object store the information collected from the
                        // database and is used to iterate through the data.
                        cursor = db.query
                        (
                                        TABLE_NAME,
                                        new String[]{TABLE_ROW_ID, TABLE_ROW_ONE, TABLE_ROW_TWO, TABLE_ROW_THREE,
                                        		TABLE_ROW_FOUR, TABLE_ROW_FIVE, TABLE_ROW_SIX},     
                                        TABLE_ROW_ID + "=" + rowID,
                                        null, null, null, null, null
                        );
         
                        // move the pointer to position zero in the cursor.
                        cursor.moveToFirst();
         
                        // if there is data available after the cursor's pointer, add
                        // it to the ArrayList that will be returned by the method.
                        if (!cursor.isAfterLast())
                        {
                                do
                                {
                                        rowArray.add(cursor.getLong(0));
                                        rowArray.add(cursor.getString(1));
                                        rowArray.add(cursor.getString(2));
                                        rowArray.add(cursor.getString(3));
                                        rowArray.add(cursor.getString(4));
                                        rowArray.add(cursor.getString(5));
                                        rowArray.add(cursor.getString(6));
                                }
                                while (cursor.moveToNext());
                        }
         
                        // let java know that you are through with the cursor.
                        cursor.close();
                }
                catch (SQLException e)
                {
                        Log.e("DB ERROR", e.toString());
                        e.printStackTrace();
                }
         
                // return the ArrayList containing the given row from the database.
                return rowArray;
        }
        
        
        public ArrayList<Object> getRowAsArray_Json(String json)
        {
                // create an array list to store data from the database row.
               
               
                //CREATE ARRAYLIST 
                ArrayList<Object> rowArray = new ArrayList<Object>();
                Cursor cursor;
         
                try
                {
                        // this is a database call that creates a "cursor" object.
                        // the cursor object store the information collected from the
                        // database and is used to iterate through the data.
                        cursor = db.query
                        (
                                        TABLE_NAME,
                                        new String[]{TABLE_ROW_ID, TABLE_ROW_ONE, TABLE_ROW_TWO, TABLE_ROW_THREE,
                                        		TABLE_ROW_FOUR, TABLE_ROW_FIVE, TABLE_ROW_SIX},     
                                        TABLE_ROW_SIX + "=?",
                                        new String[] {json}, null, null, null, null
                        );
         
                        // move the pointer to position zero in the cursor.
                        cursor.moveToFirst();
         
                        // if there is data available after the cursor's pointer, add
                        // it to the ArrayList that will be returned by the method.
                        if (!cursor.isAfterLast())
                        {
                                do
                                {
                                        rowArray.add(cursor.getLong(0));
                                        rowArray.add(cursor.getString(1));
                                        rowArray.add(cursor.getString(2));
                                        rowArray.add(cursor.getString(3));
                                        rowArray.add(cursor.getString(4));
                                        rowArray.add(cursor.getString(5));
                                        rowArray.add(cursor.getString(6));
                                }
                                while (cursor.moveToNext());
                        }
         
                        // let java know done with with the cursor.
                        cursor.close();
                }
                catch (SQLException e)
                {
                        Log.e("DB ERROR", e.toString());
                        e.printStackTrace();
                }
         
                // return the ArrayList containing the given row from the database.
                return rowArray;
        }
       
        /****************************************************************************
         * GET ITEMS AND DATES
         * 
         * Finds and returns all items and expiration dates in the user database.
         * 
         * @return A list of a list of every item with its corresponding expiration date
         */
        public ArrayList<ArrayList<Object>> getItemDates()
        {
                //CREATE ARRAYLIST 
        		ArrayList<ArrayList<Object>> rows = new ArrayList<ArrayList<Object>>();
        		Cursor cursor;
                
                try
                {
                        // ask the database object to create the cursor.
                        cursor = db.query(
                                        TABLE_NAME,
                                        new String[]{TABLE_ROW_ONE,
                                        			 TABLE_ROW_FIVE},
                                        null, null, null, null, null
                        );
                      
                        // move the cursor's pointer to position zero.
                        cursor.moveToFirst();
         
                        // if there is data after the current cursor position, add it
                        // to the ArrayList.
                        if (!cursor.isAfterLast())
                        {
                                do
                                {
                                        ArrayList<Object> dataList = new ArrayList<Object>();

                                        dataList.add(cursor.getString(0));
                                        dataList.add(cursor.getString(1));
                                        rows.add(dataList);
                                }
                                // move the cursor's pointer up one position.
                                while (cursor.moveToNext());
                        }
                }
                catch (SQLException e)
                {
                        Log.e("DB Error", e.toString());
                        e.printStackTrace();
                }
         
                // return the ArrayList that holds the data collected from
                // the database.
                return rows;
        }
       
        /**
         * This class is designed to check if there is a database that currently
         * exists for the given program.  If the database does not exist, it creates
         * one.  After the class ensures that the database exists, this class
         * will open the database for use.  Most of this functionality will be
         * handled by the SQLiteOpenHelper parent class.  The purpose of extending
         * this class is to tell the class how to create (or update) the database.
         */
        private class CustomSQLiteOpenHelper extends SQLiteOpenHelper{

                @Override
                public void onCreate (SQLiteDatabase db){
                       
                        //the SQLite query string that will create our 3 column database table
                        String newTableQueryString = "create table " +
                                        TABLE_NAME +
                                        " (" +
                                        TABLE_ROW_ID + " integer primary key autoincrement not null, " +
                                        TABLE_ROW_ONE + " text, " +
                                        TABLE_ROW_TWO + " text, " +
                                        TABLE_ROW_THREE + " text, " +
                                        TABLE_ROW_FOUR + " text, " +
                                        TABLE_ROW_FIVE + " text, " +
                                        TABLE_ROW_SIX + " text " +
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

    	//return cursor to top
    	public Cursor getCursor(){
    		Cursor cursor = db.query
                    (
                            TABLE_NAME,
                            new String[]{TABLE_ROW_ID, TABLE_ROW_ONE, TABLE_ROW_TWO, TABLE_ROW_THREE, TABLE_ROW_FOUR, TABLE_ROW_FIVE, TABLE_ROW_SIX},     
                            null, null, null, null, null, null
            );
    		
    		cursor.moveToFirst();
    		return cursor;
    	}

    	//returns TABLE_ROW_ONE
    	public String getName(){
    		return TABLE_ROW_ONE;
    	}
    	
    	public String getTripName(){
    		return TABLE_ROW_THREE;
    	}

}
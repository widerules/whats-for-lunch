package com.example.whatsforlunch;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class Description_Database extends SQLiteAssetHelper {

	private SQLiteDatabase db;
	
    private static final String DATABASE_NAME = "food_catalog";
    private static final int DATABASE_VERSION = 1;

  //column names, change to suit WHATS FOR LUNCH
    private final String TABLE_NAME = "description_table";
    private final String TABLE_ROW_ID = "_id";
    private final String TABLE_ROW_ONE = "food_name";
    private final String TABLE_ROW_TWO = "description";
    private final String TABLE_ROW_THREE = "pantry";
    private final String TABLE_ROW_FOUR = "fridge";
    private final String TABLE_ROW_FIVE = "freezer";
    
    public Description_Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); 
        
        db = getReadableDatabase();
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
    public ArrayList<Object> getRowAsArray_FoodName(String name)
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
                    cursor = 
                    		db.query
                    (
                                    TABLE_NAME,
                                    new String[]{TABLE_ROW_ID, 
                                    			 TABLE_ROW_ONE, 
                                    			 TABLE_ROW_TWO, 
                                    			 TABLE_ROW_THREE,
                                    			 TABLE_ROW_FOUR,
                                    			 TABLE_ROW_FIVE,},     
                                    TABLE_ROW_ONE+ " =?", new String[] {name},
                                    null, null, null, null
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
    
 }
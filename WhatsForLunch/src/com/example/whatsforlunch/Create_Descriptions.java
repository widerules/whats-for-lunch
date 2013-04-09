package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.List;



import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class Create_Descriptions {
	
	private Description_Database db;
	private SQLiteDatabase database;
	
	
	public void Create_Descriptions(Context context){
	db = new Description_Database(context);
	
	}
	
	 

}

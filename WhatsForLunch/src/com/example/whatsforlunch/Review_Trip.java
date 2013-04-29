package com.example.whatsforlunch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;

import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Review_Trip extends Activity {
	
	//TODO Do NOT let user press back button
	ArrayList<ArrayList<Object>> food = new ArrayList<ArrayList<Object>>();
	ArrayList<Integer> tripRows = new ArrayList<Integer>();
	private final Integer ROWID = 0;
	private final Integer ITEMNAME = 1;
	private final Integer ITEMCONDITION = 2;
	private final Integer TRIPNAME = 3;
	private final Integer TRIPDATE = 4;
	private final Integer EXPDATE = 5;
	private final Integer PendingI = 6;
	private final String  DEFAULTTRIPNAME = "default trip name";
	
	private Database_Manager db;
	private Description_Database ddb;
	private Alert_Database ad;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.review_trip);
		
		db  = new Database_Manager(this);
		ddb = new Description_Database(this);
		ad = new Alert_Database(this);
		//Get all items in the fridge
		food = db.getRowAsArray_Trip(DEFAULTTRIPNAME);
		
		//Get items from current trip
		for(ArrayList<Object> o : food){
				//Display current trip
				addTextToTextView(R.id.ReviewTripItems, R.id.ReviewTripScroller, 
						o.get(ITEMNAME).toString());
				//Record all row ID's of current trip
				tripRows.add( ((Long) o.get(ROWID)).intValue());
		}
		ArrayList<Object> f = 
				ddb.getRowAsArray_FoodName("Avocados");
		if(!f.isEmpty()){
			addTextToTextView(R.id.ReviewTripItems, R.id.ReviewTripScroller, f.get(ITEMNAME).toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_review__trip, menu);
		return true;
	}
	
	public void launchMainSaveTrip(View view){
		Intent intent = new Intent(this, MainActivity.class);
		
		TextView tripNameField = (TextView) findViewById(R.id.ReviewTripNameField);
		String tripName = tripNameField.getText().toString();


		
		
		//If user did not define a name for the trip, auto assign a name
		if(tripName.length() < 1){
			for(ArrayList<Object> o : food){
				db.updateRow((Long)o.get(ROWID), 
						(String) o.get(ITEMNAME), 
						(String) o.get(ITEMCONDITION), 
						generateTripName(), 					//generate trip name
						(String) o.get(TRIPDATE), 
						(String) o.get(EXPDATE),
						(String) o.get(PendingI));
			}
		}else{
			for(ArrayList<Object> o : food){
				db.updateRow((Long)o.get(ROWID), 
						(String) o.get(ITEMNAME), 
						(String) o.get(ITEMCONDITION), 
						tripName,
						(String) o.get(TRIPDATE), 
						(String) o.get(EXPDATE),
						(String) o.get(PendingI));
				}
		}
	}

		


	
	
	private ArrayList<ArrayList<Object>> getFridge(){
		return db.getAllRowsAsArrays();
	}
	
	private void addTextToTextView(int textViewId, int scrollViewId, String itemName)
	{
	    final TextView txtView = (TextView) findViewById(textViewId);
        final ScrollView scrollView = (ScrollView) findViewById(scrollViewId);
	    //append the new text to the bottom of the TextView
        //move new text to new line if necessary
        if(txtView.getText().toString().length() > 0){
        	txtView.append("\n");
        }
	    txtView.append(itemName);

	    //scroll to the bottom of the text
	    scrollView.post(new Runnable()
	    {
	        @Override
			public void run()
	        {
	        	scrollView.fullScroll(View.FOCUS_DOWN);
	        }
	    });
	}
	
	private String getToday(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy");
		
		return df.format(c.getTime());
	}
	
	private String generateTripName(){
		Calendar c = Calendar.getInstance();	
		SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
		
		return df.format(c.getTime());
	}

}

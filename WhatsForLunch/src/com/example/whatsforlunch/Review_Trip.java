package com.example.whatsforlunch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

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
	private final String  DEFAULTTRIPNAME = "default trip name";
	
	private Database_Manager db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.review_trip);
		
		db = new Database_Manager(this);
		
		//Get all items in the fridge
		food = getFridge();
		
		//Get items from current trip
		for(ArrayList<Object> o : food){
			//Current trip is not named yet, match to default name
			if(o.get(TRIPDATE) != null && o.get(TRIPNAME).equals(DEFAULTTRIPNAME)){
				//Display food on page if entered in current trip
				addTextToTextView(R.id.ReviewTripItems, R.id.ReviewTripScroller, 
						o.get(ITEMNAME).toString());
				//Record all row ID's of current trip
				tripRows.add( ((Long) o.get(ROWID)).intValue());
			}
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
			for(Integer i : tripRows){
				db.updateRow(i, 
						(String) food.get(i-1).get(ITEMNAME), 
						(String) food.get(i-1).get(ITEMCONDITION), 
						generateTripName(), 					//generate trip name
						(String) food.get(i-1).get(TRIPDATE), 
						(String) food.get(i-1).get(EXPDATE));
			}
		}else{
			for(Integer i : tripRows){
				db.updateRow(i, 
						(String) food.get(i-1).get(ITEMNAME), 
						(String) food.get(i-1).get(ITEMCONDITION), 
						tripName,
						(String) food.get(i-1).get(TRIPDATE), 
						(String) food.get(i-1).get(EXPDATE));
			}
		}
		
		startActivity(intent);
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

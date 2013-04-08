package com.example.whatsforlunch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class Review_Trip extends Activity {
	
	private Database_Manager db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.review_trip);
		
		db = new Database_Manager(this);
		
		ArrayList<ArrayList<Object>> food = getShoppingTrip();
		
		for(ArrayList<Object> o : food){
			if(o.get(4) != null && o.get(4).equals(getToday())){
				addTextToTextView(R.id.ReviewTripItems, R.id.ReviewTripScroller, 
						o.get(1).toString());
			}
		}
		
		try{
			
		}catch(Exception e){
			Log.e("EMPTY TRIP ERROR", e.toString()); // prints the error message to the log
			e.printStackTrace(); // prints the stack trace to the log
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_review__trip, menu);
		return true;
	}
	
	private ArrayList<ArrayList<Object>> getShoppingTrip(){
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

}

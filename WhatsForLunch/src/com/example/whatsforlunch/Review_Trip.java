package com.example.whatsforlunch;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class Review_Trip extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		TextView shopList = (TextView) findViewById(R.id.ReviewTripFoodList);
		try{
			shopList.setText(MainActivity.History.getLastTrip().getItemNamesString());
		}catch(Exception e){
			Log.e("EMPTY TRIP ERROR", e.toString()); // prints the error message to the log
			e.printStackTrace(); // prints the stack trace to the log
		}
		//Set the text view as the activity layout
		setContentView(R.layout.review_trip);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_review__trip, menu);
		return true;
	}

}

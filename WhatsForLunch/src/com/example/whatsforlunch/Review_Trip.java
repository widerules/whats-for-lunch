package com.example.whatsforlunch;

import java.util.ArrayList;

import com.example.whatsforlunch.FoodItem;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class Review_Trip extends Activity {
	
	private Database_Manager db = new Database_Manager(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		ArrayList<Object> food = getShoppingTrip();
		
		TextView tripItemsArea = (TextView) findViewById(R.id.ReviewTripItems);
		
		tripItemsArea.setText(food.get(0).toString() + "\n other food");
		
		try{
			//TODO Needs database to work properly so shopping trips can persist across activities
			//shopList.setText(trip.getItemNamesString());
		}catch(Exception e){
			Log.e("EMPTY TRIP ERROR", e.toString()); // prints the error message to the log
			e.printStackTrace(); // prints the stack trace to the log
		}
		
		setContentView(R.layout.review_trip);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_review__trip, menu);
		return true;
	}
	
	private ArrayList<Object> getShoppingTrip(){
		return db.getRowAsArray(0);
	}

}

/** What's For Lunch: Main Menu
 *  Grocery management application
 *  Keeps track of perishable groceries
 *  Suggests meals based on current groceries
 *  Educational tips for better food shopping
 */

package com.example.whatsforlunch;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.example.whatsforlunch.FoodItem;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	Description_Database db;
	Alert_Database ad;
	/**
	 * TODO Create database to hold shopping trip history
	 * Database can be set up to hold individual shopping trips and be able to
	 * retrieve, search, manipulate... trips
	 */
	//Create shell for user's history
	public static final FoodItem History = new FoodItem();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//TextView txt = (TextView) findViewById(R.id.enter_food_button1);  
		//Typeface font = Typeface.createFromAsset(getAssets(), "fonts/custFont.ttf");  
		//txt.setTypeface(font);
	
		db = new Description_Database(this);
		ad = new Alert_Database(this);
		
		
		Calendar alert = Calendar.getInstance();
		Calendar current = Calendar.getInstance();
		current.set(Calendar.YEAR, Calendar.MONTH, Calendar.DAY_OF_MONTH);
		ArrayList<ArrayList<Object>> check = new ArrayList<ArrayList<Object>>(ad.getAllRowsAsArrays());
		for(ArrayList<Object> c : check){
			//TODO: Test the date checker
			//checks if any alarms are in the past before the current date
			alert.set(Integer.parseInt(check.get(4).toString()), Integer.parseInt(check.get(2).toString()), Integer.parseInt(check.get(3).toString()));
			if(alert.before(current)){
				ad.deleteRow(Long.parseLong(c.get(0).toString()));
			}
		}
	}


	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	public void addTripToHistory(FoodItem trip){}
	/**
	 * Launches new shopping trip screen
	 * @param view
	 */
	
	public void launchEnterFoods(View view){
		Intent intent = new Intent(this, Enter_Foods.class);
		startActivity(intent);
	}
	/**
	 * Launches edit trip screen
	 * @param view
	 */
	public void launchTripSelect(View view){
		Intent intent = new Intent(this, Trip_Select.class);
		startActivity(intent);
	}
	/**
	 * Launches Whats for Lunch recipes screen
	 * @param view
	 */
	public void launchSeeLunch(View view){
		Intent intent = new Intent(this, WhatsForLunch.class);
		startActivity(intent);
	}
	/**
	 * Launches user settings option screen
	 * @param view
	 */
	public void launchSettings(View view){
		Intent intent = new Intent(this, User_Settings.class);
		startActivity(intent);
	}

}
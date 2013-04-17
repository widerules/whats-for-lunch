/** What's For Lunch: Main Menu
 *  Grocery management application
 *  Keeps track of perishable groceries
 *  Suggests meals based on current groceries
 *  Educational tips for better food shopping
 */

package com.example.whatsforlunch;


import com.example.whatsforlunch.FoodItem;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	
	Description_DatabaseTest db;
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
		db = new Description_DatabaseTest(this);
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
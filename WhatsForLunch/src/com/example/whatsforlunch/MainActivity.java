/** What's For Lunch: Main Menu
 *  Grocery management application
 *  Keeps track of perishable groceries
 *  Suggests meals based on current groceries
 *  Educational tips for better food shopping
 */

package com.example.whatsforlunch;

import com.example.whatsforlunch.R;
import com.example.whatsforlunch.Shopping_Trip_History;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
	//Create shell for user's history
	public static final Shopping_Trip_History History = new Shopping_Trip_History();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	//Launches new shopping trip screen
	//Commented section contains example of way to send an extra message
	//	to the new activity screen (requires code in other files)
	public void launchEnterFoods(View view){
		Intent intent = new Intent(this, Enter_Foods.class);
		//EditText editText = (EditText) findViewById(R.id.edit_message);
		//String message = editText.getText().toString();
		//intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}
	
	//Launches user settings option screen
	//Commented section contains example of way to send an extra message
	//	to the new activity screen (requires code in other files)
	public void launchSettings(View view){
		Intent intent = new Intent(this, User_Settings.class);
		//EditText editText = (EditText) findViewById(R.id.edit_message);
		//String message = editText.getText().toString();
		//intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

}
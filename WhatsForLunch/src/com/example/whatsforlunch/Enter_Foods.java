/**
 * This activity allows the user to enter foods into a new shopping list.
 * The user inputs the item name manually or selects the item from drop down menus.
 * The user is also prompted for the expiration date of the item.
 * Each item is added to a new shopping list which is saved upon pressing the 
 * 		finalize trip button.
 * The current items will be previewed at the bottom of the page.
 */
package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.List;

import com.example.whatsforlunch.Shopping_Trip_History.ShoppingTrip;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.TextView;

public class Enter_Foods extends Activity {
	public static ShoppingTrip currentTrip = new ShoppingTrip();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//clear out trip each time
		currentTrip.clear();
		setContentView(R.layout.enter_foods);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_enter_foods, menu);
		return true;
	}
	
	public void launchReview_Trip(View view){		
		Intent intent = new Intent(this, Review_Trip.class);
		
		MainActivity.History.addTrip(currentTrip);
		startActivity(intent);
	}
	public void addItemToTrip(View view){
		
		MultiAutoCompleteTextView editText = (MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
		String item = editText.getText().toString();
		
		//Current list contents
		TextView preview = (TextView) findViewById(R.id.ShopTripContents);
		
		//TODO: Make sure this only accepts food (toothpaste is not food)
		//Add entered item to current shopping trip
		currentTrip.addItem(item);
		
		//Update current trip item list preview
		preview.setText(currentTrip.getItemNamesString());
		//Scroll to Bottom
		TextView txtView = (TextView) findViewById(R.id.ShopTripContents);
		ScrollView scrollView = (ScrollView) findViewById(R.id.EnterFoodsTripScroller);
		
		scrollView.smoothScrollTo(0, txtView.getBottom());
		
		//Clear item from text field after adding to shopping list
		editText.setText("");
		
	}
	
}

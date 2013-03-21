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

import com.example.whatsforlunch.FoodItem;

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
	//Used to build trip currently being created
	//private static FoodItem currentTrip = new FoodItem();
	private List<FoodItem> currentTrip = new ArrayList<FoodItem>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		
		//MainActivity.History.addTrip(currentTrip);
		startActivity(intent);
	}
	
	public void addItemToTrip(View view){
		
		MultiAutoCompleteTextView editText = 
				(MultiAutoCompleteTextView) findViewById(R.id.multiAutoCompleteTextView1);
		String itemName = editText.getText().toString();
		
		//TODO: Make sure this only accepts food (toothpaste is not food)
		//Add entered item to current shopping trip
		currentTrip.add(new FoodItem(itemName));
		
		//Update current trip item list preview
		addTextToTextView(R.id.ShopTripContents, R.id.EnterFoodsTripScroller, itemName);
		
		//Clear item from text field after adding to shopping list
		editText.setText("");
		
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
	        public void run()
	        {
	        	scrollView.fullScroll(View.FOCUS_DOWN);
	        }
	    });
	}
	
}

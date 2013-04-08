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

import android.os.Build;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.database.DataSetObserver;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.MultiAutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

public class Enter_Foods extends Activity {
	
	Database_Manager db;
	
	private ExpandableListView expListView;
	//Used to build trip currently being created
	private List<FoodItem> currentTrip = new ArrayList<FoodItem>();
	
	//These are for the expandable list
	//groupTitle is main heading group1-3 are children in list
	private ArrayList<String> groupTitle = new ArrayList<String>();

	private ArrayList<String> group1 = new ArrayList<String>();
	private ArrayList<String> group2 = new ArrayList<String>();
	private ArrayList<String> group3 = new ArrayList<String>();
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.enter_foods);
		
		db = new Database_Manager(this);
		
		// Make sure we're running on Honeycomb or higher to use ActionBar APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
		
		//TODO: Make this onClick and not onCreate
		groupTitle.add("Fruit");
		groupTitle.add("Veggies");
		groupTitle.add("Meat");
		 
		group1.add("Apple");
		group1.add("Pear");
		group1.add("Strawberry");
		group1.add("Grape");
		group1.add("Orange");
		group1.add("Pinapple");
		group1.add("Bananna");
		 
		group2.add("Cucumber");
		group2.add("Carrot");
		group2.add("Peas");
		group2.add("Squash");
		group2.add("Spinach");
		 
		group3.add("Steak");
		group3.add("Chicken");
		
		ExpandableListView listView = (ExpandableListView) findViewById(R.id.enter_foods_expandable_list);
		
		//This is interface between list and data, use these to get data
		ExpandableListAdapter foodCategoryExpand = new ExpandableListAdapter() {

//			These are all auto implemented methods
//			From tutorial...			
//			onGroupExpanded() � Will be called when One of the group will expand
//			onGroupCollapsed() � Will be called when One of the group collapse
//			getGroup() � gives you the current group used
//			getGroupCount() � You have to define here how many groups you will have
//			getChildrenCount() � For each Group this method will be called, so you have to define here that how many children current group will have.
//			getChild() � this method will give you the child in the current group. it will be called for each group.	
//			
			@Override
			public boolean areAllItemsEnabled() {
				return false;
			}

			@Override
			//return child in the current group
			public Object getChild(int groupPosition, int childPosition) {
				switch (groupPosition) {
				case 0:
				return group1.get(childPosition);
				case 1:
				return group2.get(childPosition);
				case 2:
				return group3.get(childPosition);
				default:
				return null;
				}
			}

			@Override
			public long getChildId(int arg0, int arg1) {
				return 0;
			}

			
			@Override
			//Similar to getGroupView except need to account for which group child is in
			public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
					ChildHolder holder;
					if (convertView == null) {
					holder = new ChildHolder();
					LayoutInflater inflator = LayoutInflater.from(Enter_Foods.this);
					convertView = inflator.inflate(R.layout.enter_foods_child_helper,null);
					holder.tvChild = (TextView) convertView.findViewById(R.id.enter_foods_exp_lay_child);
					convertView.setTag(holder);
					} else {
					holder = (ChildHolder) convertView.getTag();
					}
					
					//switch based on which group (main heading) child is in
					switch (groupPosition) {
					case 0:
					holder.tvChild.setText(group1.get(childPosition));
					break;
					case 1:
					holder.tvChild.setText(group2.get(childPosition));
					break;
					case 2:
					holder.tvChild.setText(group3.get(childPosition));
					break;
					}
					return convertView;
			}

			@Override
			//get number of children in current? group
			public int getChildrenCount(int groupPosition) {
				switch (groupPosition) {
				case 0:
				return group1.size();
				case 1:
				return group2.size();
				case 2:
				return group3.size();
				default:
				return 0;
				}

			}

			@Override
			public long getCombinedChildId(long arg0, long arg1) {
				return 0;
			}

			@Override
			public long getCombinedGroupId(long arg0) {
				return 0;
			}

			@Override
			public Object getGroup(int arg0) {
				return null;
			}

			@Override
			public int getGroupCount() {
				return groupTitle.size();
			}

			@Override
			public long getGroupId(int arg0) {
				return 0;
			}

			@Override
			//create Group (main heading) view
				public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
					GroupHolder holder;
					 
					if (convertView == null) {
					holder = new GroupHolder();
					LayoutInflater inflator = LayoutInflater.from(Enter_Foods.this);
					//inflate xml file and then get view from that file
					convertView = inflator.inflate(R.layout.enter_foods_group_helper,null);
					holder.tvGroup = (TextView) convertView.findViewById(R.id.enter_foods_exp_lay_group);
					convertView.setTag(holder);
					 
					} else {
					holder = (GroupHolder) convertView.getTag();
					}
					//this actually gets the text we set in onCreate and sets it to the textView (holder)
					holder.tvGroup.setText(groupTitle.get(groupPosition));
					return convertView;
					
				
				
			}

			@Override
			public boolean hasStableIds() {
				return false;
			}

			@Override
			public boolean isChildSelectable(int arg0, int arg1) {
				return false;
			}

			@Override
			public boolean isEmpty() {
				return false;
			}

			@Override
			public void onGroupCollapsed(int groupPosition) {
				
			}

			@Override
			public void onGroupExpanded(int groupPosition) {
				
			}

			@Override
			public void registerDataSetObserver(DataSetObserver observer) {
				
			}

			@Override
			public void unregisterDataSetObserver(DataSetObserver observer) {
				
			}
			
		};
		

		//Set list adapter, makes it actually display
		expListView = (ExpandableListView) findViewById(R.id.enter_foods_expandable_list);
		expListView.setAdapter(foodCategoryExpand);

		
		//This is for doing something upon clicking a child
		expListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				return false;
			}
		});

	}

		
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_enter_foods, menu);
		return true;
	}
	
	public void launchReview_Trip(View view){
		Intent intent = new Intent(this, Review_Trip.class);
		
		enterTripToDatabase();
		
		startActivity(intent);
	}
	
	private void enterTripToDatabase(){
		Log.d("DB Entry", "Entering trip items from Enter Foods");
		
		for(FoodItem i : currentTrip){
			try{
				db.addRow(
						i.getItemName(), 
						i.getCondition(), 
						i.getTripName(), 
						i.getDatePurchased(), 
						i.getExpiration());
			}catch(NullPointerException e){
				e.printStackTrace();
			}
		}
	}
	
	public void toggleFoodCategoriesVisibility(View view){
		ExpandableListView expandList = (ExpandableListView) findViewById(R.id.enter_foods_expandable_list);
		if(expandList.getVisibility() == View.VISIBLE){
			expandList.setVisibility(View.GONE);
		}else{
			expandList.setVisibility(View.VISIBLE);
		}
		
		
	}
	
	public void addItemToTrip(View view){
		
		//Get Item Name
		MultiAutoCompleteTextView name = 
				(MultiAutoCompleteTextView) findViewById(R.id.itemName);
		String itemName = name.getText().toString();
		//Clear item field
		name.setText("");
		
		//Get Expiration Date
		EditText date = (EditText) findViewById(R.id.expirationDate);
		String itemDate = date.getText().toString();
		//Clear date field
		date.setText("");
		
		//Get Condition
		Spinner condition = (Spinner) findViewById(R.id.condition);
		String itemCondition = condition.getSelectedItem().toString();
		
		FoodItem item = new FoodItem();
		//Values will default to empty string if not set
		if(itemName != null){
			item.setItemName(itemName);
		}
		if(itemDate != null){
			item.setExpiration(itemDate);
		}
		if(itemCondition != null){
			item.setCondition(itemCondition);
		}
		//TODO: Make sure this only accepts food (toothpaste is not food)
		//"We do not recognize this item. Are you sure this is a food item."
		//"We do not recognize this item. You may need to enter your own expiration date."
		currentTrip.add(item); 
		
		//Update current trip item list preview
		addTextToTextView(R.id.ShopTripContents, R.id.EnterFoodsTripScroller, itemName);
		
		//second window test
		addTextToTextView(R.id.ShopTripContents2, R.id.EnterFoodsTripScroller, itemCondition);
		
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

	// Child/Group Helper classes for expandable list view
	public class ChildHolder {
		TextView tvChild;
		}
		 
		public class GroupHolder {
		TextView tvGroup;
		}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
}


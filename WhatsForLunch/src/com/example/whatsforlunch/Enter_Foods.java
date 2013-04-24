package com.example.whatsforlunch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import com.example.whatsforlunch.PromptTripNameDialog.NoticeDialogListener;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.Toast;

public class Enter_Foods extends FragmentActivity implements OnTabChangeListener, NoticeDialogListener{

	Database_Manager db;
	Description_Database ddb;

	//Used to build trip currently being created
	private List<FoodItem> currentTrip = new ArrayList<FoodItem>();

	private TabHost mTabHost;
	private HashMap<String, TabInfo> mapTabInfo = new HashMap<String, TabInfo>();
	private TabInfo mLastTab = null;
	
	private class TabInfo {
		private String tag;
		private Class clss;
		private Bundle args;
		private Fragment fragment;
		TabInfo(String tag, Class clazz, Bundle args) {
			this.tag = tag;
			this.clss = clazz;
			this.args = args;
		}
	}

	class TabFactory implements TabContentFactory {
		private final Context mContext;
		/**
		 * @param context
		 */
		 public TabFactory(Context context) {
			mContext = context;
		 }
		 /** (non-Javadoc)
		  * @see android.widget.TabHost.TabContentFactory#createTabContent(java.lang.String)
		  */
		 public View createTabContent(String tag) {
			 View v = new View(mContext);
			 v.setMinimumWidth(0);
			 v.setMinimumHeight(0);
			 return v;
		 }
	}

	/*************************************************
	 * onCreate
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_foods);
		
		//Setup databases
		db = new Database_Manager(this);
		ddb = new Description_Database(this);

		//Setup autocomplete dropdown
		dropdownSetup();

		//Setup datepicker
		datepickerSetup();

		//Setup TabHost
		initialiseTabHost(savedInstanceState);
		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab")); //set the tab as per the saved state
		}
	}
	/**********************************************
	 * Menu functions
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_enter_foods, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menu_cancel:
	        //TODO prompt if they are sure they want to cancel
	    	cancelTrip();
	        return true;
	    case R.id.menu_save:
	    	showTripNameDialog();
	        return true;
	    default:
	        //return false;
	    	return super.onOptionsItemSelected(item);
	    }
	}
	private void showTripNameDialog() {
        FragmentManager fm = getSupportFragmentManager();
        DialogFragment promptTripNameDialog = new PromptTripNameDialog();
        promptTripNameDialog.show(fm, "enter_tripname_prompt");
    }
	// The dialog fragment receives a reference to this Activity through the
    // Fragment.onAttach() callback, which it uses to call the following methods
    // defined by the NoticeDialogListener interface
	@Override
	public void onDialogPositiveClick(String tripname) {
		// Assign the given trip name to each item
		setTripName(tripname);
		saveTrip();
	}
	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// Generate a default trip name for each item
		setTripName(generateTripName());
		saveTrip();
	}
	private void setTripName(String name){
		for(FoodItem i : currentTrip){
			i.setTripName(name);
		}
	}

	/***********************************************************************
	 *  dropdownSetup
	 *  Sets up autocomplete dropdown list of food for text field
	 */
	private void dropdownSetup() {
		ArrayList<String> item_names = new ArrayList<String>();
		ArrayList<ArrayList<Object>> allInfo = ddb.getAllRowsAsArrays();
		//Add all item names to list
		//TODO make function that only retrieves names from database, much more efficient
		for(ArrayList<Object> o : allInfo){
			item_names.add((String) o.get(1));
		}
		final String[] items = item_names.toArray(new String[item_names.size()]);

		AutoCompleteTextView nameField = 
				(AutoCompleteTextView) findViewById(R.id.itemName);
		ArrayAdapter<String> adapter = 
				new ArrayAdapter<String>(this, R.layout.enter_foods_dropdown, 
						R.id.enter_foods_item, items);
		nameField.setAdapter(adapter);
	}

	/********************************************************
	 * Trip functions
	 * 
	 */
	protected FoodItem[] getTripItems(){
		return currentTrip.toArray(new FoodItem[currentTrip.size()]);
	}
	
	public void addItemToTrip(View view){
		//Get Item data
		//Get Item Name
		AutoCompleteTextView name = 
				(AutoCompleteTextView) findViewById(R.id.itemName);
		String itemName = name.getText().toString();
		//Clear item field
		name.setText("");
		//Get Expiration Date
		EditText date = (EditText) findViewById(R.id.dateText);
		String itemDate = date.getText().toString();
		//Clear date field
		date.setText("");
		
		//Build item
		FoodItem item = new FoodItem();
		//Values will default to empty string if not set
		if(itemName != null){
			item.setItemName(itemName);
		}
		if(itemDate != null){
			item.setExpiration(itemDate);
		}
		//TODO: Make sure this only accepts food (toothpaste is not food)
		//"We do not recognize this item. Are you sure this is a food item."
		//"We do not recognize this item. You may need to enter your own expiration date."
		currentTrip.add(item);
		
		//Update the fragment listview if the current trip tab is open
		//Otherwise, catch the casting error and do nothing.
		//The list will be updated on the next tab switch
		try{
		EF_CurTrip_Frag fragList = 
				(EF_CurTrip_Frag) getSupportFragmentManager()
					.findFragmentById(R.id.realtabcontent);
		fragList.updateList();
		}catch(Exception e){
			//current_trip fragment is not open
		}	
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
	public void cancelTrip(){
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
	public void saveTrip(){
		Intent intent = new Intent(this, MainActivity.class);
		enterTripToDatabase();
		startActivity(intent);
	}
	private String generateTripName(){
		Calendar c = Calendar.getInstance();	
		SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
		
		return df.format(c.getTime());
	}

	/** (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os.Bundle)
	 */
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("tab", mTabHost.getCurrentTabTag()); //save the tab selected
		super.onSaveInstanceState(outState);
	}

	/***************************************************************
	 * Setup TabHost Tabs
	 */
	private void initialiseTabHost(Bundle args) {
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabHost.setup();
		TabInfo tabInfo = null;
		Enter_Foods.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab1").setIndicator("Add Items\n by Category"), ( tabInfo = new TabInfo("Tab1", EF_Categories_Frag.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		Enter_Foods.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Tab2").setIndicator("Items in\n Current Trip"), ( tabInfo = new TabInfo("Tab2", EF_CurTrip_Frag.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		// Default to first tab
		this.onTabChanged("Tab1");
		//
		mTabHost.setOnTabChangedListener(this);
	}

	private static void addTab(Enter_Foods activity, TabHost tabHost, TabHost.TabSpec tabSpec, TabInfo tabInfo) {
		// Attach a Tab view factory to the spec
		tabSpec.setContent(activity.new TabFactory(activity));
		String tag = tabSpec.getTag();

		// Check to see if we already have a fragment for this tab, probably
		// from a previously saved state.  If so, deactivate it, because our
		// initial state is that a tab isn't shown.
		tabInfo.fragment = activity.getSupportFragmentManager().findFragmentByTag(tag);
		if (tabInfo.fragment != null && !tabInfo.fragment.isDetached()) {
			FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
			ft.detach(tabInfo.fragment);
			ft.commit();
			activity.getSupportFragmentManager().executePendingTransactions();
		}

		tabHost.addTab(tabSpec);
	}

	/** (non-Javadoc)
	 * @see android.widget.TabHost.OnTabChangeListener#onTabChanged(java.lang.String)
	 */
	public void onTabChanged(String tag) {
		TabInfo newTab = this.mapTabInfo.get(tag);
		if (mLastTab != newTab) {
			FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
			if (mLastTab != null) {
				if (mLastTab.fragment != null) {
					ft.detach(mLastTab.fragment);
				}
			}
			if (newTab != null) {
				if (newTab.fragment == null) {
					newTab.fragment = Fragment.instantiate(this,
							newTab.clss.getName(), newTab.args);
					ft.add(R.id.realtabcontent, newTab.fragment, newTab.tag);
				} else {
					ft.attach(newTab.fragment);
				}
			}

			mLastTab = newTab;
			ft.commit();
			this.getSupportFragmentManager().executePendingTransactions();
		}
	}
	
	/*****************************************************************
	 * Setup Datepicker
	 */
	private void datepickerSetup() {
		//Tell the date field to launch the datepicker fragment on touch
		EditText dateEdit = (EditText) findViewById(R.id.dateText);
		dateEdit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					//anything you want to do if user touches/ taps on the edittext box
					openDatePicker((EditText) findViewById(R.id.dateText));
				}
				return false;
			}
		});
	}

	public void openDatePicker(EditText editText){
		DialogFragment newFragment = new DatePickerFragment(editText);
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	/******************************************************************
	 * DatePickerFragment handles the fragment dialog containing
	 * 	a datepicker. Pressing 'cancel' closes the dialog and does
	 * 	nothing. Pressing 'set' sets the selected date text as the
	 * 	text of the given edit_text field.
	 *
	 */
	public class DatePickerFragment extends DialogFragment implements OnDateSetListener {

		public EditText activity_edittext;

		public DatePickerFragment(EditText edit_text) {
			activity_edittext = edit_text;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			activity_edittext.setText(String.valueOf(month + 1 ) 
					+ "/" 
					+   String.valueOf(day) 
					+ "/" 
					+ String.valueOf(year));
		}
	}
	
}

package com.example.whatsforlunch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import com.example.whatsforlunch.PromptTripNameDialog.NoticeDialogListener;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

public class Enter_Foods extends FragmentActivity implements OnTabChangeListener, NoticeDialogListener{

	final static String UNKNOWN_EXPIRATION = "unknown exp date";
	Database_Manager db;
	Description_Database ddb;
	
	AlarmManager alarmMan;
	public static DateTime expDate;
	//need foodAndDate and alarmsSet so we can add foods to exp dates when we have already added the exp
	//date, it comes down to a problem of adding no matter what but checking (if statement) without add
	public static Map<DateTime,ArrayList<String>> foodAndDate = new HashMap<DateTime,ArrayList<String>>();
	//use to see if an alarm has been set on a certain day
	public static ArrayList<DateTime> alarmsSet = new ArrayList<DateTime>();

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
	protected String[] getCategories(){
		//return currentTrip.toArray(new String[currentTrip.size()]);
		ArrayList<String> cats = ddb.getCategories();
		return cats.toArray(new String[cats.size()]);
	}
	protected String[] getFoodsInCategory(String category){
		ArrayList<String> food = ddb.getFoodsInCategory(category);
		return food.toArray(new String[food.size()]);
	}
	protected void removeItemFromTrip(int pos){
		currentTrip.remove(pos);
	}
	protected void setFoodPicked(String item){
		//TODO If item already picked, add to list automatically
		AutoCompleteTextView nameField = 
				(AutoCompleteTextView) findViewById(R.id.itemName);
		//Remove focus so the dropdown does not pop up and cover the date field
		nameField.clearFocus();
		//If item already picked, add to list automatically
		if(nameField.getText().toString().equals(item)){
			addItemToTrip(null);
		}else{
			//Else, place item name in field
			nameField.setText(item);
		}
	}
	protected void resetCategories(){
		Log.d("Enter_Foods", "Categories reset");
		EF_Categories_Frag frag = 
				(EF_Categories_Frag) getSupportFragmentManager()
					.findFragmentById(R.id.realtabcontent);
		frag.setCategories();
	}
	
	public void addItemToTrip(View view){
		//Get Item data
		//Get Item Name
		AutoCompleteTextView name = 
				(AutoCompleteTextView) findViewById(R.id.itemName);
		String itemName = name.getText().toString().trim();
		if(itemName.length() == 0){
			//No item set, require setting an item name
			Log.d("Entry Error", "No item name set! Sad day.");
			hideKeyboard();
			Toast.makeText(getApplicationContext(), "Please enter an item name", Toast.LENGTH_SHORT).show();
			return;
		}//Else the name is set and the user is clear to continue
		//Get Expiration Date
		EditText date = (EditText) findViewById(R.id.dateText);
		String itemDate = date.getText().toString();
		if(itemDate.length() == 0){
			//No date set, attempt to find a date
			itemDate = ddb.findSoonestExpiration(itemName);
			//Item not found. Prompt for date
			if(itemDate == ddb.NOT_IN_SYSTEM){
				Log.d("Database Error", "No expiration date found for: "+itemName);
				hideKeyboard();
				Toast.makeText(getApplicationContext(), " Item not found. Please\nenter an expiration date.", Toast.LENGTH_SHORT).show();
				return;
			}else{
				//Item found, accept add item request
			}
		}//Else the date is set and the user is clear to continue
		//Clear item and date fields
		date.setText("");
		name.setText("");
		
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
		//this keeps track of if we have set an alarm for this trip or not
		boolean foodAlreadyExp =false;	
	
		Log.d("DB Entry", "Entering trip items from Enter Foods");

		for(FoodItem i : currentTrip){
			try{
				db.addRow(
						i.getItemName(),
						i.getCondition(), 
						i.getTripName(), 
						i.getDatePurchased(), 
						i.getExpiration(),
						null);
				if((String) i.getExpiration()!="")
					foodAlreadyExp= prepareAlarm(foodAlreadyExp, i);
			}catch(NullPointerException e){
				e.printStackTrace();
			}
		}
		
		//save to sharedPref so we can recall later
		String exp = "SavedExpDates";
		SharedPreferences.Editor savedExpDates = getSharedPreferences(exp, MODE_PRIVATE).edit();
		//copy foodAndDates to SharedPref
		for(Map.Entry<DateTime, ArrayList<String>> entry: foodAndDate.entrySet()){
			HashSet<String> datesSet = new HashSet<String>();
			datesSet.addAll(entry.getValue());
			Iterator<String> iter = datesSet.iterator();
			String concat = "";
			while(iter.hasNext()){
				concat += iter.next() + "/";
			}
				savedExpDates.putString(entry.getKey().toString(),concat );	
		}
		savedExpDates.commit();
	}
	
	public void cancelTrip(){
		finish();
	}
	public void saveTrip(){
		enterTripToDatabase();
		finish();
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
		
		Enter_Foods.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Trip").setIndicator("      Items in\nCurrent Trip"), ( tabInfo = new TabInfo("Trip", EF_CurTrip_Frag.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		Enter_Foods.addTab(this, this.mTabHost, this.mTabHost.newTabSpec("Cat").setIndicator("   Add Items\nby Category"), ( tabInfo = new TabInfo("Cat", EF_Categories_Frag.class, args)));
		this.mapTabInfo.put(tabInfo.tag, tabInfo);
		// Default to first tab
		this.onTabChanged("Trip");
		//Set tab text colors
		for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++) 
	    {
	        TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
	        tv.setTextColor(Color.parseColor("#ffffff"));
	    } 
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
		
		hideKeyboard();
		
		if (mLastTab != newTab) {
			Log.d("Enter Foods", "Tab changed");
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
	private void hideKeyboard() {
		//Hide the keyboard so the user can see the items
    	InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE); 
    	inputManager.hideSoftInputFromWindow((null == getCurrentFocus()) ? null : getCurrentFocus().getWindowToken(), 
    			InputMethodManager.HIDE_NOT_ALWAYS);
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
		newFragment.setCancelable(false);
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

	
	public void callAlarms(int daysAfterSet,int daysBetween,boolean recurring){
		//for now I am going to make this as general as possible
		//takes as param number of days after setting it will first go off
		//and false for one alarm, true for recurring alarm	
		//TODO save pendingIntents so we can restore on phone reboot
		alarmMan = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		if(recurring)
			setRepeatingAlarm(daysAfterSet,daysBetween);
		else
			setOneTimeAlarm(daysAfterSet);
		
	}
	
	private boolean prepareAlarm(boolean foodAlreadyExp, FoodItem i) {
		String[] date= i.getExpiration().split("/");
		int month = Integer.parseInt(date[0]);
		int day = Integer.parseInt(date[1]);
		int year = Integer.parseInt(date[2]);

		//just going to set to noon for now, can change it later
		int hour = 12;
		int minute = 0;


			expDate= new DateTime(year,month,day,hour,minute);
		
		DateTime current= new DateTime();
		
		//TODO: this is where the desired user settings is taken into account
		//needs to come from user settings, default of 3 currently
		
		int daysBeforeDesiredNotif=3;
		DateTime currentPlus=current.plusDays(daysBeforeDesiredNotif);
		//add food to day
		ArrayList<String> temp= new ArrayList<String>();
		if(foodAndDate.get(expDate)!=null)
			temp= foodAndDate.get(expDate);
		temp.add(i.getItemName());
		foodAndDate.put(expDate, temp);
		
		//if currentPlus is after or on expDate 
		if(!foodAlreadyExp && (currentPlus.compareTo(expDate)>=0)){
			foodAlreadyExp = true;
			callAlarms(0,0,false);
		}
		//if currentPlus is before expDate
		else if(currentPlus.compareTo(expDate)<0){
			//TODO: if individual times are ever set, this will need to change
			//checks to see if an alarm for the day exists
			//currently it compares exact dates and times, 
			//but hour and minute are same for all so effectively only compares days
			if((!alarmsSet.contains(expDate))){
				DateTime notifDate = expDate.minusDays(daysBeforeDesiredNotif);
				alarmsSet.add(expDate);
				Duration dur = new Duration(current,notifDate);
				callAlarms((int)dur.getStandardDays(),0,false);
			}
		}
		
		
		//ad.addRow(Integer.toString(expDate.getMonthOfYear()), 
		//		Integer.toString(expDate.getDayOfMonth()), 
		//		Integer.toString(expDate.getYear()));

		return foodAlreadyExp;
	}
	
	public void setOneTimeAlarm(int daysAfterSet) {
	    //declare intent using class that will handle alarm
		Intent intent = new Intent(this, FoodExpAlarm.class);
	    //retrieve pending intent for broadcast, flag one shot means will only set once
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
	      intent, PendingIntent.FLAG_ONE_SHOT);
	    //params: specify to use system clock use RTC_WAKEUP to wakeup phone for notification,
	    //time to wait, intent
	    alarmMan.set(AlarmManager.RTC_WAKEUP,
	      System.currentTimeMillis() + (daysAfterSet * AlarmManager.INTERVAL_DAY), pendingIntent);
	 }

	 
	public void setRepeatingAlarm(int daysAfterSet,int daysBetween) {
		//Pretty sure we will end up using this one	 
	    //same as single except FLAG_CANCEL_CURRENT repeats, and days specifies how many days apart
	    Intent intent = new Intent(this, FoodExpAlarm.class);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
	      intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    alarmMan.setRepeating(AlarmManager.RTC_WAKEUP, 
	       System.currentTimeMillis() +(daysAfterSet * AlarmManager.INTERVAL_DAY),
	      daysBetween * AlarmManager.INTERVAL_DAY, pendingIntent);
	    System.out.println("Alarm repeating every 5");
	 }
	
}

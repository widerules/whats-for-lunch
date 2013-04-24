package com.example.whatsforlunch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

public class Review_Trip extends Activity {
	
	//TODO Do NOT let user press back button
	ArrayList<ArrayList<Object>> food = new ArrayList<ArrayList<Object>>();
	ArrayList<Integer> tripRows = new ArrayList<Integer>();
	AlarmManager alarmMan;
	public static DateTime expDate;
	//need foodAndDate and alarmsSet so we can add foods to exp dates when we have already added the exp
	//date, it comes down to a problem of adding no matter what but checking (if statement) without add
	public static Map<DateTime,ArrayList<String>> foodAndDate = new HashMap<DateTime,ArrayList<String>>();
	//use to see if an alarm has been set on a certain day
	public static ArrayList<DateTime> alarmsSet = new ArrayList<DateTime>();
	private final Integer ROWID = 0;
	private final Integer ITEMNAME = 1;
	private final Integer ITEMCONDITION = 2;
	private final Integer TRIPNAME = 3;
	private final Integer TRIPDATE = 4;
	private final Integer EXPDATE = 5;
	private final String  DEFAULTTRIPNAME = "default trip name";
	
	private Database_Manager db;
	private Description_Database ddb;
	private Alert_Database ad;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		
		setContentView(R.layout.review_trip);
		
		db  = new Database_Manager(this);
		ddb = new Description_Database(this);
		ad = new Alert_Database(this);
		//Get all items in the fridge
		food = db.getRowAsArray_Trip(DEFAULTTRIPNAME);
		
		//Get items from current trip
		for(ArrayList<Object> o : food){
				//Display current trip
				addTextToTextView(R.id.ReviewTripItems, R.id.ReviewTripScroller, 
						o.get(ITEMNAME).toString());
				//Record all row ID's of current trip
				tripRows.add( ((Long) o.get(ROWID)).intValue());
		}
		ArrayList<Object> f = 
				ddb.getRowAsArray_FoodName("Avocados");
		if(!f.isEmpty()){
			addTextToTextView(R.id.ReviewTripItems, R.id.ReviewTripScroller, f.get(ITEMNAME).toString());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_review__trip, menu);
		return true;
	}
	
	public void launchMainSaveTrip(View view){
		Intent intent = new Intent(this, MainActivity.class);
		
		TextView tripNameField = (TextView) findViewById(R.id.ReviewTripNameField);
		String tripName = tripNameField.getText().toString();
		//this keeps track of if we have set an alarm for this trip or not
		boolean foodAlreadyExp =false;

		
		
		//If user did not define a name for the trip, auto assign a name
		if(tripName.length() < 1){
			for(ArrayList<Object> o : food){
				db.updateRow((Long)o.get(ROWID), 
						(String) o.get(ITEMNAME), 
						(String) o.get(ITEMCONDITION), 
						generateTripName(), 					//generate trip name
						(String) o.get(TRIPDATE), 
						(String) o.get(EXPDATE));
				if((String) o.get(EXPDATE)!=""){
					foodAlreadyExp= prepareAlarm(foodAlreadyExp, o);
				}
			}
		}else{
			for(ArrayList<Object> o : food){
				db.updateRow((Long)o.get(ROWID), 
						(String) o.get(ITEMNAME), 
						(String) o.get(ITEMCONDITION), 
						tripName,
						(String) o.get(TRIPDATE), 
						(String) o.get(EXPDATE));
				if((String) o.get(EXPDATE)!=""){
					foodAlreadyExp= prepareAlarm(foodAlreadyExp, o);
				}
			}
		}
		//TODO: save foodAndDate in database here

		startActivity(intent);
	}

	private boolean prepareAlarm(boolean foodAlreadyExp, ArrayList<Object> o) {
		String[] date=((String) o.get(EXPDATE)).split("/");
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
		//MAKE SURE NULL DOESN"T GIVE ERROR HERE
		ArrayList<String> temp= new ArrayList<String>();
		if(foodAndDate.get(expDate)!=null)
			temp= foodAndDate.get(expDate);
		temp.add((String) o.get(ITEMNAME));
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
		
		//TODO: make this work
		//save in database			
//		ad.addRow(Integer.toString(expDate.getMonthOfYear()), 
//				Integer.toString(expDate.getDayOfMonth()), 
//				Integer.toString(expDate.getYear()));
		Log.d("alarmExpDays", (String) o.get(EXPDATE).toString());
		return foodAlreadyExp;
	}
	
	
	private ArrayList<ArrayList<Object>> getFridge(){
		return db.getAllRowsAsArrays();
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
	
	private String getToday(){
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy");
		
		return df.format(c.getTime());
	}
	
	private String generateTripName(){
		Calendar c = Calendar.getInstance();	
		SimpleDateFormat df = new SimpleDateFormat("MMM-dd-yyyy HH:mm:ss");
		
		return df.format(c.getTime());
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
	    System.out.println("Alarm in 5");
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

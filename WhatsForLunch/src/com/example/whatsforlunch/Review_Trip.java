package com.example.whatsforlunch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
					
//					for testing only***
					callAlarms(3,0,false);
					callAlarms(0,0,false);
//					need to write function that will compare dates and find difference					
//					callAlarms(food.get(i-1).get(EXPDATE),0,false);
					Log.d("alarmExpDays", (String) o.get(EXPDATE).toString());
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
//					callAlarms(food.get(i-1).get(EXPDATE),0,false);
					Log.d("alarmExpDays", (String) o.get(EXPDATE).toString());
				}
			}
		}
		
		startActivity(intent);
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


	public void callAlarms(int daysBefore,int daysBetween,boolean recurring){
		//for now I am going to make this as general as possible
		//takes days and false for one alarm true for recurring alarm	
		//TODO save pendingIntents so we can restore on phone reboot
		alarmMan = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		if(recurring)
			setRepeatingAlarm(daysBefore,daysBetween);
		else
			setOneTimeAlarm(daysBefore);
		
	}
	
	public void setOneTimeAlarm(int days) {
	    //declare intent using class that will handle alarm
		Intent intent = new Intent(this, FoodExpAlarm.class);
	    //retrieve pending intent for broadcast, flag one shot means will only set once
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
	      intent, PendingIntent.FLAG_ONE_SHOT);
	    //params: specify to use system clock use RTC_WAKEUP to wakeup phone for notification,
	    //time to wait, intent
	    alarmMan.set(AlarmManager.RTC_WAKEUP,
	      System.currentTimeMillis() + (days * AlarmManager.INTERVAL_DAY), pendingIntent);
	    System.out.println("Alarm in 5");
	 }
	
	 
	public void setRepeatingAlarm(int daysBefore,int daysBetween) {
		    //Pretty sure we will end up using this one	 
	    //same as single except FLAG_CANCEL_CURRENT repeats, and days specifies how many days apart
	    Intent intent = new Intent(this, FoodExpAlarm.class);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
	      intent, PendingIntent.FLAG_CANCEL_CURRENT);
	    alarmMan.setRepeating(AlarmManager.RTC_WAKEUP, 
	       System.currentTimeMillis() +(daysBefore * AlarmManager.INTERVAL_DAY),
	      daysBetween * AlarmManager.INTERVAL_DAY, pendingIntent);
	    System.out.println("Alarm repeating every 5");
	 }

}

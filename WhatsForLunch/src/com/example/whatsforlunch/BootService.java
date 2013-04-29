package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class BootService extends Service{
	AlarmManager alarmMan;
	@Override
	public void onCreate(){
		super.onCreate();
	Log.d("boot","onCreate");

	
	}

	
	//This is a method required for service
	@Override
	public IBinder onBind(Intent intent) {
		return null;

	    }
	
	//called after onCreate
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d("Boot", "Calling restoreAlarmState");
    	restoreAlarmState();
    	
 //   	stopSelf();
    	Log.d("Boot","finished resetting alarms");
		return START_REDELIVER_INTENT;
    	
    	
    }
	
	private void restoreAlarmState() {
			String exp = "SavedExpDates";
			int daysBeforeDesiredNotif=3;
			
			Log.d("boot service", "action on boot registered");
			
			
			SharedPreferences preferences = getSharedPreferences(exp, Context.MODE_PRIVATE);
			
			//read from shared pref
			Map<String, ?> items = preferences.getAll();
			for(String s : items.keySet()){
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(preferences.getString(s, "errorEmpty"));
				String str=temp.remove(0);
				temp.add(str.replace("/", ""));
				DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				DateTime dt = formatter.parseDateTime(s);
			    Enter_Foods.foodAndDate.put(dt, temp);
		
			}
			//create alarms for all the values in shared pref
			ArrayList<DateTime> temp = new ArrayList<DateTime>();
			temp.addAll(Enter_Foods.foodAndDate.keySet());
			Enter_Foods.alarmsSet.addAll(Enter_Foods.foodAndDate.keySet());
			while(!temp.isEmpty()){
				Enter_Foods.expDate=temp.remove(0);
				DateTime notifDate = Enter_Foods.expDate.minusDays(daysBeforeDesiredNotif);
				Enter_Foods.alarmsSet.add(Enter_Foods.expDate);
				Duration dur = new Duration(new DateTime(),notifDate);
				callAlarms((int)dur.getStandardDays(),0,false);
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

			public static DateTime fItemDatetoDateTime(FoodItem i) {
				String[] date= i.getExpiration().split("/");
				int month = Integer.parseInt(date[0]);
				int day = Integer.parseInt(date[1]);
				int year = Integer.parseInt(date[2]);

				//just going to set to noon for now, can change it later
				int hour = 12;
				int minute = 0;
				
				return new DateTime(year,month,day,hour,minute);
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
			 }
		
			
			//EXIT APP	
	

}
	





package com.example.whatsforlunch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AlarmSetter extends Service{

	private static final String TAG = "AlarmSetter";
	private static Database_Manager db;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onStart(Intent intent, int startid){
		Log.i(TAG, "service starting");
		
		db = new Database_Manager(this);
		
		setAlarms();
	}
	
	private void setAlarms(){
		Log.i(TAG, "setting alarms");
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		HashMap<Date, ArrayList<String>> alarmMap = new HashMap<Date, ArrayList<String>>();
		ArrayList<String> foodExpiring = new ArrayList<String>();	//Assemble expiring list
		ArrayList<Date> toBeRemoved = new ArrayList<Date>();		//Assemble dates for removal
		alarmMap = createAlarmMap(alarmMap);
		//Find all expiring foods
		for(Date date : alarmMap.keySet()){
			//If expiring soon
			if(daysTill(date) < 3){
				//Get food
				for(String s : alarmMap.get(date)){
					foodExpiring.add(s);
				}
				//schedule delete
				toBeRemoved.add(date);
			}
		}
		//delete old dates
		for(Date d : toBeRemoved){
			alarmMap.remove(d);
		}
		//Create single new date for expiring foods
		alarmMap.put(today, foodExpiring);
		//Set alarms
		for(Date date : alarmMap.keySet()){
			setOneTimeAlarm(daysTill(date) - 3);
		}
	}
	
	private void setOneTimeAlarm(int daysAfterSet) {
		AlarmManager alarmMan = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// get a Calendar object with current time and set
		//	to the appropriate date
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 24*daysAfterSet);
		//declare intent using class that will handle alarm
		Intent intent = new Intent(this, FoodExpAlarm2.class);
		//retrieve pending intent for broadcast, flag one shot means will only set once
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_ONE_SHOT);
		//params: specify to use system clock use RTC_WAKEUP to wakeup phone for notification,
		//time to wait, intent
		alarmMan.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
	}
	
	public static int daysTill(Date date){
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
	    //Get difference
	    if(today.before(date)){
	    	return Days.daysBetween(new DateTime(today), new DateTime(date)).getDays();
	    }else{
	    	return -1;
	    }
	}
	
	/*********************************************************
	 * Creates a mapping of the days food will expire to
	 * which food will expire each day.
	 * 
	 * @param currentMap
	 * @return
	 */
	public static HashMap<Date, ArrayList<String>> createAlarmMap(HashMap<Date, ArrayList<String>> currentMap) {
		HashMap<Date, ArrayList<String>> alarms;
		ArrayList<String> foodExpiring;
		ArrayList<ArrayList<Object>> items = db.getItemDates();
		
		if(currentMap != null){
			alarms = currentMap;
		}else{
			alarms = new HashMap<Date, ArrayList<String>>();
		}
		
		for(ArrayList<Object> i : items){
			foodExpiring = new ArrayList<String>();
			Date date = stringToDate((String) i.get(1));
			//Date already has alarm setup
			if(alarms.containsKey(date)){
				foodExpiring = alarms.get(date);
			}
			foodExpiring.add( (String) i.get(0));
			alarms.put(date, foodExpiring);
		}
		return alarms;
	}
	/***********************************************************
	 * Converts a string of format M/d/yyyy to a Date variable
	 * @param str
	 * @return
	 */
	private static Date stringToDate(String str){
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
	    try {
			cal.setTime(sdf.parse(str));
		} catch (ParseException e) {
			Log.e(TAG, "date parse failed");
			e.printStackTrace();
		}
	    Date date = cal.getTime(); //Parsed date
		return date;
	}
}

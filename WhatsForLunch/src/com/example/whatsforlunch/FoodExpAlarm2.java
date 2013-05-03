package com.example.whatsforlunch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class FoodExpAlarm2 extends BroadcastReceiver{
	
	NotificationManager notificationMan;
	private HashMap<Date, ArrayList<String>> alarmMap;
	
	@Override
	public void onReceive(Context context, Intent arg1) {
		Log.d("Alarm2", "handling alarm");
		Calendar cal = Calendar.getInstance();
		int id = ((Long)cal.getTimeInMillis()).intValue();
		alarmMap = AlarmSetter.createAlarmMap(alarmMap);
		
		notificationMan = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		//I think this gets pending intent from the alarm that called this method
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				new Intent(context,MainActivity.class), Intent.FLAG_ACTIVITY_NEW_TASK);
		
		//This builder uses the support library and is fully backwards compatible
		NotificationCompat.Builder builder =  
	            new NotificationCompat.Builder(context)  
	            .setSmallIcon(R.drawable.ic_launcher)  
	            .setContentTitle("Food Expiring Soon:")  
	            .setContentText(getFoodsExpiring())
	            .setContentIntent(contentIntent);
		
		notificationMan.notify(id, builder.build());
	}
	
	/*************************************************************
	 * Creates a string based on expiring foods. The string will
	 * contain up to 3 expiring item names. If more items are
	 * expiring, the string will append "and more" to the end.
	 * 
	 * @return
	 */
	private String getFoodsExpiring(){
		ArrayList<String> expiring = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		
		//Get all key values that are expired or are soon to expire
		for(Date d : alarmMap.keySet()){
			//If date has expiring foods
			if(isExpiringSoon(d, 3)){
				//Add each food to running list
				for(String s : alarmMap.get(d)){
					expiring.add(s);
				}
			}
		}
		//Add up to 3 items
		int lastIndex = expiring.size();
		for(int i=0; i<expiring.size(); i++){
			//Stop at 3 items
			if(i == 3){
				sb.append("and more");
				break;
			}
			sb.append(expiring.get(i));
			//Only add comma if not last item
			if(i != lastIndex - 1){
				sb.append(", ");
			}
		}
		return sb.toString();
	}
	/*************************************************************
	 * Determines whether the given date is expired or is
	 * expiring soon by checking to see if the expiration
	 * date is before (today + "soon days")
	 * @param d, soon
	 * @return boolean
	 */
	private boolean isExpiringSoon(Date d, int soon){
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR, 24*soon);
		Date threshold = cal.getTime();
		if(d.before(threshold)){
			return true;
		}else{
			return false;
		}
	}

}

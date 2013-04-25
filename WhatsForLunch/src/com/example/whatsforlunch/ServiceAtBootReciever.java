package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class ServiceAtBootReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
        	String exp = "SavedExpDates";
        	int daysBeforeDesiredNotif=3;
        	
//	    	Intent serviceIntent = new Intent(context, Enter_Foods.class);
//	        context.startService(serviceIntent);
//        	Alert_Database alertD = new Alert_Database(context);
//        	alertD.getAllRowsAsArrays();
//			TEST ME!!!
        	
        	
        	SharedPreferences preferences = context.
        			getSharedPreferences(exp, Context.MODE_PRIVATE);
        	
        	
        	Map<String, ?> items = preferences.getAll();
        	for(String s : items.keySet()){
        		ArrayList<String> temp = new ArrayList<String>();
        		temp.add(preferences.getString(s, "errorEmpty"));
        		temp.remove(0).split("/");
        		DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss");
        		DateTime dt = formatter.parseDateTime(s);
        	    Enter_Foods.foodAndDate.put(dt, temp);
    
        	}
        	ArrayList<DateTime> temp = new ArrayList<DateTime>();
        	temp.addAll(Enter_Foods.foodAndDate.keySet());
        	Enter_Foods.alarmsSet.addAll(Enter_Foods.foodAndDate.keySet());
        	while(!temp.isEmpty()){
        		Enter_Foods.expDate=temp.remove(0);
				DateTime notifDate = Enter_Foods.expDate.minusDays(daysBeforeDesiredNotif);
				Enter_Foods.alarmsSet.add(Enter_Foods.expDate);
				Duration dur = new Duration(new DateTime(),notifDate);
				Enter_Foods caller = new Enter_Foods();
				caller.callAlarms((int)dur.getStandardDays(),0,false);
        	}
        	
        }
    }
    
    //EXIT APP
}

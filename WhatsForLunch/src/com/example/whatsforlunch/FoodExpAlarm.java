package com.example.whatsforlunch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


//Class defining the alarm that will alert the user will see when food is close to expiring
//TODO make sure that this holds when app restarts, if not look at link
//http://stackoverflow.com/questions/10970229/alarmmanager-for-android

//class contains deprecated methods, however setLatestEventInfo was added at API 11, we are going
//for API 8 so I am leaving them for now

public class FoodExpAlarm extends BroadcastReceiver {
		 NotificationManager notificationMan;

    @SuppressWarnings("deprecation")
    @Override
	public void onReceive(Context context, Intent intent) {
    	  CharSequence from = "Food Expiring!";
		  CharSequence message = "Woot notify...";
    	
	      Toast.makeText(context, "This is an alarm",
		  Toast.LENGTH_LONG).show();
		  notificationMan = (NotificationManager) context
		    .getSystemService(Context.NOTIFICATION_SERVICE);
		  //i think this gets pending intent from the alarm that called this method
		  PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
		    new Intent(), 0);
		  //creates notification object/icon, and set text to flow across top bar
		  Notification notif = new Notification(R.drawable.ic_launcher,
		    "woot notify", System.currentTimeMillis());
		  //specify what to display when notification is shown
		  notif.setLatestEventInfo(context, from, message, contentIntent);
		  //use nofication manager to send message to phone, will update if same id
		  notificationMan.notify(1, notif);
	}
}


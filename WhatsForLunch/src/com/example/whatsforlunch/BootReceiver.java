package com.example.whatsforlunch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
	
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent alarmIntent = new Intent(context, BootService.class);
        Log.d("boot", "Calling Service");
        context.startService(alarmIntent);
    }
    
}

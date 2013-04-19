package com.example.whatsforlunch;

import java.lang.reflect.Method;
import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.app.TabActivity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TabHost;

//public class User_Settings extends Activity {
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.settings);
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.menu_settings, menu);
//		return true;
//	}
//	
//	//Return to main activity upon confirmation of settings
//	public void launchMain(View view){
//		Intent intent = new Intent(this, MainActivity.class);
//		//EditText editText = (EditText) findViewById(R.id.edit_message);
//		//String message = editText.getText().toString();
//		//intent.putExtra(EXTRA_MESSAGE, message);
//		startActivity(intent);
//	}








// ************************TESTING******
@SuppressWarnings("deprecation")
public class User_Settings extends TabActivity {
	
    TabHost tabHost;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_foods_redone);
        tabHost = (TabHost)findViewById(android.R.id.tabhost);
        TabManager.setMyTabs(tabHost, this);
    }

//		Stack overflow sol'n        
//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion >= 11) {
//          try {
//          Method m = minDateSelector.getClass().getMethod("setCalendarViewShown", boolean.class);
//            m.invoke(minDateSelector, false);
//          }
//          catch (Exception e) {} // eat exception in our case
//        }
        
     //Apparently to get a date picker without calendar mode, must do this (android dev website)
    //stack overflow had an easier solution, however couldn't get it to work	
    public static class DatePickerFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {
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
    
    	//this is a very hacky solution but I can't figure out how to direct it from on click to the method
    	//so I'm pretty sure the problem is that the method isn't in UserSettings, but it can't be because then it can't extend 
        //fragmentActivity which seems like the method has to extend in order to be found...

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
		}
		
    }
	//getSupportFragmentManager is an extension of support library and must be in a class
	//that extends Fragment Activity, I had to make it its own class because the other two
	//were already extending something 
	public static class s extends FragmentActivity {
		public void showDatePickerDialog(View v) {
		    DialogFragment newFragment = new DatePickerFragment();
		    newFragment.show(getSupportFragmentManager(), "datePicker");
		
			}

	}
 
    
}
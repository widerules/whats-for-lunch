package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.Calendar;

import org.joda.time.DateTime;
import org.joda.time.Days;

import com.example.whatsforlunch.Enter_Foods.DatePickerFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;

public class Item_Edit extends FragmentActivity {
	Long id;
	int color;
	Database_Manager db;
	String name, trip, trip_date, exp;
	EditText date;
	EditText name_view;
	Trip_Edit t;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_edit);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		Bundle b = getIntent().getExtras();
		id = b.getLong("id");
		color = b.getInt("color");		
		db = new Database_Manager(this);
		ArrayList<Object> row = db.getRowAsArray(id);
		name = (String) row.get(1);
		trip = (String) row.get(2);
		trip_date = (String) row.get(4);
		exp = (String) row.get(5);
		date = (EditText) findViewById(R.id.item_date);
		date.setText(exp);
		
		name_view = (EditText)this.findViewById(R.id.item_edit_name);
		name_view.setText(name);
		name_view.setTextColor(color);
		TextView trip_view = (TextView)this.findViewById(R.id.item_edit_trip);
		trip_view.setText(trip + " on " + trip_date);
		changeDays();
	    datepickerSetup();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_item_edit, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menu_delete_item:
	    	deleteItem();
	        return true;
	    case R.id.menu_save:
	    	save();
	        return true;
	    default:
	        //return false;
	    	return super.onOptionsItemSelected(item);
	    }
	}
	
	public void changeDays(){
		TextView days = (TextView)this.findViewById(R.id.days);
		DateTime now = new DateTime();
		String[] date_arr = exp.split("/");
	    DateTime eDate = new DateTime(Integer.parseInt(date_arr[2]), Integer.parseInt(date_arr[0]), 
	    		Integer.parseInt(date_arr[1]), 12, 0);
	    int diff = Days.daysBetween(now, eDate).getDays();
	    if(diff < -1){
	    	days.setText("Expired " + -diff + " days ago.");
	    	days.setTextColor(Color.RED);
	    	name_view.setTextColor(Color.RED);
	    }else if(diff == -1){
	    	days.setText("Expired yesterday.");
	    	days.setTextColor(Color.RED);
	    	name_view.setTextColor(Color.RED);
	    }else if(diff == 0){
	    	days.setText("Expires today.");
	    	days.setTextColor(Color.YELLOW);
	    	name_view.setTextColor(Color.YELLOW);
	    }else if(diff == 1){
	    	days.setText("Expires tomorrow.");
	    	days.setTextColor(Color.YELLOW);
	    	name_view.setTextColor(Color.YELLOW);
	    }else{
	    	days.setText("Expires in " + diff + " days.");
	    	if(diff < 3){
	    		days.setTextColor(Color.YELLOW);
	    		name_view.setTextColor(Color.YELLOW);
	    	}
	    	else{
	    		days.setTextColor(Color.BLACK);
	    		name_view.setTextColor(Color.BLACK);
	    	}
	    }
	}
	
	
	public void save(){
		if(!name_view.getText().toString().equals(""))
			name = name_view.getText().toString();
		//Trip_Edit.new_name = name;
		//Trip_Edit.new_date = date.getText().toString();
		Trip_Edit.save = true;
		Trip_Edit.args[0] = name;
		Trip_Edit.args[1] = trip;
		Trip_Edit.args[2] = "Normal";
		Trip_Edit.args[3] = trip_date;
		Trip_Edit.args[4] = date.getText().toString();
		finish();
	}
	
	// TODO: cancel alarms
	public void deleteItem(){
		Trip_Edit.delete = true;
		Trip_Edit.id_delete = id;
		finish();
	}

	/*****************************************************************
	 * Setup Datepicker
	 */
	private void datepickerSetup() {
		//Tell the date field to launch the datepicker fragment on touch
		EditText dateEdit = (EditText) findViewById(R.id.item_date);
		dateEdit.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					//anything you want to do if user touches/ taps on the edittext box
					openDatePicker((EditText) findViewById(R.id.item_date));
				}
				return false;
			}
		});
	}

	public void openDatePicker(EditText editText){
		DialogFragment newFragment = new DatePickerFragment(editText);
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	/******************************************************************
	 * DatePickerFragment handles the fragment dialog containing
	 * 	a datepicker. Pressing 'cancel' closes the dialog and does
	 * 	nothing. Pressing 'set' sets the selected date text as the
	 * 	text of the given edit_text field.
	 *
	 */
	@SuppressLint("ValidFragment")
	public class DatePickerFragment extends DialogFragment implements OnDateSetListener {

		public EditText activity_edittext;

		public DatePickerFragment(EditText edit_text) {
			activity_edittext = edit_text;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			Calendar c = Calendar.getInstance();
			String[] date_arr = exp.split("/");
			c.set(Integer.parseInt(date_arr[2]), Integer.parseInt(date_arr[0]) - 1, Integer.parseInt(date_arr[1]));
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			String d = String.valueOf(month + 1 ) 
					+ "/" 
					+   String.valueOf(day) 
					+ "/" 
					+ String.valueOf(year);
			activity_edittext.setText(d);
			exp = d;
			changeDays();
		}
	}
}

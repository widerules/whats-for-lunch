package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.google.gson.Gson;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class Trip_Edit extends ListActivity {
    //MyAdapter mListAdapter;
	String exp = "SavedExpDates";
    Database_Manager myDb;
    ListView listView;
    Cursor myCur;
    String trip_name;
    ArrayList<String> items = new ArrayList<String>();
    ArrayAdapter<String> list;
    ArrayList<Integer> red = new ArrayList<Integer>();
    ArrayList<Integer> yellow = new ArrayList<Integer>();
    ArrayList<Long> ids = new ArrayList<Long>();
    AlarmManager alarmMan;
    int type;
    public static Long id_delete;
    public static boolean delete = false;
    public static boolean save = false;
    public static String[] args = new String[5];
    private Menu menu;
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_trip_edit, menu);
		this.menu = menu;
		updateTripList();
		return true;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.trip_edit);
        myDb = new Database_Manager(this);
        myCur = myDb.getCursor();
        Bundle b = getIntent().getExtras();
        type = b.getInt("type");
        trip_name = b.getString("trip name");
        
        TextView name_text = (TextView) this.findViewById(R.id.trip_edit_name);
        if(type == 1){
        	name_text.setText("All Foods");
        }else if(type == 2){
        	name_text.setText("Expiring Foods");
        }else{
        	name_text.setText(trip_name);
        }
        
        /*
        mListAdapter = new MyAdapter(Trip_Edit.this, myCur, trip_name);
        setListAdapter(mListAdapter);
        */
        list = new ColoredAdapter(this, items);
        setListAdapter(list);
        
        listView = getListView();
        listView.setOnItemLongClickListener(new OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				int color;
				id_delete = ids.get(position);
				if(red.contains(position)){
					color = Color.RED;
				}else if(yellow.contains(position)){
					color = Color.YELLOW;
				}else{
					color = Color.BLACK;
				}
				Intent i = new Intent(Trip_Edit.this, Item_Edit.class);
				i.putExtra("id", ids.get(position));
				i.putExtra("color", color);
				startActivity(i);
				return true;
			}
        	
        });

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
    
    
    
    
    
    @Override
	protected void onResume() {
		super.onResume();
		updateTripList();
	}





	@Override
	protected void onRestart() {
		super.onRestart();
		if(delete){
			cancelAlarmsUpdateFoods(id_delete);
			myDb.deleteRow(id_delete);
			delete = false;
		}else if(save){
			//change name/date in shared Pref
			updateSharedPref(id_delete, args);
			cancelAlarmsUpdateFoods(id_delete);
			myDb.updateRow(id_delete, args[0], args[1], args[2], args[3], args[4]);
			updateAlarm(id_delete);
			save = false;
		}
		//updateTripList();
	}
    
    void updateSharedPref(long id, String[] args){
    	SharedPreferences get = getSharedPreferences(exp,MODE_PRIVATE);
	   	SharedPreferences.Editor savedExpDates = getSharedPreferences(exp, MODE_PRIVATE).edit();
	   	DateTime dt = new DateTime();
	   	ArrayList<Object> row = new ArrayList<Object>();
	   	String temp;
	   	//get old item
	   	row = myDb.getRowAsArray(id_delete);
	   	//get all foods at that date
	   	temp = get.getString((String) row.get(4),"Food not in DB");
	   	FoodItem convert = new FoodItem(args[0]);
	   	convert.setExpiration(args[4]);
	   	dt =Enter_Foods.fItemDatetoDateTime(convert);
	   	if(!temp.equals("Food not in DB")){
	   	//key expdate, value food name
	   		//this is if other foods share same date
		   	temp.concat(temp + args[0] + "/");
		   	savedExpDates.putString(dt.toString(), temp);
	   	}
	   		//if no other foods in that date
	   		savedExpDates.putString(dt.toString(), args[0]);
		   	savedExpDates.commit();
		   	
    }
    
    void updateAlarm(long rowID){
    	Gson gson = new Gson();
		String json;
		Intent intent = new Intent(this, FoodExpAlarm.class);
	    json = gson.toJson(intent);
		myDb.updateRow(rowID ,args[0], args[1], args[2], 
				args[3], args[4], json);
		FoodItem i = new FoodItem(args[0]);
		i.setExpiration(args[4]);
		i.setCondition("Normal");
		i.setPurchaseDate(args[3]);
		i.setTripName(args[1]);
		prepareAlarm(false, i);
    }
    
    boolean prepareAlarm(boolean foodAlreadyExp, FoodItem i) {

		Enter_Foods.expDate = fItemDatetoDateTime(i);
		DateTime current= new DateTime();

		//TODO: this is where the desired user settings is taken into account
		//needs to come from user settings, default of 3 currently

		int daysBeforeDesiredNotif=3;
		DateTime currentPlus=current.plusDays(daysBeforeDesiredNotif);
		//add food to day
		ArrayList<String> temp= new ArrayList<String>();
		//if other alarms are already on that day, get the foods
		if(Enter_Foods.foodAndDate.get(Enter_Foods.expDate)!=null)
			temp= Enter_Foods.foodAndDate.get(Enter_Foods.expDate);
		//add new food to list
		temp.add(i.getItemName());
		Enter_Foods.foodAndDate.put(Enter_Foods.expDate, temp);

		//if currentPlus is after or on expDate 
		if(!foodAlreadyExp && (currentPlus.compareTo(Enter_Foods.expDate)>=0)){
			foodAlreadyExp = true;
			callAlarms(0,0,false);
		}
		//if currentPlus is before expDate
		else if(currentPlus.compareTo(Enter_Foods.expDate)<0){
			//TODO: if individual times are ever set, this will need to change
			//checks to see if an alarm for the day exists
			//currently it compares exact dates and times, 
			//but hour and minute are same for all so effectively only compares days
			if((!Enter_Foods.alarmsSet.contains(Enter_Foods.expDate))){
				DateTime notifDate = Enter_Foods.expDate.minusDays(daysBeforeDesiredNotif);
				Enter_Foods.alarmsSet.add(Enter_Foods.expDate);
				Duration dur = new Duration(current,notifDate);
				callAlarms((int)dur.getStandardDays(),0,false);

			}
		}


		return foodAlreadyExp;
    }
    
    public void callAlarms(int daysAfterSet,int daysBetween,boolean recurring){
		//for now I am going to make this as general as possible
		//takes as param number of days after setting it will first go off
		//and false for one alarm, true for recurring alarm	
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
	
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.menu_delete_trip:
	    	deleteTrip();
	        return true;
	    case R.id.menu_remove:
	    	removeItems();
	        return true;
	    default:
	        //return false;
	    	return super.onOptionsItemSelected(item);
	    }
	}
    

	private void updateTripList() {
		myCur.close();
    	myCur = myDb.getCursor();
    	items.clear();
    	red.clear();
    	yellow.clear();
    	ids.clear();
    	DateTime now = new DateTime();
    	DateTime exp;
    	DateTime expY;
    	String[] date = new String[3];
    	MenuItem deleteTrip = menu.findItem(R.id.menu_delete_trip);
    	
        if(type != 0){
        	int count = 0;
        	while(!myCur.isAfterLast()){
        		if(type == 1){
        			items.add(myCur.getString(1));
        			ids.add(myCur.getLong(0));
        		}
            	date = myCur.getString(5).split("/");
			    exp = new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), 23, 59);
			    expY = exp.plusDays(-3);
			    if(now.isAfter(exp)){
			    	red.add(count);
			    	if(type == 2){
			    		items.add(myCur.getString(1));
			    		count++;
			    		ids.add(myCur.getLong(0));
			    	}
			    }else if(now.isAfter(expY)){
			    	yellow.add(count);
			    	if(type == 2){
			    		items.add(myCur.getString(1));
			    		count++;
			    		ids.add(myCur.getLong(0));
			    	}
			    }
			    if(type == 1)
			    	count++;
            	myCur.moveToNext();
            }
        	deleteTrip.setTitle("Delete All");
        }else{
        	int count = 0;
        	while(!myCur.isAfterLast()){
            	if(myCur.getString(2).equals(trip_name)){
            		items.add(myCur.getString(1));
            		ids.add(myCur.getLong(0));
                	date = myCur.getString(5).split("/");
    			    exp = new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), 23, 59);
    			    expY = exp.plusDays(-3);
    			    if(now.isAfter(exp)){
    			    	red.add(count);
    			    	if(type == 2){
    			    		items.add(myCur.getString(1));
    			    		ids.add(myCur.getLong(0));
    			    	}
    			    }else if(now.isAfter(expY)){
    			    	yellow.add(count);
    			    	if(type == 2){
    			    		items.add(myCur.getString(1));
    			    		ids.add(myCur.getLong(0));
    			    	}
    			    }
            		count++;
            	}
            	myCur.moveToNext();
            }
        	deleteTrip.setTitle("Delete Trip");
        }
        if(items.isEmpty()){
        	this.finish();
        }
        listView.clearChoices();
        list.notifyDataSetChanged();
	}

	public void removeItems(){
    	 ArrayList<String> ids = new ArrayList<String>();
    	 
    	 //get names of all food to be removed
    	 for(int k = 0; k < items.size(); k++){
    		 if(listView.isItemChecked(k)){
    			 ids.add(items.get(k));
    		 }
    	 }
    	 
    	 myCur.moveToFirst();
    	 ArrayList<Long> removes = new ArrayList<Long>();
    	 
    	 //get rowIDs of all rows to be removed
    	 while(!myCur.isAfterLast()){
    		if(type == 1){
    			if(ids.contains(myCur.getString(1))){
	         		removes.add(myCur.getLong(0)); //avoiding ConcurrentModification Exception?
	         		ids.remove(myCur.getString(1)); //only needed if we allow duplicates in a single trip
	         	}
    		}else if(type == 2){
    			int DAYS_BEFORE_EXPIRATION = -3;
				String[] date = new String[3];
				DateTime now = new DateTime();
				DateTime exp;
				date = myCur.getString(5).split("/");
			    exp = new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), 23, 59);
			    exp = exp.plusDays(DAYS_BEFORE_EXPIRATION);
			    if(now.isAfter(exp) && ids.contains(myCur.getString(1))){
			    	removes.add(myCur.getLong(0));
			    	ids.remove(myCur.getString(1));
			    }
    		}else{
    			if((myCur.getString(2).equals(trip_name))&&(ids.contains(myCur.getString(1)))){
	         		removes.add(myCur.getLong(0)); //avoiding ConcurrentModification Exception?
	         		ids.remove(myCur.getString(1)); //only needed if we allow duplicates in a single trip
	         	}
    		}
         	myCur.moveToNext();
         }
    	 

    	 //remove rows
    	 for(long j : removes){
    		 cancelAlarmsUpdateFoods(j);
    		 myDb.deleteRow(j);
    	 }
    		 
    	 updateTripList();
    }

	 void cancelAlarmsUpdateFoods(long rowID) {
		 String concat = "";
    	 SharedPreferences.Editor savedExpDates = getSharedPreferences(exp, MODE_PRIVATE).edit();
    		 //update foodAndDates, setAlarms and save in shared pref 
    	     //so we don't use old values when making alarm calculations
    	 	 Database_Manager db = new Database_Manager(this);
    		 ArrayList<Object> row= db.getRowAsArray_ID(rowID);
    		 FoodItem fi= new FoodItem(row.get(1).toString());
    		 fi.setExpiration(row.get(5).toString());
    		 //This is for foodAndDates
    		 DateTime dt= Enter_Foods.fItemDatetoDateTime(fi);
    		 
    		 //get items all foods with same date
    		 ArrayList<String> temp = new ArrayList<String>(Enter_Foods.foodAndDate.get(dt));
    		 temp.remove(fi.getItemName());
    		 
    		 if(!temp.isEmpty()){
    			//foods are left for a dt, remove food and update dt, cancel alarm
    			 Enter_Foods.foodAndDate.put(dt, temp);
	    		//This is for shared pref
	 			 Iterator<String> iter = temp.iterator();
	 			 while(iter.hasNext()){
	 				concat += iter.next() + "/";
	 			 }
	 			 savedExpDates.putString(dt.toString(),concat );

    		 }
    		 //no foods left for a given dt, so remove dt
    		 else {
    			 Enter_Foods.foodAndDate.remove(dt);
    			 Enter_Foods.alarmsSet.remove(dt);
    			 savedExpDates.remove(dt.toString());
    			 
 	 			//cancel future alarm
 	 			AlarmManager alarmMan = (AlarmManager) 
 	 					getSystemService(Context.ALARM_SERVICE);
 	 			Gson gson = new Gson();
 	 			Intent intent = gson.fromJson((String) row.get(6), Intent.class);
 	 			PendingIntent pend = PendingIntent.getBroadcast(this, 0,
 					      intent, PendingIntent.FLAG_ONE_SHOT);
 	 			alarmMan.cancel(pend);
    			 
    		 }
    		 savedExpDates.commit();
	}
    
    public void deleteTrip(){
    	if(type == 1){
    		deleteAll();
    	}else if(type == 2){
    		deleteExp();
    	}else{
	    	myDb.deleteTrip(trip_name);
    	}
    	this.finish();
    }

    
    private void deleteExp() {
    	myCur.moveToFirst();
		ArrayList<Long> removes = new ArrayList<Long>();
		int DAYS_BEFORE_EXPIRATION = -3;
		String[] date = new String[3];
		DateTime now = new DateTime();
		DateTime exp;
		
		while(!myCur.isAfterLast()){
			date = myCur.getString(5).split("/");
		    exp = new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), 23, 59);
		    exp = exp.plusDays(DAYS_BEFORE_EXPIRATION);
		    if(now.isAfter(exp)){
		    	removes.add(myCur.getLong(0));
		    }
		    myCur.moveToNext();
		}
		for(Long id : removes){
			cancelAlarmsUpdateFoods(id);
			myDb.deleteRow(id);
		}
		
	}

	private void deleteAll() {
		myCur.moveToFirst();
		ArrayList<Long> removes = new ArrayList<Long>();
		while(!myCur.isAfterLast()){
			removes.add(myCur.getLong(0));
			myCur.moveToNext();
		}
		for(Long id : removes){
			cancelAlarmsUpdateFoods(id);
			myDb.deleteRow(id);
		}
	}

	/*
    private class MyAdapter extends ResourceCursorAdapter {
    	
    	String trip_name;

        @SuppressWarnings("deprecation")
		public MyAdapter(Context context, Cursor cur, String trip) {
            super(context, R.layout.trip_edit, cur);
            trip_name = trip;
        }

        @Override
        public View newView(Context context, Cursor cur, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.trip_edit, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cur) {
            TextView tvListText = (TextView)view.findViewById(R.id.list_text);
            CheckBox cbListCheck = (CheckBox)view.findViewById(R.id.list_checkbox);
            
            if(myDb.getTripName().equals(trip_name)){
            	tvListText.setText(cur.getString(cur.getColumnIndex(myDb.getName())));
                cbListCheck.setChecked(false);
            }
            
        }
    }
    */
	
	// adapter for colors
	class ColoredAdapter extends ArrayAdapter<String> {

		Activity context;
		ArrayList<String> items;

		public ColoredAdapter(Activity aContext, ArrayList<String> items) {
			super(aContext, android.R.layout.simple_list_item_multiple_choice, items);
			context = aContext;
			this.items = items;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			TextView text = (TextView) super.getView(position, convertView, parent);
			if(red.contains(position))
				text.setTextColor(Color.RED);
			else if(yellow.contains(position)){
				text.setTextColor(Color.YELLOW);
			}else{
				text.setTextColor(Color.BLACK);
			}
			text.setText(items.get(position));
			return text;
		}
	}
	
}

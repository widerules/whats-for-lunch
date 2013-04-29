package com.example.whatsforlunch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import org.joda.time.DateTime;

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
    Database_Manager myDb;
    ListView listView;
    Cursor myCur;
    String trip_name;
    ArrayList<String> items = new ArrayList<String>();
    ArrayAdapter<String> list;
    ArrayList<Integer> red = new ArrayList<Integer>();
    ArrayList<Integer> yellow = new ArrayList<Integer>();
    ArrayList<Long> ids = new ArrayList<Long>();
    int type;

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
        updateTripList();
    }
    
    
    
    @Override
	protected void onRestart() {
		super.onRestart();
		updateTripList();
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
    	myCur = myDb.getCursor();
    	myCur.moveToFirst();
    	items.clear();
    	red.clear();
    	yellow.clear();
    	ids.clear();
    	DateTime now = new DateTime();
    	DateTime exp;
    	DateTime expY;
    	String[] date = new String[3];
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
        	TextView deleteTrip = (TextView)this.findViewById(R.id.menu_delete_trip);
        	try{
        		deleteTrip.setText("Delete All");
        	}catch(Exception e){
        	}
        }else{
        	int count = 0;
        	while(!myCur.isAfterLast()){
            	if(myCur.getString(3).equals(trip_name)){
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
        }
        if(items.isEmpty()){
        	this.finish();
        }
        listView.clearChoices();
        list.notifyDataSetChanged();
        //colorCode();
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
    			if((myCur.getString(3).equals(trip_name))&&(ids.contains(myCur.getString(1)))){
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
    	 String exp = "SavedExpDates";
    	 SharedPreferences.Editor savedExpDates = getSharedPreferences(exp, MODE_PRIVATE).edit();
    		 //update related alarm data structures so we don't use old values when making
    		 //alarm calculations
    		 ArrayList<Object> row= myDb.getRowAsArray_ID(rowID);
    		 FoodItem fi= new FoodItem(row.get(1).toString());
    		 fi.setExpiration(row.get(5).toString());
    		 //This is for foodAndDates
    		 DateTime dt= Enter_Foods.fItemDatetoDateTime(fi);
    		 
    		 ArrayList<String> temp = new ArrayList<String>(Enter_Foods.foodAndDate.get(dt));
    		 temp.remove(fi.getItemName());
    		 
    		 if(!temp.isEmpty()){
    			//foods are left for a dt, remove foods and update dt, cancel alarm
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
    		//	 while(Enter_Foods.alarmsSet.contains(dt))
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
			myDb.deleteRow(id);
			cancelAlarmsUpdateFoods(id);
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
			myDb.deleteRow(id);
			cancelAlarmsUpdateFoods(id);
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
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_trip_edit, menu);
		return true;
	}
	
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

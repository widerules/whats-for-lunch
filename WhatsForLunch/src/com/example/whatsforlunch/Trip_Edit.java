package com.example.whatsforlunch;

import java.util.ArrayList;

import org.joda.time.DateTime;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
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
				Intent i = new Intent(Trip_Edit.this, Item_Edit.class);
				i.putExtra("id", ids.get(position));
				startActivity(i);
				return true;
			}
        	
        });

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        updateTripList();
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
			    exp = new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), 12, 0);
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
        	Button deleteTrip = (Button)this.findViewById(R.id.delete_trip);
        	deleteTrip.setText("Delete All");
        }else{
        	int count = 0;
        	while(!myCur.isAfterLast()){
            	if(myCur.getString(3).equals(trip_name)){
            		items.add(myCur.getString(1));
            		ids.add(myCur.getLong(0));
                	date = myCur.getString(5).split("/");
    			    exp = new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), 12, 0);
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

	public void removeItems(View view){
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
			    exp = new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), 12, 0);
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
    		 myDb.deleteRow(j);
    	 }
    	 updateTripList();
    }
    
    public void deleteTrip(View view){
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
		    exp = new DateTime(Integer.parseInt(date[2]), Integer.parseInt(date[0]), Integer.parseInt(date[1]), 12, 0);
		    exp = exp.plusDays(DAYS_BEFORE_EXPIRATION);
		    if(now.isAfter(exp)){
		    	removes.add(myCur.getLong(0));
		    }
		    myCur.moveToNext();
		}
		for(Long id : removes)
			myDb.deleteRow(id);
		
	}

	private void deleteAll() {
		myCur.moveToFirst();
		ArrayList<Long> removes = new ArrayList<Long>();
		while(!myCur.isAfterLast()){
			removes.add(myCur.getLong(0));
			myCur.moveToNext();
		}
		for(Long id : removes)
			myDb.deleteRow(id);
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
			}
			text.setText(items.get(position));
			return text;
		}
	}
	
}

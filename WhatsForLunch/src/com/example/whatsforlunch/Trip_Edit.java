package com.example.whatsforlunch;

import java.util.ArrayList;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.trip_edit);
        myDb = new Database_Manager(this);
        myCur = myDb.getCursor();
        Bundle b = getIntent().getExtras();
        trip_name = b.getString("trip name");      
        
        /*
        mListAdapter = new MyAdapter(Trip_Edit.this, myCur, trip_name);
        setListAdapter(mListAdapter);
        */
        list = new ColoredAdapter(this, items);
        setListAdapter(list);
        
        listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        updateTripList();
    }
    
    private void updateTripList() {
    	myCur = myDb.getCursor();
    	myCur.moveToFirst();
    	items.clear();
    	red.clear();
        if(trip_name.equals("")){
        	int count = 0;
        	while(!myCur.isAfterLast()){
            	items.add(myCur.getString(1));
            	if(myCur.getString(2).equals("Aged"))
            		red.add(count);
            	myCur.moveToNext();
            	count++;
            }
        	Button deleteTrip = (Button)this.findViewById(R.id.delete_trip);
        	deleteTrip.setText("Delete All");
        }else{
        	int count = 0;
        	while(!myCur.isAfterLast()){
            	if(myCur.getString(3).equals(trip_name)){
            		items.add(myCur.getString(1));
            		// TODO: change back to Aged
            		if(myCur.getString(2).equals("Aged"))
                		red.add(count);
            		count++;
            	}
            	myCur.moveToNext();
            }
        }
        if(items.isEmpty()){
        	this.finish();
        }
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
    		if(trip_name.equals("")){
    			if(ids.contains(myCur.getString(1))){
	         		removes.add(myCur.getLong(0)); //avoiding ConcurrentModification Exception?
	         		ids.remove(myCur.getString(1)); //only needed if we allow duplicates in a single trip
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
    	if(trip_name.equals("")){
    		deleteAll();
    	}else{
	    	myDb.deleteTrip(trip_name);
    	}
    	this.finish();
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
			text.setText(items.get(position));
			return text;
		}
	}
	
}

package com.example.whatsforlunch;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Trip_Edit extends ListActivity {

    //MyAdapter mListAdapter;
    Database_Manager myDb;
    ListView listView;
    String[] trip_list;
    Cursor myCur;
    String trip_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.trip_edit);
        myDb = new Database_Manager(this);
        myCur = myDb.getCursor();
        ArrayList<String> items = new ArrayList<String>(0);
        Bundle b = getIntent().getExtras();
        trip_name = b.getString("trip name");
        myCur.moveToFirst();
        
        while(true){
        	if(myCur.getString(2).equals(trip_name))
        		items.add(myCur.getString(1));
        	myCur.moveToNext();
        	if(myCur.isAfterLast())
        		break;
        }
        trip_list = items.toArray(new String[items.size()]);
        /*
        mListAdapter = new MyAdapter(Trip_Edit.this, myCur, trip_name);
        setListAdapter(mListAdapter);
        */
        
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, trip_list));
        
        listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }
    
    public void removeItems(View view){
    	 ArrayList<String> ids = new ArrayList<String>();
    	 
    	 //get names of all food to be removed
    	 for(int k = 0; k < trip_list.length; k++){
    		 if(listView.isItemChecked(k)){
    			 ids.add(trip_list[k]);
    		 }
    	 }
    	 myCur.moveToFirst();
    	 ArrayList<Long> removes = new ArrayList<Long>();
    	 
    	 //get rowIDs of all rows to be removed
    	 while(true){
    		 // TODO: Fix trip name and item name indices
         	if((myCur.getString(2).equals(trip_name))&&(ids.contains(myCur.getString(1)))){
         		removes.add(myCur.getLong(0)); //avoiding ConcurrentModification Exception?
         		ids.remove(myCur.getString(1)); //only needed if we allow duplicates in a single trip
         	}
         	myCur.moveToNext();
         	if(myCur.isAfterLast())
         		break;
         }
    	 
    	 //remove rows
    	 for(long j : removes){
    		 myDb.deleteRow(j);
    	 }
    	 Intent i = new Intent(Trip_Edit.this, Trip_Select.class);
    	 /* OR
    	  * Intent i = new Intent(Trip_Edit.this, Trip_Select.class);
	      * myDb.addRow("name", "condition", trip_name, "tripdate", "expdate");
	     */
	     startActivity(i);
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
	
}

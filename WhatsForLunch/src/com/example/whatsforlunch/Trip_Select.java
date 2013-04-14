package com.example.whatsforlunch;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class Trip_Select extends ListActivity {

    Database_Manager myDb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.trip_select);
		
        myDb = new Database_Manager(this);
        Cursor myCur = myDb.getCursor();
        ArrayList<String> trips = new ArrayList<String>(0);
        myCur.moveToFirst();
        String name = "";
        
        while(!myCur.isAfterLast()){
        	name = myCur.getString(3);//name of trip
        	if(!trips.contains(name))//check if we already have this trip
        		trips.add(name);
        	if(!myCur.moveToNext())//false when out of bounds
        		break;
        }
        final String[] trip_names = trips.toArray(new String[trips.size()]);
        
        /*  
         * TEMPORARY
         * 
        *
        RecipeList rec = new RecipeList("strawberry");
		ArrayList<Recipe> rList = rec.getRecipes();
		ArrayList<String> rec_strings = new ArrayList<String>(rList.size());
		for(Recipe r : rList){
			rec_strings.add(r.getName());
		}
		String[] recipes = rec_strings.toArray(new String[rec_strings.size()]);
		/*  
		 * TEMPORARY
		 * 
		*/
        
        setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, 
        		 trip_names /*/recipes*/));
        
        final ListView listView = getListView();

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new OnItemClickListener(){

        	@Override
        	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
        	        Intent i = new Intent(Trip_Select.this, Trip_Edit.class);
        	        i.putExtra("trip name",trip_names[position]);
        	        startActivity(i);
        	}

        });
    }

/*
    private class MyAdapter extends ResourceCursorAdapter {
    	
    	ArrayList<String> trips;

        @SuppressWarnings("deprecation")
		public MyAdapter(Context context, Cursor cur) {
            super(context, R.layout.trip_edit, cur);
            trips = new ArrayList<String>(); 
        }

        @Override
        public View newView(Context context, Cursor cur, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.trip_edit, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cur) {
        	if(!(trips.contains(myDb.getTripName()))){
        		trips.add(myDb.getTripName());
        		TextView tvListText = (TextView)view.findViewById(R.id.list_trips);
        		tvListText.setText(cur.getString(cur.getColumnIndex(myDb.getTripName())));
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

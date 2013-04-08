package com.example.whatsforlunch;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class Trip_Select extends ListActivity {

    MyAdapter mListAdapter;
    Database_Manager myDb = new Database_Manager(this);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Cursor myCur = null;
        
        myDb.addRow("name", "condition", "tripname", "tripdate", "expdate");
        myDb.addRow("name2", "condition2", "tripname2", "tripdate", "expdate2");
        
        myCur = myDb.getCursor();

        mListAdapter = new MyAdapter(Trip_Select.this, myCur);
        setListAdapter((ListAdapter) mListAdapter);
    }


    private class MyAdapter extends ResourceCursorAdapter {

        @SuppressWarnings("deprecation")
		public MyAdapter(Context context, Cursor cur) {
            super(context, R.layout.trip_edit, cur);
        }

        @Override
        public View newView(Context context, Cursor cur, ViewGroup parent) {
            LayoutInflater li = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return li.inflate(R.layout.trip_edit, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cur) {
            TextView tvListText = (TextView)view.findViewById(R.id.list_text);
            tvListText.setText(cur.getString(cur.getColumnIndex(myDb.getTripName())));
        }
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_trip_edit, menu);
		return true;
	}
	
}

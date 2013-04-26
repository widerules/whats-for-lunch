package com.example.whatsforlunch;

import java.util.ArrayList;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TextView;

public class Item_Edit extends Activity {
	Long id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_edit);
		Bundle b = getIntent().getExtras();
		id = b.getLong("id");
		Database_Manager db = new Database_Manager(this);
		ArrayList<Object> row = db.getRowAsArray(id);
		String name = (String) row.get(1);
		String trip = (String) row.get(3);
		String trip_date = (String) row.get(4);
		String exp = (String) row.get(5);
		
		TextView name_view = (TextView)this.findViewById(R.id.item_edit_name);
		name_view.setText(name);
		TextView trip_view = (TextView)this.findViewById(R.id.item_edit_trip);
		trip_view.setText(trip + " - " + trip_date);
	}

}

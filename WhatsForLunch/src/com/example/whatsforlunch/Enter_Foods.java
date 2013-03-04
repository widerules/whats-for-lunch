package com.example.whatsforlunch;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class Enter_Foods extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_foods);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.enter_foods, menu);
		return true;
	}

}

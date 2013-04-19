package com.example.whatsforlunch;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class TabManager{
	
	public static void setMyTabs (TabHost tabHost, Context context){

		
		//create a specification for each tab
		TabSpec firstSpec = tabHost.newTabSpec("tabs");
		TabSpec secondSpec = tabHost.newTabSpec("tabs");
		
		//set text, can also set art here
		firstSpec.setIndicator("Add Items by Category");
		secondSpec.setIndicator("Items in Current Trip");
		
		//set Content for each tab
		firstSpec.setContent(new Intent(context,FirstTab.class));
		secondSpec.setContent(new Intent(context,SecondTab.class));
		
		//adds the tabs to tab host
        tabHost.addTab(firstSpec);
        tabHost.addTab(secondSpec);

        //use this to bring tab to foreground
        tabHost.getTabWidget().setCurrentTab(1);
        tabHost.setOnTabChangedListener(TabChangeListener);
	}

	private static OnTabChangeListener TabChangeListener = new OnTabChangeListener(){
		@Override
		public void onTabChanged(String tabId){
			
//          for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
//          {
//              tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.WHITE);
//          }
//
//          tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.GRAY);
		}
	};
	
}

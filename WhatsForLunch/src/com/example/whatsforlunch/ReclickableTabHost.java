package com.example.whatsforlunch;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TabHost;

public class ReclickableTabHost extends TabHost {

    public ReclickableTabHost(Context context) {
        super(context);
    }

    public ReclickableTabHost(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setCurrentTab(int index) {
        // Reset Category tab if clicked again
    	if (index == getCurrentTab()) {
        	if(getCurrentTabTag().equalsIgnoreCase("Cat")){
        		((Enter_Foods)getContext()).resetCategories();
        	}
        } else {
            super.setCurrentTab(index);
        }
    }
}

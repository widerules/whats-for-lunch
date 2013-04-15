package com.example.whatsforlunch;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView {
	public class TypefacedTextView extends TextView {

	    public TypefacedTextView(Context context, AttributeSet attrs) {
	        super(context, attrs);

	        //use for custom widgets, tells eclipse not to try to render in graphical
	        if (isInEditMode()) {
	            return;
	        }

	        TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
	        String fontName = styledAttrs.getString(R.styleable.CustomTextView_typeface);
	        styledAttrs.recycle();

	        if (fontName != null) {
	            Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
	            setTypeface(typeface);
	        }
	    }

	}
}

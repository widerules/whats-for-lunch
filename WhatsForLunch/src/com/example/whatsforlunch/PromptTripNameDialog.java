package com.example.whatsforlunch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//...
public class PromptTripNameDialog extends DialogFragment{
	
	/* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(String tripname);
        public void onDialogNegativeClick(DialogFragment dialog);
    } 
    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;
    private EditText mEditText;
    
    public PromptTripNameDialog() {
        // Empty constructor required for DialogFragment
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0); // remove title from dialogfragment
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        
        final View view = inflater.inflate(R.layout.enter_tripname_prompt, null);
        
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
        		// Add action buttons
               .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	// Send the positive button event back to the host activity
                	   //Override positive onClick in onStart() to control dismissal
                   }
               })
               .setNegativeButton(R.string.no_nameforme, new DialogInterface.OnClickListener() {
            	   @Override
                   public void onClick(DialogInterface dialog, int id) {
                	   // Send the negative button event back to the host activity
                       mListener.onDialogNegativeClick(PromptTripNameDialog.this);
                   }
               });
        return builder.create();
    }
    @Override
    public void onStart()
    {
    	//super.onStart() is where dialog.show() is actually called on the underlying dialog,
    	//so we have to do it after this point
        super.onStart();
        AlertDialog d = (AlertDialog)getDialog();

        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                        	mEditText = (EditText) getDialog().getCurrentFocus().findViewById(R.id.tripName);
                            if(mEditText.getText().toString().length() > 0){
                            	//Trip name was entered before pressing yes
                            	mListener.onDialogPositiveClick(mEditText.getText().toString());
                            	dismiss();
                            }else{
                            	//User pressed Yes without entering trip name
                            	Log.d("Entry Error", "No trip name set! Sad day.");
                            	Toast.makeText(getActivity().getApplicationContext(), 
                            			"Please enter a trip name", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
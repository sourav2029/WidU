package com.mnnit.looserz.widu;



import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


public class MainActivity extends Activity implements OnItemSelectedListener,OnClickListener{

	Spinner spinner;
	
	String Selection;
	
	double plong=0;
	
	double plat=0;
	
	String address ="";
	
	Button btn;
	
	int count;
	
	List<String> Mobile_Number= new  ArrayList<String>();
	
	String Message = "Hey there ! I am in troble .The GPS sucks so can't give you my location>";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
		spinner = (Spinner) findViewById(R.id.spinner1);
		
		btn=(Button) findViewById(R.id.button2);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.List_array, android.R.layout.simple_spinner_item);
		
		// Specify the layout to use when the list of choices appears
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		spinner.setOnItemSelectedListener(this);
		
		btn.setOnClickListener(this);
		
		GPShelp mGPSService = new GPShelp(this);
		
		mGPSService.getLocation();
		
		if (mGPSService.isLocationAvailable == true){
			
		    // Getting location co-ordinates
			
		    plat = mGPSService.getLatitude();
		    plong = mGPSService.getLongitude();
		    address = mGPSService.getLocationAddress();
		    
		Toast.makeText(getApplicationContext(), "Your address is: " + address, Toast.LENGTH_SHORT).show(); 
		
		// make sure you close the gps after using it. Save user's battery power
		mGPSService.closeGPS();
		Message="Hey there ! I am in troble .My location is \n LATITUE="+(Double.toString(plat))+"\nLONGITUDE="
				+(Double.toString(plong)+"\nADDRESS="+address);

		}
		
					
	}
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
spinner.setSelection(arg2);
		
		Selection = (String) spinner.getSelectedItem();
		
		@SuppressWarnings("rawtypes")
		Class ourClass = null;
		
		try {
			ourClass = Class.forName("com.mnnit.looserz.widu."+Selection);
		} 
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent ourIntent = new Intent(this,ourClass);
		startActivity(ourIntent);
		
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	@Override
	public void onClick(View arg0) {
		SQLiteDatabase db=openOrCreateDatabase("mydb", MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS tables (name VARCHAR,number VARCHAR);");
		Cursor cr= db.rawQuery("SELECT * FROM tables", null);
		if(cr.getCount()==0)
		{
			asktoChangeList(MainActivity.this);
		}	
		if(cr.moveToFirst()){
			do {
	             Mobile_Number.add(cr.getString(cr.getColumnIndex("number")));
	        } while (cr.moveToNext());
		}
		
		
		if (Mobile_Number != null) {
			 
            for (int i = 0; i < Mobile_Number.size(); i++) {
            	
                String tempMobileNumber = Mobile_Number.get(i).toString();
                sendSMS(tempMobileNumber);
                
            }
		
	    }
	}
		
		private void asktoChangeList(MainActivity mainActivity) {
		// TODO Auto-generated method stub
			final Context mContext=mainActivity;
			AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(mContext);

			// Setting Dialog Title
			mAlertDialog.setTitle("No Contact Selected, Select Now?")
			.setMessage("Switch to ChangeList to Select Contacts?")
			.setPositiveButton("ChangeList Now", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent("android.intent.action.CHANGELIST");
					mContext.startActivity(intent);
					}
				})
				.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						}
					}).show();
		
	}
		private void sendSMS(String phoneNumber) {

	        String SENT = "SMS_SENT";
	        String DELIVERED = "SMS_DELIVERED";
	 
	        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
	        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);
	 
	        // ---when the SMS has been sent---
	        registerReceiver(new BroadcastReceiver() {
	            @Override
	            public void onReceive(Context arg0, Intent arg1) {
	            	
	                switch (getResultCode()) {
	                
	                	case Activity.RESULT_OK:
	                	
	                		ContentValues values = new ContentValues();
	                    		for (int i = 0; i < Mobile_Number.size() - 1; i++) {
	                    	
						values.put("address", Mobile_Number.get(i).toString());
						values.put("body", Message);
				    	}

	                    		getContentResolver().insert(Uri.parse("content://sms/sent"), values);
	                	                	
	                    		Toast.makeText(getBaseContext(), "SMS sent",
						Toast.LENGTH_SHORT).show();
	                    		break;
	                    
	                	case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
	                	
	                    		Toast.makeText(getBaseContext(), "Generic failure",
						Toast.LENGTH_SHORT).show();
	                    		break;
	                    
	        		case SmsManager.RESULT_ERROR_NO_SERVICE:
	                	
	                    		Toast.makeText(getBaseContext(), "No service",
						Toast.LENGTH_SHORT).show();
	                    		break;
	                    
	                	case SmsManager.RESULT_ERROR_NULL_PDU:
	                	
	                    		Toast.makeText(getBaseContext(), "Null PDU",
						Toast.LENGTH_SHORT).show();
	                    		break;
	                    
	        		case SmsManager.RESULT_ERROR_RADIO_OFF:
	                	
	    				Toast.makeText(getBaseContext(), "Radio off",
						Toast.LENGTH_SHORT).show();
	                    		break;
	        	}
	    	    }
	        }, new IntentFilter(SENT));
	 
	        // ---when the SMS has been delivered---
	        registerReceiver(new BroadcastReceiver() {
	            @Override
	            public void onReceive(Context arg0, Intent arg1) {
	            	
	                switch (getResultCode()) {
	                
	                	case Activity.RESULT_OK:
	                	
	                    		Toast.makeText(getBaseContext(), "SMS delivered",
						Toast.LENGTH_SHORT).show();
	                    		break;
	                    
	                	case Activity.RESULT_CANCELED:
	                	
	                    		Toast.makeText(getBaseContext(), "SMS not delivered",
						Toast.LENGTH_SHORT).show();
	                    		break;
	        	}
	            }
	        }, new IntentFilter(DELIVERED));
	 
	        SmsManager sms = SmsManager.getDefault();
	        sms.sendTextMessage(phoneNumber, null, Message, sentPI, deliveredPI);
	    }
		
		
		
}

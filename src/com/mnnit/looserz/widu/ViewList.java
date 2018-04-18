package com.mnnit.looserz.widu;

import java.util.ArrayList;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class ViewList extends Activity {
	 final ArrayList<String> list = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.display2);
		ListView lv=(ListView) findViewById(R.id.lv2);
		SQLiteDatabase db=openOrCreateDatabase("mydb", MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS tables (name VARCHAR,number VARCHAR);");
		Cursor cr= db.rawQuery("SELECT * FROM tables", null);
		if (cr.moveToFirst()) {
	        do {
	             list.add(cr.getString(cr.getColumnIndex("name")));
	        } while (cr.moveToNext());
	    }
        if(list.size()>0) // check if list contains items.
        {    
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ViewList.this,android.R.layout.simple_list_item_1,list);
    lv.setAdapter(arrayAdapter);
        }  
        else
        {
           Toast.makeText(ViewList.this,"No items to display",Toast.LENGTH_LONG).show();
      }
		}
	
	}



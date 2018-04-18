package com.mnnit.looserz.widu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ChangeList extends Activity implements OnItemClickListener{

    List<String> name1 = new ArrayList<String>();
    List<String> phno1 = new ArrayList<String>();
    MyAdapter ma ;
    Button select;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display);
        Toast.makeText(ChangeList.this, "Please select any five contacts to update your list",Toast.LENGTH_SHORT).show();

        getAllContacts(this.getContentResolver());
        ListView lv= (ListView) findViewById(R.id.lv);
            ma = new MyAdapter();
            lv.setAdapter((ListAdapter) ma);
            lv.setOnItemClickListener(this); 
            lv.setItemsCanFocus(false);
            lv.setTextFilterEnabled(true);
            // adding
            final SQLiteDatabase db= openOrCreateDatabase("mydb", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS tables (name VARCHAR,number VARCHAR);");
            
           select = (Button) findViewById(R.id.button1);
        select.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {
                    StringBuilder checkedcontacts= new StringBuilder();
                    int counter=0;
                    String temp1,temp2;
                    db.execSQL("DROP TABLE IF EXISTS tables");
                    db.execSQL("CREATE TABLE IF NOT EXISTS tables (name VARCHAR,number VARCHAR);");
                    
                for(int i = 0; i < name1.size(); i++)
                    {
                		if(ma.mCheckStates.get(i)==true && counter<=5)
                			{
		                    	counter++;
		                         checkedcontacts.append(name1.get(i).toString());
		                         checkedcontacts.append("\n");
		                         temp1=name1.get(i).toString();
		                         temp2=phno1.get(i).toString();
		                         
		                         String kuch = "INSERT INTO tables VALUES('"+temp1+"'"+","+"'"+temp2+"'"+");";
		                         db.execSQL(kuch);
		                         
                			}
                    }
                   
                    if(counter>5)
                    		Toast.makeText(ChangeList.this, "Please select five or less contacts",Toast.LENGTH_SHORT).show();
                    else
                Toast.makeText(ChangeList.this, checkedcontacts,Toast.LENGTH_SHORT).show();
            }       
        });


    }
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        // TODO Auto-generated method stub
         ma.toggle(arg2);
    }

    public  void getAllContacts(ContentResolver cr) {

        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
        while (phones.moveToNext())
        {
          String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
          String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
          name1.add(name);
          phno1.add(phoneNumber);
        }

        phones.close();
     }
    class MyAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener
    { 
    	private SparseBooleanArray mCheckStates;
       LayoutInflater mInflater;
        TextView tv1,tv;
        CheckBox cb;
        MyAdapter()
        {
            mCheckStates = new SparseBooleanArray(name1.size());
            mInflater = (LayoutInflater)ChangeList.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return name1.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub

            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View vi=convertView;
            if(convertView==null)
             vi = mInflater.inflate(R.layout.row, null); 
             tv= (TextView) vi.findViewById(R.id.textView1);
             tv1= (TextView) vi.findViewById(R.id.textView2);
             cb = (CheckBox) vi.findViewById(R.id.checkBox1);
             tv.setText("Name :"+ name1.get(position));
             tv1.setText("Phone No :"+ phno1.get(position));
             cb.setTag(position);
             cb.setChecked(mCheckStates.get(position, false));
             cb.setOnCheckedChangeListener(this);

            return vi;
        }
         public boolean isChecked(int position) {
                return mCheckStates.get(position, false);
            }

            public void setChecked(int position, boolean isChecked) {
                mCheckStates.put(position, isChecked);
            }

            public void toggle(int position) {
                setChecked(position, !isChecked(position));
            }
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                boolean isChecked) {
            // TODO Auto-generated method stub

             mCheckStates.put((Integer) buttonView.getTag(), isChecked);         
        }   
    }   
}

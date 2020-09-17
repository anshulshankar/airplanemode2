package com.example.airplanemode2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    AirplaneModeReciever airplaneModeReciever;
    Switch switch_1, switch_2;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        switch_1 = (Switch)findViewById(R.id.switch_1);
        switch_2 = (Switch)findViewById(R.id.switch_2);
        onCreateStatusChecked(this);
        listView = (ListView) findViewById(R.id.listViewContactsDisplay);

        //getAndroidContacts();
        //checking the mode on the start of the app





    }

    private boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
    }

    public void onCreateStatusChecked(Context context) {
        boolean state = isAirplaneModeOn(this);

            if(state) {
                Toast.makeText(context, "on", Toast.LENGTH_SHORT).show();
                switch_1.setEnabled(false);
                switch_1.setChecked(false);
                switch_2.setEnabled(false);
                switch_2.setChecked(false);
            }
            else {
                Toast.makeText(context, "off", Toast.LENGTH_SHORT).show();
                switch_1.setEnabled(true);
                switch_2.setEnabled(false);
                switch_1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(switch_1.isChecked()) {
                            switch_2.setEnabled(true);
                            //switch_2.setChecked(true);
                        }
                        else {
                            switch_2.setChecked(false);
                            switch_2.setEnabled(false);
                        }
                    }
                });
            }

    }


    @Override
    protected void onStart() {
        super.onStart();
        airplaneModeReciever = new AirplaneModeReciever();

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        //this.registerReceiver(airplaneModeReciever, intentFilter);
        this.registerReceiver(reciever, intentFilter);
    }

    private BroadcastReceiver reciever = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            Log.i("status", "Broadcast Reciever ");
            final Switch s1, s2;
            s1 = (Switch)findViewById(R.id.switch_1);
            s2 = (Switch)findViewById(R.id.switch_2);

            boolean state = intent.getBooleanExtra("state", false);
            if(state) {
                Toast.makeText(context, "on", Toast.LENGTH_SHORT).show();
                s1.setEnabled(false);
                s1.setChecked(false);
                s2.setEnabled(false);
                s2.setChecked(false);
            }
            else {
                Toast.makeText(context, "off", Toast.LENGTH_SHORT).show();
                s1.setEnabled(true);
                s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(s1.isChecked()) {
                            s2.setEnabled(true);

                        }
                        else {
                            s2.setChecked(false);
                            s2.setEnabled(false);
                        }
                    }
                });
            }



        }
    };

    public class AndroidContacts {
        public String androidContactName = " ";
        public String androidContactNumber = " ";
        public int androidContactID =0;
    }

    public void getAndroidContacts() {
        ArrayList<AndroidContacts> contactsArrayList = new ArrayList<AndroidContacts>();

        //get all the contacts
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = null;
        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(uri, null, null, null, null);

        if(cursor.getCount() > 0) {
            while(cursor.moveToNext()) {
                AndroidContacts androidContacts = new AndroidContacts();
                String contactsDisplayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String contactID = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                androidContacts.androidContactName = contactsDisplayName;

                int hasPhoneNo = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if(hasPhoneNo > 0) {
                    Cursor phoneCursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[] {contactID},
                            null
                    );
                    while(phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        androidContacts.androidContactNumber = phoneNumber;
                    }
                    phoneCursor.close();
                }
                contactsArrayList.add(androidContacts);
            }
            /*showing result in the list*/
            AdapterForContacts adapter = new AdapterForContacts(this, contactsArrayList);
            listView.setAdapter(adapter);

        }

    }

    public class AdapterForContacts extends BaseAdapter {

        Context context;
        List<AndroidContacts> listAndroidContacts;

        public AdapterForContacts(Context context, List<AndroidContacts> contacts) {
            this.context = context;
            this.listAndroidContacts = contacts;
        }
        @Override
        public int getCount() {
            return listAndroidContacts.size();
        }

        @Override
        public Object getItem(int position) {
            return listAndroidContacts.get(position);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view =View.inflate(context, R.layout.contact_list_items, null);
            TextView textViewContactName = (TextView) view.findViewById(R.id.textViewContactsName);
            TextView textViewContactNumber = (TextView) view.findViewById(R.id.textViewContactsNumber);

            textViewContactName.setText(listAndroidContacts.get(position).androidContactName);
            textViewContactNumber.setText(listAndroidContacts.get(position).androidContactNumber);



            view.setTag(listAndroidContacts.get(position).androidContactName);
            return view;
        }
    }
}
package com.handspasms.handspasms;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.CellInfo;
import android.telephony.CellSignalStrength;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends Activity {
    Stack<messageEvent> msgList;
    Stack<String> stringLog;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instantiateList();
        sendSMSMessage("Hi","9173400996");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*
    sendSMSMessage sends String message to String phoneNumber
     */
    //timestamp added to test it
    protected void sendSMSMessage(String message, String phoneNumber) {
        Log.i("Send SMS", "");
        messageEvent msgEvent = new messageEvent(phoneNumber, message);
        String timestamp = msgEvent.getTimestamp();
        TelephonyManager  tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        try {
            if(!tm.isSmsCapable()) {
                throw new Exception();
            }
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Sent" + "\n" + timestamp);

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Message fail", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Fail" + "\nTime:" + timestamp);
            e.printStackTrace();
        }
    }

    public void instantiateList() {
        stringLog = new Stack<String>();
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stringLog);
        ListView myList = (ListView) findViewById(R.id.logView);
        myList.setAdapter(adapter);
    }

    public void addToList(String newThing) {
        stringLog.push(newThing);
        adapter.notifyDataSetChanged();
    }


    /* Google cloud Messaging credentials
        Server API key: AIzaSyC1pHP-j6JDF8QNS6dFy3CT5Twg2Y8YMlI
        Sender ID: 1063206121019
     */


}
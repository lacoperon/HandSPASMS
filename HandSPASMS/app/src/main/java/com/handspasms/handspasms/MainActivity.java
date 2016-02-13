package com.handspasms.handspasms;

import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
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
        addToList("hi");
        sendSMSMessage("hi", "9173400996");

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
        try {
            messageEvent msgEvent = new messageEvent(phoneNumber, message);
            String timestamp = msgEvent.getTimestamp();
            message = message + "\n" + timestamp;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
            msgList.peek().messageSent();
            addToList(msgList.peek().toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS fail'd, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
            addToList(msgList.peek().toString());
        }
    }

//NEED TO FIX METHOD, BREAKS
    public void sendText(String phone, String message) {
        //Phone and message to be inputted from GMS
        messageEvent msgEvent = new messageEvent(phone, message);
        String timestamp = msgEvent.getTimestamp();
        msgList.push(msgEvent);
        try {

            sendSMSMessage(message, phone, timestamp);
            //Appends message status to show that the message succeeded in sending.
            Toast.makeText(getApplicationContext(), "SMS sent!", Toast.LENGTH_LONG).show();
            msgList.peek().messageSent();
            addToList(msgList.peek().toString());

            
        }
        catch (Exception e) {
            //Appends message status to show that the message failed to send.
            Toast.makeText(getApplicationContext(), "SMS failed!", Toast.LENGTH_LONG).show();
            msgList.peek().messageFailed();
            addToList(msgList.peek().toString());
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
package com.handspasms.handspasms;

import android.os.Bundle;
import android.app.Activity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends Activity {
    Stack<messageEvent> msgList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    protected void sendSMSMessage(String message, String phoneNumber, String timestamp) {
        Log.i("Send SMS", "");
        try {
            message = message + "\n" + timestamp;
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS fail'd, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    protected String constructSMS(String patientName, String doctorName, String date, String time) {
        return ("Hello " + patientName + ", \n You have an appointment at " + time + " on " + date + " with " + doctorName);
    }

    public void smsButtonCallback(View view) {
        //Placeholder phone and message, to be replaced by server output
        String phone = "9173400996";
        String message = constructSMS("John Cena", "Doctor Mario", "6/6/6", "13pm " );
        messageEvent msgEvent = new messageEvent(phone, message);
        String timestamp = msgEvent.getTimestamp();
        try {sendSMSMessage(message, phone, timestamp);}
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS fail'd, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }



}

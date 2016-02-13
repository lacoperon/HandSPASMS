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

public class MainActivity extends Activity {
    String phone;
    String msg;
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
    protected void sendSMSMessage(String message, String phoneNumber) {
        Log.i("Send SMS", "");
        try {
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
        String message = constructSMS("John Cena", "Doctor Mario", "6/6/6", "13pm " );
        EditText phoneView = (EditText) findViewById(R.id.phoneOutput);
        phone = phoneView.getText().toString();
        EditText msgView   = (EditText) findViewById(R.id.messageOutput);
        msg   = msgView.getText().toString();
        sendSMSMessage(msg, phone);

    }



}

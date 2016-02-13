package com.handspasms.handspasms;

import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellLocation;
import android.telephony.CellSignalStrength;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class MainActivity extends Activity {
    Stack<messageEvent> msgList;
    static Stack<String> stringLog;
    static ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instantiateList();
        SimpleWebServer sws = new SimpleWebServer(1881, null);
        sws.start();
        sws.run();

        sendSMSMessage("hello", "9173400996");

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
        CellInfo cf = tm.getAllCellInfo().get(0);
        //Check for cell reception and sms preparedness
        boolean isRegistered = cf.isRegistered(); //IE is connected to the network
        boolean isSMSCapable = tm.isSmsCapable(); //IE is technically capable of sending a SMS
        boolean isActiveSim = (tm.getSimState() == 5); //IE has an active SIM card

        try {
            if(!isSMSCapable) {
                throw new notSMSCapableException();
            } else if(!isActiveSim) {
                throw new noSIMCardException();
            } else if (!isRegistered) {
                throw new notConnectedToNetworkException();
            } else if (message == ("")) {
                throw new nullMessageException();
            }

            if(!isSMSCapable | !isActiveSim | !isRegistered) {
                throw new Exception();
            }
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Sent" + "\n" + timestamp);

        } catch (notSMSCapableException e) {

            Toast.makeText(getApplicationContext(), "Message fail", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Fail (Not SMS Capable)" + "\nTime:" + timestamp);
            e.printStackTrace();

        } catch (noSIMCardException e) {
            Toast.makeText(getApplicationContext(), "Message fail", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Fail (SIM Card Not Active)" + "\nTime:" + timestamp);
            e.printStackTrace();

        } catch (notConnectedToNetworkException e) {
            Toast.makeText(getApplicationContext(), "Message fail", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Fail (No Cellular Network)" + "\nTime:" + timestamp);
            e.printStackTrace();

        } catch (nullMessageException e) {
            Toast.makeText(getApplicationContext(), "Message fail", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Fail (Null Message)" + "\nTime:" + timestamp);
            e.printStackTrace();

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

    public static void addToList(String newThing) {
        stringLog.push(newThing);
        adapter.notifyDataSetChanged();
    }

    public String getIpAddress() {
        return "";
    }


    /* Google cloud Messaging credentials
        Server API key: AIzaSyC1pHP-j6JDF8QNS6dFy3CT5Twg2Y8YMlI
        Sender ID: 1063206121019
     */


}
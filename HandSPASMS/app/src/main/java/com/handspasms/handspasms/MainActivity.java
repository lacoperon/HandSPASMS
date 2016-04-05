package com.handspasms.handspasms;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.text.SimpleDateFormat;

public class MainActivity extends Activity {
    private static final String  TAG  = "MainActivity";
    private static final Integer PORT = 1889;
    private static final Integer MSG_MAX = 100;
    private static Stack<String> stringLog;
    private static ArrayAdapter<String> adapter;
    // http://developer.android.com/reference/java/text/SimpleDateFormat.html
    private static final String TIMESTAMP_FORMAT = "hh:mm:ss a yyyy-MM-dd";
    private final SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.US);
    private SimpleWebServer sws = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instantiateList();
        ImageView hand = (ImageView) findViewById(R.id.hand);
        hand.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "Created by Brendan Fahey, Dan Meyer, " +
                        "David Stern, Wesley Wei, & Elliot Williams   Created at ID Hack 2016"
                        , Toast.LENGTH_LONG).show();
                return true;
            }
        });
        TextView ipAddress = (TextView) findViewById(R.id.ipAddress);
        ipAddress.setText("http://" + getIPAddress(true) + ':' + PORT);
        sws = new SimpleWebServer(PORT, null, this);
        sws.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(sws != null) {
            sws.stop();
            sws = null;
        }
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
    public void sendSMSMessage(String message, String phoneNumber) {

        Log.i(TAG, "Send SMS");
        String timestamp = sdf.format(new Date());
        TelephonyManager  tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        CellInfo cf = tm.getAllCellInfo().get(0);
        //Check for cell reception and sms preparedness
        boolean isRegistered = cf.isRegistered(); //IE is connected to the network
        boolean isSMSCapable = tm.isSmsCapable(); //IE is technically capable of sending a SMS
        boolean isActiveSim = (tm.getSimState() == 5); //IE has an active SIM card

        if(!isSMSCapable) {
            Toast.makeText(getApplicationContext(), "Message fail", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Fail (Not SMS Capable)" + "\nTime:" + timestamp);
        } else if(!isActiveSim) {
            Toast.makeText(getApplicationContext(), "Message fail", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Fail (SIM Card Not Active)" + "\nTime:" + timestamp);
        } else if (!isRegistered) {
            Toast.makeText(getApplicationContext(), "Message fail", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Fail (No Cellular Network)" + "\nTime:" + timestamp);
        } else if (message.equals("")) {
            Toast.makeText(getApplicationContext(), "Message fail", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Fail (Null Message)" + "\nTime:" + timestamp);
        } else {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
            addToList("To: " + phoneNumber + "\nStatus: Sent" + "\n" + timestamp);
        }
    }

    private void instantiateList() {
        stringLog = new Stack<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stringLog);
        ListView myList = (ListView) findViewById(R.id.logView);
        myList.setAdapter(adapter);
    }

    private static void addToList(String newThing) {
        if(stringLog.size() >= MSG_MAX){
            stringLog.remove(0);
        }
        stringLog.push(newThing);
        adapter.notifyDataSetChanged();
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    // http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
    private static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) { } // for now eat exceptions
        return "";
    }
}
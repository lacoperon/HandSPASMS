package com.handspasms.handspasms;


import java.util.Calendar;

/**
 * Created by Elliot on 2/13/16.
 */
public class messageEvent {
    String phone;
    String message;
    String timestamp;


    public static String getNow() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DATE);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min  = c.get(Calendar.MINUTE);
        int sec  = c.get(Calendar.SECOND);
        String format = hour+":"+min+":"+sec+" on "+day+"/"+month+"/"+year;
        return ("Message 'sent' at " + format);
    }

    messageEvent(String phone, String message) {
        this.phone = phone;
        this.message = message;
        this.timestamp = getNow();
        boolean sendStatus = false;
    }

    public String getTimestamp() {
        return timestamp;
    }


    public String getPhone() {
        return phone;
    }

    public String getMessage() {
        return message;
    }



}
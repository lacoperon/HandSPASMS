package com.handspasms.handspasms;

/**
 * Created by Elliot on 2/13/16.
 */
public class notValidPhoneNumberException extends Exception {

    public notValidPhoneNumberException(){
        super();
    }

    public notValidPhoneNumberException(String message){
        super(message);
    }
}
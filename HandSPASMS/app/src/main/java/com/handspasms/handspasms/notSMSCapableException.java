package com.handspasms.handspasms;

/**
 * Created by Elliot on 2/13/16.
 */
public class notSMSCapableException extends Exception {

    public notSMSCapableException(){
        super();
    }

    public notSMSCapableException(String message){
        super(message);
    }
}
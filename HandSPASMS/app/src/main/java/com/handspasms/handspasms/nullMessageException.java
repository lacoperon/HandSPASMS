package com.handspasms.handspasms;

/**
 * Created by Elliot on 2/13/16.
 */
public class nullMessageException extends Exception {

    public nullMessageException(){
        super();
    }

    public nullMessageException(String message){
        super(message);
    }
}
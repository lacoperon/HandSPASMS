package com.handspasms.handspasms;

/**
 * Created by Elliot on 2/13/16.
 */
public class noSIMCardException extends Exception {

    public noSIMCardException(){
        super();
    }

    public noSIMCardException(String message){
        super(message);
    }
}
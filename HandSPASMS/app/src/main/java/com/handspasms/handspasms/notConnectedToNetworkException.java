package com.handspasms.handspasms;

/**
 * Created by Elliot on 2/13/16.
 */
public class notConnectedToNetworkException extends Exception {

    public notConnectedToNetworkException(){
        super();
    }

    public notConnectedToNetworkException(String message){
        super(message);
    }
}
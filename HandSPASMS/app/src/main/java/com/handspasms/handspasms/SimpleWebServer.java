/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.handspasms.handspasms;

import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Implementation of a very basic HTTP server. The contents are loaded from the assets folder. This
 * server handles one request at a time. It only supports GET method.
 */
public class SimpleWebServer implements Runnable {

    private static final String TAG = "SimpleWebServer";
    private final MainActivity cl;

    /**
     * The port number we listen to
     */
    private final int mPort;

    /**
     * {@link android.content.res.AssetManager} for loading files to serve.
     */
    private final AssetManager mAssets;

    /**
     * True if the server is running.
     */
    private boolean mIsRunning;

    /**
     * The {@link java.net.ServerSocket} that we listen to.
     */
    private ServerSocket mServerSocket;

    /**
     * WebServer constructor.
     */
    public SimpleWebServer(int port, AssetManager assets, MainActivity cl_) {
        mPort = port;
        mAssets = assets;
        cl = cl_;
    }

    /**
     * This method starts the web server listening to the specified port.
     */
    public void start() {
        mIsRunning = true;
        new Thread(this).start();
    }

    /**
     * This method stops the web server
     */
    public void stop() {
        try {
            mIsRunning = false;
            if (null != mServerSocket) {
                mServerSocket.close();
                mServerSocket = null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing the server socket.", e);
        }
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(mPort);
            while (mIsRunning) {
                Socket socket = mServerSocket.accept();
                handle(socket);
                socket.close();
            }
        } catch (SocketException e) {
            // The server was stopped; ignore.
        } catch (IOException e) {
            Log.e(TAG, "Web server error", e);
        }
    }

    /**
     * Respond to a request from a client.
     *
     * @param socket The client socket.
     * @throws IOException
     */
    private void handle(Socket socket) throws IOException {
        BufferedReader reader = null;
        PrintStream output = null;
        try {
            // Read HTTP headers and parse out the route.
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Output stream that we send the response to
            output = new PrintStream(socket.getOutputStream());
            String line;
            while (!TextUtils.isEmpty(line = reader.readLine())) {
                if (line.startsWith("GET /")) {
                    int pos = 0;
                    int tempPos;
                    String phone_number = "";
                    String message = "";

                    // Extract the parameters
                    while(  ((tempPos = line.indexOf("?", pos)) != -1)
                         || ((tempPos = line.indexOf("&", pos)) != -1)){
                        pos = tempPos;
                        int paramEndPos = line.indexOf("=", pos);
                        int nextAmpPos  = line.indexOf("&", paramEndPos);
                        if(paramEndPos == -1) {
                            Log.e(TAG, "Error! Malformed GET request");
                            writeServerError(output);
                            return;
                        }

                        String param = line.substring(pos+1, paramEndPos);
                        String value;
                        if(nextAmpPos != -1){
                            value = line.substring(paramEndPos+1, nextAmpPos);
                            pos   = nextAmpPos;
                        } else {
                            int nextSpacePos = line.indexOf(" ", paramEndPos);
                            pos   = nextSpacePos;
                            if (nextSpacePos != -1) {
                                value = line.substring(paramEndPos+1, nextSpacePos);
                            } else {
                                Log.e(TAG, "Error! Bad GET request");
                                writeServerError(output);
                                return;
                            }
                        }

                        switch(param){
                            case "number":  phone_number = value; break;
                            case "message": message = value.replace("%20", " "); break;
                            default:
                                Log.e(TAG, "Error! Unknown parameter" + param);
                                writeServerError(output);
                                return;
                        }
                    }
                    if(phone_number.equals("") || message.equals("")) {
                        Log.e(TAG, "Error! Missing parameters");
                        writeServerError(output);
                        return;
                    }

                    // Send the message (from the UI thread)
                    final String finalMessage      = message;
                    final String finalPhone_number = phone_number;
                    cl.runOnUiThread(new Runnable() {
                        public void run() {
                            cl.sendSMSMessage(finalMessage, finalPhone_number);
                        }
                    });
                    break;
                }
            }

            // Send out the content.
            output.println("HTTP/1.0 200 OK");
            output.println("Content-Type: text/html");
            output.println();
            output.flush();
        } finally {
            if (null != output) {
                output.close();
            }
            if (null != reader) {
                reader.close();
            }
        }
    }

    /**
     * Writes a server error response (HTTP/1.0 500) to the given output stream.
     *
     * @param output The output stream.
     */
    private void writeServerError(PrintStream output) {
        output.println("HTTP/1.0 500 Internal Server Error");
        output.flush();
    }
}

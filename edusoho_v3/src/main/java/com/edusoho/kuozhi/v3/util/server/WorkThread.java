package com.edusoho.kuozhi.v3.util.server;

import android.util.Log;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import java.io.IOException;

/**
 * Created by howzhi on 14-10-24.
 */
public class WorkThread extends Thread {

    private final HttpService httpservice;
    private final HttpServerConnection conn;

    public WorkThread(HttpService httpservice, HttpServerConnection conn) {
        super();
        this.httpservice = httpservice;
        this.conn = conn;
    }

    @Override
    public void run() {
        HttpContext context = new BasicHttpContext();
        try {
            while (!Thread.interrupted() && this.conn.isOpen()) {
                this.httpservice.handleRequest(this.conn, context);
            }
        } catch (ConnectionClosedException ex) {
            System.err.println("Client closed connection");
        } catch (IOException ex) {
            ex.printStackTrace();
            System.err.println("I/O error: " + ex.getMessage());
        } catch (HttpException ex) {
            System.err.println("Unrecoverable HTTP protocol violation: " + ex.getMessage());
        } finally {
            try {
                this.conn.shutdown();
                this.conn.close();
            } catch (IOException ignore) {
            }
        }

        Log.d("WorkThread", "close");
    }

}
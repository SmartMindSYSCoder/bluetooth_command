package com.sm.bluetooth_command;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayDeque;

public class TerminalHelper implements ServiceConnection, SerialListener {
    public enum Connected { False, Pending, True }

    private SerialService service;
    private boolean initialStart = true;
    public Connected connected = Connected.False;
    final static String newline = "\r\n";

    public static String deviceAddress="";


    private final   Activity activity;
    private final   Context applicationContext;

    TerminalHelper( Activity activity, Context applicationContext){

        this.activity=activity;
        this.applicationContext =applicationContext;
    };


    public void init(){

        activity.bindService(new Intent(activity, SerialService.class), this, Context.BIND_AUTO_CREATE);



        if(service != null)
            service.attach(this);
        else
            activity.startService(new Intent(activity, SerialService.class)); // prevents service destroy on unbind from recreated activity caused by orientation change


    }

    public boolean isInitialized(){


        return  service !=null;

    }

    public boolean isConnected(){


        return  connected == Connected.True;

    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);



        status(" *******************    service connected    ********************");

//        if(initialStart ) {
//            initialStart = false;
//
//            connect();
//        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;
    }



    public void connect(String deviceAddress ) {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
              status("connecting...");
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(applicationContext, device);
            service.connect(socket);




        } catch (Exception e) {
            status("onSerialConnectError   *************");
             status("connection failed: " + e.getMessage());

            onSerialConnectError(e);
        }
    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
    }

    public void send(String str) {
        if(connected != Connected.True) {
            Toast.makeText(applicationContext, "not connected", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String msg;
            byte[] data;

                msg = str;
                data = (str + newline).getBytes();

            service.write(data);
        } catch (Exception e) {
            onSerialIoError(e);
        }
    }



    @Override
    public void onSerialConnect() {
         status("connected");
        connected = Connected.True;
    }

    @Override
    public void onSerialConnectError(Exception e) {
         status("connection failed: " + e.getMessage());
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
       // ArrayDeque<byte[]> datas = new ArrayDeque<>();
       // datas.add(data);
     //   receive(datas);
    }

    public void onSerialRead(ArrayDeque<byte[]> datas) {
      //  receive(datas);
    }

    @Override
    public void onSerialIoError(Exception e) {
           status("connection lost: " + e.getMessage());
        disconnect();
    }


    public void onDestroy() {
        if (connected != Connected.False)
            disconnect();
        activity.stopService(new Intent(activity, SerialService.class));
    }
   private void status(String msg){


        Log.d("status",msg);
   }
}

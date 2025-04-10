package com.sm.bluetooth_command;

import java.util.ArrayDeque;

interface SerialListener {
    void onSerialConnect      ();
    void onSerialConnectError (Exception e);
    void onSerialRead         (byte[] data);                // socket -> service
    void onSerialRead         (ArrayDeque<byte[]> datas);   // service -> UI thread
    void onSerialIoError      (Exception e);
}

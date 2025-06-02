package com.sm.bluetooth_command;

import androidx.annotation.NonNull;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;
import android.os.Handler;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import android.util.Log;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import org.json.JSONObject;
/** BluetoothCommandPlugin */
public class BluetoothCommandPlugin implements FlutterPlugin, MethodCallHandler ,ActivityAware{
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private MethodChannel channel;
  final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
  private MethodChannel.Result result = null;
  public Context applicationContext;
  public Activity activity;
  private  PermissionHelper permissionHelper;


  private BluetoothAdapter bluetoothAdapter;
  private BluetoothSocket bluetoothSocket;
  private OutputStream outputStream;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "bluetooth_command");
    channel.setMethodCallHandler(this);

    this.applicationContext = flutterPluginBinding.getApplicationContext();

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    this.result=result;
    


    if (call.method.equals("sendCommand")) {

      final Map<String,Object> arguments=call.arguments();
      String macAddressOrName= (String) arguments.get("macAddressOrName");
      boolean   connectByName= (boolean) arguments.get("connectByName");



      String   command= (String) arguments.get("command");


      if(permissionHelper.isPermissionsGranted()){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
          showToast("Bluetooth is not supported on this device");
          return;
        }

        if (!bluetoothAdapter.isEnabled()) {
          showToast("Please enable Bluetooth");
          return;
        }



        if(connectByName){

          if(macAddressOrName ==null || macAddressOrName.isEmpty()){

            showToast("please give a valid device name");

            return;
          }

          scanAndConnectToDeviceByName(macAddressOrName, command);
        }

        else {
          connect(macAddressOrName, command);
        }



      }
      else{

        permissionHelper.checkPermissions();

      }



    }
   else if (call.method.equals("getBondedDevices")) {



      if(permissionHelper.isPermissionsGranted()){
        getBondedDevices();

      }
      else{

        permissionHelper.checkPermissions();

      }



    }
  else  if (call.method.equals("checkPermission")) {


        permissionHelper.checkPermissions();



    }





    else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }




  @Override
  public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
    // TODO: your plugin is now attached to an Activity
//    this.activity = activityPluginBinding.getActivity();
    this.activity = activityPluginBinding.getActivity();
//    this.applicationContext = activityPluginBinding.getApplicationContext();
    permissionHelper = new PermissionHelper(activity, applicationContext);


  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // This call will be followed by onReattachedToActivityForConfigChanges().
  }

  @Override
  public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
  }

  @Override
  public void onDetachedFromActivity() {

  }



  private void connect(String macAddress, String command) {
    try {
      BluetoothDevice hc05Device = bluetoothAdapter.getRemoteDevice(macAddress);
      bluetoothSocket = hc05Device.createRfcommSocketToServiceRecord(mUUID);

      bluetoothSocket.connect();

      // Connection successful
      outputStream = bluetoothSocket.getOutputStream();
      showToast("Connected");

      // Optionally, send a test command
      sendCommand(command);
    } catch (IOException e) {
      // Connection failed
      showToast("Failed to connect ");
     // e.printStackTrace();
    }
  }

  private void sendCommand(String command) {
    final  String newline_crlf = "\r\n";

    try {
      if (outputStream != null) {

        byte[] data;

        data = (command + newline_crlf).getBytes();

        outputStream.write(data);
       // outputStream.flush();

        //      // Close the output stream
      if (outputStream != null) {
        outputStream.close();
      }

      // Close the Bluetooth socket
      if (bluetoothSocket != null && bluetoothSocket.isConnected()) {
        bluetoothSocket.close();
      }


        showToast("Command sent: " + command);
      }
    } catch (IOException e) {
      showToast("Error sending command");
    //  e.printStackTrace();
    }
  }



//  protected  void sendCommand(String macAddress, String command){
//
//
//
//
//    // Initialize Bluetooth Adapter
//    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
//
//// Get remote Bluetooth device
//    BluetoothDevice hc05 = btAdapter.getRemoteDevice(macAddress);
//
//// Initialize Bluetooth socket
//    BluetoothSocket btSocket = null;
//
//    try {
//      // Create and connect the Bluetooth socket
//      btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
//      btSocket.connect();
//
//      // Obtain the output stream
//      OutputStream outputStream = btSocket.getOutputStream();
//      showToast("Connected");
//
//      // Write the command
//      outputStream.write(command.getBytes());
//
//      // Close the output stream
//      if (outputStream != null) {
//        outputStream.close();
//      }
//
//      // Close the Bluetooth socket
//      if (btSocket != null && btSocket.isConnected()) {
//        btSocket.close();
//      }
//
//      // Indicate success
//      result.success(1);
//
//    } catch (IOException e) {
//      // Handle exceptions
//     // e.printStackTrace(); // Log the error for debugging purposes
//      result.success(0);
//    } finally {
//      // Ensure cleanup in case of exception
//      if (btSocket != null) {
//        try {
//          if (btSocket.isConnected()) {
//            btSocket.close();
//          }
//        } catch (IOException closeException) {
//          closeException.printStackTrace(); // Log any errors during cleanup
//        }
//      }
//    }
//
//
//
////    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
////
////    BluetoothDevice hc05 = btAdapter.getRemoteDevice(macAddress);
////
////
////    BluetoothSocket btSocket = null;
////
//////    Log.i("macAddress",macAddress);
////
////
////    try {
////      btSocket = hc05.createRfcommSocketToServiceRecord(mUUID);
////      btSocket.connect();
////
////      OutputStream outputStream = btSocket.getOutputStream();
////
////      outputStream.write(command.getBytes());
////      outputStream.close();
////      btSocket.close();
////      result.success(1);
////
////    } catch (IOException e) {
//////      e.printStackTrace();
////      result.success(0);
////
////    }
////
////
//
//
//
//
//
//  }
  protected  void getBondedDevices(){
    List<String> devList = new ArrayList<String>();

    try {



    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    Set<BluetoothDevice> bondedDevices = btAdapter.getBondedDevices();

    for (BluetoothDevice device : bondedDevices) {

      HashMap<String, Object> map = new HashMap<>();
      map.put("macAddress", device.getAddress());
      map.put("name", device.getName());
      JSONObject json = new JSONObject(map);

      devList.add(json.toString());
    }

//    HashMap<String, Object> response = new HashMap<String, Object>();
//    response.put("devices", devList);

    result.success(devList);


    } catch (Exception e) {
//      e.printStackTrace();
      result.success(devList);

    }

  }




  private void scanAndConnectToDeviceByName(String deviceName,String command) {
    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
    BluetoothDevice targetDevice = null;

    if (pairedDevices != null && !pairedDevices.isEmpty()) {
      for (BluetoothDevice device : pairedDevices) {
        if (deviceName.equals(device.getName())) {
          targetDevice = device;
          break;
        }
      }
    }

    if (targetDevice != null) {
      connectToDevice(targetDevice,command);
    } else {
      showToast( deviceName+" device not found.");
    }
  }
  private void connectToDevice(BluetoothDevice device,String command) {
//    UUID uuid = device.getUuids()[0].getUuid();

    try {
      bluetoothSocket = device.createRfcommSocketToServiceRecord(mUUID);
      bluetoothSocket.connect();

      outputStream = bluetoothSocket.getOutputStream();
      showToast("Connected to " + device.getName());

//      new Handler().postDelayed(() -> sendCommand(command), 3000);
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        public void run() {
      sendCommand(command);
        }
      }, 3000);


    } catch (IOException e) {
      showToast("Connection Failed:\n"+e.getMessage());
    }
  }

  private void showToast(String message) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show();
  }
}

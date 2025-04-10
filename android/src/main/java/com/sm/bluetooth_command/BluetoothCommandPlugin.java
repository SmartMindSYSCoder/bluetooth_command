package com.sm.bluetooth_command;

import androidx.annotation.NonNull;
import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

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
  private  TerminalHelper terminalHelper;


  private BluetoothAdapter bluetoothAdapter;
  private BluetoothSocket bluetoothSocket;
  private OutputStream outputStream;


  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "bluetooth_command_new");
    channel.setMethodCallHandler(this);

    this.applicationContext = flutterPluginBinding.getApplicationContext();

  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    this.result=result;
    


    if (call.method.equals("init")) {




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





          terminalHelper.init();




      }
      else{

        permissionHelper.checkPermissions();

      }



    }

   else if (call.method.equals("connect")) {



      final Map<String,Object> arguments=call.arguments();
      String deviceName= (String) arguments.get("deviceName");





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





          connect(deviceName);




      }
      else{

        permissionHelper.checkPermissions();

      }



    }


   else if (call.method.equals("sendCommand")) {

      final Map<String,Object> arguments=call.arguments();



      String   command= (String) arguments.get("command");




      if( terminalHelper !=null && terminalHelper.isConnected()){
        terminalHelper.send(command);
      }



    }
   else if (call.method.equals("isConnected")) {



     result.success(terminalHelper.isConnected());




    }
   else if (call.method.equals("isInitialized")) {



     result.success(terminalHelper.isInitialized());




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

    terminalHelper = new TerminalHelper(activity, applicationContext);

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




  private void connect(String deviceName) {
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



      terminalHelper.connect(targetDevice.getAddress());


    } else {
      showToast( deviceName+" device not found.");
    }
  }





  private void showToast(String message) {
    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show();
  }
}

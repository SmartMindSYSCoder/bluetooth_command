
import 'dart:convert';

import 'package:flutter/services.dart';

import 'bonded_device_model.dart';


class SMBluetoothCommand {

  final _methodChannel = const MethodChannel('bluetooth_command_new');


  Future init() async{

     await _methodChannel.invokeMethod('init');


  }
  Future<bool> isConnected() async{

    return await _methodChannel.invokeMethod('isConnected');


  }
  Future<bool> isInitialized() async{

    return await _methodChannel.invokeMethod('isInitialized');


  }
  Future connect({required String deviceName}) async{


     _methodChannel.invokeMethod('init');

     await Future.delayed(const Duration(seconds: 1));

     if(await isInitialized()) {
       await _methodChannel.invokeMethod('connect', {'deviceName': deviceName});
     }
     else{
       print("not Initialized");
     }

  }

  Future sendCommand({required String command}) async{


    if(await isConnected()) {
      final result = await _methodChannel.invokeMethod(
          'sendCommand', {'command': command});
    }
    else{
      print("not connected");

    }

  }


  Future<void> checkPermission() async{
    final result = await _methodChannel.invokeMethod('checkPermission');

    return result;

  }
  Future<List<BoundedDeviceModel>> getBondedDevices() async{
    final result = await _methodChannel.invokeMethod('getBondedDevices');

    // print(result);

    if(result is List){

   return   result.map((e){

     return BoundedDeviceModel.fromJson(jsonDecode(e));

      }).toList();



    }


    return [];

  }

}

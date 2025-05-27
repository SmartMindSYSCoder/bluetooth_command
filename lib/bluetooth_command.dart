
import 'dart:convert';

import 'package:flutter/services.dart';

import 'bonded_device_model.dart';


class SMBluetoothCommand {

  final _methodChannel = const MethodChannel('bluetooth_command');


  Future<int?> sendCommand({required String macAddressOrName,required String command,bool connectByName=false}) async{
    final result = await _methodChannel.invokeMethod('sendCommand',{"macAddressOrName":macAddressOrName,'command':command,'connectByName':connectByName});

 return result;

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

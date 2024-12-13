import 'package:bluetooth_command/bonded_device_model.dart';
import 'package:flutter/material.dart';

import 'package:bluetooth_command/bluetooth_command.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _smBluetoothCommand = SMBluetoothCommand();

  @override
  void initState() {
    super.initState();
  }

  List<BoundedDeviceModel> boundedDevices=[];

  String result="";

  // Platform messages are asynchronous, so we initialize in an async method.

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Bluetooth Command example app',style: TextStyle(fontSize: 14),),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [



              TextButton(onPressed: ()async{

                if(boundedDevices.isEmpty){

                  debugPrint("no devices ");
                  return;

                }

             var res=   await _smBluetoothCommand.sendCommand(macAddress:boundedDevices.first.macAddress , command: "open") ;

             setState(() {
               result="result :$res";
             });

              }, child: const Text("Send Command")),


              const SizedBox(height: 30,),
              Text(result),
              const SizedBox(height: 30,),

              TextButton(onPressed: ()async{
                var res=   await _smBluetoothCommand.getBondedDevices() ;

                boundedDevices=res;


                setState(() {
                });

              }, child: const Text("get Bounded Devices")),
              const SizedBox(height: 30,),


              const Text("Devices List\n\n"),


              Expanded(
                child: ListView.builder(itemBuilder: (bc,index){

                  return ListTile(
                    title: Text(boundedDevices[index].name,style: const TextStyle(fontSize: 15),),
                    subtitle: Text(boundedDevices[index].macAddress,style: const TextStyle(fontSize: 12),),


                  );

                }),
              )

            ],
          ),
        ),
      ),
    );
  }
}

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


           await _smBluetoothCommand.checkPermission() ;


              }, child: const Text("Check Permission")),
              // TextButton(onPressed: ()async{
              //
              //   if(boundedDevices.isEmpty){
              //
              //     debugPrint("no devices ");
              //     return;
              //
              //   }
              //
              //
              //
              // }, child: const Text("Send Command")),


              const SizedBox(height: 30,),
              Text(result),
              const SizedBox(height: 10,),
              TextButton(onPressed: ()async{

                 _smBluetoothCommand.init();

                // var result=   await _smBluetoothCommand.connect(deviceName: 'HAGenie_Control_Device') ;
                var result=   await _smBluetoothCommand.connect(deviceName: 'HC-06') ;


             await   Future.delayed(const Duration(seconds: 1));

             bool isConnected=await _smBluetoothCommand.isConnected();
             print(isConnected);


              }, child: const Text("Connect")),
              const SizedBox(height: 10,),


              TextButton(onPressed: ()async{

                var result=   await _smBluetoothCommand.sendCommand(command: 'open') ;




              }, child: const Text("Open")),

              const SizedBox(height: 10,),

              TextButton(onPressed: ()async{

                var result=   await _smBluetoothCommand.sendCommand(command: 'close') ;




              }, child: const Text("Close")),

              //
              // TextButton(onPressed: ()async{
              //   boundedDevices=   await _smBluetoothCommand.getBondedDevices() ;
              //
              //
              //
              //   setState(() {
              //   });
              //
              // }, child: const Text("get Bounded Devices")),
              // const SizedBox(height: 30,),
              //
              //
              // const Text("Devices List\n\n"),
              //
              //
              // Expanded(
              //   child: ListView.builder(
              //       itemCount: boundedDevices.length,
              //       itemBuilder: (bc,index){
              //
              //     return ListTile(
              //       onTap: ()async{
              //         // var res=   await _smBluetoothCommand.sendCommand(macAddressOrName:boundedDevices[index].macAddress , command: "open") ;
              //         //
              //         // setState(() {
              //         //   result="result :$res";
              //         // });
              //       },
              //       title: Text(boundedDevices[index].name,style: const TextStyle(fontSize: 15),),
              //       subtitle: Text(boundedDevices[index].macAddress,style: const TextStyle(fontSize: 12),),
              //
              //
              //     );
              //
              //   }),
              // )

            ],
          ),
        ),
      ),
    );
  }
}

# bluetooth_command

A new Flutter project.

## Getting Started
This plugin for send command to bluetooth

To start use this plugin you must declare an instance of plugin like below:
        
     final _smBluetoothCommand = SMBluetoothCommand();

Then you can call checkPermission 

    await _smBluetoothCommand.checkPermission() ;

Now you can get bluetooth bounded devices like :

    boundedDevices=   await _smBluetoothCommand.getBondedDevices() ;

After that you can save any device to send its macAddress to the sendCommand method like below:


    if(boundedDevices.isEmpty){

    debugPrint("No devices found");
    return;

                               }

     var res=   await _smBluetoothCommand.sendCommand(macAddress:boundedDevices.first.macAddress , command: "open") ;



Note that sendCommand method will be accept tow parameters macAddress of device and the command that must be implement


I hope this clear 

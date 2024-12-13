import 'package:flutter_test/flutter_test.dart';
import 'package:bluetooth_command/bluetooth_command.dart';
import 'package:bluetooth_command/bluetooth_command_platform_interface.dart';
import 'package:bluetooth_command/bluetooth_command_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockBluetoothCommandPlatform
    with MockPlatformInterfaceMixin
    implements BluetoothCommandPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final BluetoothCommandPlatform initialPlatform = BluetoothCommandPlatform.instance;

  test('$MethodChannelBluetoothCommand is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelBluetoothCommand>());
  });

  test('getPlatformVersion', () async {
    SMBluetoothCommand bluetoothCommandPlugin = SMBluetoothCommand();
    MockBluetoothCommandPlatform fakePlatform = MockBluetoothCommandPlatform();
    BluetoothCommandPlatform.instance = fakePlatform;

    expect(await bluetoothCommandPlugin.getPlatformVersion(), '42');
  });
}

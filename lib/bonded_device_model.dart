class BoundedDeviceModel{

 final String macAddress,name;

 BoundedDeviceModel({this.macAddress='',this.name=''});



 factory BoundedDeviceModel.fromJson(dynamic map)=>BoundedDeviceModel(macAddress: map['macAddress'],name: map['name']);

 Map <String,String> toMap()=>{  "macAddress":macAddress,"name":name };


}
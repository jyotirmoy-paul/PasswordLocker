// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'password_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

PasswordModel _$PasswordModelFromJson(Map<String, dynamic> json) {
  return PasswordModel(
    firebaseKey: json['firebaseKey'] as String,
    lastUpdatedDate: json['lastUpdatedDate'] as String,
    loginId: json['loginId'] as String,
    password: json['password'] as String,
    serviceProvider: json['serviceProvider'] as String,
  );
}

Map<String, dynamic> _$PasswordModelToJson(PasswordModel instance) =>
    <String, dynamic>{
      'firebaseKey': instance.firebaseKey,
      'lastUpdatedDate': instance.lastUpdatedDate,
      'loginId': instance.loginId,
      'password': instance.password,
      'serviceProvider': instance.serviceProvider,
    };

import 'dart:developer';

class PasswordModel {
  String firebaseKey;
  String lastUpdatedDate;
  String loginId;
  String password;
  String serviceProvider;

  PasswordModel({
    this.firebaseKey,
    this.lastUpdatedDate,
    this.loginId,
    this.password,
    this.serviceProvider,
  });

  factory PasswordModel.fromJson(Map<String, dynamic> data) {
    return PasswordModel();
  }
}

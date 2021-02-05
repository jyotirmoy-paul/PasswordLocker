import 'package:json_annotation/json_annotation.dart';

part 'password_model.g.dart';

@JsonSerializable()
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

  factory PasswordModel.fromJson(Map<String, dynamic> data) =>
      _$PasswordModelFromJson(data);

  Map<String, dynamic> toJson() => _$PasswordModelToJson(this);
}

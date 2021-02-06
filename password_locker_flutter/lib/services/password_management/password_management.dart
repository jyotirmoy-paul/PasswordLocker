import 'dart:developer';

import 'package:encrypt/encrypt.dart';
import 'package:passwordlocker/models/password_addition_model.dart';
import 'package:passwordlocker/services/database/database.dart';
import 'package:passwordlocker/utils/constants.dart';

class PasswordManagement {
  static String _getModifiedPassword(String basePassword) {
    int len = basePassword.length;

    assert(len >= 7 && len <= 32);

    int neededChars = 32 - basePassword.length;
    for (int i = 0; i < neededChars; i++) basePassword += kDeveloperPassword[i];
    log('modified password: $basePassword');
    return basePassword;
  }

  static Future<void> encodeEncryption(
      PasswordAdditionModel passwordAdditionModel) async {
    String masterPassword = await Database.getMasterPassword();

    assert(passwordAdditionModel.password != null);
    assert(masterPassword != null);

    final key = Key.fromUtf8(_getModifiedPassword(masterPassword));
    passwordAdditionModel.password = Encrypter(AES(key, mode: AESMode.ecb))
        .encrypt(passwordAdditionModel.password)
        .base64;
  }

  static String decryptPassword({
    String cipherText,
    String key,
    String iv,
  }) {
    assert(cipherText != null);
    assert(key != null);
    log('password_management : cipherText: ${cipherText.trim()} : key: $key');

    final encKey = Key.fromUtf8(_getModifiedPassword(key));
    final encIV = iv == null ? null : IV.fromUtf8(iv);

    return Encrypter(AES(encKey, mode: AESMode.ecb)).decrypt(
      Encrypted.fromBase64(cipherText.trim()),
      iv: encIV,
    );
  }
}

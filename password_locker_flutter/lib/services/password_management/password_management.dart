import 'dart:developer';

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:encrypt/encrypt.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:passwordlocker/models/password_model.dart';
import 'package:passwordlocker/utils/constants.dart';

class PasswordManagement {
  static String _getModifiedPassword(String basePassword) {
    int neededChars = 32 - basePassword.length;
    for (int i = 0; i < neededChars; i++) basePassword += kDeveloperPassword[i];

    log('modified password: $basePassword');

    return basePassword;
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

  static Stream<List<PasswordModel>> getPasswords() =>
      FirebaseFirestore.instance
          .collection(kPasswordCollections)
          .doc(FirebaseAuth.instance.currentUser.uid)
          .collection(kUserPasswordsCollection)
          .snapshots()
          .map<List<PasswordModel>>(
        (querySnapshot) {
          log('querySnapshot: $querySnapshot');
          return querySnapshot.docs
              .map<PasswordModel>((queryDocumentSnapshot) =>
                  PasswordModel.fromJson(queryDocumentSnapshot.data()))
              .toList();
        },
      );
}

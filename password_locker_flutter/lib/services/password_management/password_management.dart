import 'dart:developer';

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:passwordlocker/models/password_model.dart';
import 'package:passwordlocker/utils/constants.dart';

class PasswordManagement {
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
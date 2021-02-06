import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:intl/intl.dart';
import 'package:passwordlocker/models/password_addition_model.dart';
import 'package:passwordlocker/models/password_model.dart';
import 'package:passwordlocker/utils/constants.dart';

import 'dart:developer';

class Backend {
  static CollectionReference _getReference() => FirebaseFirestore.instance
      .collection(kPasswordCollections)
      .doc(FirebaseAuth.instance.currentUser.uid)
      .collection(kUserPasswordsCollection);

  static Future<void> savePassword(
      PasswordAdditionModel passwordAdditionModel) async {
    assert(passwordAdditionModel.loginID != null);
    assert(passwordAdditionModel.password != null);
    assert(passwordAdditionModel.serviceProvider != null);

    DocumentReference docRef = _getReference().doc();

    final PasswordModel passwordModel = PasswordModel(
      firebaseKey: docRef.id,
      lastUpdatedDate: DateFormat('d MMM yy').format(DateTime.now()),
      loginId: passwordAdditionModel.loginID,
      password: passwordAdditionModel.password,
      serviceProvider: passwordAdditionModel.serviceProvider,
    );

    log('passwordModel : ${passwordModel.toJson()}');

    return docRef.set(passwordModel.toJson());
  }

  static Stream<List<PasswordModel>> getPasswords() =>
      _getReference().snapshots().map<List<PasswordModel>>(
        (querySnapshot) {
          log('querySnapshot: $querySnapshot');
          return querySnapshot.docs
              .map<PasswordModel>((queryDocumentSnapshot) =>
                  PasswordModel.fromJson(queryDocumentSnapshot.data()))
              .toList();
        },
      );
}

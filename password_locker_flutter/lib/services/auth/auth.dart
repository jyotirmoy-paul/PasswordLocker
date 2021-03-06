import 'dart:developer';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:passwordlocker/screens/dashboard/dashboard.dart';
import 'package:passwordlocker/screens/login_screen/login_screen.dart';

class Auth {
  Auth._();

  static Widget materialApp() => MaterialApp(
        debugShowCheckedModeBanner: false,
        home: StreamBuilder(
          stream: FirebaseAuth.instance.authStateChanges(),
          builder: (_, snapshot) {
            if (snapshot.connectionState == ConnectionState.waiting)
              return Center(
                child: CircularProgressIndicator(),
              );

            if (snapshot.hasData) return Dashboard();
            return LoginScreen();
          },
        ),
      );
}

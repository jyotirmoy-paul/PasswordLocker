import 'dart:developer';

import 'package:cloud_firestore/cloud_firestore.dart';
import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:passwordlocker/models/password_model.dart';
import 'package:passwordlocker/services/password_management/password_management.dart';
import 'package:passwordlocker/utils/constants.dart';

class DashboardEntryList extends StatelessWidget {
  void _signOut() => FirebaseAuth.instance.signOut();

  AppBar _buildAppBar() => AppBar(
        centerTitle: true,
        actions: [
          IconButton(
            icon: Icon(Icons.logout),
            onPressed: _signOut,
          ),
        ],
        title: Text(
          'Passwords',
          style: TextStyle(
            color: Colors.white,
            fontSize: 20.0,
          ),
        ),
      );

  Widget _buildTextWidget(
    String text, {
    Color color = Colors.blue,
  }) =>
      Center(
        child: Text(
          text,
          style: TextStyle(
            fontSize: 20.0,
            color: color,
          ),
        ),
      );

  Widget _emptyList() => Container();

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.blue.shade50,
      appBar: _buildAppBar(),
      body: StreamBuilder(
        stream: PasswordManagement.getPasswords(),
        builder: (_, snapshot) {
          log('dashboard_entry_list : snapshot.error: ${snapshot.error}');
          log('dashboard_entry_list : snapshot.data: ${snapshot.data}');

          if (snapshot.connectionState == ConnectionState.waiting)
            return Center(child: CircularProgressIndicator());

          if (!snapshot.hasData)
            return _buildTextWidget(
              'Something went wrong',
              color: Colors.red,
            );

          List<PasswordModel> passwordModels = snapshot.data;
          if (passwordModels.isEmpty) return _emptyList();

          return ListView(
            children: [],
          );
        },
      ),
    );
  }
}

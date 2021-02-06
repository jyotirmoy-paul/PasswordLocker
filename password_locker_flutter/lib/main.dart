import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:passwordlocker/services/auth/auth.dart';
import 'package:passwordlocker/services/database/database.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await Firebase.initializeApp();
  Database.init();
  runApp(HomePage());
}

class HomePage extends StatefulWidget {
  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) => Auth.materialApp();
}

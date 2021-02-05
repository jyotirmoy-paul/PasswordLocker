import 'package:flutter/material.dart';
import 'package:passwordlocker/models/password_model.dart';
import 'package:passwordlocker/screens/dashboard/dashboard_entry_list/dashboard_entry_list.dart';
import 'package:passwordlocker/screens/dashboard/dashboard_entry_view.dart';
import 'package:provider/provider.dart';

class Dashboard extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return ListenableProvider<ValueNotifier<PasswordModel>>(
      create: (_) => ValueNotifier<PasswordModel>(null),
      child: Row(
        children: [
          Expanded(
            child: DashboardEntryList(),
          ),
          Expanded(
            flex: 3,
            child: DashboardEntryView(),
          ),
        ],
      ),
    );
  }
}

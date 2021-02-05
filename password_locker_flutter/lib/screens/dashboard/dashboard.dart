import 'package:flutter/material.dart';
import 'package:passwordlocker/screens/dashboard/dashboard_entry_list.dart';
import 'package:passwordlocker/screens/dashboard/dashboard_entry_view.dart';

class Dashboard extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: DashboardEntryList(),
        ),
        Expanded(
          flex: 3,
          child: DashboardEntryView(),
        ),
      ],
    );
  }
}

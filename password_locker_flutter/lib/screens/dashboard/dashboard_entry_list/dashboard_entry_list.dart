import 'dart:developer';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:passwordlocker/models/password_addition_model.dart';
import 'package:passwordlocker/models/password_model.dart';
import 'package:passwordlocker/screens/dashboard/dashboard_entry_list/add_password_dialog.dart';
import 'package:passwordlocker/services/backend/backend.dart';
import 'package:passwordlocker/services/password_management/password_management.dart';
import 'package:provider/provider.dart';

class DashboardEntryList extends StatelessWidget {
  void _signOut() => FirebaseAuth.instance.signOut();

  void _addNewPassword(BuildContext context) async {
    PasswordAdditionModel passwordAdditionModel =
        await showDialog<PasswordAdditionModel>(
      context: context,
      builder: (_) => AddPasswordDialog(),
    );

    if (passwordAdditionModel == null) return;

    await PasswordManagement.encodeEncryption(passwordAdditionModel);
    await Backend.savePassword(passwordAdditionModel);
  }

  AppBar _buildAppBar() => AppBar(
        backgroundColor: Colors.blueAccent,
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

  Widget _emptyWidget({
    bool searchEmpty = false,
  }) =>
      searchEmpty
          ? Center(
              child: Text(
                'We tried hard, but nothing matched your search\n\n:-(',
                textAlign: TextAlign.center,
                style: TextStyle(
                  color: Colors.redAccent,
                  fontSize: 20.0,
                  fontWeight: FontWeight.w800,
                ),
              ),
            )
          : Center(
              child: Text(
                "You don't have any passwords yet\n\nAdding one is as easy as 1 + 2 x 3 = 7 ...or 9?",
                textAlign: TextAlign.center,
                style: TextStyle(
                  color: Colors.blueAccent,
                  fontSize: 20.0,
                  fontWeight: FontWeight.w800,
                ),
              ),
            );

  Widget _buildPasswordEntryItem(PasswordModel model, BuildContext context) =>
      InkWell(
        onTap: () => Provider.of<ValueNotifier<PasswordModel>>(
          context,
          listen: false,
        ).value = model,
        child: Card(
          elevation: 1.0,
          color: Colors.white,
          child: Padding(
            padding: const EdgeInsets.symmetric(
              horizontal: 20.0,
              vertical: 10.0,
            ),
            child: Row(
              children: [
                /* avatar */
                Container(
                  width: 40.0,
                  height: 40.0,
                  padding: EdgeInsets.all(10.0),
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: Colors.blueAccent,
                  ),
                  child: Center(
                    child: Text(
                      model.serviceProvider.substring(0, 1).toUpperCase(),
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 18.0,
                      ),
                    ),
                  ),
                ),

                /* main body */
                Expanded(
                  child: Container(
                    margin: const EdgeInsets.symmetric(horizontal: 10.0),
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        /* displaying service provider */
                        Text(
                          model.serviceProvider,
                          style: TextStyle(
                            color: Colors.black,
                            fontSize: 20.0,
                            fontWeight: FontWeight.w800,
                          ),
                        ),
                        const SizedBox(
                          height: 5.0,
                        ),

                        /* displaying login ID */
                        Text(
                          model.loginId,
                          style: TextStyle(
                            color: Colors.black.withOpacity(0.60),
                            fontSize: 15.0,
                          ),
                        ),
                      ],
                    ),
                  ),
                ),

                /* last updated date */
                Text(
                  model.lastUpdatedDate,
                  style: TextStyle(
                    color: Colors.blue,
                    fontSize: 15.0,
                  ),
                ),
              ],
            ),
          ),
        ),
      );

  Widget _buildListView(
    List<PasswordModel> passwordModels,
    BuildContext context,
  ) =>
      ListView(
        physics: BouncingScrollPhysics(),
        children: passwordModels
            .map((model) => _buildPasswordEntryItem(model, context))
            .toList(),
      );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.blue.shade50,
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.add),
        onPressed: () => _addNewPassword(context),
      ),
      appBar: _buildAppBar(),
      body: StreamBuilder(
        stream: Backend.getPasswords(),
        builder: (_, snapshot) {
          if (snapshot.connectionState == ConnectionState.waiting)
            return Center(child: CircularProgressIndicator());

          if (!snapshot.hasData)
            return _buildTextWidget(
              'Something went wrong',
              color: Colors.red,
            );

          List<PasswordModel> passwordModels = snapshot.data;
          if (passwordModels.isEmpty) return _emptyWidget();

          return ListenableProvider<ValueNotifier<String>>(
            create: (_) => ValueNotifier<String>(''),
            builder: (context, _) => Column(
              children: [
                /* text field to allow searching among the passwords */
                Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 20.0,
                    vertical: 10.0,
                  ),
                  child: TextField(
                    onChanged: (String s) => Provider.of<ValueNotifier<String>>(
                      context,
                      listen: false,
                    ).value = s,
                    style: TextStyle(
                      color: Colors.blue,
                      fontSize: 20.0,
                    ),
                    decoration: InputDecoration(
                      hintText: 'Search in your passwords',
                      hintStyle: TextStyle(
                        color: Colors.blue.withOpacity(
                          0.70,
                        ),
                      ),
                      prefixIcon: Icon(
                        Icons.search,
                        color: Colors.blue,
                        size: 28.0,
                      ),
                    ),
                  ),
                ),

                /* show list of passwords */

                Flexible(
                  child: Consumer<ValueNotifier<String>>(
                    builder: (_, vnQuery, __) {
                      List<PasswordModel> _filteredList = <PasswordModel>[];
                      String query = vnQuery.value.toLowerCase();

                      if (query.isNotEmpty) {
                        for (PasswordModel model in passwordModels) {
                          if (model.serviceProvider
                                  .toLowerCase()
                                  .contains(query) ||
                              model.loginId.toLowerCase().contains(query))
                            _filteredList.add(model);
                        }
                      } else {
                        _filteredList.addAll(passwordModels);
                      }

                      if (_filteredList.isEmpty)
                        return _emptyWidget(
                          searchEmpty: true,
                        );

                      return _buildListView(
                        _filteredList,
                        context,
                      );
                    },
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}

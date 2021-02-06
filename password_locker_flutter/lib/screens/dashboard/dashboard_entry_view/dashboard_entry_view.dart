import 'dart:developer' as dev;
import 'dart:math';

import 'package:clipboard/clipboard.dart';
import 'package:flutter/material.dart';
import 'package:passwordlocker/models/password_addition_model.dart';
import 'package:passwordlocker/models/password_model.dart';
import 'package:passwordlocker/screens/dashboard/dashboard_entry_list/add_password_dialog.dart';
import 'package:passwordlocker/services/backend/backend.dart';
import 'package:passwordlocker/services/database/database.dart';
import 'package:passwordlocker/services/password_management/password_management.dart';
import 'package:passwordlocker/utils/constants.dart';
import 'package:provider/provider.dart';

enum DashboardEntryViewState {
  PASSWORD,
  ERROR,
}

class DashboardEntryView extends StatelessWidget {
  void _onDeletePress(PasswordModel passwordModel, BuildContext context) async {
    // ask for confirmation

    bool confirm = await showDialog<bool>(
      context: context,
      builder: (context) => Dialog(
        child: Padding(
          padding: const EdgeInsets.symmetric(
            horizontal: 20.0,
            vertical: 10.0,
          ),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Text(
                'Are you sure you don\'t need this password anymore?',
                style: TextStyle(
                  color: Colors.red,
                  fontSize: 20.0,
                ),
              ),
              const SizedBox(
                height: 20.0,
              ),
              MaterialButton(
                color: Colors.red,
                onPressed: () => Navigator.pop(context, true),
                child: Padding(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 20.0,
                    vertical: 10.0,
                  ),
                  child: Text(
                    'Confirm',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 20.0,
                    ),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );

    if (confirm == null || confirm == false) return;

    await Backend.deletePassword(passwordModel);

    // set the current view to null
    Provider.of<ValueNotifier<PasswordModel>>(
      context,
      listen: false,
    ).value = null;
  }

  void _onEditPress(PasswordModel pm, BuildContext context) async {
    PasswordModel passwordModel = PasswordModel.copy(pm);

    /* this dialog directly mutates on the password model */
    PasswordAdditionModel passwordAdditionModel =
        await showDialog<PasswordAdditionModel>(
      context: context,
      builder: (_) => AddPasswordDialog(
        passwordAdditionModel: PasswordAdditionModel(
          serviceProvider: passwordModel.serviceProvider,
          loginID: passwordModel.loginId,
        ),
      ),
    );

    if (passwordAdditionModel == null) return;

    await PasswordManagement.encodeEncryption(passwordAdditionModel);

    // todo: shall we allow other changes (phone number / service provider) ?
    passwordModel.password = passwordAdditionModel.password;

    await Backend.editPassword(passwordModel);
    Provider.of<ValueNotifier<PasswordModel>>(
      context,
      listen: false,
    ).value = passwordModel;
  }

  void _setState(
    DashboardEntryViewState state,
    BuildContext context,
  ) =>
      Provider.of<ValueNotifier<DashboardEntryViewState>>(
        context,
        listen: false,
      ).value = state;

  Widget _buildNothingSelected(BuildContext context) {
    Random randomizer = Random();
    int imageIdx = randomizer.nextInt(kEmptyPasswordsImage.length);
    int textIdx = randomizer.nextInt(kEmptyPasswordsTexts.length);

    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        SizedBox(
          width: MediaQuery.of(context).size.width * 0.30,
          child: Image.asset(
            kEmptyPasswordsImage[imageIdx],
          ),
        ),
        const SizedBox(
          height: 30.0,
        ),
        Text(
          kEmptyPasswordsTexts[textIdx],
          style: TextStyle(
            color: Colors.blueAccent,
            fontSize: 20.0,
            fontWeight: FontWeight.w500,
          ),
        ),
      ],
    );
  }

  Widget _buildBottomView(
    DashboardEntryViewState state,
    BuildContext context, {
    PasswordModel passwordModel,
  }) {
    switch (state) {
      case DashboardEntryViewState.PASSWORD:
        final String password = Provider.of<ValueNotifier<String>>(
              context,
              listen: false,
            ).value?.trim() ??
            '';

        return Row(
          key: ValueKey(DashboardEntryViewState.PASSWORD),
          children: [
            IconButton(
              icon: Icon(
                Icons.copy,
                color: Colors.green,
              ),
              onPressed: () => FlutterClipboard.copy(password),
            ),
            const SizedBox(
              width: 10.0,
            ),
            Text(
              password,
              style: TextStyle(
                color: Colors.green,
                fontSize: 20.0,
              ),
            ),
            const SizedBox(
              width: 10.0,
            ),
            IconButton(
              icon: Icon(
                Icons.block,
                color: Colors.red,
              ),
              onPressed: () => _setState(null, context),
            ),
          ],
        );

      case DashboardEntryViewState.ERROR:
        return Row(
          key: ValueKey(DashboardEntryViewState.ERROR),
          children: [
            IconButton(
              icon: Icon(
                Icons.repeat,
                color: Colors.red,
              ),
              onPressed: () => _setState(
                null,
                context,
              ),
            ),
            const SizedBox(
              width: 10.0,
            ),
            Text(
              'Wrong master password',
              style: TextStyle(
                color: Colors.red,
                fontSize: 20.0,
              ),
            ),
          ],
        );
    }

    String enteredKey = '';
    return Row(
      key: ValueKey('prompt'),
      children: [
        Flexible(
          child: TextField(
            obscureText: true,
            style: TextStyle(
              color: Colors.blue,
              fontSize: 20.0,
            ),
            onChanged: (String s) => enteredKey = s,
            decoration: InputDecoration(
              enabledBorder: OutlineInputBorder(
                borderSide: BorderSide(
                  color: Colors.blue,
                ),
              ),
              border: OutlineInputBorder(
                borderSide: BorderSide(
                  color: Colors.red,
                ),
              ),
              hintText: 'Master Password',
              hintStyle: TextStyle(
                color: Colors.blue.withOpacity(0.70),
              ),
            ),
          ),
        ),
        const SizedBox(
          width: 10.0,
        ),
        MaterialButton(
          color: Colors.green,
          child: Container(
            padding: const EdgeInsets.symmetric(
              horizontal: 20.0,
              vertical: 10.0,
            ),
            child: Text(
              'Decrypt',
              style: TextStyle(
                color: Colors.white,
                fontSize: 18.0,
              ),
            ),
          ),
          onPressed: () {
            try {
              String decryptedPassword = PasswordManagement.decryptPassword(
                cipherText: passwordModel.password,
                key: enteredKey,
              );

              Provider.of<ValueNotifier<String>>(
                context,
                listen: false,
              ).value = decryptedPassword;
              _setState(DashboardEntryViewState.PASSWORD, context);
            } catch (e) {
              print('dashboard_entry_view : error : $e');
              _setState(DashboardEntryViewState.ERROR, context);
            }
          },
        ),
      ],
    );
  }

  Widget _buildPasswordViewWidget(PasswordModel model, BuildContext context) =>
      Padding(
        key: ValueKey(model),
        padding: const EdgeInsets.symmetric(
          horizontal: 20.0,
        ),
        child: Card(
          color: Colors.white,
          elevation: 10.0,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(10.0),
          ),
          child: Container(
            decoration: BoxDecoration(
              color: Colors.blue.withOpacity(0.10),
              borderRadius: BorderRadius.circular(10.0),
            ),
            padding: const EdgeInsets.symmetric(
              horizontal: 20.0,
              vertical: 10.0,
            ),
            child: Row(
              children: [
                /* avatar */
                Container(
                  width: 50.0,
                  height: 50.0,
                  padding: EdgeInsets.all(10.0),
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: Colors.redAccent,
                  ),
                  child: Center(
                    child: Text(
                      model.serviceProvider.substring(0, 1).toUpperCase(),
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 20.0,
                      ),
                    ),
                  ),
                ),
                const SizedBox(width: 20.0),
                Expanded(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      /* service provider */
                      Text(
                        model.serviceProvider,
                        style: TextStyle(
                          color: Colors.black,
                          fontSize: 25.0,
                          fontWeight: FontWeight.w800,
                        ),
                      ),
                      const SizedBox(
                        height: 20.0,
                      ),

                      /* username */
                      Text(
                        model.loginId ?? 'login ID',
                        style: TextStyle(
                          color: Colors.black,
                          fontSize: 20.0,
                        ),
                      ),
                      const SizedBox(
                        height: 5.0,
                      ),

                      /* encrypted password / password */
                      Text(
                        model.password.trim() ?? 'password',
                        style: TextStyle(
                          color: Colors.redAccent,
                          fontSize: 20.0,
                        ),
                      ),
                      const SizedBox(
                        height: 20.0,
                      ),
                      ListenableProvider<
                          ValueNotifier<DashboardEntryViewState>>(
                        create: (_) => ValueNotifier<DashboardEntryViewState>(
                          null,
                        ),
                        builder: (context, _) =>
                            Consumer<ValueNotifier<DashboardEntryViewState>>(
                          builder: (_, vnState, __) => AnimatedSwitcher(
                            switchInCurve: Curves.easeInOut,
                            switchOutCurve: Curves.easeInOut,
                            transitionBuilder: (widget, animation) =>
                                SizeTransition(
                              sizeFactor: animation,
                              child: widget,
                            ),
                            duration: const Duration(milliseconds: 500),
                            child: Padding(
                              key: ValueKey(model),
                              padding: const EdgeInsets.symmetric(
                                vertical: 10.0,
                              ),
                              child: ListenableProvider<ValueNotifier<String>>(
                                create: (_) => ValueNotifier<String>(null),
                                builder: (context, _) => AnimatedSwitcher(
                                  switchInCurve: Curves.easeInOut,
                                  switchOutCurve: Curves.easeInOut,
                                  duration: const Duration(milliseconds: 200),
                                  child: _buildBottomView(
                                    vnState.value,
                                    context,
                                    passwordModel: model,
                                  ),
                                ),
                              ),
                            ),
                          ),
                        ),
                      ),
                      const SizedBox(
                        height: 20.0,
                      ),

                      /* edit and delete buttons */

                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          IconButton(
                            onPressed: () => _onEditPress(model, context),
                            icon: Icon(
                              Icons.edit,
                              color: Colors.blueAccent,
                            ),
                          ),
                          const SizedBox(
                            width: 20.0,
                          ),
                          IconButton(
                            onPressed: () => _onDeletePress(model, context),
                            icon: Icon(
                              Icons.delete,
                              color: Colors.redAccent,
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        ),
      );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Center(
        child: Consumer<ValueNotifier<PasswordModel>>(
          builder: (_, vnPasswordModel, __) => vnPasswordModel?.value == null
              ? _buildNothingSelected(context)
              : _buildPasswordViewWidget(
                  vnPasswordModel.value,
                  context,
                ),
        ),
      ),
    );
  }
}

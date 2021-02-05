import 'dart:math';

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:passwordlocker/models/password_model.dart';
import 'package:passwordlocker/services/password_management/password_management.dart';
import 'package:passwordlocker/utils/constants.dart';
import 'package:provider/provider.dart';

enum DashboardEntryViewState {
  PASSWORD,
  PROMPT,
  ERROR,
}

class DashboardEntryView extends StatelessWidget {
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
        return Row(
          children: [
            IconButton(
              icon: Icon(
                Icons.copy,
                color: Colors.green,
              ),
              onPressed: () => () {
                // TODO: COPY TO CLIPBOARD
              },
            ),
            const SizedBox(
              width: 10.0,
            ),
            Text(
              Provider.of<ValueNotifier<String>>(
                    context,
                    listen: false,
                  ).value?.trim() ??
                  '',
              style: TextStyle(
                color: Colors.green,
                fontSize: 20.0,
              ),
            ),
          ],
        );
      case DashboardEntryViewState.PROMPT:
        String enteredKey = '';
        return Row(
          children: [
            Flexible(
              child: TextField(
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
      case DashboardEntryViewState.ERROR:
        return Row(
          children: [
            IconButton(
              icon: Icon(
                Icons.repeat,
                color: Colors.red,
              ),
              onPressed: () => _setState(null, context),
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

    return MaterialButton(
      color: Colors.green,
      child: Container(
        padding: const EdgeInsets.symmetric(
          horizontal: 20.0,
          vertical: 10.0,
        ),
        child: Text(
          'Show',
          style: TextStyle(
            color: Colors.white,
            fontSize: 18.0,
          ),
        ),
      ),
      onPressed: () => _setState(
        DashboardEntryViewState.PROMPT,
        context,
      ),
    );
  }

  Widget _buildPasswordViewWidget(PasswordModel model, BuildContext context) =>
      Padding(
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
              color: Colors.blueAccent.withOpacity(0.10),
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
                        key: ValueKey(model),
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
                              padding: const EdgeInsets.symmetric(
                                vertical: 10.0,
                              ),
                              child: ListenableProvider<ValueNotifier<String>>(
                                create: (_) => ValueNotifier<String>(null),
                                builder: (context, _) => _buildBottomView(
                                  vnState.value,
                                  context,
                                  passwordModel: model,
                                ),
                              ),
                            ),
                          ),
                        ),
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

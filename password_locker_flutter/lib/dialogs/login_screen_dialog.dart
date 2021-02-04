import 'dart:developer';

import 'package:firebase_auth/firebase_auth.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

enum LoginScreenDialogState {
  LOADING,
  SUCCESS,
  ERROR,
}

class LoginScreenDialog extends StatelessWidget {
  final TextEditingController _textEditingController = TextEditingController();

  void setState(LoginScreenDialogState state, BuildContext context) =>
      Provider.of<ValueNotifier<LoginScreenDialogState>>(
        context,
        listen: false,
      ).value = state;

  void _sendOtp(BuildContext context) async {
    String phoneNumber = _textEditingController.text.trim();

    setState(LoginScreenDialogState.LOADING, context);

    try {
      ConfirmationResult result =
          await FirebaseAuth.instance.signInWithPhoneNumber(
        phoneNumber,
      );

      log('OTP send to $phoneNumber, Verification ID: ${result.verificationId}');
      setState(LoginScreenDialogState.SUCCESS, context);
    } catch (e) {
      log('error: $e');
      setState(LoginScreenDialogState.ERROR, context);
    }
  }

  Widget _intro() => Text(
        'Hey Stranger!',
        textAlign: TextAlign.center,
        style: TextStyle(
          color: Colors.blue,
          fontSize: 30.0,
          fontWeight: FontWeight.w800,
        ),
      );

  Widget _promise() => Text(
        'I promise, I will keep your number safe :-)',
        textAlign: TextAlign.center,
        style: TextStyle(
          color: Colors.green.withOpacity(0.80),
          fontSize: 15.0,
        ),
      );

  Widget _phoneNumberPrompt() => Text(
        'Phone Number',
        style: TextStyle(
          color: Colors.blue,
          fontSize: 15.0,
        ),
      );

  Widget _phoneNumberInput(BuildContext context) => Row(
        children: [
          // field
          Expanded(
            child: TextField(
              controller: _textEditingController,
              style: TextStyle(
                color: Colors.black,
                fontSize: 18.0,
              ),
              decoration: InputDecoration(
                border: InputBorder.none,
                hintText: '+91 860XXXXXXX',
                hintStyle: TextStyle(
                  color: Colors.black.withOpacity(0.50),
                ),
              ),
            ),
          ),

          // button
          MaterialButton(
            color: Colors.green,
            child: Container(
              padding: const EdgeInsets.symmetric(
                horizontal: 10.0,
                vertical: 5.0,
              ),
              child: Text(
                'Send OTP',
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 18.0,
                ),
              ),
            ),
            onPressed: () => _sendOtp(context),
          ),
        ],
      );

  Widget _buildBottomChild(LoginScreenDialogState state) {
    switch (state) {
      case LoginScreenDialogState.LOADING:
        return Align(
          alignment: Alignment.center,
          child: CircularProgressIndicator(
            valueColor: AlwaysStoppedAnimation<Color>(Colors.redAccent),
          ),
        );

      case LoginScreenDialogState.SUCCESS:
        return Container();

      case LoginScreenDialogState.ERROR:
        return Align(
          alignment: Alignment.center,
          child: Text(
            'Something went wrong',
            style: TextStyle(
              fontSize: 18.0,
              color: Colors.red,
            ),
          ),
        );
    }

    return Container();
  }

  @override
  Widget build(BuildContext _) {
    return ListenableProvider<ValueNotifier<LoginScreenDialogState>>(
      create: (_) => ValueNotifier<LoginScreenDialogState>(null),
      builder: (BuildContext context, _) => Dialog(
        child: Container(
          width: MediaQuery.of(context).size.width * 0.50,
          padding: const EdgeInsets.symmetric(
            horizontal: 10.0,
            vertical: 20.0,
          ),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            mainAxisSize: MainAxisSize.min,
            children: [
              _intro(),
              const SizedBox(
                height: 5.0,
              ),
              _promise(),
              Divider(
                height: 40.0,
                color: Colors.blue.withOpacity(0.50),
              ),
              _phoneNumberPrompt(),
              _phoneNumberInput(context),
              Consumer<ValueNotifier<LoginScreenDialogState>>(
                builder: (_, vnDialogState, __) => AnimatedSwitcher(
                  switchInCurve: Curves.easeInOut,
                  switchOutCurve: Curves.easeInOut,
                  transitionBuilder: (widget, animation) => SizeTransition(
                    sizeFactor: animation,
                    child: widget,
                  ),
                  duration: const Duration(milliseconds: 500),
                  child: Padding(
                    padding: const EdgeInsets.symmetric(
                      vertical: 10.0,
                    ),
                    child: _buildBottomChild(vnDialogState.value),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

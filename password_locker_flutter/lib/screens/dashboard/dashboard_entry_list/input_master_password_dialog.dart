import 'package:flutter/material.dart';
import 'package:passwordlocker/services/database/database.dart';
import 'package:provider/provider.dart';

class InputMasterPasswordDialog extends StatelessWidget {
  final masterPassword = <String>[''];

  @override
  Widget build(BuildContext context) {
    return Dialog(
      child: Padding(
        padding: const EdgeInsets.symmetric(
          horizontal: 20.0,
          vertical: 10.0,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              "We ask this only once.\nYour master password will be securely saved locally for future use.",
              style: TextStyle(
                color: Colors.blueAccent,
                fontWeight: FontWeight.w800,
                fontSize: 20.0,
              ),
            ),
            const SizedBox(
              height: 10.0,
            ),
            Text(
              "If you have used our service before, please keep the same master password to avoid confusion while decrypting your passwords",
              style: TextStyle(
                color: Colors.redAccent,
                fontSize: 15.0,
              ),
            ),
            const SizedBox(
              height: 20.0,
            ),
            ListenableProvider<ValueNotifier<bool>>(
              create: (_) => ValueNotifier(true),
              builder: (context, _) => Row(
                children: [
                  Flexible(
                    child: Consumer<ValueNotifier<bool>>(
                      builder: (_, vnObscureText, __) => TextField(
                        obscureText: vnObscureText.value,
                        style: TextStyle(
                          color: Colors.blue,
                          fontSize: 20.0,
                        ),
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
                          prefixIcon: Icon(
                            Icons.lock,
                            color: Colors.blueAccent,
                          ),
                        ),
                        onChanged: (String s) => masterPassword[0] = s,
                      ),
                    ),
                  ),
                  const SizedBox(
                    width: 10.0,
                  ),
                  Consumer<ValueNotifier<bool>>(
                    builder: (_, vnObscureText, __) => IconButton(
                      icon: Icon(
                        vnObscureText.value
                            ? Icons.visibility
                            : Icons.visibility_off,
                        color: vnObscureText.value ? Colors.red : Colors.green,
                      ),
                      onPressed: () {
                        ValueNotifier<bool> vn =
                            Provider.of<ValueNotifier<bool>>(
                          context,
                          listen: false,
                        );
                        vn.value = !vn.value;
                      },
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(
              height: 20.0,
            ),
            MaterialButton(
              color: Colors.green,
              child: Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: 20.0,
                  vertical: 10.0,
                ),
                child: Text(
                  'Save Locally',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 20.0,
                  ),
                ),
              ),
              onPressed: () async {
                String masterPass = masterPassword.first;
                if (masterPass.isEmpty) return;
                if (masterPass.length < 7 || masterPassword.length > 32) return;

                Database.setMasterPassword(masterPass);
                Navigator.pop(context, true);
              },
            ),
          ],
        ),
      ),
    );
  }
}

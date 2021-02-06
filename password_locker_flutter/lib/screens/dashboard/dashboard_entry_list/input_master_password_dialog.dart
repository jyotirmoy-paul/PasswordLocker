import 'package:flutter/material.dart';
import 'package:passwordlocker/services/database/database.dart';

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
              "We ask this only once.\nYour master password will be securely saved locally for use from next time.",
              style: TextStyle(
                color: Colors.blue,
                fontWeight: FontWeight.w800,
                fontSize: 20.0,
              ),
            ),
            const SizedBox(
              height: 20.0,
            ),
            TextField(
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
              ),
              onChanged: (String s) => masterPassword[0] = s,
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

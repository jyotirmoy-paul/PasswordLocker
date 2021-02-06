import 'package:flutter/material.dart';
import 'package:passwordlocker/models/password_addition_model.dart';
import 'package:passwordlocker/screens/dashboard/dashboard_entry_list/input_master_password_dialog.dart';
import 'package:passwordlocker/services/database/database.dart';
import 'package:provider/provider.dart';

class AddPasswordDialog extends StatelessWidget {
  final PasswordAdditionModel _passwordAdditionModel = PasswordAdditionModel();

  void _onSavePress(BuildContext context) async {
    if (_passwordAdditionModel.password == null ||
        _passwordAdditionModel.password.isEmpty) return;
    if (_passwordAdditionModel.serviceProvider == null ||
        _passwordAdditionModel.serviceProvider.isEmpty) return;
    if (_passwordAdditionModel.loginID == null ||
        _passwordAdditionModel.loginID.isEmpty) return;

    // check for saved master password locally, if not found prompt for the master password
    String masterPassword = await Database.getMasterPassword();

    if (masterPassword == null) {
      bool done = await showDialog<bool>(
        context: context,
        builder: (_) => InputMasterPasswordDialog(),
      );

      if (done)
        Navigator.pop(context, _passwordAdditionModel);
      else
        Navigator.pop(context);
    } else {
      Navigator.pop(context, _passwordAdditionModel);
    }
  }

  Widget _buildInputWidget({
    @required String title,
    @required String hintText,
    @required IconData prefixIcon,
    @required Function(String) onChanged,
    bool visibilityStateChangeReq = false,
  }) =>
      Container(
        margin: EdgeInsets.symmetric(
          vertical: 10.0,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            /* title */
            Text(
              title,
              style: TextStyle(
                color: Colors.black.withOpacity(0.70),
                fontSize: 15.0,
              ),
            ),
            const SizedBox(
              height: 5.0,
            ),

            /* this value notifier holds the variable - obscureText */
            ListenableProvider<ValueNotifier<bool>>(
              create: (_) => ValueNotifier(true),
              builder: (context, _) => Row(
                children: [
                  Flexible(
                    child: Consumer<ValueNotifier<bool>>(
                      builder: (_, vnObscureText, __) => TextField(
                        obscureText: vnObscureText.value,
                        onChanged: onChanged,
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
                          hintText: hintText,
                          prefixIcon: Icon(prefixIcon),
                        ),
                      ),
                    ),
                  ),
                  visibilityStateChangeReq
                      ? const SizedBox(
                          width: 10.0,
                        )
                      : const SizedBox.shrink(),
                  visibilityStateChangeReq
                      ? Consumer<ValueNotifier<bool>>(
                          builder: (_, vnObscureText, __) => IconButton(
                            icon: Icon(
                              vnObscureText.value
                                  ? Icons.visibility
                                  : Icons.visibility_off,
                              color: vnObscureText.value
                                  ? Colors.red
                                  : Colors.green,
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
                        )
                      : const SizedBox.shrink(),
                ],
              ),
            ),
          ],
        ),
      );

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
          children: [
            /* input the service provider */
            _buildInputWidget(
              title: 'Service Provider',
              hintText: 'Website / App name',
              prefixIcon: Icons.public,
              onChanged: (String s) =>
                  _passwordAdditionModel.serviceProvider = s,
            ),

            /* input the login id */
            _buildInputWidget(
              title: 'Login ID / Phone Number',
              hintText: 'Login Details',
              prefixIcon: Icons.assignment_ind,
              onChanged: (String s) => _passwordAdditionModel.loginID = s,
            ),

            /* input the password */
            _buildInputWidget(
              title: 'Password of the account',
              hintText: 'Password',
              prefixIcon: Icons.lock,
              onChanged: (String s) => _passwordAdditionModel.password = s,
              visibilityStateChangeReq: true,
            ),
            const SizedBox(
              height: 30.0,
            ),

            /* show the save button */
            MaterialButton(
              color: Colors.green,
              child: Padding(
                padding: const EdgeInsets.symmetric(
                  horizontal: 20.0,
                  vertical: 10.0,
                ),
                child: Text(
                  'Encrypt And Save',
                  style: TextStyle(
                    fontSize: 20.0,
                    color: Colors.white,
                  ),
                ),
              ),
              onPressed: () => _onSavePress(context),
            ),
          ],
        ),
      ),
    );
  }
}

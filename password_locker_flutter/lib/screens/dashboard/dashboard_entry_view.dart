import 'dart:math';

import 'package:flutter/material.dart';
import 'package:passwordlocker/models/password_model.dart';
import 'package:passwordlocker/utils/constants.dart';
import 'package:provider/provider.dart';

class DashboardEntryView extends StatelessWidget {
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
                Column(
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
                    MaterialButton(
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
                      onPressed: () {},
                    ),
                  ],
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

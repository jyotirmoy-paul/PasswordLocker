import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:passwordlocker/dialogs/login_screen_dialog.dart';
import 'package:passwordlocker/utils/constants.dart';
import 'package:url_launcher/url_launcher.dart';

class LoginScreen extends StatelessWidget {
  void _openDialog(BuildContext context) => showDialog(
        context: context,
        builder: (_) => LoginScreenDialog(),
      );

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: Padding(
        padding: const EdgeInsets.symmetric(
          horizontal: 10.0,
          vertical: 20.0,
        ),
        child: Center(
          child: Column(
            children: [
              SizedBox(
                height: MediaQuery.of(context).size.height * 0.45,
                child: Image.asset(kAuthenticateImage),
              ),

              const SizedBox(
                height: 10.0,
              ),

              // title text
              const Text(
                'Welcome to Password Locker',
                textAlign: TextAlign.center,
                style: const TextStyle(
                  color: Colors.blue,
                  fontSize: 40.0,
                  fontWeight: FontWeight.w600,
                ),
              ),

              const SizedBox(
                height: 5.0,
              ),

              // sub title text
              RichText(
                textAlign: TextAlign.center,
                text: TextSpan(
                  style: const TextStyle(
                    color: Colors.blue,
                    fontSize: 15.0,
                  ),
                  children: [
                    TextSpan(
                      text: 'Maintained by ',
                    ),
                    TextSpan(
                      style: TextStyle(
                        decoration: TextDecoration.underline,
                      ),
                      text: 'Jyotirmoy Paul',
                      recognizer: TapGestureRecognizer()
                        ..onTap = () => launch(
                              kProjectGithubLink,
                            ),
                    ),
                  ],
                ),
              ),

              Spacer(),

              ClipRRect(
                borderRadius: BorderRadius.circular(
                  20.0,
                ),
                child: MaterialButton(
                  color: Colors.green,
                  child: Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 20.0,
                      vertical: 10.0,
                    ),
                    child: Text(
                      "Let's Go",
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 20.0,
                      ),
                    ),
                  ),
                  onPressed: () => _openDialog(context),
                ),
              ),

              Spacer(),
            ],
          ),
        ),
      ),
    );
  }
}

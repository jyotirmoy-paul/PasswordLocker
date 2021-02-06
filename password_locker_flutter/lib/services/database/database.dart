import 'package:hive/hive.dart';

class Database {
  static const _boxName = 'MASTER_PASSWORD_BOX';
  static const _masterPasswordKey = 'master_password_key';

  static void init() {
    // hive init is not necessary in the browser
    // Hive.init(path);
  }

  static Future<String> getMasterPassword() async {
    Box box = await Hive.openBox(_boxName);
    return box.get(_masterPasswordKey) as String;
  }

  static Future<void> setMasterPassword(String masterPassword) async {
    assert(masterPassword != null);
    assert(masterPassword.isNotEmpty);

    Box box = await Hive.openBox(_boxName);
    return box.put(_masterPasswordKey, masterPassword);
  }
}

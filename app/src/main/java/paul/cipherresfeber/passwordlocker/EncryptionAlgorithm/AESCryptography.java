package paul.cipherresfeber.passwordlocker.EncryptionAlgorithm;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCryptography{

    private static final String ALGORITHM = "AES";
    private static String KEY;
    private static final int KEY_SIZE = 32;
    private static final String SINGLE_CHARACTER_TO_APPEND = "P";

    // method for encrypting a value
    public static String encrypt(String value, String k) throws Exception{
        KEY = k;
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(AESCryptography.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        return Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
    }

    // method for decrypting a value
    public static String decrypt(String value, String k) throws Exception{
        KEY = k;
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(AESCryptography.ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptedValue64 = Base64.decode(value, Base64.DEFAULT);
        byte [] decryptedByteValue = cipher.doFinal(decryptedValue64);
        return new String(decryptedByteValue,"utf-8");
    }

    // method for generating keys
    private static Key generateKey() throws Exception{
        Key key = new SecretKeySpec(AESCryptography.KEY.getBytes(), AESCryptography.ALGORITHM);
        return key;
    }

    // utility method to modify the user password before fitting into the AES Cryptography
    public static String modifyPassword(String password, Context c){

        StringBuilder modifiedPassword = new StringBuilder(password);
        int neededChars = KEY_SIZE - password.length();

        /*
        read from the key password file and get the developer side password
        combine the developer and user password to make a killer combination
        use the combined password to encrypt user data
        */

        BufferedReader reader = null;
        try {

            // read from the password file saved in assets folder
            reader = new BufferedReader(
                    new InputStreamReader(c.getAssets().open("keypassword"),
                    "UTF-8")
            );

            StringBuilder developerPassword = new StringBuilder();
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                developerPassword.append(mLine);
            }

            // finally getting the needed portion of the developerPassword
            StringBuilder neededPassword = new StringBuilder();
            for(int i=0; i<neededChars; i++){
                neededPassword.append(developerPassword.charAt(i));
            }

            modifiedPassword.append(neededPassword);

        }  catch (Exception e){
            // just append character 'P' to the end of user's password and log the message

            for(int i=0; i<neededChars; i++){
                modifiedPassword.append(SINGLE_CHARACTER_TO_APPEND);
            }

            Log.i("AESEncryption", e.getMessage());
        }

        // this is guaranteed to be of length 32
        return modifiedPassword.toString();
    }

}


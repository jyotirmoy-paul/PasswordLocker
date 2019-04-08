package android.cipherresfeber.passwordlocker.EncryptionAlgorithm;

import android.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESCryptography{

    private static final String ALGORITHM = "AES";
    private static String KEY = null; // Key is set by the user

    // method for encrypting a value
    public static String encrypt(String value) throws Exception{
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(AESCryptography.ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte [] encryptedByteValue = cipher.doFinal(value.getBytes("utf-8"));
        return Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
    }

    // method for decrypting a value
    public static String decrypt(String value) throws Exception{
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

    public static void setKey(String key){
        AESCryptography.KEY = key;
    }

}


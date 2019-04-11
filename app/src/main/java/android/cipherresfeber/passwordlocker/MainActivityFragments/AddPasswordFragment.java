package android.cipherresfeber.passwordlocker.MainActivityFragments;

import android.cipherresfeber.passwordlocker.Constants.DatabaseConstants;
import android.cipherresfeber.passwordlocker.EncryptionAlgorithm.AESCryptography;
import android.cipherresfeber.passwordlocker.R;
import android.cipherresfeber.passwordlocker.UserDataTypes.PasswordData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPasswordFragment extends Fragment {

    CollectionReference reference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_passwords, container, false);

        reference = FirebaseFirestore.getInstance()
                .collection(DatabaseConstants.DATABASE_PASSWORD_COLLECTION)
                .document("user_uid")
                .collection(DatabaseConstants.DATABASE_USER_PASSWORD);

        final EditText editTextServiceProvider = view.findViewById(R.id.etServiceProvider);
        final EditText editTextLoginId = view.findViewById(R.id.etLoginId);
        final EditText editTextPassword = view.findViewById(R.id.etPassword);
        final EditText editTextEncryptionPassword = view.findViewById(R.id.etEncryptionPassword);
        Button buttonEncryptAndSave = view.findViewById(R.id.btnEncryptAndSave);

        buttonEncryptAndSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // every time create a new key
                DocumentReference ref = reference.document();

                String serviceProvider = editTextServiceProvider.getText().toString().trim();
                String loginId = editTextLoginId.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String encryptionPassword = editTextEncryptionPassword.getText().toString().trim();
                String firebaseKey = ref.getId();

                // encrypt the password and store to the firebase database
                try {
                    AESCryptography.setKey(modifyUserPassword(encryptionPassword));
                    password = AESCryptography.encrypt(password);

                } catch (Exception e){
                    Toast.makeText(getContext(), "Could not encrypt!", Toast.LENGTH_SHORT).show();
                }

                DateFormat dateFormat = new SimpleDateFormat("dd MMM yy");

                PasswordData data = new PasswordData(serviceProvider, loginId, password, firebaseKey, dateFormat.format(new Date()));

                ref.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // password addition successful
                        Toast.makeText(getContext(), "Added Successfully!", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // password addition failed
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        Log.i("Add Password Fragment", e.getMessage());
                    }
                });

            }
        });

        return view;
    }

    // utility method to modify the user password before fitting into the AES Cryptography
    private String modifyUserPassword(String password){
        int lengthOfPassword = password.length();
        int charToGenerate = 16 - lengthOfPassword;

        String modifiedPassword = password;
        for(int i=0; i<charToGenerate; i++){
            modifiedPassword += "P";
        }

        return modifiedPassword;
    }

}

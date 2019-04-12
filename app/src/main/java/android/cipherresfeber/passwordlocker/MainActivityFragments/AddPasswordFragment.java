package android.cipherresfeber.passwordlocker.MainActivityFragments;

import android.cipherresfeber.passwordlocker.Constants.DatabaseConstants;
import android.cipherresfeber.passwordlocker.EncryptionAlgorithm.AESCryptography;
import android.cipherresfeber.passwordlocker.R;
import android.cipherresfeber.passwordlocker.UserDataTypes.PasswordData;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
        final TextView textViewLablePassword = view.findViewById(R.id.txvLablePassword);
        final ImageView imageViewShowPassword = view.findViewById(R.id.imvShowPassword);
        Button buttonEncryptAndSave = view.findViewById(R.id.btnEncryptAndSave);

        buttonEncryptAndSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // every time create a new key
                DocumentReference ref = reference.document();

                String serviceProvider = editTextServiceProvider.getText().toString().trim();
                String loginId = editTextLoginId.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String firebaseKey = ref.getId();

                // encrypt the password and store to the firebase database
                try {
                    String encryptionPassword = "tempPass"; // TODO: get the user set value
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

        // let user know that their password is in safer hands, with end to end encryption, no middle
        // man can read any passwords
        textViewLablePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userInfoDisplay = "Your entered password is encrypted using AES Algorithm with a key" +
                        " (set by you at login time) and without the key no one can decrypt your password." +
                        "\nRest assure, your password will be in good hand.";

                new AlertDialog.Builder(getContext())
                        .setTitle("AES Encryption")
                        .setMessage(userInfoDisplay)
                        .setPositiveButton(android.R.string.yes,null)
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setCancelable(false)
                        .show();

            }
        });

        // show / hide password in password edit text
        imageViewShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if already hidden, show password else vice versa
                if(editTextPassword.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()){
                    editTextPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else{
                    editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }

                // finally set the selection to the end
                editTextPassword.setSelection(editTextPassword.getText().length());
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

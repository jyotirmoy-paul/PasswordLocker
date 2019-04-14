package android.cipherresfeber.passwordlocker.MainActivityFragments;

import android.app.ProgressDialog;
import android.cipherresfeber.passwordlocker.Constants.DatabaseConstants;
import android.cipherresfeber.passwordlocker.Constants.UserConstants;
import android.cipherresfeber.passwordlocker.EncryptionAlgorithm.AESCryptography;
import android.cipherresfeber.passwordlocker.R;
import android.cipherresfeber.passwordlocker.UserDataTypes.PasswordData;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPasswordFragment extends Fragment {

    CollectionReference reference;
    String userSetEncryptionKey;
    String userUid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_passwords, container, false);

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userSetEncryptionKey = getContext()
                .getSharedPreferences(UserConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                .getString(UserConstants.USER_ENCRYPTION_PASS_CODE, UserConstants.DEFAULT_ENCRYPTION_PASS_CODE);


        reference = FirebaseFirestore.getInstance()
                .collection(DatabaseConstants.DATABASE_PASSWORD_COLLECTION)
                .document(userUid)
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

                // every time create a new database key
                DocumentReference ref = reference.document();

                String serviceProvider = editTextServiceProvider.getText().toString().trim();
                String loginId = editTextLoginId.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String firebaseKey = ref.getId();

                // check for invalid data entry
                if(serviceProvider.length() < 2 || serviceProvider.length() > 30){
                    editTextServiceProvider.setError("Length 2 - 30 only");
                    editTextServiceProvider.requestFocus();
                    return;
                }

                if(loginId.isEmpty()){
                    editTextLoginId.setError("Use a Login ID");
                    editTextLoginId.requestFocus();
                    return;
                }

                if(password.isEmpty()) {
                    editTextPassword.setError("Password can't be empty");
                    editTextPassword.requestFocus();
                    return;
                }

                // encrypt the password and store to the firebase database
                try {
                    String encryptionPassword = userSetEncryptionKey;
                    AESCryptography.setKey(modifyUserPassword(encryptionPassword));
                    password = AESCryptography.encrypt(password);

                } catch (Exception e){
                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    Log.i("Main Activity", e.getMessage());
                }

                DateFormat dateFormat = new SimpleDateFormat("dd MMM yy");
                PasswordData data = new PasswordData(serviceProvider, loginId, password, firebaseKey, dateFormat.format(new Date()));

                final ProgressDialog pd = new ProgressDialog(getContext());
                pd.setTitle("Please Wait");
                pd.setMessage("Uploading Data...");
                pd.setCancelable(false);
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                ref.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        // clear all the editText entries
                        editTextLoginId.setText("");
                        editTextPassword.setText("");
                        editTextServiceProvider.setText("");

                        // dismiss the progress dialog
                        pd.dismiss();

                        Toast.makeText(getContext(),"Password Added", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // password addition failed
                        Toast.makeText(getContext(), "Oops! Something went wrong, try again later", Toast.LENGTH_SHORT).show();
                        Log.i("Main Activity", e.getMessage());
                        pd.dismiss();
                    }
                });

            }
        });

        // let user know that their password is in safer hands, with end to end encryption, no middle
        // man can read any passwords
        textViewLablePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userInfoDisplay = "Your passwords are encrypted using AES Algorithm with a key" +
                        " (set by you at registration time) and without the key no one can decrypt your password." +
                        "\n" +
                        "That means only you could see your passwords." +
                        "\n" +
                        "Security is Guaranteed!";

                new AlertDialog.Builder(getContext())
                        .setTitle("Advanced Encryption Standard Algorithm Used")
                        .setMessage(userInfoDisplay)
                        .setPositiveButton(android.R.string.yes,null)
                        .setIcon(R.drawable.ic_info)
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

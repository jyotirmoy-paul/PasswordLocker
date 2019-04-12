package android.cipherresfeber.passwordlocker.BottomSheetFragment;

import android.cipherresfeber.passwordlocker.Constants.DatabaseConstants;
import android.cipherresfeber.passwordlocker.EncryptionAlgorithm.AESCryptography;
import android.cipherresfeber.passwordlocker.R;
import android.cipherresfeber.passwordlocker.UserDataTypes.PasswordData;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditBottomSheetFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_bottom_sheet_layout, container, false);

        final String serviceProvider;
        final String loginId;
        final String key;

        TextView textViewServiceProviderName = view.findViewById(R.id.txvServiceProviderName);
        TextView textViewLoginId = view.findViewById(R.id.txvLoginId);

        ImageView btnUpdatePassword = view.findViewById(R.id.btnUpdatePassword);

        final EditText editTextNewPassword = view.findViewById(R.id.etNewPassword);
        final EditText editTextDecryptionPassword = view.findViewById(R.id.etDecryptionPassword);

        Bundle bundle = getArguments();
        if(bundle == null){
            serviceProvider = DatabaseConstants.SOMETHING_WENT_WRONG;
            loginId = DatabaseConstants.SOMETHING_WENT_WRONG;
            key = DatabaseConstants.SOMETHING_WENT_WRONG;
        } else{
            serviceProvider = bundle.getString(DatabaseConstants.SERVICE_PROVIDER_NAME);
            loginId = bundle.getString(DatabaseConstants.LOGIN_ID);
            key = bundle.getString(DatabaseConstants.DATABASE_KEY);
        }

        textViewServiceProviderName.setText(serviceProvider);
        textViewLoginId.setText(loginId);

        // reference to the firestore for updating data
        final DocumentReference reference = FirebaseFirestore.getInstance()
                .collection(DatabaseConstants.DATABASE_PASSWORD_COLLECTION)
                .document("user_uid")
                .collection(DatabaseConstants.DATABASE_USER_PASSWORD).document(key);

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPassword = editTextNewPassword.getText().toString().trim();
                String decryptionPassword = editTextDecryptionPassword.getText().toString().trim();

                // TODO: check for the decryption password and take decision accordingly

                try{
                    // TODO: get user set password
                    AESCryptography.setKey(modifyUserPassword("tempPass"));
                    String newEncryptedPassword = AESCryptography.encrypt(newPassword);

                    DateFormat dateFormat = new SimpleDateFormat("dd MMM yy");

                    PasswordData pd = new PasswordData(serviceProvider,loginId,
                            newEncryptedPassword,key,dateFormat.format(new Date()));

                    reference.set(pd).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(),
                                    "Password Updated!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),
                                    "Something went wrong", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", e.getMessage());
                        }
                    });


                } catch (Exception e){
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    Log.i("MainActivity", e.getMessage());
                }

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

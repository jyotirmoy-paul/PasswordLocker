package paul.cipherresfeber.passwordlocker.BottomSheetFragment;

import android.app.ProgressDialog;
import paul.cipherresfeber.passwordlocker.Constants.DatabaseConstants;
import paul.cipherresfeber.passwordlocker.Constants.UserConstants;
import paul.cipherresfeber.passwordlocker.EncryptionAlgorithm.AESCryptography;
import paul.cipherresfeber.passwordlocker.R;
import paul.cipherresfeber.passwordlocker.UserDataTypes.PasswordData;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import com.google.firebase.auth.FirebaseAuth;
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

        final SharedPreferences preferences = getContext().getSharedPreferences(
                UserConstants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );

        final String serviceProvider;
        final String loginId;
        final String key;

        TextView textViewServiceProviderName = view.findViewById(R.id.txvServiceProviderName);
        TextView textViewLoginId = view.findViewById(R.id.txvLoginId);

        ImageView btnUpdatePassword = view.findViewById(R.id.btnUpdatePassword);

        final EditText editTextNewPassword = view.findViewById(R.id.etNewPassword);
        final EditText editTextDecryptionPassword = view.findViewById(R.id.etDecryptionPassword);

        // eye button for showing password
        ImageView imageViewNewPasswordShow = view.findViewById(R.id.imvNewShowPassword);
        imageViewNewPasswordShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if already hidden, show password else vice versa
                if(editTextNewPassword.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()){
                    editTextNewPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else{
                    editTextNewPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }

                // finally set the selection to the end
                editTextNewPassword.setSelection(editTextNewPassword.getText().length());


            }
        });

        ImageView imageViewDecryptionPasswordShow = view.findViewById(R.id.imvDecryptionShowPassword);
        imageViewDecryptionPasswordShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // if already hidden, show password else vice versa
                if(editTextDecryptionPassword.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()){
                    editTextDecryptionPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else{
                    editTextDecryptionPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }

                // finally set the selection to the end
                editTextDecryptionPassword.setSelection(editTextDecryptionPassword.getText().length());

            }
        });

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

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Updating Password");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        // reference to the firestore for updating data
        final DocumentReference reference = FirebaseFirestore.getInstance()
                .collection(DatabaseConstants.DATABASE_PASSWORD_COLLECTION)
                .document(userUid)
                .collection(DatabaseConstants.DATABASE_USER_PASSWORD).document(key);

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPassword = editTextNewPassword.getText().toString().trim();
                String decryptionPassword = editTextDecryptionPassword.getText().toString().trim();

                if(newPassword.isEmpty()){
                    editTextNewPassword.setError("Can't be empty!");
                    editTextNewPassword.requestFocus();
                    return;
                }

                if(decryptionPassword.length() < 7 || decryptionPassword.length() > 32){
                    Toast.makeText(getContext(),
                            "Master Password of Invalid Length", Toast.LENGTH_SHORT).show();
                    editTextDecryptionPassword.setError("");
                    editTextDecryptionPassword.requestFocus();
                    return;
                }

                String savedDecryptionPassword = preferences
                        .getString(UserConstants.USER_ENCRYPTION_PASS_CODE, UserConstants.DEFAULT_ENTRY_PASS_CODE);

                if(!savedDecryptionPassword.equals(decryptionPassword)){
                    Toast.makeText(getContext(),
                            "Wrong Password!", Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                progressDialog.show();

                try{
                    String newEncryptedPassword = AESCryptography.encrypt(
                            newPassword,
                            AESCryptography.modifyPassword(savedDecryptionPassword, getContext())
                    );

                    DateFormat dateFormat = new SimpleDateFormat("dd MMM yy");

                    PasswordData pd = new PasswordData(serviceProvider,loginId,
                            newEncryptedPassword,key,dateFormat.format(new Date()));

                    reference.set(pd).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(),
                                    "Password Updated!", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                            dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),
                                    "Something went wrong", Toast.LENGTH_SHORT).show();
                            Log.i("MainActivity", e.getMessage());
                            progressDialog.cancel();
                        }
                    });


                } catch (Exception e){
                    Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    Log.i("MainActivity", e.getMessage());
                    progressDialog.cancel();
                }

            }
        });


        return view;
    }

}

package android.cipherresfeber.passwordlocker.BottomSheetFragment;

import android.cipherresfeber.passwordlocker.Constants.DatabaseConstants;
import android.cipherresfeber.passwordlocker.EncryptionAlgorithm.AESCryptography;
import android.cipherresfeber.passwordlocker.R;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class PasswordBottomSheetFragment extends BottomSheetDialogFragment {

    private String encryptedPassword;
    private String decryptedPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        getDialog().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        String serviceProvider;
        String id;

        // get the service provider, login id and password info from bundle
        Bundle bundle = getArguments();

        if(bundle == null){
            serviceProvider = DatabaseConstants.SOMETHING_WENT_WRONG;
            id = DatabaseConstants.SOMETHING_WENT_WRONG;
            encryptedPassword = DatabaseConstants.SOMETHING_WENT_WRONG;
        } else{
            serviceProvider = bundle.getString(DatabaseConstants.SERVICE_PROVIDER_NAME);
            id = bundle.getString(DatabaseConstants.LOGIN_ID);
            encryptedPassword = bundle.getString(DatabaseConstants.PASSWORD);
        }

        TextView textViewServiceProviderName = view.findViewById(R.id.txvServiceProviderName);
        final TextView textViewLoginId = view.findViewById(R.id.txvLoginId);
        final TextView textViewPassword = view.findViewById(R.id.txvPassword);
        final EditText editTextDecryptionPassword = view.findViewById(R.id.etDecryptionPassword);
        final ImageView imageViewCopyPassword = view.findViewById(R.id.imvCopyContent);
        final TextView headingOfBottomSheet = view.findViewById(R.id.lableHeading);
        ImageView btnDecryptPassword = view.findViewById(R.id.btnDecryptPassword);


        textViewServiceProviderName.setText(serviceProvider);
        textViewLoginId.setText(id);
        textViewPassword.setText(encryptedPassword.trim());

        btnDecryptPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String decryptionPassword = editTextDecryptionPassword.getText().toString().trim();
                if(decryptionPassword.length() < 10 || decryptionPassword.length() > 16){
                    editTextDecryptionPassword.setError("Invalid Length");
                    editTextDecryptionPassword.requestFocus();
                    return;
                }

                try{
                    // get the user password and use that to decrypt the "Stored Password"
                    AESCryptography.setKey(modifyUserPassword(decryptionPassword));
                    decryptedPassword = AESCryptography.decrypt(encryptedPassword);

                    textViewPassword.setText(decryptedPassword);
                    headingOfBottomSheet.setText("-- Decryption Successfull --");
                    textViewPassword.setTextColor(getResources().getColor(R.color.colorAccent));

                    // allowing copying of password only if password retrieval is successful
                    imageViewCopyPassword.setVisibility(View.VISIBLE);
                    imageViewCopyPassword.setOnClickListener(onClickListener);

                    // remove value from the edit text
                    editTextDecryptionPassword.setText("");

                } catch(Exception e){
                    Toast.makeText(getContext(), "Wrong Password", Toast.LENGTH_SHORT).show();
                    Log.i("BottomSheetDialog", e.getMessage());
                    dismiss();
                }

            }
        });

        // open soft input keyboard automatically
        editTextDecryptionPassword.requestFocus();
        getDialog().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);


        return view;

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("password", decryptedPassword);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Password Copied", Toast.LENGTH_SHORT).show();
        }
    };

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

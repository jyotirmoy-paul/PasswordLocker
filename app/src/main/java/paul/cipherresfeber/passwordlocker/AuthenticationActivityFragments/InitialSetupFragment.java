package paul.cipherresfeber.passwordlocker.AuthenticationActivityFragments;

import android.app.AlertDialog;
import paul.cipherresfeber.passwordlocker.Constants.UserConstants;
import paul.cipherresfeber.passwordlocker.MainActivity;
import paul.cipherresfeber.passwordlocker.R;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class InitialSetupFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public InitialSetupFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_initial_setup, container, false);

        SharedPreferences preferences = getContext().getSharedPreferences(
                UserConstants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        final SharedPreferences.Editor editor = preferences.edit();

        TextView textViewInfoAboutLockPin = view.findViewById(R.id.infoAboutLockPin);
        TextView textViewInfoAboutDecryptionPassword = view.findViewById(R.id.infoAboutDecryptionPassword);

        final EditText editTextLockPin = view.findViewById(R.id.etLockPin);
        final EditText editTextConfirmLockPin = view.findViewById(R.id.etConfirmLockPin);

        final EditText editTextDecryptionPassword = view.findViewById(R.id.etDecryptionPassword);
        final EditText editTextConfirmDecryptionPassword = view.findViewById(R.id.etConfirmDecryptionPassword);

        ImageView btnContinue = view.findViewById(R.id.btnContinue);

        // display info about lock pin
        textViewInfoAboutLockPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userMessage =
                        "Lock Pin is a 4 digit pin used to login to the app. " +
                        "This is the first line of defense against intruders." +
                        "\n" +
                        "Use a memorable but hard to guess PIN.";

                new AlertDialog.Builder(getContext())
                        .setTitle("Lock Pin")
                        .setMessage(userMessage)
                        .setIcon(R.drawable.ic_lock)
                        .setCancelable(false)
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });

        // display info about decryption password
        textViewInfoAboutDecryptionPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userMessage =
                        "Master Password is used to decrypt/encrypt your saved passwords. " +
                        "So that even if an attacker manages to get data from our server, he/she won't " +
                        "be able to decrypt them." +
                        "\n" +
                        "Use a memorable but hard to guess master password." +
                                "\n\n" +
                                "(If you have previously used our service, please use the same Master Password, to avoid confusion)";

                new AlertDialog.Builder(getContext())
                        .setTitle("Master Password")
                        .setMessage(userMessage)
                        .setIcon(R.drawable.ic_decryption_key)
                        .setCancelable(false)
                        .setPositiveButton("Ok", null)
                        .show();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check for password Consistency and save them in SharedPreferences

                // check for lockPin
                String lockPin = editTextLockPin.getText().toString().trim();
                String confirmLockPin = editTextConfirmLockPin.getText().toString().trim();

                if(lockPin.length() != 4){
                    editTextLockPin.setError("Must be of length 4");
                    editTextLockPin.requestFocus();
                    return;
                }

                if(!lockPin.equals(confirmLockPin)){
                    editTextConfirmLockPin.setError("Does not match");
                    editTextConfirmLockPin.requestFocus();
                    return;
                }

                // check for decryption Password
                String decryptionPassword = editTextDecryptionPassword.getText().toString().trim();
                String confirmDecryptionPassword = editTextConfirmDecryptionPassword.getText().toString().trim();

                if(decryptionPassword.length() < 7 || decryptionPassword.length() > 32){
                    editTextDecryptionPassword.setError("7 - 32 chars only");
                    editTextDecryptionPassword.requestFocus();
                    return;
                }

                if(!decryptionPassword.equals(confirmDecryptionPassword)){
                    editTextConfirmDecryptionPassword.setError("Does not match");
                    editTextConfirmDecryptionPassword.requestFocus();
                    return;
                }

                // finally write to the shared Preference
                editor.putString(UserConstants.USER_ENTRY_PASS_CODE, lockPin);
                editor.putString(UserConstants.USER_ENCRYPTION_PASS_CODE, decryptionPassword);

                editor.apply();

                String userMessage = "Congrats! You have successfully setup your account on Password Locker. " +
                        "\nLocker Pin is used to get into the app, and the Master Password is used to " +
                        "encrypt/decrypt your saved passwords!" +
                        "\nYou can change the Locker Pin, but Master Password " +
                        "is set permanent (this is done to avoid confusion)" +
                        "\n" +
                        "\n" +
                        "*Passwords encrypted with an master key can only be decrypted by the same key, this is why we don't encourage you to change the master key." +
                        "\n" +
                        "\n" +
                        "Press 'Ok' to get to the Home Page";

                new AlertDialog.Builder(getContext())
                        .setTitle("Welcome Abroad!")
                        .setMessage(userMessage)
                        .setIcon(R.drawable.ic_info)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(getContext(), MainActivity.class));
                                getActivity().finish();
                            }
                        })
                        .show();
            }
        });

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}

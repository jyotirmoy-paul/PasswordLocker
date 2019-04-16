package paul.cipherresfeber.passwordlocker.MainActivityFragments;

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
import android.widget.Toast;

public class EditProfileFragment extends Fragment {

    private static final String USER_NAME = "user_name";

    private String userName;
    private String savedLockerPin;
    private SharedPreferences preferences;

    private OnFragmentInteractionListener mListener;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance(String userName) {
        EditProfileFragment fragment = new EditProfileFragment();
        Bundle args = new Bundle();
        args.putString(USER_NAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(USER_NAME);
        }
        preferences = getContext().getSharedPreferences(
                UserConstants.SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE
        );
        savedLockerPin = preferences.getString(UserConstants.USER_ENTRY_PASS_CODE, UserConstants.DEFAULT_ENTRY_PASS_CODE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        final EditText editTextNewUserName = view.findViewById(R.id.etNewUserName);
        final EditText editTextNewLockerPin = view.findViewById(R.id.etNewLockerPin);
        final EditText editTextConfirmLockerPin = view.findViewById(R.id.etConfirmLockerPin);
        final EditText editTextOldLockerPin = view.findViewById(R.id.etOldLockerPin);
        ImageView buttonUpdateProfile = view.findViewById(R.id.btnUpdateProfile);

        editTextNewUserName.setHint(userName);

        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newUserName = editTextNewUserName.getText().toString().trim();
                String newLockerPin = editTextNewLockerPin.getText().toString().trim();
                String confirmLockerPin = editTextConfirmLockerPin.getText().toString().trim();
                String oldLockerPin = editTextOldLockerPin.getText().toString().trim();

                boolean updateUserName = false;

                if(!newUserName.isEmpty()){
                    if(newUserName.length() > 20 || newUserName.length() < 5){
                        editTextNewUserName.setError("5 - 20 chars only");
                        editTextNewUserName.requestFocus();
                        return;
                    }

                    updateUserName = true;

                }

                if(!newLockerPin.isEmpty()){

                    if(newLockerPin.length() != 4){
                        editTextNewLockerPin.setError("4 digits only");
                        editTextNewLockerPin.requestFocus();
                        return;
                    }

                    if(!newLockerPin.equals(confirmLockerPin)){
                        editTextConfirmLockerPin.setError("Does not match");
                        editTextConfirmLockerPin.requestFocus();
                        return;
                    }

                    if(!savedLockerPin.equals(oldLockerPin)){
                        editTextOldLockerPin.setError("Wrong PIN");
                        editTextOldLockerPin.requestFocus();
                        return;
                    }

                    preferences.edit().putString(UserConstants.USER_ENTRY_PASS_CODE, newLockerPin).apply();

                }

                if(updateUserName){
                    preferences.edit().putString(UserConstants.USER_NAME, newUserName).apply();
                }

                if(!newLockerPin.isEmpty() || !newUserName.isEmpty()){
                    new AlertDialog.Builder(getContext())
                            .setCancelable(false)
                            .setTitle("Profile Updated")
                            .setMessage("Successfully updated your profile")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(getContext(), MainActivity.class));
                                    getActivity().finish();
                                }
                            })
                            .setIcon(R.drawable.ic_done)
                            .show();
                } else{
                    Toast.makeText(getContext(),
                            "Nothing to update!", Toast.LENGTH_SHORT).show();
                }

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

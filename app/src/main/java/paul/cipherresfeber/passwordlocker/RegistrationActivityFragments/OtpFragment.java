package paul.cipherresfeber.passwordlocker.RegistrationActivityFragments;

import android.app.ProgressDialog;
import paul.cipherresfeber.passwordlocker.AuthenticationActivity;
import paul.cipherresfeber.passwordlocker.Constants.UserConstants;
import paul.cipherresfeber.passwordlocker.R;
import paul.cipherresfeber.passwordlocker.UserDataTypes.UserData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class OtpFragment extends Fragment {

    private String userName;
    private String userPhoneNumber;
    private String userGeneratedOTP;

    FirebaseAuth firebaseAuth;

    ProgressDialog pd;

    private OnFragmentInteractionListener mListener;

    public OtpFragment() {
    }

    public static OtpFragment newInstance(String userName, String userPhone, String otp) {
        OtpFragment fragment = new OtpFragment();
        Bundle args = new Bundle();
        args.putString(UserConstants.USER_NAME, userName);
        args.putString(UserConstants.USER_PHONE_NUMBER, userPhone);
        args.putString(UserConstants.USER_GENERATED_OTP, otp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userName = getArguments().getString(UserConstants.USER_NAME);
            userPhoneNumber = getArguments().getString(UserConstants.USER_PHONE_NUMBER);
            userGeneratedOTP = getArguments().getString(UserConstants.USER_GENERATED_OTP);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_otp, container, false);

        TextView textViewUserPhoneNumber = view.findViewById(R.id.txvUserPhoneNumber);
        TextView textViewUserMessage = view.findViewById(R.id.txvUserMessage);
        final EditText editTextOtp = view.findViewById(R.id.etOTP);
        Button btnVerifyOtp = view.findViewById(R.id.btnVerifyOtp);

        String firstName = userName.split(" ")[0];

        textViewUserPhoneNumber.setText("+91" + " " + userPhoneNumber);
        textViewUserMessage.setText("Verify your number, " + firstName);

        firebaseAuth = FirebaseAuth.getInstance();

        btnVerifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String otpEnteredByUser = editTextOtp.getText().toString().trim();

                if(otpEnteredByUser.isEmpty()){
                    editTextOtp.setError("Can't be empty");
                    editTextOtp.requestFocus();
                    return;
                }

                PhoneAuthCredential credential = PhoneAuthProvider
                        .getCredential(userGeneratedOTP, otpEnteredByUser);

                signInWithPhoneAuthCredential(credential);

                pd.show();

            }
        });

        pd = new ProgressDialog(getContext());
        pd.setCancelable(false);
        pd.setCanceledOnTouchOutside(false);
        pd.setTitle("Please Wait");
        pd.setMessage("Verifying OTP");

        return view;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            String uid = firebaseAuth.getCurrentUser().getUid();

                            DatabaseReference ref = FirebaseDatabase.getInstance()
                                    .getReference().child("user_data").child(uid);

                            ref.setValue(
                                    new UserData(
                                        userName,
                                        userPhoneNumber,
                                        uid,
                                        (new Date()).toString()
                                    )
                            ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    SharedPreferences.Editor editor = getContext()
                                            .getSharedPreferences(UserConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE).edit();

                                    editor.putString(UserConstants.USER_NAME, userName);
                                    editor.putString(UserConstants.USER_PHONE_NUMBER, userPhoneNumber);

                                    editor.apply();
                                    pd.cancel();

                                    startActivity(new Intent(getContext(), AuthenticationActivity.class));
                                    getActivity().finish();

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.cancel();
                                            Toast.makeText(getContext(),
                                                    "Something went wrong!", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else{
                            pd.cancel();
                            Toast.makeText(getContext(),
                                    "Wrong OTP!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.cancel();
                        Toast.makeText(getContext(),
                                "Wrong OTP!", Toast.LENGTH_SHORT).show();
                    }
                });
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

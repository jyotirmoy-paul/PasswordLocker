package android.cipherresfeber.passwordlocker;

import android.app.ProgressDialog;
import android.cipherresfeber.passwordlocker.Constants.UserConstants;
import android.cipherresfeber.passwordlocker.RegistrationActivityFragments.OtpFragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegistrationActivity extends AppCompatActivity implements OtpFragment.OnFragmentInteractionListener{

    private ProgressDialog pd;

    private String userName;
    private String userPhoneNumber;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        try{
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            if(!uid.isEmpty()){
                // the user exists, continue
                startActivity(new Intent(RegistrationActivity.this, AuthenticationActivity.class));
                RegistrationActivity.this.finish();
            }

        } catch (Exception e){
            // the user is not signed in, continue with signup
            Log.i("Registration Activity", e.getMessage());
        }



        Button buttonGetOtp = findViewById(R.id.btnGetOtp);
        final EditText editTextUserPhoneNumber = findViewById(R.id.etUserPhoneNumber);
        final EditText editTextUserName = findViewById(R.id.etUserName);

        pd = new ProgressDialog(RegistrationActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.setTitle("Please Wait");
        pd.setMessage("Sending OTP.....");

        buttonGetOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPhoneNumber = editTextUserPhoneNumber.getText().toString().trim();
                userName = editTextUserName.getText().toString().trim();

                if(userName.length() > 20 || userName.length() < 5){
                    editTextUserName.setError("5 - 20 chars only");
                    editTextUserName.requestFocus();
                    return;
                }

                if(userPhoneNumber.length() != 10){
                    editTextUserPhoneNumber.setError("10 digits only");
                    editTextUserPhoneNumber.requestFocus();
                    return;
                }

                setUpVerificationCallbacks();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + userPhoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        RegistrationActivity.this,
                        verificationCallbacks
                );

                // finally show a progress dialog box
                pd.show();
            }
        });

    }

    // method for setting up verification callbacks
    private void setUpVerificationCallbacks(){
        verificationCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                pd.cancel();
                Toast.makeText(RegistrationActivity.this,
                        "Something went wrong!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                pd.cancel();
                Toast.makeText(RegistrationActivity.this,
                        "Something went wrong!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                pd.cancel();

                // open the otp verification fragment
                OtpFragment otpFragment = OtpFragment.newInstance(userName, userPhoneNumber, s);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                        R.anim.enter_from_right, R.anim.exit_to_right);
                transaction.addToBackStack(null);
                transaction.add(R.id.fragment_container, otpFragment, "OTPFragment").commit();

            }
        };
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}

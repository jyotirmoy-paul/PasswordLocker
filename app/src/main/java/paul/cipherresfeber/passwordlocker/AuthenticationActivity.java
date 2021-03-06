package paul.cipherresfeber.passwordlocker;

import paul.cipherresfeber.passwordlocker.AuthenticationActivityFragments.InitialLogoDisplayFragment;
import paul.cipherresfeber.passwordlocker.AuthenticationActivityFragments.InitialSetupFragment;
import paul.cipherresfeber.passwordlocker.Constants.UserConstants;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AuthenticationActivity extends AppCompatActivity implements InitialSetupFragment.OnFragmentInteractionListener{

    private int pinCodeCounter;
    private String userEnteredCode;
    private String savedPassCode;

    private View firstCircle;
    private View secondCircle;
    private View thirdCircle;
    private View fourthCircle;

    private TextView textViewEntryPasscodeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        SharedPreferences sharedPreferences = getSharedPreferences(UserConstants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        savedPassCode = sharedPreferences.getString(UserConstants.USER_ENTRY_PASS_CODE,
                UserConstants.DEFAULT_ENTRY_PASS_CODE);
        String userFirstName = sharedPreferences.getString(UserConstants.USER_NAME,
                "Default User").split(" ")[0];

        LinearLayout parentLayoutFirstTime = findViewById(R.id.linearLayoutFirst);
        LinearLayout parentLayoutGeneralTime = findViewById(R.id.linearLayoutGeneral);
        textViewEntryPasscodeStatus = findViewById(R.id.txvEntryPasscodeStatus);

        if(savedPassCode.equals(UserConstants.DEFAULT_ENTRY_PASS_CODE)){
            // first time open
            // user needs to set a pass code and an decryption key

            parentLayoutFirstTime.setVisibility(View.VISIBLE);
            parentLayoutGeneralTime.setVisibility(View.GONE);

            TextView textViewUserSecurityMessage = findViewById(R.id.txvUserSecurityMessage);
            ImageView btnContinue = findViewById(R.id.btnContinue);

            String userSecurityMessage = "Welcome Abroad, " + userFirstName + "\n" +
                    "You are requested to set an app Lock PIN and Master Password in the next screen. " +
                    "Choose your Lock PIN wisely, as it will be the first line of defense.\n" +
                    "If you already had an account with us, we request you to set the same master password as before (to avoid confusion).";
            textViewUserSecurityMessage.setText(userSecurityMessage);

            btnContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InitialSetupFragment fragment = new InitialSetupFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right,
                            R.anim.enter_from_right, R.anim.exit_to_right);
                    transaction.addToBackStack(null);
                    transaction.add(R.id.fragmentContainer, fragment, "InitialSetupFragment").commit();
                }
            });

        } else{
            // general time open
            InitialLogoDisplayFragment logoDisplayFragment = new InitialLogoDisplayFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, logoDisplayFragment, "LogoDisplayFragment").commit();

            parentLayoutFirstTime.setVisibility(View.GONE);
            parentLayoutGeneralTime.setVisibility(View.VISIBLE);

            // initializing variables
            pinCodeCounter = 0;
            userEnteredCode = "";

            // initialize the layouts
            firstCircle = findViewById(R.id.viewFirstCircle);
            secondCircle = findViewById(R.id.viewSecondCircle);
            thirdCircle = findViewById(R.id.viewThirdCircle);
            fourthCircle = findViewById(R.id.viewFourthCircle);
        }

    }

    public void numberButton(View v){

        String buttonText = ((Button) v).getText().toString();

        if(!android.text.TextUtils.isDigitsOnly(buttonText)){
            if(pinCodeCounter > 0){
                pinCodeCounter--;
                if(userEnteredCode.length() > 0)
                    userEnteredCode = userEnteredCode.substring(0, userEnteredCode.length()-1);
            }

        } else{
            if(pinCodeCounter < 4){
                pinCodeCounter++;
                userEnteredCode += buttonText;
            }
        }

        // fill the view accordingly
        if(pinCodeCounter == 0){

            firstCircle.setBackgroundResource(R.drawable.white_circular_bg);
            secondCircle.setBackgroundResource(R.drawable.white_circular_bg);
            thirdCircle.setBackgroundResource(R.drawable.white_circular_bg);
            fourthCircle.setBackgroundResource(R.drawable.white_circular_bg);

        } else if(pinCodeCounter == 1){

            firstCircle.setBackgroundResource(R.drawable.black_circular_bg);
            secondCircle.setBackgroundResource(R.drawable.white_circular_bg);
            thirdCircle.setBackgroundResource(R.drawable.white_circular_bg);
            fourthCircle.setBackgroundResource(R.drawable.white_circular_bg);

        } else if(pinCodeCounter == 2){

            firstCircle.setBackgroundResource(R.drawable.black_circular_bg);
            secondCircle.setBackgroundResource(R.drawable.black_circular_bg);
            thirdCircle.setBackgroundResource(R.drawable.white_circular_bg);
            fourthCircle.setBackgroundResource(R.drawable.white_circular_bg);

        } else if(pinCodeCounter == 3){

            firstCircle.setBackgroundResource(R.drawable.black_circular_bg);
            secondCircle.setBackgroundResource(R.drawable.black_circular_bg);
            thirdCircle.setBackgroundResource(R.drawable.black_circular_bg);
            fourthCircle.setBackgroundResource(R.drawable.white_circular_bg);

        } else if(pinCodeCounter == 4){

            firstCircle.setBackgroundResource(R.drawable.black_circular_bg);
            secondCircle.setBackgroundResource(R.drawable.black_circular_bg);
            thirdCircle.setBackgroundResource(R.drawable.black_circular_bg);
            fourthCircle.setBackgroundResource(R.drawable.black_circular_bg);

            if(savedPassCode.equals(userEnteredCode)){
                textViewEntryPasscodeStatus.setVisibility(View.INVISIBLE);
                startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                AuthenticationActivity.this.finish();
            } else{
                // show an shake animation notifying the use about wrong passcode
                Animation animation = AnimationUtils.loadAnimation(AuthenticationActivity.this,
                        R.anim.shake);
                textViewEntryPasscodeStatus.startAnimation(animation);
                textViewEntryPasscodeStatus.setVisibility(View.VISIBLE);
                textViewEntryPasscodeStatus.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        userEnteredCode = "";
                        pinCodeCounter = 0;
                        firstCircle.setBackgroundResource(R.drawable.white_circular_bg);
                        secondCircle.setBackgroundResource(R.drawable.white_circular_bg);
                        thirdCircle.setBackgroundResource(R.drawable.white_circular_bg);
                        fourthCircle.setBackgroundResource(R.drawable.white_circular_bg);
                    }
                }, 100);
            }

        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }
}

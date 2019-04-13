package android.cipherresfeber.passwordlocker;

import android.cipherresfeber.passwordlocker.Constants.UserConstants;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.util.NumberUtils;

import io.opencensus.internal.StringUtil;

public class AuthenticationActivity extends AppCompatActivity {

    private int pinCodeCounter;
    private String userEnteredCode;
    private String savedPassCode;

    private View firstCircle;
    private View secondCircle;
    private View thirdCircle;
    private View fourthCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        SharedPreferences sharedPreferences = getSharedPreferences(UserConstants.SHARED_PREFERENCE_NAME, MODE_PRIVATE);
        savedPassCode = sharedPreferences.getString(UserConstants.USER_ENTRY_PASS_CODE,
                UserConstants.DEFAULT_ENTRY_PASS_CODE);
        savedPassCode = "1234";

        LinearLayout parentLayoutFirstTime = findViewById(R.id.linearLayoutFirst);
        LinearLayout parentLayoutGeneralTime = findViewById(R.id.linearLayoutGeneral);

        if(savedPassCode.equals(UserConstants.DEFAULT_ENTRY_PASS_CODE)){
            // first time open
            // user needs to set a pass code and an decryption key

            parentLayoutFirstTime.setVisibility(View.VISIBLE);
            parentLayoutGeneralTime.setVisibility(View.GONE);

            // TODO: do it


        } else{
            // general time open

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
                startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                AuthenticationActivity.this.finish();
            }

        }
    }
}

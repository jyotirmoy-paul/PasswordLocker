package android.cipherresfeber.passwordlocker;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.util.NumberUtils;

import io.opencensus.internal.StringUtil;

public class AuthenticationActivity extends AppCompatActivity {

    private int pinCodeCounter;
    private String userEnteredCode;

    private View firstCircle;
    private View secondCircle;
    private View thirdCircle;
    private View fourthCircle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        // initializing variables
        pinCodeCounter = 0;
        userEnteredCode = "";

        // initialize the layouts
        firstCircle = findViewById(R.id.viewFirstCircle);
        secondCircle = findViewById(R.id.viewSecondCircle);
        thirdCircle = findViewById(R.id.viewThirdCircle);
        fourthCircle = findViewById(R.id.viewFourthCircle);


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

            // check for the password and match with the saved pass code
            // TODO: need to work!

            Toast.makeText(this, userEnteredCode, Toast.LENGTH_SHORT).show();

        }
    }
}

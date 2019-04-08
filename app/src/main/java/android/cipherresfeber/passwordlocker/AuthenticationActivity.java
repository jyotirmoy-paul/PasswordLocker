package android.cipherresfeber.passwordlocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AuthenticationActivity extends AppCompatActivity {

    private final String TEMP_PIN = "1234";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        final EditText editTextEnterPin = findViewById(R.id.etEnterPin);

        Button btnCheckPin = findViewById(R.id.btnCheckPin);
        btnCheckPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pinCode =  editTextEnterPin.getText().toString().trim();
                if(TEMP_PIN.equals(pinCode)){
                    startActivity(new Intent(AuthenticationActivity.this, MainActivity.class));
                    AuthenticationActivity.this.finish();
                } else{
                    Toast.makeText(AuthenticationActivity.this,
                            "Password: " + TEMP_PIN, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

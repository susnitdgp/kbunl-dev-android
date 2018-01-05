package kbunl.com.kbunl_dev;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class SignUpSuccessActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSignInBack;
    private TextView textSignUpResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_success);
        setTitle("Registered Successfully");



        textSignUpResult=(TextView)findViewById(R.id.textSignUpResult);

        textSignUpResult.setText("Sign Up Successfully [Verification Mail Sent]");



    }

    @Override
    public void onClick(View v) {


    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);

    }
}

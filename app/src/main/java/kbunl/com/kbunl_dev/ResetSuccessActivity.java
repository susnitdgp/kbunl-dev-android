package kbunl.com.kbunl_dev;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class ResetSuccessActivity extends AppCompatActivity implements View.OnClickListener  {

    Button buttonSIgnIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_success);

        setTitle("Reset Successful");

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);

    }

}

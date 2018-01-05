package kbunl.com.kbunl_dev;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ResetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    private ProgressDialog mAuthProgressDialog;
    EditText mailText;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitle("Reset Password");

        mAuth = FirebaseAuth.getInstance();


        mailText=(EditText)findViewById(R.id.editTextMail);
        mButton=(Button)findViewById(R.id.buttonResetPassword);

        mButton.setOnClickListener(this);

    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);

    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mailText.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            mailText.setError("Required");
            valid = false;
        } else {
            mailText.setError(null);
        }

        if(!isValidEmail(email)){
            mailText.setError("Not a valid NTPC Email");
            valid = false;
        }else{
            mailText.setError(null);
        }

        return valid;
    }
    public void showProgressDialog(){

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Password Reset");
        mAuthProgressDialog.setMessage("Resetting password");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
    }
    public void hideProgressDialog(){
        mAuthProgressDialog.dismiss();
    }


    @Override
    public void onClick(View v) {

        if (!validateForm()) {
            return;
        }

        String emailAddress=mailText.getText().toString().trim();

        showProgressDialog();

        mAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                                if (task.isSuccessful()) {
                                    Log.d("ResetPasswordActivity:-", "Email sent.");

                                    hideProgressDialog();

                                    Intent intent = new Intent(ResetPasswordActivity.this, ResetSuccessActivity.class);
                                    startActivity(intent);

                                }else{

                                    hideProgressDialog();
                                    String error_message=task.getException().getMessage();
                                    Toast.makeText(ResetPasswordActivity.this, error_message,
                                            Toast.LENGTH_SHORT).show();
                                }



                        }
                    });



    }

    //Validate Email Address
    public static boolean isValidEmail(String email)
    {
        if (email != null)
        {
            Pattern p = Pattern.compile("^[A-Za-z-0-9-]+@ntpc\\.co\\.in$");
            Matcher m = p.matcher(email);
            return m.find();
        }
        return false;
    }


}

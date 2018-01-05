package kbunl.com.kbunl_dev;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "KBUNLPrefsFile";

    private EditText mEmailField;
    private EditText mPasswordField;
    private CheckBox checkBoxSaveUserPassword;

    private ProgressDialog mAuthProgressDialog;
    private Boolean exit = false;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTitle("Login to KBUNL");

        mAuth = FirebaseAuth.getInstance();
        mEmailField = (EditText) findViewById(R.id.field_email);
        mPasswordField = (EditText) findViewById(R.id.field_password);

        checkBoxSaveUserPassword=(CheckBox)findViewById(R.id.checkBoxSaveUserPassword);
        checkBoxSaveUserPassword.setChecked(true);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String savedEmail=settings.getString("EMAIL","");
        String savedPassword=settings.getString("PASSWORD","");
        mEmailField.setText(savedEmail);
        mPasswordField.setText(savedPassword);

        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        //findViewById(R.id.buttonRegister).setOnClickListener(this);
        //findViewById(R.id.buttonResetPassword).setOnClickListener(this);

        findViewById(R.id.link_signup).setOnClickListener(this);
        findViewById(R.id.link_forget_password).setOnClickListener(this);


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //user.isEmailVerified() code to be user
                //if(use)

                if (user != null && user.isEmailVerified()) {
                    // User is signed in
                    Log.d("SignInActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                    //Redirect to Main Activity through Intent Call

                    Intent intent = new Intent(SignInActivity.this, LandingActivity.class);
                    startActivity(intent);

                } else {
                    // User is signed out
                    Log.d("SignInActivity", "onAuthStateChanged:signed_out");
                    //Perform Sign In Activity
                }
                //
            }
        };

    }


    @Override
    public void onBackPressed(){

        Toast.makeText(SignInActivity.this, "Close App from Android Menu",
                Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i==R.id.link_signup){
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        }
        if(i==R.id.link_forget_password){
            Intent intent = new Intent(this, ResetPasswordActivity.class);
            startActivity(intent);
        }

        if (i == R.id.email_sign_in_button){
            signIn(mEmailField.getText().toString().trim(), mPasswordField.getText().toString().trim());
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    //Validate Form
    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Email Required");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        if(!isValidEmail(email)){
            mEmailField.setError("Not a valid NTPC Email");
            valid = false;
        }else{
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Password Required");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    //Sign in Method
    private void signIn(String email, String password) {
        Log.d("SignInActivity", "signIn:" + email);

        if (!validateForm()) {
            return;
        }

        if(!isNetworkAvailable()){
            Toast.makeText(SignInActivity.this, "Data Connection Not Available. Try Again",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("SignInActivity", "signInWithEmail:onComplete:" + task.isSuccessful());

                        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            hideProgressDialog();
                            Log.w("SignInActivity", "signInWithEmail:failed", task.getException());
                            String error_message=task.getException().getMessage();
                            Toast.makeText(SignInActivity.this, error_message,
                                    Toast.LENGTH_SHORT).show();
                        }
                        if(user !=null){
                            if(!user.isEmailVerified()){

                                hideProgressDialog();
                                Toast.makeText(SignInActivity.this, "Email is yet to be verified.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // [START_EXCLUDE]
                            if (task.isSuccessful() && user.isEmailVerified()) {

                                hideProgressDialog();

                                String mail=mEmailField.getText().toString();
                                String pass=mPasswordField.getText().toString();
                                if (checkBoxSaveUserPassword.isChecked()){

                                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                                    SharedPreferences.Editor editor = settings.edit();
                                    editor.putString("EMAIL",mail);
                                    editor.putString("PASSWORD",pass);
                                    editor.commit();

                                }

                                Intent intent = new Intent(SignInActivity.this, LandingActivity.class);
                                startActivity(intent);
                            }
                        }

                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    //Validate [ntpc.co.in] domain Email Address
    public static boolean isValidEmail(String email)
    {
        if (email != null)
        {
                Pattern p = Pattern.compile("^[A-Za-z-0-9-]+@ntpc\\.co\\.in$");
           // Pattern p = Pattern.compile("^[A-Za-z-0-9-]+@gmail\\.com$");
                Matcher m = p.matcher(email);
                return m.find();

        }
        return false;
    }
    //Check Network Availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    //Show Progress Dialogue
    private void showProgressDialog(){

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Authenticating");
        mAuthProgressDialog.setMessage("Authenticating with Email/password");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
    }
    ////Hide Progress Dialogue
    private void hideProgressDialog(){
        mAuthProgressDialog.dismiss();
    }

}

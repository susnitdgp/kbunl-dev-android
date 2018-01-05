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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kbunl.com.kbunl_dev.model.Employee;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private EditText registerName;
    private EditText registerNumber;

    private EditText registerEmail;

    private EditText registerPassword1;

    private Button buttonRegister;
    private ProgressDialog mAuthProgressDialog;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        setTitle("Register User");

        mAuth = FirebaseAuth.getInstance();

        registerName =(EditText)findViewById(R.id.registerName);
        registerNumber =(EditText)findViewById(R.id.registerNumber);
        registerEmail =(EditText)findViewById(R.id.registerEmail);
        registerPassword1 =(EditText)findViewById(R.id.registerPassword1);

        buttonRegister=(Button)findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(this);


        database=FirebaseDatabase.getInstance();
        myRef = database.getReference("EMPLOYEES");
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);

    }


    private boolean validateForm() {
        boolean valid = true;

        String name=registerName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            registerName.setError("EMP Name Required");
            valid = false;
        } else {
            registerName.setError(null);
        }

        String number=registerNumber.getText().toString().trim();
        if (TextUtils.isEmpty(number)) {
            registerNumber.setError("EMP Number Required");
            valid = false;
        } else {
            registerNumber.setError(null);
        }


        String email = registerEmail.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            registerEmail.setError("Email Required");
            valid = false;
        } else {
            registerEmail.setError(null);
        }

        if(!isValidEmail(email)){
            registerEmail.setError("Not a valid NTPC Email");
            valid = false;
        }else{
            registerEmail.setError(null);
        }

        String password = registerPassword1.getText().toString().trim();
        if (TextUtils.isEmpty(password)) {
            registerPassword1.setError("Password Required");
            valid = false;
        } else {
            registerPassword1.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {

        if (!validateForm()) {
            return;
        }

        final String email=registerEmail.getText().toString().trim();
        String password=registerPassword1.getText().toString().trim();

        String employee_name=registerName.getText().toString().trim();
        String employee_number=registerNumber.getText().toString().trim();


        int index=email.indexOf('@');
        final String email_without_domain=email.substring(0,index);

        final Employee emp=new Employee();
        emp.setEmployeeName(employee_name);
        emp.setEmployeeNumber(employee_number);
        emp.setEmail(email);

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("SignUpActivity:-", "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        if (!task.isSuccessful()) {
                            hideProgressDialog();
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }else{

                            final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

                            if(user != null){
                                //Update user Display Name
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(registerName.getText().toString().trim()).build();
                                user.updateProfile(profileUpdates);

                                //Add Other details of user in Real Time Database
                                myRef.child(email_without_domain).setValue(emp);

                                user.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("SignUpActivity", "Email sent.");

                                                }
                                            }
                                        });

                                hideProgressDialog();

                                Intent intent = new Intent(SignUpActivity.this, SignUpSuccessActivity.class);
                                startActivity(intent);
                            }

                        }

                    }
                });

    }

    private void showProgressDialog(){

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Registering");
        mAuthProgressDialog.setMessage("Registering with Email/Password");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
    }
    private void hideProgressDialog(){
        mAuthProgressDialog.dismiss();
    }

    //Validate NTPC Email Address
    public static boolean isValidEmail(String email)
    {

        if (email != null)
        {

                Pattern p = Pattern.compile("^[A-Za-z-0-9-]+@ntpc\\.co\\.in$");
                //Pattern p = Pattern.compile("^[A-Za-z-0-9-]+@gmail\\.com$");
                Matcher m = p.matcher(email);
                return m.find();
        }
        return false;

    }
}

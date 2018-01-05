package kbunl.com.kbunl_dev;

import android.content.Context;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.kbunl_dev.iTComplainService.ITComplainService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kosalgeek.android.photoutil.CameraPhoto;
import com.kosalgeek.android.photoutil.ImageLoader;

import java.io.FileNotFoundException;
import java.io.IOException;

import kbunl.com.kbunl_dev.service.CameraPreview;


public class RegisterITcomplainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String DEBUG_TAG = "RegisterITcomplain";

    private TextView mTextMessage;

    private FirebaseAuth mAuth;
    private String email=null;

    EditText name;

    private EditText complainLocation;
    private EditText complainDescription;
    private Button insertButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_itcomplain);
        setTitle("Register IT Complain");

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.isEmailVerified()) {

            email=user.getEmail();
        }
        name=(EditText)findViewById(R.id.complainUsName);
        name.setText(email);
        name.setEnabled(false);

        complainLocation=(EditText)findViewById(R.id.complainLocation);
        complainDescription=(EditText)findViewById(R.id.complainDescription);
        insertButton=(Button)findViewById(R.id.complainInsertButton);
        insertButton.setOnClickListener(this);


    }

    //Validate Form
    private boolean validateForm() {
        boolean valid = true;


        String location = complainLocation.getText().toString().trim();
        if (TextUtils.isEmpty(location)) {
            complainLocation.setError("Location Required");
            valid = false;
        } else {
            complainLocation.setError(null);
        }


        String desc = complainDescription.getText().toString().trim();
        if (TextUtils.isEmpty(desc)) {
            complainDescription.setError("Description Required");
            valid = false;
        } else {
            complainDescription.setError(null);
        }


        return valid;
    }

    @Override
    public void onClick(View view) {

        int i = view.getId();
        if(i==R.id.complainInsertButton){

            if (!validateForm()) {
                return;
            }
            if(isNetworkAvailable()){
                new RegisterITcomplainActivity.EndpointsAsyncTask().execute(
                        email,"COMPUTER",
                        complainLocation.getText().toString(),
                        complainDescription.getText().toString());

            }else{
                Toast.makeText(this, "Data Network Not Available",
                        Toast.LENGTH_SHORT).show();
            }

        }
    }

    //Check Network Availability
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private class EndpointsAsyncTask extends AsyncTask<String, Void, String> {

        private ITComplainService complainService=null;

        @Override
        protected String doInBackground(String... args) {

            if(complainService == null){

                ITComplainService.Builder builder=new  ITComplainService.Builder(AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(), null);
                complainService=builder.build();
            }
            try{

                return complainService.addComplain(args[0],args[1],args[2],args[3]).execute().getMessage();

            }catch(IOException e){

                return e.getMessage();
            }

        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

            Intent intent = new Intent(getApplicationContext(),ItComplainListActivity.class);
            startActivity(intent);

        }

    }


}

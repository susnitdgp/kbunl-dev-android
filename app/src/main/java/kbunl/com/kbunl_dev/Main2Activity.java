package kbunl.com.kbunl_dev;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.kbunl_dev.iTComplainService.ITComplainService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private Button logOutButton;
    private Button fetchApiButton;
    private Boolean signOut = false;


    private FirebaseAuth mAuth;

    private ProgressDialog mAuthProgressDialog;

    String email=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        mAuth = FirebaseAuth.getInstance();


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {

            email=user.getEmail();
        }

        logOutButton=(Button)findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(this);

        fetchApiButton=(Button)findViewById(R.id.buttonFetchApi);
        fetchApiButton.setOnClickListener(this);


       // Firebase Log reported to Cloud(Crash Reporting)
        FirebaseCrash.report(new Exception("Main2Activity Opened"));

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("test_key");

        final TextView text=(TextView)findViewById(R.id.myTextView);

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("APPS DEBUG CONSOLE:",value);
                text.setText( value);
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.d("APPS DEBUG CONSOLE:",error.getMessage());
                text.setText( "Error" + error.getMessage());
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //return
        /*
        menu.getItem(0).setEnabled(employee.getMainActivity());
        menu.getItem(1).setEnabled(employee.getTeldirActivity());
        menu.getItem(2).setEnabled(employee.getGenDisplayActivity());
        menu.getItem(3).setEnabled(employee.getEnvironmentDisplaytActivity());
        menu.getItem(4).setEnabled(employee.getCoalDisplayActivity());
        menu.getItem(5).setEnabled(employee.getRldcDisplayActivity());
        menu.getItem(6).setEnabled(true);
        */

        super.onPrepareOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.reg_it_complain_activity) {

            if(isNetworkAvailable()){
                Intent intent = new Intent(this, RegisterITcomplainActivity.class);
                startActivity(intent);
                return true;
            }else{
                Toast.makeText(Main2Activity.this, "Data Connection Not Available. Try Again",
                        Toast.LENGTH_SHORT).show();
            }

        }

        if (id == R.id.view_it_complain_activity) {

            if(isNetworkAvailable()){
                Intent intent = new Intent(this, LandingActivity.class);
                startActivity(intent);
                return true;
            }else{
                Toast.makeText(Main2Activity.this, "Data Connection Not Available. Try Again",
                        Toast.LENGTH_SHORT).show();
            }

        }


        if (id == R.id.view_master_detail_activity) {


                Toast.makeText(Main2Activity.this, "Activity Not added. Try Again",
                        Toast.LENGTH_SHORT).show();


        }


        if(id==R.id.apps_about){
            Toast.makeText(Main2Activity.this, "Version:1.0.0.1 build on 02/12/17 16:30PM",
                    Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {

        int i = v.getId();

        if(i==R.id.logOutButton){
            mAuth.signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }

        if(i==R.id.buttonFetchApi){

           new EndpointsAsyncTask().execute(email,"DESKTOP","TEST LOCATION","Test Description");

            //Toast.makeText(Main2Activity.this, "Clicked", Toast.LENGTH_LONG).show();

        }


    }
    /*
    @Override
    public void onBackPressed() {
        // your code.
        if(signOut){
            mAuth.signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }else{

            Toast.makeText(Main2Activity.this, "Press Back again to Sign Out",
                    Toast.LENGTH_SHORT).show();
            signOut=true;
        }

    } */

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showProgressDialog(){

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Please Wait");
        mAuthProgressDialog.setMessage("Profile Details Loading..");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
    }
    private void hideProgressDialog(){
        mAuthProgressDialog.dismiss();
    }


    private class EndpointsAsyncTask extends AsyncTask<String, Void, String> {

        private ITComplainService complainService=null;


        @Override
        protected String doInBackground(String... args) {

            if(complainService == null){

                /*
                ComplainService.Builder builder=new  ComplainService.Builder(AndroidHttp.newCompatibleTransport(),
                        new AndroidJsonFactory(), null)
                        .setRootUrl("http://kbunl-dev.appspot.com/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        }); */
                ITComplainService.Builder builder=new  ITComplainService.Builder(AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(), null);

                complainService=builder.build();
            }


            try{

                //return complainService.addComplain(args[0],args[1]).execute().getMessage();

                return complainService.addComplain(args[0],args[1],args[2],args[3]).execute().getMessage();

            }catch(IOException e){

                return e.getMessage();
            }

        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
            TextView responseView = (TextView) findViewById(R.id.textViewForComplain);
            responseView.setText(result);


        }

    }



}

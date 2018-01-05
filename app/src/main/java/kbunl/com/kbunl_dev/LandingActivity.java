package kbunl.com.kbunl_dev;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class LandingActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView profileImageView;
    private TextView txtProfileName;
    private TextView txtProfileMail;

    private FirebaseAuth mAuth;
    private ProgressDialog mAuthProgressDialog;

    private FirebaseAuth.AuthStateListener mAuthListener;

    private LinearLayout itLayout;
    private LinearLayout civilLayout;
    private LinearLayout elecLayout;
    private LinearLayout safetyLayout;

    int backCount=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        setTitle("Welcome to KBUNL APPS");



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //user.isEmailVerified() code to be user

                if (user != null && user.isEmailVerified()) {
                    // User is signed in
                    Log.d("LandingActivity", "onAuthStateChanged:signed_in:" + user.getUid());


                } else {
                    Intent intent = new Intent(LandingActivity.this, SignInActivity.class);
                    startActivity(intent);

                }
                //
            }
        };

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl("gs://kbunl-dev.appspot.com");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String displayName=user.getDisplayName();

        profileImageView=(ImageView)findViewById(R.id.profileImageView);
        String email=null;
        String email_without_domain=null;
        if (user != null && user.isEmailVerified()) {

            email=user.getEmail();

            int index=email.indexOf('@');
            email_without_domain=email.substring(0,index);
        }

        String imagePath="employee_pics/"+email_without_domain.toLowerCase() + ".jpg";
        StorageReference imagesRef = storageRef.child(imagePath);

        final long ONE_MEGABYTE = 1024 * 1024;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);


        //Landing Activity AppBar Set Up
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.
        setSupportActionBar(toolbar);

        itLayout=(LinearLayout) findViewById(R.id.layout_it_area);
        itLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LandingActivity.this, ItComplainListActivity.class);
                startActivity(intent);

            }
        });
        civilLayout=(LinearLayout)findViewById(R.id.layout_civil_area);
        civilLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Development Under Progress... ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        elecLayout=(LinearLayout)findViewById(R.id.layout_electrical_area);
        elecLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Development Under Progress... ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        safetyLayout=(LinearLayout)findViewById(R.id.layout_safety_area);
        safetyLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Development Under Progress... ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Feedback feature will be enabled soon", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);

        TextView txtProfileName = (TextView)hView.findViewById(R.id.txtProfileName);
        txtProfileMail=(TextView)hView.findViewById(R.id.txtProfileMail);
        profileImageView=(ImageView)hView.findViewById(R.id.profileImageView);

        txtProfileName.setText(displayName);
        txtProfileMail.setText(email);

        imagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed

                Bitmap bm = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                profileImageView.setMinimumHeight(20);
                profileImageView.setMinimumWidth(25);
                profileImageView.setImageBitmap(bm);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("Landing Activity", exception.getMessage());
            }
        });


        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            backCount=0;
        } else {
            backCount=backCount +1;
            if(backCount==1){
                Toast.makeText(LandingActivity.this, "Press Back again to Sign Out",
                        Toast.LENGTH_SHORT).show();

            }else{

                mAuth.signOut();
                Intent intent = new Intent(this, SignInActivity.class);
                startActivity(intent);

            }

            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, AppSettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_register_it_complain) {
            // Handle the camera action
            //Toast.makeText(getApplicationContext(), "Camera is clicked", Toast.LENGTH_SHORT).show();
            if(isNetworkAvailable()){
                Intent intent = new Intent(this, ItComplainListActivity.class);
                startActivity(intent);
            }else{
                Toast.makeText(getApplicationContext(), "Data Network Not available", Toast.LENGTH_SHORT).show();
            }


        } else if (id == R.id.nav_register_civil_complain) {


                Toast.makeText(getApplicationContext(), "Activity Under Progress", Toast.LENGTH_SHORT).show();



        }else if (id == R.id.nav_log_out) {

            mAuth.signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void showProgressDialog(String message){

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Please Wait");
        mAuthProgressDialog.setMessage(message);
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
    }
    private void hideProgressDialog(){
        mAuthProgressDialog.dismiss();
    }
}

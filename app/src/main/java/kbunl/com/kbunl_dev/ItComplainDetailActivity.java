package kbunl.com.kbunl_dev;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.appspot.kbunl_dev.iTComplainService.ITComplainService;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import kbunl.com.kbunl_dev.model.ItComplainRoles;

/**
 * An activity representing a single ItComplain detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItComplainListActivity}.
 */
public class ItComplainDetailActivity extends AppCompatActivity {



    private FirebaseAuth mAuth=null;
    String email=null;
    ItComplainRoles roles=null;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    FloatingActionButton fab=null;

    ItComplainDetailFragment fragment=null;
    FragmentTransaction ft=null;

    String status=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itcomplain_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {

            email=user.getEmail();
        }
        int index=email.indexOf('@');
        final String email_without_domain=email.substring(0,index);
        status=getIntent().getStringExtra(ItComplainDetailFragment.ARG_ITEM_STATUS);
        database=FirebaseDatabase.getInstance();
        myRef = database.getReference("EMPLOYEES/"+email_without_domain + "/roles");


        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.it_complain_assign_dialog);
        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Endpoint Method Call
                new EndpointsAsyncTask().execute(getIntent().getStringExtra(ItComplainDetailFragment.ARG_ITEM_ID),
                        email,"vinod@gmail.com");

                dialog.dismiss();
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "MY Text:" + getIntent().getStringExtra(ItComplainDetailFragment.ARG_ITEM_ID), Snackbar.LENGTH_LONG)
                       // .setAction("Action", null).show();

                dialog.show();
            }
        });

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roles = dataSnapshot.getValue(ItComplainRoles.class);

                Log.d("ItComplainDetActivity",roles.getItComplainSupervisor().toString());
                if (roles.getItComplainSupervisor()){
                   fab.setVisibility(View.VISIBLE);

                }
                if(!roles.getItComplainSupervisor()){
                    fab.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        //fab.setVisibility(View.GONE);
        //fab.setEnabled(false);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(ItComplainDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItComplainDetailFragment.ARG_ITEM_ID));
            fragment = new ItComplainDetailFragment();
            fragment.setArguments(arguments);
            ft=getSupportFragmentManager().beginTransaction();

            ft.add(R.id.itcomplain_detail_container, fragment);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, ItComplainListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
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

                return complainService.assignComplain(args[0],args[1],args[2]).execute().getMessage();

            }catch(IOException e){

                return e.getMessage();
            }

        }
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();

            ft.detach(fragment);
            ft.attach(fragment);

            //ft.commit();

        }

    }
}

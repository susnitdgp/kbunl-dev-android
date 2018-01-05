package kbunl.com.kbunl_dev;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.appspot.kbunl_dev.iTComplainService.ITComplainService;
import com.appspot.kbunl_dev.iTComplainService.model.ItComplain;
import com.appspot.kbunl_dev.iTComplainService.model.ItComplainCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kosalgeek.android.photoutil.CameraPhoto;

import kbunl.com.kbunl_dev.model.Employee;
import kbunl.com.kbunl_dev.model.ItComplainRoles;
import kbunl.com.kbunl_dev.model.ListModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Complains. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItComplainDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItComplainListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private ProgressDialog mAuthProgressDialog;
    private boolean mTwoPane;
    List<ListModel> mobileArray = new ArrayList<ListModel>();
    View recyclerView=null;

    private FirebaseAuth mAuth=null;
    String email=null;
    ItComplainRoles roles=null;

    private FirebaseDatabase database;
    private DatabaseReference myRef;
    BottomNavigationView navigation=null;
    String titleText="Complains List";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itcomplain_list);
        setTitle(titleText);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && user.isEmailVerified()) {

            email=user.getEmail();
        }
        int index=email.indexOf('@');
        final String email_without_domain=email.substring(0,index);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_register_it_complain);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                  //      .setAction("Action", null).show();
                Intent intent=new Intent(view.getContext(),RegisterITcomplainActivity.class);
                startActivity(intent);
            }
        });
        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (findViewById(R.id.itcomplain_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        if(isNetworkAvailable()){

            showProgressDialog("Loading Complain List");
            new ItComplainListActivity.EndpointsAsyncTask().execute(email,"NEW","USER");

        }else{
            Toast.makeText(getApplicationContext(), "Data Connection not available", Toast.LENGTH_LONG).show();
        }


        recyclerView = findViewById(R.id.itcomplain_list);
        assert recyclerView != null;
       //setupRecyclerView((RecyclerView) recyclerView);

        navigation = (BottomNavigationView) findViewById(R.id.complain_list_bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        database=FirebaseDatabase.getInstance();
        myRef = database.getReference("EMPLOYEES/"+email_without_domain + "/roles");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                roles = dataSnapshot.getValue(ItComplainRoles.class);

                Log.d("ItComplainListActivity",roles.getItComplainSupervisor().toString());
                if (roles.getItComplainSupervisor()){
                   //Menu bottomMenu=navigation.getMenu();
                  // bottomMenu.add();

                }
                if(!roles.getItComplainSupervisor()){
                    Menu bottomMenu=navigation.getMenu();
                    bottomMenu.removeItem(R.id.complain_navigation_supervisor);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, mobileArray, mTwoPane));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {

                case R.id.complain_navigation_pending:

                    mobileArray.clear();
                    showProgressDialog("Loading New complaints");
                    //Toast.makeText(getApplicationContext(), "Assigned Fired", Toast.LENGTH_LONG).show();
                    new ItComplainListActivity.EndpointsAsyncTask().execute(email,"NEW","USER");

                    break;
                case R.id.complain_navigation_assigned:

                    mobileArray.clear();
                    showProgressDialog("Loading Assigned complaints");
                    //Toast.makeText(getApplicationContext(), "Assigned Fired", Toast.LENGTH_LONG).show();
                    new ItComplainListActivity.EndpointsAsyncTask().execute(email,"ASSIGNED","USER");

                    break;
                case R.id.complain_navigation_completed:
                    //Toast.makeText(getApplicationContext(), "Completed Event Fired", Toast.LENGTH_LONG).show();
                    mobileArray.clear();
                    showProgressDialog("Loading completed complaints");
                    //Toast.makeText(getApplicationContext(), "Assigned Fired", Toast.LENGTH_LONG).show();
                    new ItComplainListActivity.EndpointsAsyncTask().execute(email,"COMPLETED","USER");
                    break;

                case R.id.complain_navigation_partial:
                    //Toast.makeText(getApplicationContext(), "Completed Event Fired", Toast.LENGTH_LONG).show();
                    mobileArray.clear();
                    showProgressDialog("Loading Assigned complaints");
                    //Toast.makeText(getApplicationContext(), "Assigned Fired", Toast.LENGTH_LONG).show();
                    new ItComplainListActivity.EndpointsAsyncTask().execute(email,"COMPLETED","USER");
                    break;

                case R.id.complain_navigation_supervisor:
                    mobileArray.clear();
                    showProgressDialog("Loading All Pending Complaints");
                    new ItComplainListActivity.EndpointsAsyncTask().execute(email,"NEW","SUPERVISOR");
                    //Toast.makeText(getApplicationContext(), "Supervisor Event Fired", Toast.LENGTH_LONG).show();
                    break;


            }
            return true;
        }
    };
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showProgressDialog(String message){

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Please Wait");
        mAuthProgressDialog.setMessage(message);
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
    }
    private void hideProgressDialog(){
        mAuthProgressDialog.dismiss();
    }


    // Recycler View Adapter Class Start
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItComplainListActivity mParentActivity;
        private final List<ListModel> mValues;
        private final boolean mTwoPane;



        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListModel item = (ListModel) view.getTag();

                if(mParentActivity.isNetworkAvailable()){

                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItComplainDetailFragment.ARG_ITEM_ID, item.getId());
                        ItComplainDetailFragment fragment = new ItComplainDetailFragment();
                          fragment.setArguments(arguments);
                        mParentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.itcomplain_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = view.getContext();
                        Intent intent = new Intent(context, ItComplainDetailActivity.class);
                        intent.putExtra(ItComplainDetailFragment.ARG_ITEM_ID, item.getId());
                        intent.putExtra(ItComplainDetailFragment.ARG_ITEM_STATUS,item.getStatus());

                        context.startActivity(intent);
                    }
                }else{

                    Toast.makeText(mParentActivity.getApplication(), "Data Connection Not available", Toast.LENGTH_LONG).show();
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ItComplainListActivity parent,
                                      List<ListModel> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.itcomplain_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getLocation());
            holder.mContentView.setText(mValues.get(position).getDesc());
            holder.mTimeView.setText(mValues.get(position).getTim());

            switch(mValues.get(position).getType()){
                case "COMPUTER":
                    holder.listImage.setImageResource(R.drawable.ic_desktop_windows_black_8dp);
                    break;
                case"UPS":
                    holder.listImage.setImageResource(R.drawable.ic_print_black_24dp);
                    break;
                default:
                    holder.listImage.setImageResource(R.drawable.ic_fiber_new_black_24dp);


            }

            holder.itemView.setTag(mValues.get(position));

            holder.itemView.setOnClickListener(mOnClickListener);


        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;
            final TextView mTimeView;
            final ImageView listImage;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.it_complain_location);
                mContentView = (TextView) view.findViewById(R.id.it_complain_desc);
                mTimeView = (TextView) view.findViewById(R.id.it_complain_time);
                listImage=(ImageView) view.findViewById(R.id.list_image);
            }
        }
    }//Recycler view class end

    private class EndpointsAsyncTask extends AsyncTask<String, Void, ItComplainCollection> {

        private ITComplainService complainService=null;

        @Override
        protected ItComplainCollection doInBackground(String... strings) {
            if(complainService == null){

                ITComplainService.Builder builder=new  ITComplainService.Builder(AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(), null);

                complainService=builder.build();
            }
            try{

                return complainService.listComUser(strings[0],strings[1],strings[2]).execute();

            }catch(IOException e){

                return null;
            }
        }

        @Override
        protected void onPostExecute(ItComplainCollection complaints) {
           //Toast.makeText(getApplicationContext(), "Size is: " + complaints.getItems().size(), Toast.LENGTH_LONG).show();
           List<ItComplain> lst=complaints.getItems();

            int i=1;
            for (ItComplain complain:lst){
                //Log.d("DATASTORE: " ,complain.getDescription());
                ListModel model=new ListModel();

                model.setId(Long.toString(complain.getNumber()));
                model.setDesc(complain.getDescription());
                model.setLocation(complain.getLocation());
                model.setType(complain.getType());
                //model.setTim(complain.getTime().substring(0,20));
                model.setTim(complain.getTime());
                model.setStatus(complain.getStatus());
                mobileArray.add(model);

                i=i+1;
            }
            hideProgressDialog();
            setupRecyclerView((RecyclerView) recyclerView);

        }

    }

}

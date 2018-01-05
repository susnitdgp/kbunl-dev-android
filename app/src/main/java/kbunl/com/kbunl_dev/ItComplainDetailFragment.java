package kbunl.com.kbunl_dev;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.appspot.kbunl_dev.iTComplainService.ITComplainService;
import com.appspot.kbunl_dev.iTComplainService.model.ItComplain;
import com.appspot.kbunl_dev.iTComplainService.model.ItComplainCollection;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.json.gson.GsonFactory;

import java.io.IOException;

import kbunl.com.kbunl_dev.model.ListModel;

/**
 * A fragment representing a single ItComplain detail screen.
 * This fragment is either contained in a {@link ItComplainListActivity}
 * in two-pane mode (on tablets) or a {@link ItComplainDetailActivity}
 * on handsets.
 */
public class ItComplainDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_STATUS= "NEW";

    private ProgressDialog mAuthProgressDialog;

    /**
     * The dummy content this fragment is presenting.
     */
    private ListModel mItem=new ListModel();

    TextView it_complain_by_display=null;
    TextView it_complain_description_display=null;
    TextView it_complain_status_display=null;
    TextView it_complain_time_display=null;
    TextView it_complain_location_display=null;
    TextView it_complain_assignby_display=null;
    TextView it_complain_assigntime_display=null;
    TextView it_complain_assignto_display=null;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItComplainDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

           // mItem = DummyContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));


            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                //appBarLayout.setTitle(mItem.getId());
                appBarLayout.setTitle(getArguments().getString(ARG_ITEM_ID));
            }
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.itcomplain_detail, container, false);

        // Show the dummy content as text in a TextView.
        //if (mItem != null) {
            it_complain_by_display=((TextView) rootView.findViewById(R.id.it_complain_by_display));
            it_complain_description_display=((TextView) rootView.findViewById(R.id.it_complain_description_display));
            it_complain_status_display=((TextView) rootView.findViewById(R.id.it_complain_status_display));
            it_complain_time_display=((TextView) rootView.findViewById(R.id.it_complain_time_display));
            it_complain_location_display=((TextView) rootView.findViewById(R.id.it_complain_location_display));
            it_complain_assignby_display=((TextView) rootView.findViewById(R.id.it_complain_assignby_display));
            it_complain_assigntime_display=((TextView) rootView.findViewById(R.id.it_complain_assigntime_display));
            it_complain_assignto_display=((TextView) rootView.findViewById(R.id.it_complain_assignto_display));
       // }

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(isNetworkAvailable()){
            //Toast.makeText(getActivity(), "Data Connection available", Toast.LENGTH_LONG).show();
            showProgressDialog();
            new ItComplainDetailFragment.EndpointsAsyncTask().execute(getArguments().getString(ARG_ITEM_ID));
        }else{

            Toast.makeText(getActivity(), "Data Connection not available", Toast.LENGTH_LONG).show();
        }


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private void showProgressDialog(){

        mAuthProgressDialog = new ProgressDialog(this.getActivity());
        mAuthProgressDialog.setTitle("Please Wait");
        mAuthProgressDialog.setMessage("Complain Details Loading..");
        mAuthProgressDialog.setCancelable(false);
        mAuthProgressDialog.show();
    }
    private void hideProgressDialog(){
        mAuthProgressDialog.dismiss();
    }

    private class EndpointsAsyncTask extends AsyncTask<String, Void, ItComplain> {

        private ITComplainService complainService=null;

        @Override
        protected ItComplain doInBackground(String... strings) {
            if(complainService == null){

                ITComplainService.Builder builder=new  ITComplainService.Builder(AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(), null);

                complainService=builder.build();
            }
            try{


                return complainService.detailComplain(strings[0]).execute();

            }catch(IOException e){

                return null;
            }
        }

        @Override
        protected void onPostExecute(ItComplain complain) {
            //Toast.makeText(getActivity(), "Size is: " + complain.getNumber(), Toast.LENGTH_LONG).show();
            hideProgressDialog();
            String number=complain.getNumber().toString();
            mItem.setId("test2222");
            mItem.setDesc(complain.getDescription());
            mItem.setTim(complain.getLocation());

            it_complain_by_display.setText(complain.getUser());

            it_complain_time_display.setText( complain.getTime());
            it_complain_location_display.setText(complain.getLocation());
            it_complain_description_display.setText(complain.getDescription());
            it_complain_status_display.setText(complain.getStatus());
            it_complain_assignby_display.setText(complain.getAssignby());
            it_complain_assigntime_display.setText(complain.getAssigntime());
            it_complain_assignto_display.setText(complain.getAssignto());
        }

    }


}

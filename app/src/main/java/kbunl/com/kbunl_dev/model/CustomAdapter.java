package kbunl.com.kbunl_dev.model;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.zip.Inflater;

import kbunl.com.kbunl_dev.R;

/**
 * Created by Admin on 12/16/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context context;
    List<ListModel> complainList;

    LayoutInflater inflter;

    public CustomAdapter(Context applicationContext, List<ListModel> complainList) {
        this.context = context;
        this.complainList = complainList;

        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return complainList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
       /*

        view = inflter.inflate(R.layout.it_complain_list_view, null);
        TextView labelDescription = (TextView)view.findViewById(R.id.complainLabelDescription);
       // TextView labelTime = (TextView)view.findViewById(R.id.complainLabelTime);
        //ImageView icon = (ImageView)view.findViewById(R.id.icon);
        labelDescription.setText(complainList.get(i).getDesc());
       // labelTime.setText(complainList.get(i).getTim());
        //icon.setImageResource(flags[i]);
        return view; */
       return null;
    }
}

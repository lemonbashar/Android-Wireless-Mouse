package com.garland.wifimouse.utility;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.garland.wifimouse.R;

import java.util.List;

/**
 * Created by lemon on 9/13/2017.
 */

public class ConnectionAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<IpDetails> detailsList;

    public ConnectionAdapter(LayoutInflater inflater, List<IpDetails> detailsList) {
        this.inflater = inflater;
        this.detailsList = detailsList;
    }

    @Override
    public int getCount() {
        return detailsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if(view==null)
            view=inflater.inflate(R.layout.list_item,null);
        ((TextView)view.findViewById(R.id.list_item_text)).setText(detailsList.get(position).getDeviceName());
        return view;
    }
}

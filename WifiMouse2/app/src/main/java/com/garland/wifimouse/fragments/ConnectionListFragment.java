package com.garland.wifimouse.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.garland.wifimouse.MainActivity;
import com.garland.wifimouse.R;
import com.garland.wifimouse.events.FragmentListener;
import com.garland.wifimouse.utility.Configure;
import com.garland.wifimouse.utility.ConnectionAdapter;
import com.garland.wifimouse.utility.IpDetails;

import java.util.List;

/**
 * Created by lemon on 9/12/2017.
 */

public class ConnectionListFragment extends Fragment {
    private FragmentListener listener;
    private ListView listView;
    private ConnectionAdapter adapter;
    private View mainView;
    private List<IpDetails> ipDetailsList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_connection_list,container,false);
        setHasOptionsMenu(true);
        ipDetailsList= Configure.fetchConnectionList();

        if (!ipDetailsList.isEmpty()) {
            listView= (ListView) mainView.findViewById(R.id.list_view);
            adapter=new ConnectionAdapter(inflater,ipDetailsList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("Details Information");
                    builder.setMessage(ipDetailsList.get(position).getContentDescription());
                    builder.setCancelable(true);
                    builder.setNegativeButton(R.string.cancel,null);
                    builder.setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            connectPc(ipDetailsList.get(position).getIpAddress());
                        }
                    });

                    builder.create().show();
                }
            });
        }
        else {
            MainActivity.setCurrentMsg("Your device is not connect to any other device...\nPlease connect your device by turn wifi hotspot...");
            MainActivity.setCurrentTitle("Connection List Error!");
            new DialogRequest().show(getActivity().getSupportFragmentManager(),"");
        }

        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_connection_list,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if(item.getItemId()==R.id.action_refresh) {
                List<IpDetails> ips=Configure.fetchConnectionList();
                if(!ips.isEmpty()){
                    ipDetailsList.clear();
                    ipDetailsList.addAll(ips);
                    adapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            MainActivity.crushReport(e);
        }
        return super.onOptionsItemSelected(item);
    }

    public void setListener(FragmentListener listener) {
        this.listener = listener;
    }

    private void connectPc(String ipAddress) {
        if(listener!=null) listener.onListen(ipAddress);
    }

}

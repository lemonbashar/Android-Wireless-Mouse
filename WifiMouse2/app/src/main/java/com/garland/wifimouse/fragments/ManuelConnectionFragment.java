package com.garland.wifimouse.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.garland.wifimouse.MainActivity;
import com.garland.wifimouse.R;
import com.garland.wifimouse.events.FragmentListener;
import com.garland.wifimouse.utility.Report;

/**
 * Created by lemon on 9/13/2017.
 */

public class ManuelConnectionFragment extends Fragment {
    private View mainView;
    private FragmentListener listener;
    private TextView textView;
    private EditText editText;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.fragment_manuel_connect,container,false);
        try {
            textView= (TextView) mainView.findViewById(R.id.id_ip_control_text);
            editText= (EditText) mainView.findViewById(R.id.input_ip);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    textView.setText("");
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(s.toString().isEmpty()) textView.setText(R.string.valid_ip_msg);
                    else if(!s.toString().matches(Report.PATTERN_IP_ADDRESS))
                        textView.setText(R.string.ip_msg);
                    else textView.setText(R.string.msg_apply);
                }
            });
            mainView.findViewById(R.id.ip_apply_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String input=editText.getText().toString();
                    if(input.matches(Report.PATTERN_IP_ADDRESS)){
                        if(listener!=null)
                            listener.onListen(input);
                        editText.setText("");
                    }
                    else {
                        MainActivity.setCurrentMsg("Please Enter Correct Ip Address");
                        MainActivity.setCurrentTitle("IP Address Fact!");
                        new DialogRequest().show(getActivity().getSupportFragmentManager(),"");
                    }
                }
            });
        } catch (Exception e) {
            MainActivity.crushReport(e);
        }
        return mainView;
    }

    public void setListener(FragmentListener listener) {
        this.listener = listener;
    }
}

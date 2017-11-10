package com.garland.wifimouse.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.garland.wifimouse.MainActivity;


/**
 * Created by lemon on 8/21/2017.
 */

public class DialogRequest extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle(MainActivity.getCurrentTitle());
        builder.setMessage(MainActivity.getCurrentMsg());
        builder.setNegativeButton("Ok",null);
        builder.setCancelable(true);

        return builder.create();
    }
}

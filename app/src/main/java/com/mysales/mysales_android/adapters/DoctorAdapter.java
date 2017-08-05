package com.mysales.mysales_android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mysales.mysales_android.R;
import com.mysales.mysales_android.models.Doctor;

import java.util.ArrayList;

/**
 * Created by wfsiew on 8/5/17.
 */

public class DoctorAdapter extends ArrayAdapter<Doctor> {

    private LayoutInflater inflater;

    public DoctorAdapter(@NonNull Context context, ArrayList<Doctor> items) {
        super(context, 0, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        Doctor o = (Doctor) getItem(position);
        v = inflater.inflate(R.layout.list_doctor, null);
        TextView txtname = (TextView) v.findViewById(R.id.txtname);
        TextView txtphone = (TextView) v.findViewById(R.id.txtphone);
        TextView txtemail = (TextView) v.findViewById(R.id.txtemail);

        txtname.setText(o.getName());
        txtphone.setText(o.getPhone());
        txtemail.setText(o.getEmail());

        return v;
    }
}

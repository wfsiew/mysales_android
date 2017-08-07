package com.mysales.mysales_android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.mysales.mysales_android.R;
import com.mysales.mysales_android.models.Doctor;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wfsiew on 8/5/17.
 */

public class DoctorAdapter extends ArrayAdapter<Doctor> {

    private LayoutInflater inflater;
    private boolean showSelect;
    private Button btndel;
    private HashMap<Integer, Integer> selected;
    private final ArrayList<Doctor> items;

    public DoctorAdapter(@NonNull Context context, ArrayList<Doctor> items, Button btndel) {
        super(context, 0, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.btndel = btndel;
        this.items = items;
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

    public boolean toggleSelect() {
        showSelect = !showSelect;
        return showSelect;
    }

    public String getSelectedIds() {
        if (selected == null)
            return null;

        StringBuffer sb = new StringBuffer();
        for (Integer i : selected.keySet()) {
            sb.append(i + ",");
        }

        String r = sb.substring(0, sb.length() - 1);
        return r;
    }

    public String getIds() {
        StringBuffer sb = new StringBuffer();
        for (Doctor o : items) {
            sb.append(o.getId() + ",");
        }

        String r = sb.substring(0, sb.length() - 1);
        return r;
    }

    public void select(View v, int position) {
        if (v != null) {
            CheckBox chk = (CheckBox) v.findViewById(R.id.chk);
            chk.setChecked(!chk.isChecked());
            doSelect(chk, position);
        }
    }

    private void doSelect(CheckBox chk, int position) {
        if (selected == null)
            selected = new HashMap<Integer, Integer>();

        if (chk.isChecked())
            selected.put(items.get(position).getId(), 1);

        else
            selected.remove(items.get(position).getId());

        btndel.setEnabled(!selected.isEmpty());
    }
}

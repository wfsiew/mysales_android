package com.mysales.mysales_android.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.mysales.mysales_android.R;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.models.Doctor;

import java.security.GuardedObject;
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

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View v = inflater.inflate(R.layout.list_doctor, null);
        TextView txtname = v.findViewById(R.id.txtname);
        TextView txtphone = v.findViewById(R.id.txtphone);
        TextView txthp = v.findViewById(R.id.txthp);
        TextView txtemail = v.findViewById(R.id.txtemail);
        TextView lbphone = v.findViewById(R.id.lbphone);
        TextView lbhp = v.findViewById(R.id.lbhp);
        TextView lbemail = v.findViewById(R.id.lbemail);
        TextView txtday = v.findViewById(R.id.txtday);
        TextView txtcust = v.findViewById(R.id.txtcust);
        CheckBox chk = v.findViewById(R.id.chk);

        Doctor o = getItem(position);
        txtname.setText(o.getName());
        txtphone.setText(o.getPhone());
        txthp.setText(o.getHp());
        txtemail.setText(o.getEmail());
        txtday.setText(o.getShortDays());
        txtcust.setText(String.format("%s - %s", o.getCustCode(), o.getCustName()));
        chk.setVisibility(showSelect ? View.VISIBLE : View.GONE);

        lbphone.setVisibility(Utils.isEmpty(o.getPhone()) ? View.GONE : View.VISIBLE);
        lbhp.setVisibility(Utils.isEmpty(o.getHp()) ? View.GONE : View.VISIBLE);
        lbemail.setVisibility(Utils.isEmpty(o.getEmail()) ? View.GONE : View.VISIBLE);
        txtphone.setVisibility(lbphone.getVisibility());
        txthp.setVisibility(lbhp.getVisibility());
        txtemail.setVisibility(lbemail.getVisibility());
        txtday.setVisibility(Utils.isEmpty(o.getShortDays()) ? View.GONE : View.VISIBLE);

        chk.setTag(position);
        chk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox c = (CheckBox) view;
                int i = (int) c.getTag();
                doSelect(c, i);
            }
        });

        return v;
    }

    public boolean toggleSelect() {
        showSelect = !showSelect;
        return showSelect;
    }

    public String getSelectedIds() {
        if (selected == null)
            return null;

        StringBuilder sb = new StringBuilder();
        for (Integer i : selected.keySet()) {
            sb.append(i).append(",");
        }

        return sb.substring(0, sb.length() - 1);
    }

    public String getIds() {
        StringBuilder sb = new StringBuilder();
        for (Doctor o : items) {
            sb.append(o.getId()).append(",");
        }

        return sb.substring(0, sb.length() - 1);
    }

    public void select(View v, int position) {
        if (v != null) {
            CheckBox chk = v.findViewById(R.id.chk);
            chk.setChecked(!chk.isChecked());
            doSelect(chk, position);
        }
    }

    @SuppressLint("UseSparseArrays")
    private void doSelect(CheckBox chk, int position) {
        if (selected == null) {
            selected = new HashMap<>();
        }

        if (chk.isChecked()) {
            selected.put(items.get(position).getId(), 1);
        }

        else {
            selected.remove(items.get(position).getId());
        }

        btndel.setEnabled(!selected.isEmpty());
    }
}

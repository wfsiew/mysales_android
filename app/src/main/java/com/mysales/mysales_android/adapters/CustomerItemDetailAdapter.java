package com.mysales.mysales_android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mysales.mysales_android.R;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.models.CustomerItem;

import java.util.ArrayList;

/**
 * Created by wfsiew on 8/2/17.
 */

public class CustomerItemDetailAdapter extends ArrayAdapter {

    private LayoutInflater inflater;

    public CustomerItemDetailAdapter(@NonNull Context context, ArrayList<CustomerItem> items) {
        super(context, 0, items);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        CustomerItem o = (CustomerItem) getItem(position);

        if (o.isHeader()) {
            v = inflater.inflate(R.layout.section_customer_item_detail_header, null);

            v.setClickable(false);

            TextView header = (TextView) v.findViewById(R.id.section_header);
            header.setText(o.getHeader());
        }

        else if (o.isFooter()) {
            v = inflater.inflate(R.layout.section_customer_item_detail_footer, null);

            v.setClickable(false);

            TextView footer = (TextView) v.findViewById(R.id.section_val);

            String val = String.format("%d (Sales Unit) %d (Bonus Unit) %s (Sales Value)", o.getSumunit(),
                    o.getSumbonus(), Utils.formatDouble(o.getSumvalue()));
            footer.setText(val);
        }

        else {
            v = inflater.inflate(R.layout.list_customer_item_detail, null);
            TextView txtitem = (TextView) v.findViewById(R.id.txtitem);
            TextView txtval = (TextView) v.findViewById(R.id.txtval);

            txtitem.setText(o.getItem());
            String val = String.format("%d (Sales Unit) %d (Bonus Unit) %s (Sales Value)", o.getUnit(),
                    o.getBonus(), Utils.formatDouble(o.getValue()));
            txtval.setText(val);
        }

        return v;
    }
}

package com.mysales.mysales_android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import com.mysales.mysales_android.R;
import com.mysales.mysales_android.models.Customer;

import java.util.ArrayList;

/**
 * Created by wingfei.siew on 8/8/2017.
 */

public class CustomerAdapter extends ArrayAdapter<Customer> {

    private final ArrayList<Customer> items;

    public CustomerAdapter(@NonNull Context context, ArrayList<Customer> items) {
        super(context, android.R.layout.simple_spinner_item, items);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.items = items;
    }

    public int getPosition(String code, String name) {
        int k = 0;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getCode().equals(code) &&
                    items.get(i).getName().equals(name)) {
                k = i;
                break;
            }
        }

        return k;
    }
}

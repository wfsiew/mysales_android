package com.mysales.mysales_android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysales.mysales_android.R;
import com.mysales.mysales_android.models.Customer;

/**
 * Created by wingfei.siew on 7/31/2017.
 */

public class CustomerItemRecyclerViewAdapter
        extends RecyclerView.Adapter<CustomerItemRecyclerViewAdapter.ViewHolder> {

    private final Customer[] values;

    public CustomerItemRecyclerViewAdapter(Customer[] items) {
        values = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_customer_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (values == null)
            return;

        holder.item = values[position];
        Customer customer = values[position];
        holder.txtcustname.setText(customer.getName());
        holder.txtcustcode.setText(customer.getCode());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
            }
        });
    }

    @Override
    public int getItemCount() {
        return values == null ? 0 :  values.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View view;
        public final TextView txtcustname;
        public final TextView txtcustcode;
        public Customer item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            txtcustname = (TextView) view.findViewById(R.id.txtcustname);
            txtcustcode = (TextView) view.findViewById(R.id.txtcustcode);
        }
    }
}

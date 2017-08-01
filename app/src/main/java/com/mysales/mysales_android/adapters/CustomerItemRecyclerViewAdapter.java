package com.mysales.mysales_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysales.mysales_android.CustomerItemDetailActivity;
import com.mysales.mysales_android.R;
import com.mysales.mysales_android.models.Customer;

/**
 * Created by wingfei.siew on 7/31/2017.
 */

public class CustomerItemRecyclerViewAdapter
        extends RecyclerView.Adapter<CustomerItemRecyclerViewAdapter.ViewHolder> {

    private final Customer[] values;
    private String period;
    private String year;

    public CustomerItemRecyclerViewAdapter(Customer[] items, String period, String year) {
        values = items;
        this.period = period;
        this.year = year;
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
        final Customer customer = values[position];
        holder.txtcustname.setText(customer.getName());
        holder.txtcustcode.setText(customer.getCode());

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, CustomerItemDetailActivity.class);
                intent.putExtra(CustomerItemDetailActivity.ARG_CUST, customer.getCode());
                intent.putExtra(CustomerItemDetailActivity.ARG_CUST_NAME, customer.getName());
                intent.putExtra(CustomerItemDetailActivity.ARG_PERIOD, period);
                intent.putExtra(CustomerItemDetailActivity.ARG_YEAR, year);

                context.startActivity(intent);
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

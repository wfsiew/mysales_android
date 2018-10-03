package com.mysales.mysales_android.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysales.mysales_android.AddDoctorActivity;
import com.mysales.mysales_android.CustomerItemDetailActivity;
import com.mysales.mysales_android.R;
import com.mysales.mysales_android.models.Customer;

/**
 * Created by wingfei.siew on 7/31/2017.
 */

public class CustomerItemRecyclerViewAdapter
        extends RecyclerView.Adapter<CustomerItemRecyclerViewAdapter.ViewHolder> {

    private final Customer[] values;
    private String item;
    private String productgroup;
    private String territory;
    private String period;
    private String year;

    public CustomerItemRecyclerViewAdapter(Customer[] items, String item, String productgroup, String territory, String period, String year) {
        values = items;
        this.item = item;
        this.productgroup = productgroup;
        this.territory = territory;
        this.period = period;
        this.year = year;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_customer, parent, false);
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
                intent.putExtra(CustomerItemDetailActivity.ARG_ITEM, item);
                intent.putExtra(CustomerItemDetailActivity.ARG_PRODUCT_GROUP, productgroup);
                intent.putExtra(CustomerItemDetailActivity.ARG_PERIOD, period);
                intent.putExtra(CustomerItemDetailActivity.ARG_YEAR, year);

                context.startActivity(intent);
            }
        });

        holder.view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Context context = view.getContext();
                Intent intent = new Intent(context, AddDoctorActivity.class);
                intent.putExtra(AddDoctorActivity.ARG_CUST, customer.getCode());
                intent.putExtra(AddDoctorActivity.ARG_CUST_NAME, customer.getName());

                context.startActivity(intent);
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return values == null ? 0 :  values.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View view;
        final TextView txtcustname;
        final TextView txtcustcode;
        Customer item;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            txtcustname = view.findViewById(R.id.txtcustname);
            txtcustcode = view.findViewById(R.id.txtcustcode);
        }
    }
}

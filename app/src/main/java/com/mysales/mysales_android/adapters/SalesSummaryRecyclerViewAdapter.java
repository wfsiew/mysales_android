package com.mysales.mysales_android.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mysales.mysales_android.R;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.models.SalesSummary;

/**
 * Created by wfsiew on 3/22/18.
 */

public class SalesSummaryRecyclerViewAdapter
        extends RecyclerView.Adapter<SalesSummaryRecyclerViewAdapter.ViewHolder> {

    private final SalesSummary[] values;

    public SalesSummaryRecyclerViewAdapter(SalesSummary[] items) {
        values = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_salessummary, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (values == null)
            return;

        holder.item = values[position];
        final SalesSummary salesSummary = values[position];
        holder.txtproductgroup.setText(salesSummary.getProductGroup());
        holder.txtactual.setText(String.format("Actual: %s", Utils.formatDouble(salesSummary.getActual())));
        holder.txttarget.setText(String.format("Target: %s (%d %%, %s)",
                Utils.formatDouble(salesSummary.getTarget()),
                salesSummary.getActualvsTarget(),
                Utils.formatDouble(salesSummary.getActualvsTargetDiff())
                ));
        holder.txtprevactual.setText(String.format("Last Year: %s (%d %%, %s)",
                Utils.formatDouble(salesSummary.getActual1()),
                salesSummary.getActualvsPrevYear(),
                Utils.formatDouble(salesSummary.getActualvsPrevYearDiff())
                ));
    }

    @Override
    public int getItemCount() {
        return values == null ? 0 :  values.length;
    }

    private int getPercentage(double a, double b) {
        return (int) Math.round((a * 100) / b);
    }

    private double getDiff(double a, double b) {
        return a - b;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        final View view;
        final TextView txtproductgroup;
        final TextView txtactual;
        final TextView txttarget;
        final TextView txtprevactual;
        SalesSummary item;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            txtproductgroup = view.findViewById(R.id.txtproductgroup);
            txtactual = view.findViewById(R.id.txtactual);
            txttarget = view.findViewById(R.id.txttarget);
            txtprevactual = view.findViewById(R.id.txtprevactual);
        }
    }
}

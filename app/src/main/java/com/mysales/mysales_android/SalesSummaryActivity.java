package com.mysales.mysales_android;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.mysales.mysales_android.adapters.SalesSummaryRecyclerViewAdapter;
import com.mysales.mysales_android.helpers.DBHelper;
import com.mysales.mysales_android.helpers.TargetDBHelper;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.models.SalesSummary;
import com.mysales.mysales_android.models.Target;
import com.mysales.mysales_android.tasks.CommonTask;

import java.util.ArrayList;
import java.util.HashMap;

import needle.Needle;

public class SalesSummaryActivity extends AppCompatActivity {

    private View progress;
    private RecyclerView listsalessummary;

    private DBHelper db;
    private TargetDBHelper tdb;

    private SalesSummaryTask salesSummaryTask = null;

    private String month;
    private String quarter;
    private String halfyear;

    public static final String ARG_MONTH = "month";
    public static final String ARG_QUARTER = "quarter";
    public static final String ARG_HALFYEAR = "halfyear";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_summary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progress = findViewById(R.id.progress);
        listsalessummary = findViewById(R.id.listsalessummary);

        month = getIntent().getStringExtra(ARG_MONTH);
        quarter = getIntent().getStringExtra(ARG_QUARTER);
        halfyear = getIntent().getStringExtra(ARG_HALFYEAR);

        listsalessummary.setAdapter(new SalesSummaryRecyclerViewAdapter(null));

        db = new DBHelper(this);
        tdb = new TargetDBHelper(this);

        load();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (salesSummaryTask != null && !salesSummaryTask.isCanceled())
            salesSummaryTask.cancel();
    }

    private void load() {
        salesSummaryTask = new SalesSummaryTask(month, quarter, halfyear);
        Needle.onBackgroundThread()
                .withTaskType("salesSummary")
                .execute(salesSummaryTask);
    }

    private void showProgress(final boolean show) {
        Utils.showProgress(show, progress, getApplicationContext());
        listsalessummary.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    class SalesSummaryTask extends CommonTask<ArrayList<SalesSummary>> {

        private static final String CLASS_NAME = "SalesSummaryTask";

        private String month;
        private String quarter;
        private String halfyear;

        SalesSummaryTask(String month, String quarter, String halfyear) {
            super(SalesSummaryActivity.this);
            this.month = month;
            this.quarter = quarter;
            this.halfyear = halfyear;
            showProgress(true);
        }

        @Override
        protected ArrayList<SalesSummary> doWork() {
            ArrayList<SalesSummary> ls = new ArrayList<>();

            try {
                tdb.openDataBase();
                HashMap<String, Target> mt = null;
                ArrayList<String> productGroups = tdb.getProductGroups();

                if (!Utils.isEmpty(month))
                    mt = tdb.getMonthlyTarget(month);

                else if (!Utils.isEmpty(quarter))
                    mt = tdb.getQuarterlyTarget(quarter);

                else if (!Utils.isEmpty(halfyear))
                    mt = tdb.getHalfYearlyTarget(halfyear);

                db.openDataBase();

                for (String product : productGroups) {
                    SalesSummary monthlySummary = null;

                    if (!Utils.isEmpty(month))
                        monthlySummary = db.getMontlySummary(month, product, mt.get(product));

                    else if (!Utils.isEmpty(quarter))
                        monthlySummary = db.getQuarterlySummary(quarter, product, mt.get(product));

                    else if (!Utils.isEmpty(halfyear))
                        monthlySummary = db.getHalfYearlySummary(halfyear, product, mt.get(product));

                    if (monthlySummary != null)
                        ls.add(monthlySummary);
                }
            }

            catch (Exception e) {
                Log.e(CLASS_NAME, e.getMessage(), e);
            }

            finally {
                db.close();
                tdb.close();
            }

            return ls;
        }

        @Override
        protected void thenDoUiRelatedWork(ArrayList<SalesSummary> ls) {
            showProgress(false);
            SalesSummaryRecyclerViewAdapter adapter = new SalesSummaryRecyclerViewAdapter(ls.toArray(new SalesSummary[0]));
            listsalessummary.setAdapter(adapter);

            listsalessummary.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
            Utils.unlockScreenOrientation(SalesSummaryActivity.this);
        }
    }
}

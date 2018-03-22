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

public class SalesSummaryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private View progress;
    private RecyclerView listsalessummary;

    private DBHelper db;
    private TargetDBHelper tdb;

    private SalesSummaryTask salesSummaryTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_summary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        progress = findViewById(R.id.progress);
        listsalessummary = findViewById(R.id.listsalessummary);

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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_main) {
            super.onBackPressed();
        }

        else if (id == R.id.nav_doctor) {
            Intent i = new Intent(this, SalesSummaryActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (salesSummaryTask != null && !salesSummaryTask.isCanceled())
            salesSummaryTask.cancel();
    }

    private void load() {
        salesSummaryTask = new SalesSummaryTask();
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

        SalesSummaryTask() {
            super(SalesSummaryActivity.this);
            showProgress(true);
        }

        @Override
        protected ArrayList<SalesSummary> doWork() {
            ArrayList<SalesSummary> ls = new ArrayList<>();

            try {
                System.out.println("xxxxxxxx");
                tdb.openDataBase();
                System.out.println("gggggggggggggggggggg");
                ArrayList<String> productGroups = tdb.getProductGroups();
                HashMap<String, Target> monthlyTarget = tdb.getMonthlyTarget(3);
                System.out.println("000000000000000000000");

                db.openDataBase();
                SalesSummary montlySummary = db.getMontlySummary(3, "DIFFLAM", monthlyTarget.get("DIFFLAM"));

                ls.add(montlySummary);
                System.out.println("---------" + ls.size());
            }

            catch (Exception e) {
                System.out.println("eeeeeeeeeeeeeeeeeeeeeeee");
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

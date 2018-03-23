package com.mysales.mysales_android;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.mysales.mysales_android.helpers.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SalesSummaryMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MultiSpinnerSearch spmonth, spquarter, sphalfyear;
    private Button btnsubmit1, btnsubmit2, btnsubmit3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_summary_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        spmonth = findViewById(R.id.spmonth);
        spquarter = findViewById(R.id.spquarter);
        sphalfyear = findViewById(R.id.sphalfyear);
        btnsubmit1 = findViewById(R.id.btnsubmit1);
        btnsubmit2 = findViewById(R.id.btnsubmit2);
        btnsubmit3 = findViewById(R.id.btnsubmit3);

        final List<String> periodlist = Arrays.asList(getResources().getStringArray(R.array.period));
        final List<KeyPairBoolData> la = new ArrayList<>();

        for (int i = 0; i < periodlist.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            String v = periodlist.get(i);
            h.setId(Integer.valueOf(v));
            h.setName(v);
            h.setSelected(false);
            la.add(h);
        }

        spmonth.setLimit(-1, null);
        spmonth.setItems(la, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

            }
        });

        final List<String> quarterlist = Arrays.asList(getResources().getStringArray(R.array.quarter));
        final List<KeyPairBoolData> lb = new ArrayList<>();

        for (int i = 0; i < quarterlist.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            String v = quarterlist.get(i);
            h.setId(Integer.valueOf(v));
            h.setName(v);
            h.setSelected(false);
            lb.add(h);
        }

        spquarter.setLimit(-1, null);
        spquarter.setItems(lb, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

            }
        });

        final List<String> halfyearlist = Arrays.asList(getResources().getStringArray(R.array.halfyear));
        final List<KeyPairBoolData> lc = new ArrayList<>();

        for (int i = 0; i < halfyearlist.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            String v = halfyearlist.get(i);
            h.setId(Integer.valueOf(v));
            h.setName(v);
            h.setSelected(false);
            lc.add(h);
        }

        sphalfyear.setLimit(-1, null);
        sphalfyear.setItems(lc, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

            }
        });

        btnsubmit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewMonthly();
            }
        });

        btnsubmit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewQuarterly();
            }
        });

        btnsubmit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHalfYearly();
            }
        });
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
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

        else if (id == R.id.nav_doctor) {
            Intent i = new Intent(this, DoctorListActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void viewMonthly() {
        String month = Utils.getSelected(spmonth.getSelectedItems());

        if (Utils.isEmpty(month))
            return;

        Intent i = new Intent(this, SalesSummaryActivity.class);
        i.putExtra(SalesSummaryActivity.ARG_MONTH, month);
        i.putExtra(SalesSummaryActivity.ARG_QUARTER, "");
        i.putExtra(SalesSummaryActivity.ARG_HALFYEAR, "");

        startActivity(i);
    }

    private void viewQuarterly() {
        String quarter = Utils.getSelected(spquarter.getSelectedItems());

        if (Utils.isEmpty(quarter))
            return;

        Intent i = new Intent(this, SalesSummaryActivity.class);
        i.putExtra(SalesSummaryActivity.ARG_MONTH, "");
        i.putExtra(SalesSummaryActivity.ARG_QUARTER, quarter);
        i.putExtra(SalesSummaryActivity.ARG_HALFYEAR, "");

        startActivity(i);
    }

    private void viewHalfYearly() {
        String h = Utils.getSelected(sphalfyear.getSelectedItems());

        if (Utils.isEmpty(h))
            return;

        Intent i = new Intent(this, SalesSummaryActivity.class);
        i.putExtra(SalesSummaryActivity.ARG_MONTH, "");
        i.putExtra(SalesSummaryActivity.ARG_QUARTER, "");
        i.putExtra(SalesSummaryActivity.ARG_HALFYEAR, h);

        startActivity(i);
    }
}

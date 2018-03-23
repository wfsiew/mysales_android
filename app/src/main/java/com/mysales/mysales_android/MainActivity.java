package com.mysales.mysales_android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.mysales.mysales_android.helpers.DBHelper;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.tasks.CommonTask;

import java.util.Calendar;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import needle.Needle;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 0;

    private AppCompatAutoCompleteTextView txtcust;
    private MultiSpinnerSearch spitem, spproductgroup, spperiod, spyear;
    private Button btnsubmit;

    private DBHelper db;

    private PopulateCustomerTask populateCustomerTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        txtcust = findViewById(R.id.txtcust);
        spitem = findViewById(R.id.spitem);
        spproductgroup = findViewById(R.id.spproductgroup);
        spperiod = findViewById(R.id.spperiod);
        spyear = findViewById((R.id.spyear));
        btnsubmit = findViewById(R.id.btnsubmit);

        btnsubmit.setEnabled(false);

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

        spperiod.setLimit(-1, null);
        spperiod.setItems(la, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

            }
        });

        //final List<String> yearlist = Arrays.asList(getResources().getStringArray(R.array.year));
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int gap = year - 2012 + 1;
        final List<KeyPairBoolData> lb = new ArrayList<>();

        for (int i = 0; i < gap; i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            String v = String.valueOf(year);
            h.setId(i + 1);
            h.setName(v);
            h.setSelected(false);

            lb.add(h);
            --year;
        }

        spyear.setLimit(-1, null);
        spyear.setItems(lb, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

            }
        });
        spyear.setSelectedIds(new Integer[] { 1 });

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchData();
            }
        });

        db = new DBHelper(this);
        checkPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (populateCustomerTask != null && !populateCustomerTask.isCanceled()) {
            populateCustomerTask.cancel();
        }
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
        if (id == R.id.nav_doctor) {
            Intent i = new Intent(this, DoctorListActivity.class);
            startActivity(i);
        }

        else if (id == R.id.nav_summary) {
            Intent i = new Intent(this, SalesSummaryMainActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void searchData() {
        String period = Utils.getSelected(spperiod.getSelectedItems());
        String year = Utils.getSelected(spyear.getSelectedItems());
        String item = Utils.getSelected(spitem.getSelectedItems(), true);
        String productgroup = Utils.getSelected(spproductgroup.getSelectedItems(), true);

        Intent i = new Intent(this, CustomerListActivity.class);
        i.putExtra(CustomerListActivity.ARG_CUST, txtcust.getText().toString());
        i.putExtra(CustomerListActivity.ARG_ITEM, item);
        i.putExtra(CustomerListActivity.ARG_PRODUCT_GROUP, productgroup);
        i.putExtra(CustomerListActivity.ARG_PERIOD, period);
        i.putExtra(CustomerListActivity.ARG_YEAR, year);

        startActivity(i);
    }

    private void checkPermission() {
        if (!mayRequestReadExternalStorage()) {
            return;
        }

        populateAutoComplete();
    }

    private void populateAutoComplete() {
        populateCustomerTask = new PopulateCustomerTask();
        Needle.onBackgroundThread()
                .withTaskType("populateCustomer")
                .execute(populateCustomerTask);
    }

    private boolean mayRequestReadExternalStorage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(txtcust, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    });
        }

        else {
            requestPermissions(new String[] { WRITE_EXTERNAL_STORAGE }, REQUEST_WRITE_EXTERNAL_STORAGE);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            }
        }
    }

    class PopulateCustomerTask extends CommonTask<HashMap<String, ArrayList<String>>> {

        private static final String CLASS_NAME = "PopulateCustomerTask";

        PopulateCustomerTask() {
            super(MainActivity.this);
        }

        @Override
        protected HashMap<String, ArrayList<String>> doWork() {
            HashMap<String, ArrayList<String>> m = new HashMap<>();
            ArrayList<String> ls;
            ArrayList<String> li;
            ArrayList<String> lp;

            try {
                db.openDataBase();
                ls = db.getCustomers();
                li = db.getItems();
                lp = db.getProductGroups();
                m.put("customer", ls);
                m.put("item", li);
                m.put("productgroup", lp);
            }

            catch (Exception e) {
                Log.e(CLASS_NAME, e.getMessage(), e);
            }

            finally {
                db.close();
            }

            return m;
        }

        @Override
        protected void thenDoUiRelatedWork(HashMap<String, ArrayList<String>> m) {
            ArrayList<String> ls = m.get("customer");
            ArrayList<String> li = m.get("item");
            ArrayList<String> lp = m.get("productgroup");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_dropdown_item_1line, ls);

            final List<KeyPairBoolData> la = new ArrayList<>();
            final List<KeyPairBoolData> lb = new ArrayList<>();

            for (int i = 0; i < li.size(); i++) {
                KeyPairBoolData h = new KeyPairBoolData();
                String v = li.get(i);
                h.setId(i + 1);
                h.setName(v);
                h.setSelected(false);
                la.add(h);
            }

            for (int i = 0; i < lp.size(); i++) {
                KeyPairBoolData h = new KeyPairBoolData();
                String  v= lp.get(i);
                h.setId(i + 1);
                h.setName(v);
                h.setSelected(false);
                lb.add(h);
            }

            spitem.setLimit(-1, null);
            spitem.setItems(la, -1, new SpinnerListener() {
                @Override
                public void onItemsSelected(List<KeyPairBoolData> items) {

                }
            });

            spproductgroup.setLimit(-1, null);
            spproductgroup.setItems(lb, -1, new SpinnerListener() {
                @Override
                public void onItemsSelected(List<KeyPairBoolData> items) {

                }
            });

            btnsubmit.setEnabled(true);
            txtcust.setAdapter(adapter);
            Utils.unlockScreenOrientation(MainActivity.this);
        }
    }
}

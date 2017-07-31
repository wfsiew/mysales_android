package com.mysales.mysales_android;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.mysales.mysales_android.helpers.DBHelper;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.tasks.CommonTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import needle.Needle;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {

    private static final String CLASS_NAME = "MainActivity";
    private static final int REQUEST_READ_EXTERNAL_STORAGE = 0;

    private AppCompatAutoCompleteTextView txtcust;
    private MultiSpinnerSearch spperiod, spyear;
    private Button btnsubmit;

    private DBHelper db;

    private PopulateCustomerTask populateCustomerTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtcust = (AppCompatAutoCompleteTextView) findViewById(R.id.txtcust);
        spperiod = (MultiSpinnerSearch) findViewById(R.id.spperiod);
        spyear = (MultiSpinnerSearch) findViewById((R.id.spyear));
        btnsubmit = (Button) findViewById(R.id.btnsubmit);

        btnsubmit.setEnabled(false);

        final List<String> periodlist = Arrays.asList(getResources().getStringArray(R.array.period));
        final List<KeyPairBoolData> la = new ArrayList<>();

        for (int i = 0; i < periodlist.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            String v = periodlist.get(i);
            h.setId(Long.valueOf(v));
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

        final List<String> yearlist = Arrays.asList(getResources().getStringArray(R.array.year));
        final List<KeyPairBoolData> lb = new ArrayList<>();

        for (int i = 0; i < yearlist.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            String v = yearlist.get(i);
            h.setId(Long.valueOf(v));
            h.setName(v);

            if (v.equals("2017"))
                h.setSelected(true);

            else
                h.setSelected(false);

            lb.add(h);
        }

        spyear.setLimit(-1, null);
        spyear.setItems(lb, -1, new SpinnerListener() {
            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

            }
        });

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
        if (populateCustomerTask != null && !populateCustomerTask.isCanceled())
            populateCustomerTask.cancel();
    }

    private void searchData() {
        String period = getSelected(spperiod.getSelectedIds());
        String year = getSelected(spyear.getSelectedIds());

        Intent i = new Intent(this, CustomerListActivity.class);
        i.putExtra(CustomerListActivity.ARG_CUST, txtcust.getText().toString());
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

        if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)) {
            Snackbar.make(txtcust, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
        }

        else {
            requestPermissions(new String[] { READ_EXTERNAL_STORAGE }, REQUEST_READ_EXTERNAL_STORAGE);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            }
        }
    }

    private String getSelected(List<Long> li) {
        StringBuffer sb = new StringBuffer();
        String r = "";

        if (li.isEmpty()) {
            return r;
        }

        for (int i = 0; i < li.size(); i++) {
            sb.append(li.get(i));
            if (i < li.size() - 1) {
                sb.append(",");
            }
        }

        r = sb.toString();
        return r;
    }

    public class PopulateCustomerTask extends CommonTask<ArrayList<String>> {

        private static final String CLASS_NAME = "PopulateCustomerTask";

        public PopulateCustomerTask() {
            super(MainActivity.this);
        }

        @Override
        protected ArrayList<String> doWork() {
            ArrayList<String> ls = new ArrayList<>();

            try {
                ls = db.getCustomers();
            }

            catch (Exception e) {
                Log.e(CLASS_NAME, e.getMessage(), e);
            }

            finally {
                db.close();
            }

            return ls;
        }

        @Override
        protected void thenDoUiRelatedWork(ArrayList<String> ls) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                    android.R.layout.simple_dropdown_item_1line, ls);

            btnsubmit.setEnabled(true);
            txtcust.setAdapter(adapter);
            Utils.unlockScreenOrientation(MainActivity.this);
        }
    }
}

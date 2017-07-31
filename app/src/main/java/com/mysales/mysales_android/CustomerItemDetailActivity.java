package com.mysales.mysales_android;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mysales.mysales_android.helpers.DBHelper;
import com.mysales.mysales_android.models.CustomerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CustomerItemDetailActivity extends AppCompatActivity {

    private View progress;
    private TextView txtcontent;

    private DBHelper db;

    public static final String ARG_CUST = "cust_code";
    public static final String ARG_PERIOD = "period";
    public static final String ARG_YEAR = "year";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_item_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        progress = findViewById(R.id.progress);
        txtcontent = (TextView) findViewById(R.id.txtcontent);

        db = new DBHelper(this);

        load();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void load() {
        String cust = getIntent().getStringExtra(ARG_CUST);
        String period = getIntent().getStringExtra(ARG_PERIOD);
        String year = getIntent().getStringExtra(ARG_YEAR);

        HashMap<String, ArrayList<CustomerItem>> m = new HashMap<>();

        try {
            m = db.getItemsByCustomer(cust, period, year);
        }

        catch (Exception e) {
            Log.e("xxx", e.getMessage(), e);
        }

        finally {
            db.close();
        }

        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, ArrayList<CustomerItem>> entry: m.entrySet()) {
            String key = entry.getKey();
            ArrayList<CustomerItem> l = entry.getValue();

            sb.append(key + "\n");
            for (CustomerItem o: l) {
                sb.append(o.getItem() + "  " + o.getUnit() + "  " + o.getValue() + "\n");
            }

            sb.append("\n\n");
        }

        txtcontent.setText(sb.toString());
    }
}

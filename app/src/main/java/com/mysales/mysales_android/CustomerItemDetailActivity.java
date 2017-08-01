package com.mysales.mysales_android;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mysales.mysales_android.helpers.DBHelper;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.models.CustomerItem;
import com.mysales.mysales_android.tasks.CommonTask;

import java.util.ArrayList;
import java.util.HashMap;

import needle.Needle;

public class CustomerItemDetailActivity extends AppCompatActivity {

    private View progress;
    private TextView txtcontent;

    private DBHelper db;

    private CustomerItemDetailTask customerItemDetailTask = null;

    private String cust;
    private String period;
    private String year;
    private String sort = "";

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

        cust = getIntent().getStringExtra(ARG_CUST);
        period = getIntent().getStringExtra(ARG_PERIOD);
        year = getIntent().getStringExtra(ARG_YEAR);

        db = new DBHelper(this);

        load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customerItemDetailTask != null && !customerItemDetailTask.isCanceled())
            customerItemDetailTask.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_item_detail, menu);
        final MenuItem sort_item = menu.findItem(R.id.sort_item);
        final MenuItem sort_salesunit = menu.findItem(R.id.sort_salesunit);
        final MenuItem sort_salesvalue = menu.findItem(R.id.sort_salesvalue);

        sort_item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                sort = "item_name";
                load();
                return false;
            }
        });

        sort_salesunit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                sort = "salesu desc";
                load();
                return false;
            }
        });

        sort_salesvalue.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                sort = "salesv desc";
                load();
                return false;
            }
        });

        return true;
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
        customerItemDetailTask = new CustomerItemDetailTask(cust, period, year, sort);
        Needle.onBackgroundThread()
                .withTaskType("customerItemDetail")
                .execute(customerItemDetailTask);
    }

    private void showProgress(final boolean show) {
        Utils.showProgress(show, progress, getApplicationContext());
        txtcontent.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public class CustomerItemDetailTask extends CommonTask<HashMap<String, ArrayList<CustomerItem>>> {

        private static final String CLASS_NAME = "CustomerItemDetailTask";

        private String cust;
        private String period;
        private String year;
        private String sort;
        private ArrayList<String> la;

        public CustomerItemDetailTask(String cust, String period, String year, String sort) {
            super(CustomerItemDetailActivity.this);
            this.cust = cust;
            this.period = period;
            this.year = year;
            this.sort = sort;
            la = new ArrayList<>();
            showProgress(true);
        }

        @Override
        protected HashMap<String, ArrayList<CustomerItem>> doWork() {
            HashMap<String, ArrayList<CustomerItem>> m = new HashMap<>();

            try {
                m = db.getItemsByCustomer(cust, period, year, sort, la);
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
        protected void thenDoUiRelatedWork(HashMap<String, ArrayList<CustomerItem>> m) {
            showProgress(false);
            StringBuffer sb = new StringBuffer();

            int salesunittotal = 0;
            double salesvaluetotal = 0;

            if (la.size() > 0) {
                ArrayList<CustomerItem> lx = m.get(la.get(0));
                if (lx.size() > 0) {
                    CustomerItem x = lx.get(0);
                    sb.append(x.getName() + "\n")
                            .append("Total Sales Unit: XXX\nTotal Sales Value: YYY\n\n");
                }
            }

            for (String key: la) {
                ArrayList<CustomerItem> l = m.get(key);

                int salesunit = 0;
                double salesvalue = 0;

                sb.append(key + "\n");
                for (CustomerItem o: l) {
                    sb.append(o.getItem() + "\n")
                            .append("Sales Unit: " + o.getUnit() + "  Sales Value: " + Utils.formatDouble(o.getValue()) + "\n")
                            .append("------------------------------------------------\n");
                    salesunit += o.getUnit();
                    salesvalue += o.getValue();

                    salesunittotal += o.getUnit();
                    salesvaluetotal += o.getValue();
                }

                sb.append("Total Sales Unit: " + salesunit + "  Total Sales Value: " + Utils.formatDouble(salesvalue) + "\n");
                sb.append("\n");
            }

            String r = sb.toString()
                    .replace("XXX", String.valueOf(salesunittotal))
                    .replace("YYY", Utils.formatDouble(salesvaluetotal));

            txtcontent.setText(r);

            Utils.unlockScreenOrientation(CustomerItemDetailActivity.this);
        }
    }
}

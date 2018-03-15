package com.mysales.mysales_android;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.mysales.mysales_android.adapters.CustomerItemDetailAdapter;
import com.mysales.mysales_android.helpers.DBHelper;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.models.CustomerAddress;
import com.mysales.mysales_android.models.CustomerItem;
import com.mysales.mysales_android.models.Result;
import com.mysales.mysales_android.tasks.CommonTask;

import java.util.ArrayList;
import java.util.HashMap;

import needle.Needle;

public class CustomerItemDetailActivity extends AppCompatActivity {

    private View progress;
    private TextView txtmain;
    private ListView listitem;

    private DBHelper db;

    private CustomerItemDetailTask customerItemDetailTask = null;

    private String cust;
    private String custName;
    private String item;
    private String productgroup;
    private String period;
    private String year;
    private String sort = "";

    public static final String ARG_CUST = "cust_code";
    public static final String ARG_CUST_NAME = "cust_name";
    public static final String ARG_ITEM = "item_name";
    public static final String ARG_PRODUCT_GROUP = "product_group";
    public static final String ARG_PERIOD = "period";
    public static final String ARG_YEAR = "year";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_item_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progress = findViewById(R.id.progress);
        txtmain = findViewById(R.id.txtmain);
        listitem = findViewById(R.id.listitem);

        cust = getIntent().getStringExtra(ARG_CUST);
        custName = getIntent().getStringExtra(ARG_CUST_NAME);
        item = getIntent().getStringExtra(ARG_ITEM);
        productgroup = getIntent().getStringExtra(ARG_PRODUCT_GROUP);
        period = getIntent().getStringExtra(ARG_PERIOD);
        year = getIntent().getStringExtra(ARG_YEAR);

        db = new DBHelper(this);

        load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customerItemDetailTask != null && !customerItemDetailTask.isCanceled()) {
            customerItemDetailTask.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_item_detail, menu);
        final MenuItem sort_item = menu.findItem(R.id.sort_item);
        final MenuItem sort_salesunit = menu.findItem(R.id.sort_salesunit);
        final MenuItem sort_salesvalue = menu.findItem(R.id.sort_salesvalue);
        final MenuItem sort_bonusunit = menu.findItem(R.id.sort_bonusunit);

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

        sort_bonusunit.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                sort = "bonusu desc";
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
        customerItemDetailTask = new CustomerItemDetailTask(cust, custName, item, productgroup, period, year, sort);
        Needle.onBackgroundThread()
                .withTaskType("customerItemDetail")
                .execute(customerItemDetailTask);
    }

    private void showProgress(final boolean show) {
        Utils.showProgress(show, progress, getApplicationContext());
        txtmain.setVisibility(show ? View.GONE : View.VISIBLE);
        listitem.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    class CustomerItemDetailTask extends CommonTask<HashMap<String, ArrayList<CustomerItem>>> {

        private static final String CLASS_NAME = "CustomerItemDetailTask";

        private String cust;
        private String custName;
        private String item;
        private String productgroup;
        private String period;
        private String year;
        private String sort;
        private ArrayList<String> la;
        private CustomerAddress addr;

        CustomerItemDetailTask(String cust, String custName, String item, String productgroup, String period, String year, String sort) {
            super(CustomerItemDetailActivity.this);
            this.cust = cust;
            this.custName = custName;
            this.item = item;
            this.productgroup = productgroup;
            this.period = period;
            this.year = year;
            this.sort = sort;
            la = new ArrayList<>();
            addr = new CustomerAddress();
            showProgress(true);
        }

        @Override
        protected HashMap<String, ArrayList<CustomerItem>> doWork() {
            HashMap<String, ArrayList<CustomerItem>> m = new HashMap<>();

            try {
                m = db.getItemsByCustomer(cust, custName, item, productgroup, period, year, sort, addr, la);
            }

            catch (Exception e) {
                Log.e(CLASS_NAME, e.getMessage(), e);
            }

            finally {
                db.close();
            }

            return m;
        }

        private Result process(HashMap<String, ArrayList<CustomerItem>> m) {
            Result r = new Result();
            ArrayList<CustomerItem> lk = new ArrayList<>();
            r.setList(lk);

            if (la.isEmpty()) {
                return r;
            }

            int salesunittotal = 0;
            double salesvaluetotal = 0;
            int bonustotal = 0;

            for (String key: la) {
                ArrayList<CustomerItem> l = m.get(key);
                CustomerItem h = new CustomerItem();
                h.setHeader(true);
                h.setHeader(key);
                lk.add(h);

                int salesunit = 0;
                double salesvalue = 0;
                int bonus = 0;

                for (CustomerItem o: l) {
                    CustomerItem i = new CustomerItem();
                    i.setItem(o.getItem());
                    i.setUnit(o.getUnit());
                    i.setBonus(o.getBonus());
                    i.setValue(o.getValue());
                    lk.add(i);

                    salesunit += o.getUnit();
                    salesvalue += o.getValue();
                    bonus += o.getBonus();

                    salesunittotal += o.getUnit();
                    salesvaluetotal += o.getValue();
                    bonustotal += o.getBonus();
                }

                CustomerItem f = new CustomerItem();
                f.setFooter(true);
                f.setSumunit(salesunit);
                f.setSumbonus(bonus);
                f.setSumvalue(salesvalue);
                lk.add(f);
            }

            r.setList(lk);
            r.setTotalSalesUnit(salesunittotal);
            r.setTotalBonusUnit(bonustotal);
            r.setTotalSalesValue(salesvaluetotal);
            return r;
        }

        @Override
        protected void thenDoUiRelatedWork(HashMap<String, ArrayList<CustomerItem>> m) {
            showProgress(false);
            Result r = process(m);
            ArrayList<CustomerItem> lk = r.getList();

            CustomerItemDetailAdapter adapter = new CustomerItemDetailAdapter(CustomerItemDetailActivity.this, lk);
            listitem.setAdapter(adapter);

            StringBuilder sb = new StringBuilder();

            if (la.size() > 0) {
                ArrayList<CustomerItem> lx = m.get(la.get(0));
                if (lx.size() > 0) {
                    CustomerItem x = lx.get(0);
                    sb.append(x.getCode()).append(" - ").append(x.getName()).append("\n");

                    if (!Utils.isEmpty(addr.getAddr1()))
                        sb.append(addr.getAddr1());

                    if (!Utils.isEmpty(addr.getAddr2())) {
                        if (sb.toString().trim().endsWith(",")) {
                            sb.append(" ").append(addr.getAddr2());
                        }

                        else {
                            sb.append(", ").append(addr.getAddr2());
                        }
                    }

                    if (!Utils.isEmpty(addr.getAddr3())) {
                        if (sb.toString().trim().endsWith(",")) {
                            sb.append(" ").append(addr.getAddr3());
                        }

                        else {
                            sb.append(", ").append(addr.getAddr3());
                        }
                    }

                    sb.append("\nTotal Sales Unit: ").append(r.getTotalSalesUnit()).append("\n")
                            .append("Total Bonus Unit: ").append(r.getTotalBonusUnit()).append("\n")
                            .append("Total Sales Value: ").append(Utils.formatDouble(r.getTotalSalesValue())).append("\n");
                    txtmain.setText(sb.toString());
                }
            }

            Utils.unlockScreenOrientation(CustomerItemDetailActivity.this);
        }
    }
}

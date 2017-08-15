package com.mysales.mysales_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


import com.mysales.mysales_android.adapters.CustomerItemRecyclerViewAdapter;
import com.mysales.mysales_android.helpers.DBHelper;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.models.Customer;
import com.mysales.mysales_android.tasks.CommonTask;

import java.util.ArrayList;

import needle.Needle;

/**
 * An activity representing a list of Customers. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link CustomerItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class CustomerListActivity extends AppCompatActivity {

    private View progress;
    private RecyclerView listcust;
    private TextView empty;

    private DBHelper db;

    private CustomerListTask customerListTask = null;

    private String cust;
    private String item;
    private String period;
    private String year;
    private String sort = "";

    public static final String ARG_CUST = "cust_name";
    public static final String ARG_ITEM = "item_name";
    public static final String ARG_PERIOD = "period";
    public static final String ARG_YEAR = "year";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        progress = findViewById(R.id.progress);
        listcust = findViewById(R.id.listcust);
        empty = findViewById(R.id.empty);

        cust = getIntent().getStringExtra(ARG_CUST);
        item = getIntent().getStringExtra(ARG_ITEM);
        period = getIntent().getStringExtra(ARG_PERIOD);
        year = getIntent().getStringExtra(ARG_YEAR);

        listcust.setAdapter(new CustomerItemRecyclerViewAdapter(null, period, year));

        db = new DBHelper(this);

        load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customerListTask != null && !customerListTask.isCanceled())
            customerListTask.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_list, menu);
        final MenuItem sort_custcode = menu.findItem(R.id.sort_custcode);
        final MenuItem sort_custname = menu.findItem(R.id.sort_custname);
        final MenuItem sort_salesunit = menu.findItem(R.id.sort_salesunit);
        final MenuItem sort_salesvalue = menu.findItem(R.id.sort_salesvalue);
        final MenuItem sort_bonusunit = menu.findItem(R.id.sort_bonusunit);

        sort_custcode.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                sort = "cust_code";
                load();
                return false;
            }
        });

        sort_custname.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                sort = "cust_name";
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

    private void load() {
        customerListTask = new CustomerListTask(cust, item, period, year, sort);
        Needle.onBackgroundThread()
                .withTaskType("customerList")
                .execute(customerListTask);
    }

    private void showProgress(final boolean show) {
        Utils.showProgress(show, progress, getApplicationContext());
        listcust.setVisibility(View.GONE);
        empty.setVisibility(View.GONE);
    }

    class CustomerListTask extends CommonTask<ArrayList<Customer>> {

        private static final String CLASS_NAME = "CustomerListTask";

        private String cust;
        private String item;
        private String period;
        private String year;
        private String sort;

        CustomerListTask(String cust, String item, String period, String year, String sort) {
            super(CustomerListActivity.this);
            this.cust = cust;
            this.item = item;
            this.period = period;
            this.year = year;
            this.sort = sort;
            showProgress(true);
        }

        @Override
        protected ArrayList<Customer> doWork() {
            ArrayList<Customer> ls = new ArrayList<>();

            try {
                ls = db.filterCustomer(cust, item, period, year, sort);
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
        protected void thenDoUiRelatedWork(ArrayList<Customer> ls) {
            showProgress(false);
            CustomerItemRecyclerViewAdapter adapter = new CustomerItemRecyclerViewAdapter(ls.toArray(new Customer[0]), period, year);
            listcust.setAdapter(adapter);

            listcust.setVisibility(adapter.getItemCount() > 0 ? View.VISIBLE : View.GONE);
            empty.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            Utils.unlockScreenOrientation(CustomerListActivity.this);
        }
    }
}

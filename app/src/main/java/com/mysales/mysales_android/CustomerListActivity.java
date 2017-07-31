package com.mysales.mysales_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    private String period;
    private String year;

    public static final String ARG_CUST = "cust_name";
    public static final String ARG_PERIOD = "period";
    public static final String ARG_YEAR = "year";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        progress = findViewById(R.id.progress);
        listcust = (RecyclerView) findViewById(R.id.listcust);
        empty = (TextView) findViewById(R.id.empty);

        cust = getIntent().getStringExtra(ARG_CUST);
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

    private void load() {
        customerListTask = new CustomerListTask(cust, period, year);
        Needle.onBackgroundThread()
                .withTaskType("customerList")
                .execute(customerListTask);
    }

    private void showProgress(final boolean show) {
        Utils.showProgress(show, progress, getApplicationContext());
        listcust.setVisibility(View.GONE);
        empty.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public class CustomerListTask extends CommonTask<ArrayList<Customer>> {

        private static final String CLASS_NAME = "CustomerListTask";

        private String cust;
        private String period;
        private String year;

        public  CustomerListTask(String cust, String period, String year) {
            super(CustomerListActivity.this);
            this.cust = cust;
            this.period = period;
            this.year = year;
            showProgress(true);
        }

        @Override
        protected ArrayList<Customer> doWork() {
            ArrayList<Customer> ls = new ArrayList<>();

            try {
                ls = db.filterCustomer(cust, period, year);
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

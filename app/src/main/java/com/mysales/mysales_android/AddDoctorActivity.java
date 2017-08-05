package com.mysales.mysales_android;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.mysales.mysales_android.helpers.DBHelper;

public class AddDoctorActivity extends AppCompatActivity {

    private EditText txtname, txtphone, txtmobile, txtemail;
    private TextView txttitle;

    private DBHelper db;

    private String cust;
    private String custName;

    public static final String ARG_CUST = "cust_code";
    public static final String ARG_CUST_NAME = "cust_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        txttitle = (TextView) findViewById(R.id.txttitle);
        txtname = (EditText) findViewById(R.id.txtname);
        txtphone = (EditText) findViewById(R.id.txtphone);
        txtmobile = (EditText) findViewById(R.id.txtmobile);
        txtemail = (EditText) findViewById(R.id.txtemail);

        cust = getIntent().getStringExtra(ARG_CUST);
        custName = getIntent().getStringExtra(ARG_CUST_NAME);

        txttitle.setText(String.format("%s - %s", cust, custName));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_doctor, menu);
        final MenuItem menu_save = menu.findItem(R.id.menu_save);

        menu_save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
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
}

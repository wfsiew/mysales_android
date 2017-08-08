package com.mysales.mysales_android;

import android.app.AlertDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mysales.mysales_android.adapters.CustomerAdapter;
import com.mysales.mysales_android.helpers.DBHelper;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.helpers.WriteDBHelper;
import com.mysales.mysales_android.models.Customer;
import com.mysales.mysales_android.models.Doctor;
import com.mysales.mysales_android.tasks.CommonTask;

import java.util.ArrayList;

import needle.Needle;

public class AddDoctorActivity extends AppCompatActivity {

    private View progresssubmit;
    private EditText txtname, txtphone, txtmobile, txtemail;
    private TextView txttitle, txtcust;
    private Spinner spcust;
    private CheckBox chkmon_mor, chkmon_aft;
    private CheckBox chktue_mor, chktue_aft;
    private CheckBox chkwed_mor, chkwed_aft;
    private CheckBox chkthu_mor, chkthu_aft;
    private CheckBox chkfri_mor, chkfri_aft;
    private CheckBox chksat_mor, chksat_aft;
    private CheckBox chksun_mor, chksun_aft;

    private WriteDBHelper db;
    private DBHelper dbr;

    private String cust;
    private String custName;
    private int submit = 0;
    private static int selectedCustPosition = 0;

    private PopulateCustomerTask populateCustomerTask = null;

    public static final String ARG_CUST = "cust_code";
    public static final String ARG_CUST_NAME = "cust_name";

    public static final int SUBMITTED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progresssubmit = findViewById(R.id.progresssubmit);
        txttitle = (TextView) findViewById(R.id.txttitle);
        txtcust = (TextView) findViewById(R.id.txtcust);
        txtname = (EditText) findViewById(R.id.txtname);
        txtphone = (EditText) findViewById(R.id.txtphone);
        txtmobile = (EditText) findViewById(R.id.txtmobile);
        txtemail = (EditText) findViewById(R.id.txtemail);
        spcust = (Spinner) findViewById(R.id.spcust);

        chkmon_mor = (CheckBox) findViewById(R.id.chkmon_morning);
        chkmon_aft = (CheckBox) findViewById(R.id.chkmon_afternoon);
        chktue_mor = (CheckBox) findViewById(R.id.chktue_morning);
        chktue_aft = (CheckBox) findViewById(R.id.chktue_afternoon);
        chkwed_mor = (CheckBox) findViewById(R.id.chkwed_morning);
        chkwed_aft = (CheckBox) findViewById(R.id.chkwed_afternoon);
        chkthu_mor = (CheckBox) findViewById(R.id.chkthu_morning);
        chkthu_aft = (CheckBox) findViewById(R.id.chkthu_afternoon);
        chkfri_mor = (CheckBox) findViewById(R.id.chkfri_morning);
        chkfri_aft = (CheckBox) findViewById(R.id.chkfri_afternoon);
        chksat_mor = (CheckBox) findViewById(R.id.chksat_morning);
        chksat_aft = (CheckBox) findViewById(R.id.chksat_afternoon);
        chksun_mor = (CheckBox) findViewById(R.id.chksun_morning);
        chksun_aft = (CheckBox) findViewById(R.id.chksun_afternoon);

        cust = getIntent().getStringExtra(ARG_CUST);
        custName = getIntent().getStringExtra(ARG_CUST_NAME);

        if (Utils.isEmpty(cust) || Utils.isEmpty(custName)) {
            txttitle.setVisibility(View.GONE);
            dbr = new DBHelper(this);
            txtcust.setVisibility(View.VISIBLE);
            spcust.setVisibility(View.VISIBLE);
            spcust.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedCustPosition = i;
                }
            });

            populateCustomerTask = new PopulateCustomerTask();
            Needle.onBackgroundThread()
                    .withTaskType("populateCustomer")
                    .execute(populateCustomerTask);
        }

        else {
            txttitle.setText(String.format("%s - %s", cust, custName));
        }

        db = new WriteDBHelper(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (populateCustomerTask != null && !populateCustomerTask.isCanceled()) {
            populateCustomerTask.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_doctor, menu);
        final MenuItem menu_save = menu.findItem(R.id.menu_save);

        menu_save.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                submit();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            setResult(submit);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<String> validateSubmit() {
        ArrayList<String> ls = new ArrayList<>();
        if (Utils.isEmpty(txtname.getText().toString())) {
            ls.add("Name is required");
        }

        return ls;
    }

    private void submit() {
        ArrayList<String> ls = validateSubmit();
        String m = Utils.getMessages(ls);
        if (!Utils.isEmpty(m)) {
            new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.mandatory))
                    .setMessage(m)
                    .show();
            return;
        }

        String ccode = cust;
        String cname = custName;

        if (Utils.isEmpty(ccode) || Utils.isEmpty(cname)) {
            int i = spcust.getSelectedItemPosition();
            CustomerAdapter x = (CustomerAdapter) spcust.getAdapter();
            final Customer customer = x.getItem(i);
            ccode = customer.getCode();
            cname = customer.getName();
        }

        Doctor o = new Doctor();
        o.setName(Utils.getSqlStr(txtname.getText().toString()));
        o.setPhone(Utils.getSqlStr(txtphone.getText().toString()));
        o.setHp(Utils.getSqlStr(txtmobile.getText().toString()));
        o.setEmail(Utils.getSqlStr(txtemail.getText().toString()));
        o.setCustCode(ccode);
        o.setCustName(cname);
        o.setMonMor(chkmon_mor.isChecked());
        o.setMonAft(chkmon_aft.isChecked());
        o.setTueMor(chktue_mor.isChecked());
        o.setTueAft(chktue_aft.isChecked());
        o.setWedMor(chkwed_mor.isChecked());
        o.setWedAft(chkwed_aft.isChecked());
        o.setThuMor(chkthu_mor.isChecked());
        o.setThuAft(chkthu_aft.isChecked());
        o.setFriMor(chkfri_mor.isChecked());
        o.setFriAft(chkfri_aft.isChecked());
        o.setSatMor(chksat_mor.isChecked());
        o.setSatAft(chksat_aft.isChecked());
        o.setSunMor(chksun_mor.isChecked());
        o.setSunAft(chksun_aft.isChecked());

        Needle.onBackgroundThread().execute(new AddDoctorTask(o));
    }

    private void reset() {
        txtname.setText(null);
        txtphone.setText(null);
        txtmobile.setText(null);
        txtemail.setText(null);
        chkmon_mor.setChecked(false);
        chkmon_aft.setChecked(false);
        chktue_mor.setChecked(false);
        chktue_aft.setChecked(false);
        chkwed_mor.setChecked(false);
        chkwed_aft.setChecked(false);
        chkthu_mor.setChecked(false);
        chkthu_aft.setChecked(false);
        chkfri_mor.setChecked(false);
        chkfri_aft.setChecked(false);
        chksat_mor.setChecked(false);
        chksat_aft.setChecked(false);
        chksun_mor.setChecked(false);
        chksun_aft.setChecked(false);
    }

    class PopulateCustomerTask extends CommonTask<ArrayList<Customer>> {

        private static final String CLASS_NAME = "PopulateCustomerTask";

        public PopulateCustomerTask() {
            super(AddDoctorActivity.this);
        }

        @Override
        protected ArrayList<Customer> doWork() {
            ArrayList<Customer> ls = new ArrayList<>();

            try {
                dbr.openDataBase();
                ls = dbr.getCustomers1();
            }

            catch (Exception e) {
                Log.e(CLASS_NAME, e.getMessage(), e);
            }

            finally {
                dbr.close();
            }

            return ls;
        }

        @Override
        protected void thenDoUiRelatedWork(ArrayList<Customer> ls) {
            CustomerAdapter adapter = new CustomerAdapter(AddDoctorActivity.this, ls);
            spcust.setAdapter(adapter);
            spcust.setSelection(selectedCustPosition);
            Utils.unlockScreenOrientation(AddDoctorActivity.this);
        }
    }

    class AddDoctorTask extends CommonTask<String> {

        private Doctor doctor;

        private static final String CLASS_NAME = "AddDoctorTask";

        public AddDoctorTask(Doctor doctor) {
            super(AddDoctorActivity.this);
            this.doctor = doctor;
            Utils.showProgress(true, progresssubmit, AddDoctorActivity.this);
        }

        @Override
        protected String doWork() {
            String r = null;

            try {
                db.createDoctor(doctor);
                r = "success";
            }

            catch (Exception e) {
                Log.e(CLASS_NAME, e.getMessage(), e);
                r = null;
            }

            finally {
                db.close();
            }

            return r;
        }

        @Override
        protected void thenDoUiRelatedWork(String s) {
            Utils.showProgress(false, progresssubmit, AddDoctorActivity.this);

            try {
                if (s == null) {
                    Toast.makeText(AddDoctorActivity.this, R.string.add_doctor_fail, Toast.LENGTH_SHORT).show();
                    Utils.unlockScreenOrientation(AddDoctorActivity.this);
                    return;
                }

                if ("success".equals(s)) {
                    reset();
                    submit = SUBMITTED;
                    Toast.makeText(AddDoctorActivity.this, R.string.add_doctor_ok, Toast.LENGTH_SHORT).show();
                }

                else {
                    Toast.makeText(AddDoctorActivity.this, R.string.add_doctor_fail, Toast.LENGTH_SHORT).show();
                }

                Utils.unlockScreenOrientation(AddDoctorActivity.this);
            }

            catch (Exception e) {
                Log.e(CLASS_NAME, e.getMessage(), e);
            }
        }
    }
}

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

public class EditDoctorActivity extends AppCompatActivity {

    private View progresssubmit;
    private EditText txtname, txtphone, txtmobile, txtemail, txtasst1, txtasst2, txtasst3;
    private TextView txtcust;
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

    private int id;
    private int submit = 0;
    private static int selectedCustPosition = 0;

    private PopulateCustomerTask populateCustomerTask = null;
    private LoadDoctorTask loadDoctorTask = null;

    public static final String ARG_DOCTOR_ID = "id";

    public static final int SUBMITTED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_doctor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        progresssubmit = findViewById(R.id.progresssubmit);
        txtcust = findViewById(R.id.txtcust);
        txtname = findViewById(R.id.txtname);
        txtphone = findViewById(R.id.txtphone);
        txtmobile = findViewById(R.id.txtmobile);
        txtemail = findViewById(R.id.txtemail);
        txtasst1 = findViewById(R.id.txtasst1);
        txtasst2 = findViewById(R.id.txtasst2);
        txtasst3 = findViewById(R.id.txtasst3);
        spcust = findViewById(R.id.spcust);

        chkmon_mor = findViewById(R.id.chkmon_morning);
        chkmon_aft = findViewById(R.id.chkmon_afternoon);
        chktue_mor = findViewById(R.id.chktue_morning);
        chktue_aft = findViewById(R.id.chktue_afternoon);
        chkwed_mor = findViewById(R.id.chkwed_morning);
        chkwed_aft = findViewById(R.id.chkwed_afternoon);
        chkthu_mor = findViewById(R.id.chkthu_morning);
        chkthu_aft = findViewById(R.id.chkthu_afternoon);
        chkfri_mor = findViewById(R.id.chkfri_morning);
        chkfri_aft = findViewById(R.id.chkfri_afternoon);
        chksat_mor = findViewById(R.id.chksat_morning);
        chksat_aft = findViewById(R.id.chksat_afternoon);
        chksun_mor = findViewById(R.id.chksun_morning);
        chksun_aft = findViewById(R.id.chksun_afternoon);

        id = getIntent().getIntExtra(ARG_DOCTOR_ID, 0);

        dbr = new DBHelper(this);
        spcust.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCustPosition = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        populateCustomerTask = new PopulateCustomerTask();
        Needle.onBackgroundThread()
                .withTaskType("populateCustomer")
                .execute(populateCustomerTask);

        db = new WriteDBHelper(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDoctorTask != null && !loadDoctorTask.isCanceled()) {
            loadDoctorTask.cancel();
        }

        if (populateCustomerTask != null && !populateCustomerTask.isCanceled()) {
            populateCustomerTask.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_doctor, menu);
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

        int i = spcust.getSelectedItemPosition();
        CustomerAdapter x = (CustomerAdapter) spcust.getAdapter();
        final Customer customer = x.getItem(i);

        Doctor o = new Doctor();
        o.setId(id);
        o.setName(Utils.getSqlStr(txtname.getText().toString()));
        o.setPhone(Utils.getSqlStr(txtphone.getText().toString()));
        o.setHp(Utils.getSqlStr(txtmobile.getText().toString()));
        o.setEmail(Utils.getSqlStr(txtemail.getText().toString()));
        o.setCustCode(customer.getCode());
        o.setCustName(customer.getName());
        o.setAssistant1(Utils.getSqlStr(txtasst1.getText().toString()));
        o.setAssistant2(Utils.getSqlStr(txtasst2.getText().toString()));
        o.setAssistant3(Utils.getSqlStr(txtasst3.getText().toString()));
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

        Needle.onBackgroundThread().execute(new UpdateDoctorTask(o));
    }

    class LoadDoctorTask extends CommonTask<Doctor> {

        private static final String CLASS_NAME = "LoadDoctorTask";

        LoadDoctorTask() {
            super(EditDoctorActivity.this);
        }

        @Override
        protected Doctor doWork() {
            Doctor o;

            try {
                o = db.getDoctor(id);
            }

            catch (Exception e) {
                Log.e(CLASS_NAME, e.getMessage(), e);
                o = null;
            }

            finally {
                db.close();
            }

            return o;
        }

        @Override
        protected void thenDoUiRelatedWork(Doctor o) {
            if (o == null) {
                Toast.makeText(EditDoctorActivity.this, "Failed to load doctor deatils with id " + id, Toast.LENGTH_SHORT).show();
                return;
            }

            txtname.setText(o.getName());
            txtphone.setText(o.getPhone());
            txtmobile.setText(o.getHp());
            txtemail.setText(o.getEmail());
            txtasst1.setText(o.getAssistant1());
            txtasst2.setText(o.getAssistant2());
            txtasst3.setText(o.getAssistant3());

            CustomerAdapter x = (CustomerAdapter) spcust.getAdapter();
            int i = x.getPosition(o.getCustCode(), o.getCustName());
            spcust.setSelection(i);

            chkmon_mor.setChecked(o.isMonMor());
            chkmon_aft.setChecked(o.isMonAft());
            chktue_mor.setChecked(o.isTueMor());
            chktue_aft.setChecked(o.isTueAft());
            chkwed_mor.setChecked(o.isWedMor());
            chkwed_aft.setChecked(o.isWedAft());
            chkthu_mor.setChecked(o.isThuMor());
            chkthu_aft.setChecked(o.isThuAft());
            chkfri_mor.setChecked(o.isFriMor());
            chkfri_aft.setChecked(o.isFriAft());
            chksat_mor.setChecked(o.isSatMor());
            chksat_aft.setChecked(o.isSatAft());
            chksun_mor.setChecked(o.isSunMor());
            chksun_aft.setChecked(o.isSunAft());

            Utils.unlockScreenOrientation(EditDoctorActivity.this);
        }
    }

    class PopulateCustomerTask extends CommonTask<ArrayList<Customer>> {

        private static final String CLASS_NAME = "PopulateCustomerTask";

        PopulateCustomerTask() {
            super(EditDoctorActivity.this);
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
            CustomerAdapter adapter = new CustomerAdapter(EditDoctorActivity.this, ls);
            spcust.setAdapter(adapter);
            spcust.setSelection(selectedCustPosition);
            Utils.unlockScreenOrientation(EditDoctorActivity.this);

            loadDoctorTask = new LoadDoctorTask();
            Needle.onBackgroundThread()
                    .withTaskType("loadDoctor")
                    .execute(loadDoctorTask);
        }
    }

    class UpdateDoctorTask extends CommonTask<String> {

        private Doctor doctor;

        private static final String CLASS_NAME = "UpdateDoctorTask";

        UpdateDoctorTask(Doctor doctor) {
            super(EditDoctorActivity.this);
            this.doctor = doctor;
            Utils.showProgress(true, progresssubmit, EditDoctorActivity.this);
        }

        @Override
        protected String doWork() {
            String r;

            try {
                db.updateDoctor(doctor);
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
            Utils.showProgress(false, progresssubmit, EditDoctorActivity.this);

            try {
                if (s == null) {
                    Toast.makeText(EditDoctorActivity.this, R.string.update_doctor_fail, Toast.LENGTH_SHORT).show();
                    Utils.unlockScreenOrientation(EditDoctorActivity.this);
                    return;
                }

                if ("success".equals(s)) {
                    submit = SUBMITTED;
                    Toast.makeText(EditDoctorActivity.this, R.string.update_doctor_ok, Toast.LENGTH_SHORT).show();
                    setResult(submit);
                    finish();
                }

                else {
                    Toast.makeText(EditDoctorActivity.this, R.string.update_doctor_fail, Toast.LENGTH_SHORT).show();
                }

                Utils.unlockScreenOrientation(EditDoctorActivity.this);
            }

            catch (Exception e) {
                Log.e(CLASS_NAME, e.getMessage(), e);
            }
        }
    }
}

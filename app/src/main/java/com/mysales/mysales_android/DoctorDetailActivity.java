package com.mysales.mysales_android;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.helpers.WriteDBHelper;
import com.mysales.mysales_android.models.Doctor;
import com.mysales.mysales_android.tasks.CommonTask;

import needle.Needle;

import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class DoctorDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CALL_PHONE = 0;

    private TextView txtname, txtcust, txtphone, txtmobile, txtemail, txtasst1, txtasst2, txtasst3, txtday;
    private TextView lbphone, lbmobile, lbemail, lbasst1, lbasst2, lbasst3;
    private View vphone, vmobile, vemail, vasst1, vasst2, vasst3;
    private Button btnedit, btndel, btnphone, btnmobile, btnmobilesms;
    private View lyphone, lymobile;

    private WriteDBHelper db;

    private int id;
    private int submit = 0;
    private String callNo;

    public static final String ARG_CUST = "cust_code";
    public static final String ARG_CUST_NAME = "cust_name";

    private LoadDoctorTask loadDoctorTask = null;

    public static final String ARG_DOCTOR_ID = "id";

    public static final int SUBMITTED = 1;
    private static final int EDITDOCTOR_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        txtname = (TextView) findViewById(R.id.txtname);
        txtcust = (TextView) findViewById(R.id.txtcust);
        txtphone = (TextView) findViewById(R.id.txtphone);
        txtmobile = (TextView) findViewById(R.id.txtmobile);
        txtemail = (TextView) findViewById(R.id.txtemail);
        txtasst1 = (TextView) findViewById(R.id.txtasst1);
        txtasst2 = (TextView) findViewById(R.id.txtasst2);
        txtasst3 = (TextView) findViewById(R.id.txtasst3);
        txtday = (TextView) findViewById(R.id.txtday);

        lbphone = (TextView) findViewById(R.id.lbphone);
        lbmobile = (TextView) findViewById(R.id.lbmobile);
        lbemail = (TextView) findViewById(R.id.lbemail);
        lbasst1 = (TextView) findViewById(R.id.lbasst1);
        lbasst2 = (TextView) findViewById(R.id.lbasst2);
        lbasst3 = (TextView) findViewById(R.id.lbasst3);

        vphone = findViewById(R.id.vphone);
        vmobile = findViewById(R.id.vmobile);
        vemail = findViewById(R.id.vemail);
        vasst1 = findViewById(R.id.vasst1);
        vasst2 = findViewById(R.id.vasst2);
        vasst3 = findViewById(R.id.vasst3);

        lyphone = findViewById(R.id.lyphone);
        lymobile = findViewById(R.id.lymobile);

        btnedit = (Button) findViewById(R.id.btnedit);
        btndel = (Button) findViewById(R.id.btndel);
        btnphone = (Button) findViewById(R.id.btnphone);
        btnmobile = (Button) findViewById(R.id.btnmobile);
        btnmobilesms = (Button) findViewById(R.id.btnmobilesms);

        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorDetailActivity.this, EditDoctorActivity.class);
                i.putExtra(EditDoctorActivity.ARG_DOCTOR_ID, id);
                startActivityForResult(i, EDITDOCTOR_REQUEST_CODE);
            }
        });

        btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(DoctorDetailActivity.this)
                        .setTitle("Delete doctor")
                        .setMessage("Do you want to delete the selected doctor?")
                        .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Needle.onBackgroundThread()
                                        .withTaskType("deletedoctor")
                                        .execute(new DeleteDoctorTask(String.valueOf(id)));
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancel), null)
                        .create()
                        .show();
            }
        });

        btnphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(txtphone.getText().toString());
            }
        });

        btnmobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call(txtmobile.getText().toString());
            }
        });

        btnmobilesms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sms(txtmobile.getText().toString());
            }
        });

        id = getIntent().getIntExtra(ARG_DOCTOR_ID, 0);

        db = new WriteDBHelper(this);

        load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDoctorTask != null && !loadDoctorTask.isCanceled()) {
            loadDoctorTask.cancel();
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDITDOCTOR_REQUEST_CODE && resultCode == EditDoctorActivity.SUBMITTED) {
            submit = SUBMITTED;
            setResult(submit);
            load();
        }
    }

    private void load() {
        loadDoctorTask = new LoadDoctorTask();
        Needle.onBackgroundThread()
                .withTaskType("loadDoctor")
                .execute(loadDoctorTask);
    }

    private void sms(String s) {
        callNo = s;
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("sms:" + callNo));
        startActivity(i);
    }

    private void call(String s) {
        callNo = s;
        checkCallPermission();
    }

    private void checkCallPermission() {
        if (!mayRequestCallPhone()) {
            return;
        }

        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:" + callNo));
        startActivity(i);
    }

    private boolean mayRequestCallPhone() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }

        if (shouldShowRequestPermissionRationale(CALL_PHONE)) {
            Snackbar.make(txtname, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[] { CALL_PHONE }, REQUEST_CALL_PHONE);
                        }
                    });
        }

        else {
            requestPermissions(new String[] { CALL_PHONE }, REQUEST_CALL_PHONE);
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkCallPermission();
            }
        }
    }

    class LoadDoctorTask extends CommonTask<Doctor> {

        private static final String CLASS_NAME = "LoadDoctorTask";

        public LoadDoctorTask() {
            super(DoctorDetailActivity.this);
        }

        @Override
        protected Doctor doWork() {
            Doctor o = null;

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
                Toast.makeText(DoctorDetailActivity.this, "Failed to load doctor deatils with id " + id, Toast.LENGTH_SHORT).show();
                return;
            }

            txtname.setText(o.getName());
            txtcust.setText(String.format("%s - %s", o.getCustCode(), o.getCustName()));
            txtphone.setText(o.getPhone());
            txtmobile.setText(o.getHp());
            txtemail.setText(o.getEmail());
            txtasst1.setText(o.getAssistant1());
            txtasst2.setText(o.getAssistant2());
            txtasst3.setText(o.getAssistant3());

            int v1 = Utils.isEmpty(o.getPhone()) ? View.GONE : View.VISIBLE;
            txtphone.setVisibility(v1);
            lbphone.setVisibility(v1);
            vphone.setVisibility(v1);
            lyphone.setVisibility(v1);

            int v2 = Utils.isEmpty(o.getHp()) ? View.GONE : View.VISIBLE;
            txtmobile.setVisibility(v2);
            lbmobile.setVisibility(v2);
            vmobile.setVisibility(v2);
            lymobile.setVisibility(v2);

            int v3 = Utils.isEmpty(o.getEmail()) ? View.GONE : View.VISIBLE;
            txtemail.setVisibility(v3);
            lbemail.setVisibility(v3);
            vemail.setVisibility(v3);

            int v4 = Utils.isEmpty(o.getAssistant1()) ? View.GONE : View.VISIBLE;
            txtasst1.setVisibility(v4);
            lbasst1.setVisibility(v4);
            vasst1.setVisibility(v4);

            int v5 = Utils.isEmpty(o.getAssistant2()) ? View.GONE : View.VISIBLE;
            txtasst2.setVisibility(v5);
            lbasst2.setVisibility(v5);
            vasst2.setVisibility(v5);

            int v6 = Utils.isEmpty(o.getAssistant3()) ? View.GONE : View.VISIBLE;
            txtasst3.setVisibility(v6);
            lbasst3.setVisibility(v6);
            vasst3.setVisibility(v6);

            txtday.setText(o.getDays());

            Utils.unlockScreenOrientation(DoctorDetailActivity.this);
        }
    }

    class DeleteDoctorTask extends CommonTask<String> {

        private String ids;

        private static final String CLASS_NAME = "DeleteDoctorTask";

        public DeleteDoctorTask(String ids) {
            super(DoctorDetailActivity.this);
            this.ids = ids;
        }

        @Override
        protected String doWork() {
            String r = null;

            try {
                db.deletedoctors(ids);
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
            if (s == null) {
                Toast.makeText(DoctorDetailActivity.this, "Doctor failed to be deleted, please retry", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("success".equals(s)) {
                submit = SUBMITTED;
                Toast.makeText(DoctorDetailActivity.this, "Doctor have been successfully deleted", Toast.LENGTH_SHORT).show();
                setResult(SUBMITTED);
                finish();
            }

            else {
                Toast.makeText(DoctorDetailActivity.this, "Doctor failed to be deleted, please retry", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

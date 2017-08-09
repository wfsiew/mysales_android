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

    private TextView txtname, txtcust, txtphone, txtmobile, txtemail, txtday;
    private TextView lbphone, lbmobile, lbemail;
    private View vphone, vmobile, vemail;
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
        txtday = (TextView) findViewById(R.id.txtday);

        lbphone = (TextView) findViewById(R.id.lbphone);
        lbmobile = (TextView) findViewById(R.id.lbmobile);
        lbemail = (TextView) findViewById(R.id.lbemail);

        vphone = findViewById(R.id.vphone);
        vmobile = findViewById(R.id.vmobile);
        vemail = findViewById(R.id.vemail);

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
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Needle.onBackgroundThread()
                                        .withTaskType("deletedoctor")
                                        .execute(new DeleteDoctorTask(String.valueOf(id)));
                            }
                        })
                        .setNegativeButton("Cancel", null)
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

            if (Utils.isEmpty(o.getPhone())) {
                txtphone.setVisibility(View.GONE);
                lbphone.setVisibility(View.GONE);
                vphone.setVisibility(View.GONE);
                lyphone.setVisibility(View.GONE);
            }

            if (Utils.isEmpty(o.getHp())) {
                txtmobile.setVisibility(View.GONE);
                lbmobile.setVisibility(View.GONE);
                vmobile.setVisibility(View.GONE);
                lymobile.setVisibility(View.GONE);
            }

            if (Utils.isEmpty(o.getEmail())) {
                txtemail.setVisibility(View.GONE);
                lbemail.setVisibility(View.GONE);
                vemail.setVisibility(View.GONE);
            }

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

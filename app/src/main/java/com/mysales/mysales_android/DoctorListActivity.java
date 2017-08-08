package com.mysales.mysales_android;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.mysales.mysales_android.adapters.CustomerItemRecyclerViewAdapter;
import com.mysales.mysales_android.adapters.DoctorAdapter;
import com.mysales.mysales_android.helpers.Utils;
import com.mysales.mysales_android.helpers.WriteDBHelper;
import com.mysales.mysales_android.models.Customer;
import com.mysales.mysales_android.models.Doctor;
import com.mysales.mysales_android.tasks.CommonTask;

import java.util.ArrayList;

import needle.Needle;

public class DoctorListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View progress, lybottom, empty;
    private ListView listdoctor;
    private FloatingActionButton fab;
    private SearchView searchView;
    private Button btndel, btndelall;
    private ProgressDialog pd;
    private Dialog dlg;
    private boolean showSelect;
    private String query;
    private String day;

    private WriteDBHelper db;

    private DoctorListTask doctorListTask = null;

    private static final int ADDDOCTOR_REQUEST_CODE = 1;
    private static final int EDITDOCTOR_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DoctorListActivity.this, AddDoctorActivity.class);
                startActivityForResult(i, ADDDOCTOR_REQUEST_CODE);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        lybottom = findViewById(R.id.lybottom);
        btndel = (Button) findViewById(R.id.btndel);
        btndelall = (Button) findViewById(R.id.btndelall);
        progress = findViewById(R.id.progress);
        listdoctor = (ListView) findViewById(R.id.listdoctor);
        empty = findViewById(R.id.empty);

        listdoctor.setEmptyView(empty);
        listdoctor.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                toggleSelection();
                DoctorAdapter x = (DoctorAdapter) listdoctor.getAdapter();
                String s = x.getSelectedIds();
                if (s == null)
                    btndel.setEnabled(false);

                return true;
            }
        });

        listdoctor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (showSelect) {
                    DoctorAdapter x = (DoctorAdapter) listdoctor.getAdapter();
                    x.select(view, position);
                }

                else {
                    Doctor o = (Doctor) listdoctor.getAdapter().getItem(position);
                    Intent i = new Intent(DoctorListActivity.this, EditDoctorActivity.class);
                    i.putExtra(EditDoctorActivity.ARG_DOCTOR_ID, o.getId());
                    startActivityForResult(i, EDITDOCTOR_REQUEST_CODE);
                }
            }
        });

        btndel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorAdapter x = (DoctorAdapter) listdoctor.getAdapter();
                String s = x.getSelectedIds();
                if (s == null)
                    return;

                pd = ProgressDialog.show(DoctorListActivity.this, "",
                        getResources().getString(R.string.delete_wait));
                Needle.onBackgroundThread()
                        .withTaskType("deletedoctor")
                        .execute(new DeleteDoctorTask(s));
            }
        });

        btndelall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoctorAdapter x = (DoctorAdapter) listdoctor.getAdapter();
                String s = x.getIds();
                if (s == null)
                    return;

                pd = ProgressDialog.show(DoctorListActivity.this, "",
                        getResources().getString(R.string.delete_wait));
                Needle.onBackgroundThread()
                        .withTaskType("deletedoctor")
                        .execute(new DeleteDoctorTask(s));
            }
        });

        db = new WriteDBHelper(this);

        dlg = new Dialog(this);
        dlg.setContentView(R.layout.dialog_days);
        dlg.setTitle("Please Select Day");

        Spinner spday = (Spinner) dlg.findViewById(R.id.spday);
        spday.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] a = getResources().getStringArray(R.array.days);
                String x = a[i];
                if ("All".equals(x)) {
                    x = null;
                }

                dlg.dismiss();
                load(query, x);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        load();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else if (!searchView.isIconified()) {
            searchView.setIconified(true);
        }

        else if (showSelect) {
            toggleSelection();
        }

        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_main) {
            super.onBackPressed();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (doctorListTask != null && !doctorListTask.isCanceled()) {
            doctorListTask.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_doctor_list, menu);
        searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                load(query, day);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (Utils.isEmpty(newText))
                    load();

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add_doctor) {
            Intent i = new Intent(this, AddDoctorActivity.class);
            startActivityForResult(i, ADDDOCTOR_REQUEST_CODE);
            return true;
        }

        else if (id == R.id.menu_reload) {
            load(query, day);
            return false;
        }

        else if (id == R.id.menu_day) {
            dlg.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDDOCTOR_REQUEST_CODE && resultCode == AddDoctorActivity.SUBMITTED) {
            load(query, day);
        }

        else if (requestCode == EDITDOCTOR_REQUEST_CODE && resultCode == EditDoctorActivity.SUBMITTED) {
            load(query, day);
        }
    }

    private void load() {
        load(null, null);
    }

    private void load(String q, String d) {
        query = q;
        day = d;
        Toast.makeText(this, "===== query = " + query + " --" + day, Toast.LENGTH_SHORT).show();
        doctorListTask = new DoctorListTask();
        Needle.onBackgroundThread()
                .withTaskType("doctorList")
                .execute(doctorListTask);
    }

    private void toggleSelection() {
        DoctorAdapter x = (DoctorAdapter) listdoctor.getAdapter();
        showSelect = x.toggleSelect();
        lybottom.setVisibility(showSelect ? View.VISIBLE : View.GONE);
        fab.setVisibility(showSelect ? View.GONE : View.VISIBLE);
        x.notifyDataSetChanged();
    }

    private void showProgress(final boolean show) {
        Utils.showProgress(show, progress, getApplicationContext());
        listdoctor.setVisibility(show ? View.GONE : View.VISIBLE);
        empty.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    class DoctorListTask extends CommonTask<ArrayList<Doctor>> {

        private static final String CLASS_NAME = "DoctorListTask";

        private String keyword, day;

        public DoctorListTask() {
            super(DoctorListActivity.this);
            showProgress(true);
        }

        @Override
        protected ArrayList<Doctor> doWork() {
            ArrayList<Doctor> ls = new ArrayList<>();

            try {
                ls = db.filterDoctor(query, day);
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
        protected void thenDoUiRelatedWork(ArrayList<Doctor> ls) {
            showProgress(false);
            DoctorAdapter adapter = new DoctorAdapter(DoctorListActivity.this, ls, btndel);
            listdoctor.setAdapter(adapter);
            lybottom.setVisibility(showSelect ? View.VISIBLE : View.GONE);
            Utils.unlockScreenOrientation(DoctorListActivity.this);
        }
    }

    class DeleteDoctorTask extends CommonTask<String> {

        private String ids;

        private static final String CLASS_NAME = "DeleteDoctorTask";

        public DeleteDoctorTask(String ids) {
            super(DoctorListActivity.this);
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
            pd.dismiss();
            if (s == null) {
                Toast.makeText(DoctorListActivity.this, "Doctor(s) failed to be deleted, please retry", Toast.LENGTH_SHORT).show();
                return;
            }

            if ("success".equals(s)) {
                Toast.makeText(DoctorListActivity.this, "Doctor(s) have been successfully deleted", Toast.LENGTH_SHORT).show();
                load(query, day);
            }

            else {
                Toast.makeText(DoctorListActivity.this, "Doctor(s) failed to be deleted, please retry", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
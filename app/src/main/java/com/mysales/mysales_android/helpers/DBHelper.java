package com.mysales.mysales_android.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.mysales.mysales_android.models.Customer;

import java.util.ArrayList;

/**
 * Created by wingfei.siew on 7/31/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private String db_path = null;
    private SQLiteDatabase db;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        db_path = Environment.getExternalStorageDirectory() + "/mysales/app.db";
    }

    public void openDataBase() throws SQLException {
        db = SQLiteDatabase.openDatabase(db_path, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (db != null)
            db.close();

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<String> getCustomers() {
        ArrayList<String> ls = new ArrayList<>();
        openDataBase();
        Cursor cur = db.rawQuery("select distinct cust_name from sales order by cust_name", null);
        cur.moveToFirst();

        while (cur.isAfterLast() == false) {
            ls.add(cur.getString(cur.getColumnIndex("cust_name")));
            cur.moveToNext();
        }

        return ls;
    }

    public ArrayList<Customer> filterCustomer(String name, String period, String year) {
        ArrayList<Customer> ls = new ArrayList<>();
        boolean and = false;
        openDataBase();
        StringBuffer sb = new StringBuffer();
        sb.append("select id, cust_code, cust_name from sales");

        if (name != null && !name.isEmpty() && !period.isEmpty() && !year.isEmpty()) {
            sb.append(" where");

            if (name != null && !name.isEmpty()) {
                sb.append(" name like %" + name + "%");
                and = true;
            }

            if (!period.isEmpty()) {
                if (and) {
                    sb.append(" and period in (" + period + ")");
                }

                else {
                    sb.append("period in (" + period + ")");
                    and = true;
                }
            }

            if (!year.isEmpty()) {
                if (and) {
                    sb.append(" and year in (" + year + ")");
                }

                else {
                    sb.append(" year in (" + year + ")");
                }
            }
        }

        sb.append(" order by cust_name");

        String q = sb.toString();
        Cursor cur = db.rawQuery(q, null);
        cur.moveToFirst();

        while (cur.isAfterLast() == false) {
            Customer o = new Customer();
            o.setId(cur.getInt(cur.getColumnIndex("id")));
            o.setCode(cur.getString(cur.getColumnIndex("cust_code")));
            o.setName(cur.getString(cur.getColumnIndex("cust_name")));
            ls.add(o);
            cur.moveToNext();
        }

        return ls;
    }
}

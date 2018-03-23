package com.mysales.mysales_android.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.mysales.mysales_android.models.Target;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by wingfei.siew on 3/22/2018.
 */

public class TargetDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "target.db";

    private String db_path = null;
    private SQLiteDatabase db;

    public TargetDBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
        db_path = Environment.getExternalStorageDirectory() + "/mysales/target.db";
    }

    public void openDataBase() throws SQLException {
        db = SQLiteDatabase.openDatabase(db_path, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (db != null) {
            db.close();
        }

        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<String> getProductGroups() {
        ArrayList<String> ls = new ArrayList<>();
        Cursor cur = null;

        try {
            cur = db.rawQuery("select distinct product_group from target order by product_group", null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                String v = cur.getString(cur.getColumnIndex("product_group"));
                ls.add(v);
                cur.moveToNext();
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return ls;
    }

    public HashMap<String, Target> getHalfYearlyTarget(String h) {
        HashMap<String, Target> m = new HashMap<>();
        Cursor cur = null;

        try {
            StringBuilder sb = new StringBuilder();
            int year = Calendar.getInstance().get(Calendar.YEAR);

            String months = Utils.getHalfYearMonths(h);

            sb.append("select product_group, sum(sales_value) salesv from target")
                    .append(" where year = ").append(year)
                    .append(" and month in (").append(months).append(")")
                    .append(" group by product_group");

            String q = sb.toString();
            cur = db.rawQuery(q, null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                Target o = new Target();
                o.setProductGroup(cur.getString(cur.getColumnIndex("product_group")));
                o.setYear(year);
                o.setValue(cur.getDouble(cur.getColumnIndex("salesv")));
                m.put(o.getProductGroup(), o);
                cur.moveToNext();
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return m;
    }

    public HashMap<String, Target> getQuarterlyTarget(String quarter) {
        HashMap<String, Target> m = new HashMap<>();
        Cursor cur = null;

        try {
            StringBuilder sb = new StringBuilder();
            int year = Calendar.getInstance().get(Calendar.YEAR);

            String months = Utils.getQuarterMonths(quarter);

            sb.append("select product_group, sum(sales_value) salesv from target")
                    .append(" where year = ").append(year)
                    .append(" and month in (").append(months).append(")")
                    .append(" group by product_group");

            String q = sb.toString();
            cur = db.rawQuery(q, null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                Target o = new Target();
                o.setProductGroup(cur.getString(cur.getColumnIndex("product_group")));
                o.setYear(year);
                o.setValue(cur.getDouble(cur.getColumnIndex("salesv")));
                m.put(o.getProductGroup(), o);
                cur.moveToNext();
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return m;
    }

    public HashMap<String, Target> getMonthlyTarget(String months) {
        HashMap<String, Target> m = new HashMap<>();
        Cursor cur = null;

        try {
            StringBuilder sb = new StringBuilder();
            int year = Calendar.getInstance().get(Calendar.YEAR);

            sb.append("select product_group, sum(sales_value) salesv from target")
                    .append(" where year = ").append(year)
                    .append(" and month in (").append(months).append(")")
                    .append(" group by product_group");

            String q = sb.toString();
            cur = db.rawQuery(q, null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                Target o = new Target();
                o.setProductGroup(cur.getString(cur.getColumnIndex("product_group")));
                o.setYear(year);
                o.setValue(cur.getDouble(cur.getColumnIndex("salesv")));
                m.put(o.getProductGroup(), o);
                cur.moveToNext();
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return m;
    }
}

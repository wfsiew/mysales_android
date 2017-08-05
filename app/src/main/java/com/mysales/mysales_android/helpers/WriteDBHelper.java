package com.mysales.mysales_android.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.mysales.mysales_android.models.Doctor;

import java.util.ArrayList;

/**
 * Created by wfsiew on 8/5/17.
 */

public class WriteDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "data.db";

    private String db_path = null;
    private SQLiteDatabase db;

    public WriteDBHelper(Context context, boolean write) {
        super(context, DATABASE_NAME, null, 1);
        db_path = Environment.getExternalStorageDirectory() + "/mysales/data.db";
    }

    public void openDataBase() throws SQLException {
        db = SQLiteDatabase.openDatabase(db_path, null, SQLiteDatabase.OPEN_READWRITE);
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

    public void createDoctor(Doctor doctor) {
        openDataBase();
        String q = String.format("insert into doctor (name, phone, hp, email) values('%s', '%s', '%s', '%s')");
        db.execSQL(q);
    }

    public ArrayList<Doctor> getDoctors(String search) {
        ArrayList<Doctor> ls = new ArrayList<>();
        openDataBase();
        StringBuffer sb = new StringBuffer();

        sb.append("select id, name, phone, hp, email from doctor");

        if (!Utils.isEmpty(search)) {
            sb.append(" where name like '%" + search + "%' or")
                    .append(" phone like '%" + search + "%' or")
                    .append(" hp like '%" + search + "%' or")
                    .append(" email like '%" + search + "%'");
        }

        sb.append(" order by name");

        String q = sb.toString();
        Cursor cur = db.rawQuery(q, null);
        cur.moveToFirst();

        while (cur.isAfterLast() == false) {
            Doctor o = new Doctor();
            o.setId(cur.getInt(cur.getColumnIndex("id")));
            o.setName(cur.getString(cur.getColumnIndex("name")));
            o.setPhone(cur.getString(cur.getColumnIndex("phone")));
            o.setHp(cur.getString(cur.getColumnIndex("hp")));
            o.setEmail(cur.getString(cur.getColumnIndex("email")));
            ls.add(o);
            cur.moveToNext();
        }

        return ls;
    }
}

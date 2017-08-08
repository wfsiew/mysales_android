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

    public WriteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db_path = Environment.getExternalStorageDirectory() + "/mysales/data.db";
    }

    public void openDataBase() throws SQLException {
        db = SQLiteDatabase.openDatabase(db_path, null, SQLiteDatabase.OPEN_READWRITE);
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

    public Doctor getDoctor(int id) {
        Doctor o = new Doctor();
        openDataBase();
        StringBuffer sb = new StringBuffer();

        sb.append("select id, name, phone, hp, email, cust_code, cust_name, ")
                .append("mon_mor, mon_aft, tue_mor, tue_aft, ")
                .append("wed_mor, wed_aft, thu_mor, thu_aft, ")
                .append("fri_mor, fri_aft, sat_mor, sat_aft, sun_mor, sun_aft ")
                .append("from doctor ")
                .append("where id = " + id);
        String q = sb.toString();
        Cursor cur = db.rawQuery(q, null);
        cur.moveToFirst();

        if (cur.isAfterLast())
            return o;

        o.setId(cur.getInt(cur.getColumnIndex("id")));
        o.setName(cur.getString(cur.getColumnIndex("name")));
        o.setPhone(cur.getString(cur.getColumnIndex("phone")));
        o.setHp(cur.getString(cur.getColumnIndex("hp")));
        o.setEmail(cur.getString(cur.getColumnIndex("email")));
        o.setCustCode(cur.getString(cur.getColumnIndex("cust_code")));
        o.setCustName(cur.getString(cur.getColumnIndex("cust_name")));
        o.setMonMor(Utils.getBoolean(cur.getInt(cur.getColumnIndex("mon_mor"))));
        o.setMonAft(Utils.getBoolean(cur.getInt(cur.getColumnIndex("mon_aft"))));
        o.setTueMor(Utils.getBoolean(cur.getInt(cur.getColumnIndex("tue_mor"))));
        o.setTueAft(Utils.getBoolean(cur.getInt(cur.getColumnIndex("tue_aft"))));
        o.setWedMor(Utils.getBoolean(cur.getInt(cur.getColumnIndex("wed_mor"))));
        o.setWedAft(Utils.getBoolean(cur.getInt(cur.getColumnIndex("wed_aft"))));
        o.setThuMor(Utils.getBoolean(cur.getInt(cur.getColumnIndex("thu_mor"))));
        o.setThuAft(Utils.getBoolean(cur.getInt(cur.getColumnIndex("thu_aft"))));
        o.setFriMor(Utils.getBoolean(cur.getInt(cur.getColumnIndex("fri_mor"))));
        o.setFriAft(Utils.getBoolean(cur.getInt(cur.getColumnIndex("fri_aft"))));
        o.setSatMor(Utils.getBoolean(cur.getInt(cur.getColumnIndex("sat_mor"))));
        o.setSatAft(Utils.getBoolean(cur.getInt(cur.getColumnIndex("sat_aft"))));
        o.setSunAft(Utils.getBoolean(cur.getInt(cur.getColumnIndex("sun_mor"))));
        o.setSunAft(Utils.getBoolean(cur.getInt(cur.getColumnIndex("sun_aft"))));

        return o;
    }

    public void createDoctor(Doctor doctor) {
        openDataBase();
        StringBuffer sb = new StringBuffer();

        sb.append("insert into doctor (name, phone, hp, email, cust_code, cust_name, ")
                .append("mon_mor, mon_aft, tue_mor, tue_aft, ")
                .append("wed_mor, wed_aft, thu_mor, thu_aft, ")
                .append("fri_mor, fri_aft, sat_mor, sat_aft, sun_mor, sun_aft) ")
                .append("values(?, ?, ?, ?, ?, ?, ")
                .append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        String q = sb.toString();
        Object[] p = new Object[] { doctor.getName(), doctor.getPhone(), doctor.getHp(), doctor.getEmail(),
                doctor.getCustCode(), doctor.getCustName(),
                Utils.getInt(doctor.isMonMor()), Utils.getInt(doctor.isMonAft()), Utils.getInt(doctor.isTueMor()), Utils.getInt(doctor.isTueAft()),
                Utils.getInt(doctor.isWedMor()), Utils.getInt(doctor.isWedAft()), Utils.getInt(doctor.isThuMor()), Utils.getInt(doctor.isThuAft()),
                Utils.getInt(doctor.isFriMor()), Utils.getInt(doctor.isFriAft()), Utils.getInt(doctor.isSatMor()), Utils.getInt(doctor.isSatAft()),
                Utils.getInt(doctor.isSunMor()), Utils.getInt(doctor.isSunAft())
        };
        db.execSQL(q, p);
    }

    public void updateDoctor(Doctor doctor) {
        openDataBase();
        StringBuffer sb = new StringBuffer();

        sb.append("update doctor set name = ?, phone = ?, hp = ?, email = ?, cust_code = ?, cust_name = ?, ")
                .append("mon_mor = ?, mon_aft = ?, tue_mor = ?, tue_aft = ?, ")
                .append("wed_mor = ?, wed_aft = ?, thu_mor = ?, thu_aft = ?, ")
                .append("fri_mor = ?, fri_aft = ?, sat_mor = ?, sat_aft = ?, sun_mor = ?, sun_aft = ? ")
                .append("where id = ?");
        String q = sb.toString();
        Object[] p = new Object[] { doctor.getName(), doctor.getPhone(), doctor.getHp(), doctor.getEmail(),
                doctor.getCustCode(), doctor.getCustName(),
                Utils.getInt(doctor.isMonMor()), Utils.getInt(doctor.isMonAft()), Utils.getInt(doctor.isTueMor()), Utils.getInt(doctor.isTueAft()),
                Utils.getInt(doctor.isWedMor()), Utils.getInt(doctor.isWedAft()), Utils.getInt(doctor.isThuMor()), Utils.getInt(doctor.isThuAft()),
                Utils.getInt(doctor.isFriMor()), Utils.getInt(doctor.isFriAft()), Utils.getInt(doctor.isSatMor()), Utils.getInt(doctor.isSatAft()),
                Utils.getInt(doctor.isSunMor()), Utils.getInt(doctor.isSunAft()),
                doctor.getId()
        };
        db.execSQL(q, p);
    }

    public void deletedoctors(String s) {
        openDataBase();
        String q = "delete from doctor where id in (" + s + ")";
        db.execSQL(q);
    }

    public ArrayList<Doctor> filterDoctor(String search, String day) {
        ArrayList<Doctor> ls = new ArrayList<>();
        openDataBase();
        StringBuffer sb = new StringBuffer();

        sb.append("select id, name, phone, hp, email, cust_code, cust_name, ")
                .append("mon_mor, mon_aft, tue_mor, tue_aft, ")
                .append("wed_mor, wed_aft, thu_mor, thu_aft, ")
                .append("fri_mor, fri_aft, sat_mor, sat_aft, sun_mor, sun_aft ")
                .append("from doctor");

        if (!Utils.isEmpty(search)) {
            sb.append(" where (name like '%" + search + "%' or")
                    .append(" phone like '%" + search + "%' or")
                    .append(" hp like '%" + search + "%' or")
                    .append(" email like '%" + search + "%')");

            if (!Utils.isEmpty(day)) {
                sb.append(" and " + getDay(day) + " = 1");
            }
        }

        else {
            if (!Utils.isEmpty(day)) {
                sb.append(" where " + getDay(day) + " = 1");
            }
        }

        sb.append(" order by name");

        String q = sb.toString();
        //System.out.println("===========" + q);
        Cursor cur = db.rawQuery(q, null);
        cur.moveToFirst();

        while (cur.isAfterLast() == false) {
            Doctor o = new Doctor();
            o.setId(cur.getInt(cur.getColumnIndex("id")));
            o.setName(cur.getString(cur.getColumnIndex("name")));
            o.setPhone(cur.getString(cur.getColumnIndex("phone")));
            o.setHp(cur.getString(cur.getColumnIndex("hp")));
            o.setEmail(cur.getString(cur.getColumnIndex("email")));
            o.setCustCode(cur.getString(cur.getColumnIndex("cust_code")));
            o.setCustName(cur.getString(cur.getColumnIndex("cust_name")));
            ls.add(o);
            cur.moveToNext();
        }

        return ls;
    }

    private String getDay(String s) {
        String a = null;

        if ("Mon Morning".equals(s)) {
            a = "mon_mor";
        }

        else if ("Mon Afternoon".equals(s)) {
            a = "mon_aft";
        }

        else if ("Tue Morning".equals(s)) {
            a = "tue_mor";
        }

        else if ("Tue Afternoon".equals(s)) {
            a = "tue_aft";
        }

        else if ("Wed Morning".equals(s)) {
            a = "wed_mor";
        }

        else if ("Wed Afternoon".equals(s)) {
            a = "wed_aft";
        }

        else if ("Thu Morning".equals(s)) {
            a = "thu_mor";
        }

        else if ("Thu Afternoon".equals(s)) {
            a = "thu_aft";
        }

        else if ("Fri Morning".equals(s)) {
            a = "fri_mor";
        }

        else if ("Fri Afternoon".equals(s)) {
            a = "fri_aft";
        }

        else if ("Sat Morning".equals(s)) {
            a = "sat_mor";
        }

        else if ("Sat Afternoon".equals(s)) {
            a = "sat_aft";
        }

        else if ("Sun Morning".equals(s)) {
            a = "sun_mor";
        }

        else if ("Sun Afternoon".equals(s)) {
            a = "sun_aft";
        }

        return a;
    }
}

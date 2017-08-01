package com.mysales.mysales_android.helpers;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.mysales.mysales_android.models.Customer;
import com.mysales.mysales_android.models.CustomerAddress;
import com.mysales.mysales_android.models.CustomerItem;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

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

    public ArrayList<Customer> filterCustomer(String name, String period, String year, String sort) {
        ArrayList<Customer> ls = new ArrayList<>();
        boolean and = false;
        openDataBase();
        StringBuffer sb = new StringBuffer();

        if (Utils.isEmpty(sort))
            sort = "cust_name";

        if (sort.equals("cust_name"))
            sb.append("select distinct cust_code, cust_name from sales");

        else {
            sb.append("select cust_code, cust_name, sum(sales_unit) salesu, sum(sales_value) salesv, sum(bonus_unit) bonusu from sales");
        }

        if (!Utils.isEmpty(name) || !period.isEmpty() || !year.isEmpty()) {
            sb.append(" where");

            if (!Utils.isEmpty(name)) {
                sb.append(" cust_name like '%" + name + "%'");
                and = true;
            }

            if (!period.isEmpty()) {
                if (and) {
                    sb.append(" and period in (" + period + ")");
                }

                else {
                    sb.append(" period in (" + period + ")");
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

        if (!sort.equals("cust_name")) {
            sb.append(" group by cust_code, cust_name");
        }

        sb.append(" order by " + sort);
        //System.out.println("===========" + sb.toString());

        String q = sb.toString();
        Cursor cur = db.rawQuery(q, null);
        cur.moveToFirst();

        while (cur.isAfterLast() == false) {
            Customer o = new Customer();
            o.setCode(cur.getString(cur.getColumnIndex("cust_code")));
            o.setName(cur.getString(cur.getColumnIndex("cust_name")));
            ls.add(o);
            cur.moveToNext();
        }

        return ls;
    }

    public CustomerAddress getCustomerAddress(String name) {
        CustomerAddress o = new CustomerAddress();
        String q = "select cust_addr1, cust_addr2, cust_addr3, postal_code, area, territory from sales where cust_name = '" + name  + "'";
        Cursor cur = db.rawQuery(q, null);
        cur.moveToFirst();

        String addr1 = cur.getString(cur.getColumnIndex("cust_addr1"));
        String addr2 = cur.getString(cur.getColumnIndex("cust_addr2"));
        String addr3 = cur.getString(cur.getColumnIndex("cust_addr3"));
        String postalcode = cur.getString(cur.getColumnIndex("postal_code"));
        String area = cur.getString(cur.getColumnIndex("area"));
        String territory = cur.getString(cur.getColumnIndex("territory"));

        o.setAddr1(addr1);
        o.setAddr2(addr2);
        o.setAddr3(addr3);
        o.setPostalCode(postalcode);
        o.setArea(area);
        o.setTerritory(territory);

        return o;
    }

    public HashMap<String, ArrayList<CustomerItem>> getItemsByCustomer(String code, String name, String period, String year,
                                                                       String sort,
                                                                       CustomerAddress addr,
                                                                       ArrayList<String> ls) {
        HashMap<String, ArrayList<CustomerItem>> m = new HashMap<>();
        openDataBase();
        CustomerAddress address = getCustomerAddress(name);
        addr.set(address);
        StringBuffer sb = new StringBuffer();
        StringBuffer sa = new StringBuffer();

        if (Utils.isEmpty(sort))
            sort = "salesv desc";

        sb.append("select period, year, item_name, sum(sales_unit) salesu, sum(sales_value) salesv, sum(bonus_unit) bonusu from sales")
                .append(" where cust_name = '" + name + "'");
        sa.append("select period, year, sum(sales_unit) salesu, sum(sales_value) salesv, sum(bonus_unit) bonusu from sales")
                .append(" where cust_name = '" + name + "'");

        if (!period.isEmpty()) {
            sb.append(" and period in (" + period + ")");
            sa.append(" and period in (" + period + ")");
        }

        if (!year.isEmpty()) {
            sb.append(" and year in (" + year + ")");
            sa.append(" and year in (" + year + ")");
        }

        sb.append(" group by period, year, item_name")
                .append(" order by period, year, item_name");
        sa.append(" group by period, year")
                .append(" order by " + sort);
        //System.out.println("===========" + sb.toString());

        String q = sb.toString();
        Cursor cur = db.rawQuery(q, null);
        cur.moveToFirst();

        while (cur.isAfterLast() == false) {
            int month = cur.getInt(cur.getColumnIndex("period"));
            int y = cur.getInt(cur.getColumnIndex("year"));
            String item = cur.getString(cur.getColumnIndex("item_name"));
            int salesq = cur.getInt(cur.getColumnIndex("salesu"));
            double salesv = cur.getDouble(cur.getColumnIndex("salesv"));
            int bonus = cur.getInt(cur.getColumnIndex("bonusu"));

            String key = String.format("%d-%d", y, month);
            CustomerItem x = new CustomerItem();
            x.setCode(code);
            x.setName(name);
            x.setItem(item);
            x.setUnit(salesq);
            x.setValue(salesv);
            x.setBonus(bonus);

            if (m.containsKey(key)) {
                m.get(key).add(x);
            }

            else {
                ArrayList<CustomerItem> l = new ArrayList<>();
                l.add(x);
                m.put(key, l);

                if (sort.equals("item_name"))
                    ls.add(key);
            }

            cur.moveToNext();
        }

        if (!sort.equals("item_name")) {
            q = sa.toString();
            cur = db.rawQuery(q, null);
            cur.moveToFirst();

            while (cur.isAfterLast() == false) {
                int month = cur.getInt(cur.getColumnIndex("period"));
                int y = cur.getInt(cur.getColumnIndex("year"));

                String key = String.format("%d-%d", y, month);
                ls.add(key);
                cur.moveToNext();
            }
        }

        return m;
    }
}

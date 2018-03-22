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
import com.mysales.mysales_android.models.SalesSummary;
import com.mysales.mysales_android.models.Target;

import java.util.ArrayList;
import java.util.Calendar;
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

    public ArrayList<String> getCustomers() {
        ArrayList<String> ls = new ArrayList<>();
        Cursor cur = null;

        try {
            cur = db.rawQuery("select distinct cust_name from sales order by cust_name", null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                ls.add(cur.getString(cur.getColumnIndex("cust_name")));
                cur.moveToNext();
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return ls;
    }

    public ArrayList<Customer> getCustomers1() {
        ArrayList<Customer> ls = new ArrayList<>();
        Cursor cur = null;

        try {
            cur = db.rawQuery("select distinct cust_code, cust_name from sales order by cust_name", null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                Customer o = new Customer();
                o.setCode(cur.getString(cur.getColumnIndex("cust_code")));
                o.setName(cur.getString(cur.getColumnIndex("cust_name")));
                ls.add(o);
                cur.moveToNext();
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return ls;
    }

    public ArrayList<String> getItems() {
        ArrayList<String> ls = new ArrayList<>();
        Cursor cur = null;

        try {
            cur = db.rawQuery("select distinct item_name from sales order by item_name", null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                ls.add(cur.getString(cur.getColumnIndex("item_name")));
                cur.moveToNext();
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return ls;
    }

    public ArrayList<String> getProductGroups() {
        ArrayList<String> ls = new ArrayList<>();
        Cursor cur = null;

        try {
            cur = db.rawQuery("select distinct product_group from sales where product_group not in ('0') order by product_group", null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                ls.add(cur.getString(cur.getColumnIndex("product_group")));
                cur.moveToNext();
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return ls;
    }

    public ArrayList<Customer> filterCustomer(String name, String item, String productgroup, String period, String year, String sort) {
        ArrayList<Customer> ls = new ArrayList<>();
        Cursor cur = null;
        boolean and = false;

        try {
            openDataBase();
            StringBuilder sb = new StringBuilder();

            if (Utils.isEmpty(sort))
                sort = "cust_name";

            if ("cust_name".equals(sort)) {
                sb.append("select distinct cust_code, cust_name from sales");
            } else {
                sb.append("select cust_code, cust_name, sum(sales_unit) salesu, sum(sales_value) salesv,")
                        .append(" sum(bonus_unit) bonusu from sales");
            }

            if (!Utils.isEmpty(name) || !Utils.isEmpty(item) || !Utils.isEmpty(period) || !Utils.isEmpty(year)) {
                sb.append(" where");

                if (!Utils.isEmpty(name)) {
                    sb.append(" cust_name like '%").append(name).append("%'");
                    and = true;
                }

                if (!Utils.isEmpty(item)) {
                    if (and) {
                        sb.append(" and item_name in (").append(item).append(")");
                    } else {
                        sb.append(" item_name in (").append(item).append(")");
                        and = true;
                    }
                }

                if (!Utils.isEmpty(productgroup)) {
                    if (and) {
                        sb.append(" and product_group in (").append(productgroup).append(")");
                    } else {
                        sb.append(" product_group in (").append(productgroup).append(")");
                        and = true;
                    }
                }

                if (!Utils.isEmpty(period)) {
                    if (and) {
                        sb.append(" and period in (").append(period).append(")");
                    } else {
                        sb.append(" period in (").append(period).append(")");
                        and = true;
                    }
                }

                if (!Utils.isEmpty(year)) {
                    if (and) {
                        sb.append(" and year in (").append(year).append(")");
                    } else {
                        sb.append(" year in (").append(year).append(")");
                    }
                }
            }

            if (!"cust_name".equals(sort)) {
                sb.append(" group by cust_code, cust_name");
            }

            sb.append(" order by ").append(sort);
            //System.out.println("===========" + sb.toString());

            String q = sb.toString();
            cur = db.rawQuery(q, null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                Customer o = new Customer();
                o.setCode(cur.getString(cur.getColumnIndex("cust_code")));
                o.setName(cur.getString(cur.getColumnIndex("cust_name")));
                ls.add(o);
                cur.moveToNext();
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return ls;
    }

    private CustomerAddress getCustomerAddress(String code, String name) {
        CustomerAddress o = new CustomerAddress();
        String q = "select cust_addr1, cust_addr2, cust_addr3, postal_code, area, territory from sales" +
                " where cust_code = '" + code  + "'  and cust_name = '" + name + "'";
        Cursor cur = null;

        try {
            cur = db.rawQuery(q, null);
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
        }

        finally {
            Utils.closeCursor(cur);
        }

        return o;
    }

    public HashMap<String, ArrayList<CustomerItem>> getItemsByCustomer(String code, String name, String items, String productGroup, String period, String year,
                                                                       String sort,
                                                                       CustomerAddress addr,
                                                                       ArrayList<String> ls) {
        HashMap<String, ArrayList<CustomerItem>> m = new HashMap<>();
        Cursor cur = null;

        try {
            openDataBase();
            CustomerAddress address = getCustomerAddress(code, name);
            addr.set(address);
            StringBuilder sb = new StringBuilder();
            StringBuilder sa = new StringBuilder();

            if (Utils.isEmpty(sort))
                sort = "salesv desc";

            sb.append("select period, year, item_name, sum(sales_unit) salesu, sum(sales_value) salesv, sum(bonus_unit) bonusu from sales")
                    .append(" where cust_code = '").append(code)
                    .append("' and cust_name = '")
                    .append(name).append("'");
            sa.append("select period, year, sum(sales_unit) salesu, sum(sales_value) salesv, sum(bonus_unit) bonusu from sales")
                    .append(" where cust_code = '")
                    .append(code).append("' and cust_name = '")
                    .append(name).append("'");

            if (!Utils.isEmpty(items)) {
                sb.append(" and item_name in (").append(items).append(")");
                sa.append(" and item_name in (").append(items).append(")");
            }

            if (!Utils.isEmpty(productGroup)) {
                sb.append(" and product_group in (").append(productGroup).append(")");
                sa.append(" and product_group in (").append(productGroup).append(")");
            }

            if (!Utils.isEmpty(period)) {
                sb.append(" and period in (").append(period).append(")");
                sa.append(" and period in (").append(period).append(")");
            }

            if (!Utils.isEmpty(year)) {
                sb.append(" and year in (").append(year).append(")");
                sa.append(" and year in (").append(year).append(")");
            }

            sb.append(" group by period, year, item_name")
                    .append(" order by ").append(sort).append(", period, year");
            sa.append(" group by period, year")
                    .append(" order by ").append(sort).append(", period, year");
            //System.out.println("===========" + sb.toString());

            String q = sb.toString();
            cur = db.rawQuery(q, null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
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
                } else {
                    ArrayList<CustomerItem> l = new ArrayList<>();
                    l.add(x);
                    m.put(key, l);

                    if ("item_name".equals(sort))
                        ls.add(key);
                }

                cur.moveToNext();
            }

            if (!"item_name".equals(sort)) {
                q = sa.toString();
                cur = db.rawQuery(q, null);
                cur.moveToFirst();

                while (!cur.isAfterLast()) {
                    int month = cur.getInt(cur.getColumnIndex("period"));
                    int y = cur.getInt(cur.getColumnIndex("year"));

                    String key = String.format("%d-%d", y, month);
                    ls.add(key);

                    cur.moveToNext();
                }
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return m;
    }

    public SalesSummary getHalfYearlySummary(int h, String product, Target t) {
        SalesSummary o = new SalesSummary();
        Cursor cur = null;

        try {
            o.setProductGroup(product);
            o.setTarget(t.getValue());

            StringBuilder sb = new StringBuilder();

            int year = Calendar.getInstance().get(Calendar.YEAR);
            int pyear = year - 1;
            String years = year + "," + pyear;

            String period = "";
            if (h == 1)
                period = "1,2,3,4,5,6";

            else if (h == 2)
                period = "7,8,9,10,11,12";

            sb.append("select year, sum(sales_value) salesv from sales")
                    .append(" where period in (").append(period).append(")")
                    .append(" and product_group like '").append(product).append("%'")
                    .append(" and year in (").append(years).append(")")
                    .append(" group by year");

            String q = sb.toString();
            cur = db.rawQuery(q, null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                int y = cur.getInt(cur.getColumnIndex("year"));
                if (y == year)
                    o.setActual(cur.getDouble(cur.getColumnIndex("salesv")));

                else if (y == pyear)
                    o.setActual1(cur.getDouble(cur.getColumnIndex("salesv")));
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return o;
    }

    public SalesSummary getQuarterlySummary(int quarter, String product, Target t) {
        SalesSummary o = new SalesSummary();
        Cursor cur = null;

        try {
            o.setProductGroup(product);
            o.setTarget(t.getValue());

            StringBuilder sb = new StringBuilder();

            int year = Calendar.getInstance().get(Calendar.YEAR);
            int pyear = year - 1;
            String years = year + "," + pyear;

            String period = "";
            switch (quarter) {
                case 1:
                    period = "1,2,3";
                    break;

                case 2:
                    period = "4,5,6";
                    break;

                case 3:
                    period = "7,8,9";
                    break;

                case 4:
                    period = "10,11,12";
                    break;
            }

            sb.append("select year, sum(sales_value) salesv from sales")
                    .append(" where period in (").append(period).append(")")
                    .append(" and product_group like '").append(product).append("%'")
                    .append(" and year in (").append(years).append(")")
                    .append(" group by year");

            String q = sb.toString();
            cur = db.rawQuery(q, null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                int y = cur.getInt(cur.getColumnIndex("year"));
                if (y == year)
                    o.setActual(cur.getDouble(cur.getColumnIndex("salesv")));

                else if (y == pyear)
                    o.setActual1(cur.getDouble(cur.getColumnIndex("salesv")));
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return o;
    }

    public SalesSummary getMontlySummary(int month, String product, Target t) {
        SalesSummary o = new SalesSummary();
        Cursor cur = null;

        try {
            o.setProductGroup(product);
            o.setTarget(t.getValue());

            StringBuilder sb = new StringBuilder();

            int year = Calendar.getInstance().get(Calendar.YEAR);
            int pyear = year - 1;
            String years = year + "," + pyear;

            sb.append("select year, sum(sales_value) as salesv from sales")
                    .append(" where period = ").append(month)
                    .append(" and product_group like '").append(product).append("%'")
                    .append(" and year in (").append(years).append(")")
                    .append(" group by year");

            String q = sb.toString();
            cur = db.rawQuery(q, null);
            cur.moveToFirst();

            while (!cur.isAfterLast()) {
                int y = cur.getInt(cur.getColumnIndex("year"));
                if (y == year)
                    o.setActual(cur.getDouble(cur.getColumnIndex("salesv")));

                else if (y == pyear)
                    o.setActual1(cur.getDouble(cur.getColumnIndex("salesv")));
            }
        }

        finally {
            Utils.closeCursor(cur);
        }

        return o;
    }
}

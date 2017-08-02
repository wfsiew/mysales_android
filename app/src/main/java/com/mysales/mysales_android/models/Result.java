package com.mysales.mysales_android.models;

import java.util.ArrayList;

/**
 * Created by wfsiew on 8/2/17.
 */

public class Result {

    private ArrayList<CustomerItem> list;
    private int totalSalesUnit;
    private int totalBonusUnit;
    private double totalSalesValue;

    public ArrayList<CustomerItem> getList() {
        return list;
    }

    public void setList(ArrayList<CustomerItem> list) {
        this.list = list;
    }

    public int getTotalSalesUnit() {
        return totalSalesUnit;
    }

    public void setTotalSalesUnit(int totalSalesUnit) {
        this.totalSalesUnit = totalSalesUnit;
    }

    public int getTotalBonusUnit() {
        return totalBonusUnit;
    }

    public void setTotalBonusUnit(int totalBonusUnit) {
        this.totalBonusUnit = totalBonusUnit;
    }

    public double getTotalSalesValue() {
        return totalSalesValue;
    }

    public void setTotalSalesValue(double totalSalesValue) {
        this.totalSalesValue = totalSalesValue;
    }
}

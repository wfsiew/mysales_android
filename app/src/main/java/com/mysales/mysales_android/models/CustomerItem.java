package com.mysales.mysales_android.models;

/**
 * Created by wfsiew on 7/31/17.
 */

public class CustomerItem {

    private String code;
    private String name;
    private String item;
    private int unit;
    private double value;
    private int bonus;

    private String header;
    private boolean isHeader;
    private boolean isFooter;

    private int sumunit;
    private int sumbonus;
    private double sumvalue;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean header) {
        isHeader = header;
    }

    public boolean isFooter() {
        return isFooter;
    }

    public void setFooter(boolean footer) {
        isFooter = footer;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public int getSumunit() {
        return sumunit;
    }

    public void setSumunit(int sumunit) {
        this.sumunit = sumunit;
    }

    public int getSumbonus() {
        return sumbonus;
    }

    public void setSumbonus(int sumbonus) {
        this.sumbonus = sumbonus;
    }

    public double getSumvalue() {
        return sumvalue;
    }

    public void setSumvalue(double sumvalue) {
        this.sumvalue = sumvalue;
    }
}

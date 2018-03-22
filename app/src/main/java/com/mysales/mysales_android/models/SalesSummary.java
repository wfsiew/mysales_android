package com.mysales.mysales_android.models;

/**
 * Created by wingfei.siew on 3/22/2018.
 */

public class SalesSummary {

    private String productGroup;
    private double actual;
    private double target;
    private double actual1;

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public double getActual() {
        return actual;
    }

    public void setActual(double actual) {
        this.actual = actual;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(double target) {
        this.target = target;
    }

    public double getActual1() {
        return actual1;
    }

    public void setActual1(double actual1) {
        this.actual1 = actual1;
    }
}

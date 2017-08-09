package com.mysales.mysales_android.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wingfei.siew on 7/31/2017.
 */

public class Customer {

    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        if ("All".equals(getCode())) {
            return getCode();
        }

        return String.format("%s - %s", getCode(), getName());
    }
}

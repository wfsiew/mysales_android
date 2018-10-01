package com.mysales.mysales_android.models;

/**
 * Created by wfsiew on 8/1/17.
 */

public class CustomerAddress {

    private String addr1;
    private String addr2;
    private String addr3;
    private String postalCode;
    private String area;
    private String territory;
    private String telephone;
    private String contact;

    public String getAddr1() {
        return addr1;
    }

    public void setAddr1(String addr1) {
        this.addr1 = addr1;
        if ("0".equals(this.addr1)) {
            this.addr1 = "";
        }
    }

    public String getAddr2() {
        return addr2;
    }

    public void setAddr2(String addr2) {
        this.addr2 = addr2;
        if ("0".equals(this.addr2)) {
            this.addr2 = "";
        }
    }

    public String getAddr3() {
        return addr3;
    }

    public void setAddr3(String addr3) {
        this.addr3 = addr3;
        if ("0".equals(this.addr3)) {
            this.addr3 = "";
        }
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
        if ("0".equals(this.postalCode)) {
            this.postalCode = "";
        }
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
        if ("0".equals(this.area)) {
            this.area = "";
        }
    }

    public String getTerritory() {
        return territory;
    }

    public void setTerritory(String territory) {
        this.territory = territory;
        if ("0".equals(this.territory)) {
            this.territory = "";
        }
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
        if ("0".equals(this.telephone)) {
            this.telephone = "";
        }
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
        if ("0".equals(this.contact)) {
            this.contact = "";
        }
    }

    public void set(CustomerAddress address) {
        setAddr1(address.getAddr1());
        setAddr2(address.getAddr2());
        setAddr3(address.getAddr3());
        setPostalCode(address.getPostalCode());
        setArea(address.getArea());
        setTerritory(address.getTerritory());
        setTelephone(address.getTelephone());
        setContact(address.getContact());
    }
}

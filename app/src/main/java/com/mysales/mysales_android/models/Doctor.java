package com.mysales.mysales_android.models;

import com.mysales.mysales_android.helpers.Utils;

import java.util.ArrayList;

/**
 * Created by wfsiew on 8/5/17.
 */

public class Doctor {

    private int id;
    private String name;
    private String phone;
    private String hp;
    private String email;
    private String assistant1;
    private String assistant2;
    private String assistant3;
    private String custCode;
    private String custName;
    private boolean monMor;
    private boolean monAft;
    private boolean tueMor;
    private boolean tueAft;
    private boolean wedMor;
    private boolean wedAft;
    private boolean thuMor;
    private boolean thuAft;
    private boolean friMor;
    private boolean friAft;
    private boolean satMor;
    private boolean satAft;
    private boolean sunMor;
    private boolean sunAft;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        this.hp = hp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAssistant1() {
        return assistant1;
    }

    public void setAssistant1(String assistant1) {
        this.assistant1 = assistant1;
    }

    public String getAssistant2() {
        return assistant2;
    }

    public void setAssistant2(String assistant2) {
        this.assistant2 = assistant2;
    }

    public String getAssistant3() {
        return assistant3;
    }

    public void setAssistant3(String assistant3) {
        this.assistant3 = assistant3;
    }

    public String getCustCode() {
        return custCode;
    }

    public void setCustCode(String custCode) {
        this.custCode = custCode;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public boolean isMonMor() {
        return monMor;
    }

    public void setMonMor(boolean monMor) {
        this.monMor = monMor;
    }

    public boolean isMonAft() {
        return monAft;
    }

    public void setMonAft(boolean monAft) {
        this.monAft = monAft;
    }

    public boolean isTueMor() {
        return tueMor;
    }

    public void setTueMor(boolean tueMor) {
        this.tueMor = tueMor;
    }

    public boolean isTueAft() {
        return tueAft;
    }

    public void setTueAft(boolean tueAft) {
        this.tueAft = tueAft;
    }

    public boolean isWedMor() {
        return wedMor;
    }

    public void setWedMor(boolean wedMor) {
        this.wedMor = wedMor;
    }

    public boolean isWedAft() {
        return wedAft;
    }

    public void setWedAft(boolean wedAft) {
        this.wedAft = wedAft;
    }

    public boolean isThuMor() {
        return thuMor;
    }

    public void setThuMor(boolean thuMor) {
        this.thuMor = thuMor;
    }

    public boolean isThuAft() {
        return thuAft;
    }

    public void setThuAft(boolean thuAft) {
        this.thuAft = thuAft;
    }

    public boolean isFriMor() {
        return friMor;
    }

    public void setFriMor(boolean friMor) {
        this.friMor = friMor;
    }

    public boolean isFriAft() {
        return friAft;
    }

    public void setFriAft(boolean friAft) {
        this.friAft = friAft;
    }

    public boolean isSatMor() {
        return satMor;
    }

    public void setSatMor(boolean satMor) {
        this.satMor = satMor;
    }

    public boolean isSatAft() {
        return satAft;
    }

    public void setSatAft(boolean satAft) {
        this.satAft = satAft;
    }

    public boolean isSunMor() {
        return sunMor;
    }

    public void setSunMor(boolean sunMor) {
        this.sunMor = sunMor;
    }

    public boolean isSunAft() {
        return sunAft;
    }

    public void setSunAft(boolean sunAft) {
        this.sunAft = sunAft;
    }

    public String getDays() {
        String s = "";
        StringBuffer sb = new StringBuffer();
        if (isMonMor()) {
            sb.append("Mon Morning | ");
        }

        if (isMonAft()) {
            sb.append("Mon Afternoon | ");
        }

        if (isTueMor()) {
            sb.append("Tue Morning | ");
        }

        if (isTueAft()) {
            sb.append("Tue Afternoon | ");
        }

        if (isWedMor()) {
            sb.append("Wed Morning | ");
        }

        if (isWedAft()) {
            sb.append("Wed Afternoon | ");
        }

        if (isThuMor()) {
            sb.append("Thu Morning | ");
        }

        if (isThuAft()) {
            sb.append("Thu Afternoon | ");
        }

        if (isFriMor()) {
            sb.append("Fri Morning | ");
        }

        if (isFriAft()) {
            sb.append("Fri Afternoon | ");
        }

        if (isSatMor()) {
            sb.append("Sat Morning | ");
        }

        if (isSatAft()) {
            sb.append("Sat Afternoon | ");
        }

        if (isSunMor()) {
            sb.append("Sun Morning | ");
        }

        if (isSunAft()) {
            sb.append("Sun Afternoon | ");
        }

        if (sb.length() > 0) {
            s = sb.substring(0, sb.length() - 3);
        }

        return s;
    }
}

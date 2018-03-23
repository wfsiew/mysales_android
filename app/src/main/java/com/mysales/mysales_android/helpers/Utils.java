package com.mysales.mysales_android.helpers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.view.View;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wingfei.siew on 7/31/2017.
 */

public class Utils {

    public static boolean isEmpty(String s) {
        boolean b = false;
        if (s == null) {
            b = true;
        }

        else if (s.isEmpty()) {
            b = true;
        }

        return b;
    }

    public static String getSqlStr(String s) {
        return s;
    }

    public static String getEmptyString(String v) {
        return getEmptyString(v, "");
    }

    private static String getEmptyString(String v, String k) {
        String s = v;
        if (isEmpty(v) || "null".equalsIgnoreCase(v)) {
            s = k;
        }

        return s;
    }

    public static String getMessages(ArrayList<String> ls) {
        if (ls.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ls.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, ls.get(i)));
        }

        return sb.toString();
    }

    public static String formatDouble(double x) {
        DecimalFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(x);
    }

    public static String escapeStr(String s) {
        String r = s;
        if (isEmpty(s)) {
            return s;
        }

        r = r.replace("'", "''");
        return r;
    }

    public static int getInt(boolean x) {
        int a = x ? 1 : 0;
        return a;
    }

    static boolean getBoolean(int x) {
        return x == 1;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static void showProgress(final boolean show, final View progress, final Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

                progress.setVisibility(show ? View.VISIBLE : View.GONE);
                progress.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progress.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                progress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        }

        catch (Exception e) {

        }
    }

    public static void lockScreenOrientation(Activity a) {
        try {
            Resources res = a.getResources();
            int currentOrientation = res.getConfiguration().orientation;
            if (currentOrientation == Configuration.ORIENTATION_PORTRAIT) {
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }

            else {
                a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        catch (Exception e) {

        }
    }

    public static void unlockScreenOrientation(Activity a) {
        if (a == null)
            return;

        a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
    }

    static void closeCursor(Cursor cur) {
        if (cur != null) cur.close();
    }

    public static String getSelected(List<KeyPairBoolData> li) {
        return getSelected(li, false);
    }

    public static String getSelected(List<KeyPairBoolData> li, boolean usequote) {
        StringBuilder sb = new StringBuilder();
        String r = "";

        if (li.isEmpty()) {
            return r;
        }

        for (int i = 0; i < li.size(); i++) {
            String v = Utils.escapeStr(li.get(i).getName());
            if (usequote) {
                sb.append(String.format("'%s'", v));
            }

            else {
                sb.append(v);
            }

            if (i < li.size() - 1) {
                sb.append(",");
            }
        }

        r = sb.toString();
        return r;
    }

    static String getHalfYearMonths(String h) {
        String[] arr = null;
        StringBuilder sb = new StringBuilder();

        if (h.contains(",")) {
            arr = h.split(",");
        }

        else {
            arr = new String[] { h };
        }

        for (String y : arr) {
            if ("1".equals(y)) {
                sb.append("1,2,3,4,5,6");
            }

            else if ("2".equals(y)) {
                sb.append("7,8,9,10,11,12");
            }
        }

        return sb.toString();
    }

    static String getQuarterMonths(String quarter) {
        String[] arr = null;
        StringBuilder sb = new StringBuilder();

        if (quarter.contains(",")) {
            arr = quarter.split(",");
        }

        else {
            arr = new String[] { quarter };
        }

        for (String period : arr) {
            if ("1".equals(period)) {
                sb.append("1,2,3");
            }

            else if ("2".equals(period)) {
                sb.append("4,5,6");
            }

            else if ("3".equals(period)) {
                sb.append("7,8,9");
            }

            else if ("4".equals(period)) {
                sb.append("10,11,12");
            }
        }

        return sb.toString();
    }
}

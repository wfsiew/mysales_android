package com.mysales.mysales_android.helpers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;

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
        return escapeStr(getEmptyString(s));
    }

    public static String getEmptyString(String v) {
        return getEmptyString(v, "");
    }

    public static String getEmptyString(String v, String k) {
        String s = v;
        if (isEmpty(v) || "null".equalsIgnoreCase(v)) {
            s = k;
        }

        return s;
    }

    public static String getMessages(ArrayList<String> ls) {
        String s = null;
        if (ls.isEmpty()) {
            return s;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ls.size(); i++) {
            sb.append(String.format("%d. %s\n", i + 1, ls.get(i)));
        }

        s = sb.toString();
        return s;
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
        int a = x == true ? 1 : 0;
        return a;
    }

    public static boolean getBoolean(int x) {
        boolean a = x == 1 ? true : false;
        return a;
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
}

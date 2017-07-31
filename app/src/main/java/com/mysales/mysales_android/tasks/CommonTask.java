package com.mysales.mysales_android.tasks;

import android.app.Activity;

import com.mysales.mysales_android.helpers.Utils;

import needle.UiRelatedTask;

/**
 * Created by wingfei.siew on 7/31/2017.
 */

public abstract class CommonTask<T> extends UiRelatedTask<T> {

    private Activity activity;

    protected CommonTask() {}

    protected CommonTask(Activity a) {
        activity = a;
        init();
    }

    protected void init() {
        Utils.lockScreenOrientation(activity);
    }
}

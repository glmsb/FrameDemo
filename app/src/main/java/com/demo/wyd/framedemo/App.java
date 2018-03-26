package com.demo.wyd.framedemo;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Description:
 * Created by wyd on 2018-3-26.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}

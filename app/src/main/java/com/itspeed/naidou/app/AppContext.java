package com.itspeed.naidou.app;

import android.app.Application;

import org.kymjs.kjframe.bitmap.BitmapConfig;
import org.kymjs.kjframe.http.HttpConfig;

/**
 * 
 */
public class AppContext extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        BitmapConfig.CACHEPATH = AppConfig.imgCachePath;
        HttpConfig.CACHEPATH = AppConfig.httpCachePath;
    }




}
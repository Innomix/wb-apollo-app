package me.fmtech.apollo.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatDelegate;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.slamtec.slamware.AbstractSlamwarePlatform;
import com.slamtec.slamware.discovery.DeviceManager;

import java.util.HashSet;
import java.util.Set;

import me.fmtech.apollo.di.component.AppComponent;
import me.fmtech.apollo.di.component.DaggerAppComponent;
import me.fmtech.apollo.di.module.AppModule;
import me.fmtech.apollo.di.module.HttpModule;

public class App extends Application {

    public static AppComponent appComponent;
    private static App instance;
    private static AbstractSlamwarePlatform sSlamwarePlatform;

    static {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }

    private Set<Activity> allActivities;

    public static synchronized App getInstance() {
        return instance;
    }

    public static AppComponent getAppComponent() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(instance))
                    .httpModule(new HttpModule())
                    .build();
        }
        return appComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void addActivity(Activity act) {
        if (allActivities == null) {
            allActivities = new HashSet<>();
        }
        allActivities.add(act);
    }

    public void removeActivity(Activity act) {
        if (allActivities != null) {
            allActivities.remove(act);
        }

        if (null == allActivities || allActivities.size() == 0) {
            disConnect();
        }
    }

    public static AbstractSlamwarePlatform getSlamwarePlatform() {
        if (null == sSlamwarePlatform) {
            synchronized (App.class) {
                if (null == sSlamwarePlatform) {
                    try {
                        sSlamwarePlatform = DeviceManager.connect("192.168.11.1", 1445);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return sSlamwarePlatform;
    }

    public static AbstractSlamwarePlatform reConnect() {
        disConnect();
        return getSlamwarePlatform();
    }

    public static void disConnect() {
        if (null != sSlamwarePlatform) {
            sSlamwarePlatform.disconnect();
            sSlamwarePlatform = null;
        }
    }

    public void exitApp() {
        if (allActivities != null) {
            synchronized (allActivities) {
                for (Activity act : allActivities) {
                    act.finish();
                }
            }
        }
//        android.os.Process.killProcess(android.os.Process.myPid());
//        System.exit(0);
    }
}

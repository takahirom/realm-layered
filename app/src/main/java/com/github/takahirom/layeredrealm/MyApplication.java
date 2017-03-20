package com.github.takahirom.layeredrealm;

import android.app.Application;

import io.realm.Realm;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealmHelper.init(this);
    }
}
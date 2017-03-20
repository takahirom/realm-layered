package com.github.takahirom.layeredrealm;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;

class RealmHelper {

    private static RealmConfiguration cacheRealmConfiguration;
    // Retention for performance
    private static Realm defaultRealm;
    private static Realm cacheRealm;

    public static void init(Context context) {
        Realm.init(context);
        defaultRealm = Realm.getDefaultInstance();

        cacheRealmConfiguration = new RealmConfiguration.Builder()
                .name("cache.realm")
                .deleteRealmIfMigrationNeeded()
                .build();
        cacheRealm = Realm.getInstance(cacheRealmConfiguration);
    }

    public static Realm getCacheRealm() {
        return Realm.getInstance(cacheRealmConfiguration);
    }
}

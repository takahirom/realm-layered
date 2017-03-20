# realm-layered

Custom Application

```java
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RealmHelper.init(this);
    }
}
```

Helper class for realm

```java
class RealmHelper {

    private static RealmConfiguration cacheRealmConfiguration;
    // just for performance
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
```

MainActivity for get and save data

```java

public class MainActivity extends AppCompatActivity {
...
    private void load() {
        final long start = SystemClock.uptimeMillis();
        dataStore.getUser()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BiConsumer<List<User>, Throwable>() {
                    @Override
                    public void accept(@NonNull List<User> users, @NonNull Throwable throwable) throws Exception {
                        for (User user : users) {
                            // for performance check
                            user.getAge();
                            user.getName();
                        }
                        long end = SystemClock.uptimeMillis();
                        logText.setText("■load:" + (end - start) + "ms realmCount:" + RealmUtil.dumpRealmCount() + " \n" + logText.getText());
                    }
                });
    }
    ...
```

DataStore class using copyFromRealm

```java
public class DataStore {
...
    public Single<List<User>> getUser() {
        return Single.create(new SingleOnSubscribe<List<User>>() {
            @Override
            public void subscribe(SingleEmitter<List<User>> e) throws Exception {
                Realm realm = RealmHelper.getCacheRealm();
                RealmResults<User> users = realm
                        .where(User.class)
                        .lessThanOrEqualTo("age", 100)
                        .findAll();
                List<User> standAloneList = realm.copyFromRealm(users);
                realm.close();
                e.onSuccess(standAloneList);
            }
        });
    }
}
```

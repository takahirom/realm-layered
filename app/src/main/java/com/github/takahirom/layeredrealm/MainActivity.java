package com.github.takahirom.layeredrealm;

import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.RealmUtil;

public class MainActivity extends AppCompatActivity {

    private static final boolean STRICT_MODE = false;
    private TextView logText;
//    public List<User> memoryCacheUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (STRICT_MODE) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()   // or .detectAll() for all detectable problems
                    .penaltyLog()
                    .penaltyDialog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }
        setContentView(R.layout.activity_main);
        logText = (TextView) findViewById(R.id.log);
//        Button nothing = (Button) findViewById(R.id.nothing);
//        nothing.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final long start = SystemClock.uptimeMillis();
//                Single.just("test")
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(new BiConsumer<String, Throwable>() {
//                            @Override
//                            public void accept(@NonNull String s, @NonNull Throwable throwable) throws Exception {
//                                long end = SystemClock.uptimeMillis();
//                                logText.setText("■nothing:" + (end - start) + "ms "+ RealmUtil.dumpRealmCount()+" \n" + logText.getText());
//                            }
//                        });
//            }
//        });

        Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long start = SystemClock.uptimeMillis();
                saveData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action() {
                            @Override
                            public void run() throws Exception {
                                long end = SystemClock.uptimeMillis();
                                logText.setText("■save:" + (end - start) + "ms realmCount:" + RealmUtil.dumpRealmCount() + " \n" + logText.getText());
                            }
                        });
            }
        });
        Button loadButton = (Button) findViewById(R.id.load);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long start = SystemClock.uptimeMillis();
                loadStandaloneObject()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BiConsumer<List<User>, Throwable>() {
                            @Override
                            public void accept(@NonNull List<User> users, @NonNull Throwable throwable) throws Exception {
                                for (User user : users) {
                                    user.getAge();
                                    user.getName();
                                }
                                long end = SystemClock.uptimeMillis();
                                logText.setText("■load:" + (end - start) + "ms realmCount:" + RealmUtil.dumpRealmCount() + " \n" + logText.getText());
                            }
                        });
            }
        });

        Button liveObjectLoad = (Button) findViewById(R.id.load_live_object);
        liveObjectLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long start = SystemClock.uptimeMillis();
                loadLiveObject()
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<RealmResults<User>>() {
                            @Override
                            public void accept(@NonNull RealmResults<User> users) throws Exception {
                                for (User user : users) {
                                    user.getAge();
                                    user.getName();
                                }
                                long end = SystemClock.uptimeMillis();
                                logText.setText("■load live object:" + (end - start) + "ms realmCount:" + RealmUtil.dumpRealmCount() + " \n" + logText.getText());
                            }
                        });

            }
        });
        final Button loadAsync = (Button) findViewById(R.id.load_async);
        loadAsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long start = SystemClock.uptimeMillis();
                loadLiveObjectAsync()
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<RealmResults<User>>() {
                            @Override
                            public void accept(@NonNull RealmResults<User> users) throws Exception {
                                for (User user : users) {
                                    user.getAge();
                                    user.getName();
                                }
                                long end = SystemClock.uptimeMillis();
                                logText.setText("■load live object async:" + (end - start) + "ms realmCount:" + RealmUtil.dumpRealmCount() + " \n" + logText.getText());
                            }
                        });
            }
        });

        Button snapshot = (Button) findViewById(R.id.load_snapshot);
        snapshot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final long start = SystemClock.uptimeMillis();
                loadLiveObjectSnapshot()
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<List<User>>() {
                            @Override
                            public void accept(@NonNull List<User> users) throws Exception {
                                for (User user : users) {
                                    user.getAge();
                                    user.getName();
                                }
                                long end = SystemClock.uptimeMillis();
                                logText.setText("■load live object async:" + (end - start) + "ms realmCount:" + RealmUtil.dumpRealmCount() + " \n" + logText.getText());
                            }
                        });
            }
        });
    }

    private Completable saveData() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                for (int i = 0; i < 200; i++) {
                    User user = new User();
                    user.setName("John" + i);
                    user.setAge(i);
                    realm.copyToRealmOrUpdate(user);
                }
                realm.commitTransaction();
                e.onComplete();
                realm.close();
            }
        });
    }

    private Single<List<User>> loadStandaloneObject() {
        return Single.create(new SingleOnSubscribe<List<User>>() {
            @Override
            public void subscribe(SingleEmitter<List<User>> e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                RealmResults<User> users = realm
                        .where(User.class)
                        .greaterThan("age", 100)
                        .findAll();
                List<User> standAloneList = realm.copyFromRealm(users);
                realm.close();
                e.onSuccess(standAloneList);
            }
        });
    }

    private Single<RealmResults<User>> loadLiveObject() {
        return Single.create(new SingleOnSubscribe<RealmResults<User>>() {
            @Override
            public void subscribe(final SingleEmitter<RealmResults<User>> e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                final RealmResults<User> users = realm
                        .where(User.class)
                        .greaterThan("age", 100)
                        .findAll();
                e.onSuccess(users);
            }
        });
    }

    private Single<RealmResults<User>> loadLiveObjectAsync() {
        return Single.create(new SingleOnSubscribe<RealmResults<User>>() {
            @Override
            public void subscribe(final SingleEmitter<RealmResults<User>> e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                final RealmResults<User> users = realm
                        .where(User.class)
                        .greaterThan("age", 100)
                        .findAllAsync();
                e.onSuccess(users);
            }
        });
    }

    private Single<List<User>> loadLiveObjectSnapshot() {
        return Single.create(new SingleOnSubscribe<List<User>>() {
            @Override
            public void subscribe(final SingleEmitter<List<User>> e) throws Exception {
                Realm realm = Realm.getDefaultInstance();
                final RealmResults<User> users = realm
                        .where(User.class)
                        .greaterThan("age", 100)
                        .findAllAsync();
                e.onSuccess(users.createSnapshot());
            }
        });
    }
}


package com.github.takahirom.layeredrealm;

import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmUtil;

public class MainActivity extends AppCompatActivity {

    private TextView logText;
    private DataStore dataStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dataStore = new DataStore();
        logText = (TextView) findViewById(R.id.log);

        Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });
        Button loadButton = (Button) findViewById(R.id.load);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });

    }

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

    private void save() {
        final long start = SystemClock.uptimeMillis();
        dataStore.saveUser()
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

}


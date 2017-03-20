package com.github.takahirom.layeredrealm;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

public class DataStore {

    public Completable saveUser() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(CompletableEmitter e) throws Exception {
                Realm realm = null;
                try {
                    realm = RealmHelper.getCacheRealm();
                    realm.beginTransaction();
                    for (int i = 0; i < 200; i++) {
                        User user = new User();
                        user.setName("John" + i);
                        user.setAge(i);
                        realm.copyToRealmOrUpdate(user);
                    }
                    realm.commitTransaction();
                    e.onComplete();
                } catch (RealmException ignored) {

                } finally {
                    if (realm != null) {
                        if (realm.isInTransaction()) {
                            realm.cancelTransaction();
                        }
                        realm.close();
                    }
                }
            }
        });
    }

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

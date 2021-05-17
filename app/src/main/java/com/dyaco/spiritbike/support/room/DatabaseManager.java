package com.dyaco.spiritbike.support.room;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.dyaco.spiritbike.support.room.entity.FavoritesEntity;
import com.dyaco.spiritbike.support.room.entity.HistoryEntity;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndFavorites;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndHistory;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndTemplates;
import com.dyaco.spiritbike.MyApplication;

import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

import static com.dyaco.spiritbike.support.room.SoleDatabase.MIGRATION_1_2;
import static com.dyaco.spiritbike.support.room.SoleDatabase.MIGRATION_2_3;

public class DatabaseManager {
    private static DatabaseManager instance;
    private final SoleDatabase db;
    private static final String DB_NAME = "sole_database";

    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SoleDatabase.class) {
                if (instance == null) {
                    instance = new DatabaseManager(context);
                }
            }
        }
        return instance;
    }

    public DatabaseManager(Context context) {
        db = Room.databaseBuilder(context, SoleDatabase.class, DB_NAME)
                // .fallbackToDestructiveMigration() //清空
                .addMigrations(MIGRATION_1_2) //升級
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                //  .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
                .build();
    }

    public void clearTable() {
        db.clearAllTables();
    }

    public void checkGuest(final DatabaseCallback<Integer> databaseCallback) {

//        Disposable d = db.soleDao().checkGuest()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(databaseCallback::onCount, throwable ->
//                        databaseCallback.onError(throwable.toString())
//                );

        db.soleDao().checkGuest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new DisposableMaybeObserver<Integer>() {
                    @Override
                    public void onSuccess(@NonNull Integer integer) {
                        databaseCallback.onCount(integer);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onNoData();
                    }
                });
        // compositeDisposable.add(d);
    }

    public void getUserProfiles(final DatabaseCallback<UserProfileEntity> databaseCallback) {
        Disposable d = db.soleDao()
                .getUserProfiles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(databaseCallback::onDataLoadedList);

        compositeDisposable.add(d);
    }

    public void getUserProfilesGuest(final DatabaseCallback<UserProfileEntity> databaseCallback) {
        Disposable d = db.soleDao()
                .getUserProfilesGuest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(databaseCallback::onDataLoadedList);

        compositeDisposable.add(d);
    }

    public void getSyncUserProfiles(final DatabaseCallback<UserProfileEntity> databaseCallback) {
        Disposable d = db.soleDao()
                .getSyncUserProfiles()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(databaseCallback::onDataLoadedList);

        compositeDisposable.add(d);
    }


    public void checkSyncAccount(String accountNo, final DatabaseCallback<Integer> callback) {
        Disposable d = db.soleDao().checkSyncAccount(accountNo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onCount);

        compositeDisposable.add(d);
    }

    private Disposable mDisposable;

    public void insertUserProfile(final UserProfileEntity userProfileEntities,
                                  final DatabaseCallback<UserProfileEntity> databaseCallback) {
        //這裡使用Completable是因為數據庫處理完後不發射數據，只處理onComplete和onError事件
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.soleDao().insertUserProfile(userProfileEntities)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //  compositeDisposable.add(d);
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());

                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void insertFavorite(final FavoritesEntity favoritesEntity,
                               final DatabaseCallback<FavoritesEntity> databaseCallback) {
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.soleDao().insertFavorite(favoritesEntity)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void deleteUserProfile(final int id,
                                  final DatabaseCallback<UserProfileEntity> databaseCallback) {
        /**
         * Completable
         * 適合用在執行的內容沒有回傳值，只要知道成功或失敗就好的時候，
         * 例如更新個人資料。在callback有onComplete()和onError(Throwable e)兩個方法，
         * 其中onComplete()是沒有參數的表示執行完不會有回傳值。
         */
        //建立我們要執行的內容。
        Completable
                .fromAction(() -> {
                    UserProfileEntity userProfileEntity = new UserProfileEntity();
                    userProfileEntity.setUid(id);
                    db.soleDao().deleteUserProfile(userProfileEntity);
                })
                .subscribeOn(Schedulers.io()) //表示這些內容要在哪個thread執行，Schedulers.io()是RxJava提供的background thread之一
                .observeOn(AndroidSchedulers.mainThread()) //表示執行後的callback要在哪個thread執行
                .subscribe(new CompletableObserver() { //真的開始執行  - DisposableCompletableObserver
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void updateUserProfile(final UserProfileEntity userProfileEntity,
                                  final DatabaseCallback<UserProfileEntity> callback) {
        Completable.fromAction(() -> {
            MyApplication.getInstance().setUserProfile(userProfileEntity);
            db.soleDao().updateUserProfile(userProfileEntity);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        callback.onUpdated();
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e.getMessage());
                    }
                });
    }

    public void getUserProfileById(long id,
                                   final DatabaseCallback<UserProfileEntity> callback) {
        Disposable d = db.soleDao().getUserProfileById(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedBean);

        compositeDisposable.add(d);
    }

    public void getFavoriteFromUserProfile(int id,
                                           final DatabaseCallback<UserProfileAndFavorites> callback) {
        Disposable d = db.soleDao()
                .getFavoriteForUserProfile(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedList);

        compositeDisposable.add(d);
    }

    public void deleteFavorite(final int favoriteParentUid, final int favoriteType,
                               final DatabaseCallback<FavoritesEntity> databaseCallback) {
        Completable
                .fromAction(() -> {
                    FavoritesEntity favoritesEntity = new FavoritesEntity();
                    favoritesEntity.setFavoriteParentUid(favoriteParentUid);
                    favoritesEntity.setFavoriteType(favoriteType);
                    db.soleDao().deleteFavorite(favoritesEntity);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //   compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }


    /**
     * Template
     */
    public void insertTemplate(final TemplateEntity templateEntity,
                               final DatabaseCallback<TemplateEntity> databaseCallback) {
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.soleDao().insertTemplate(templateEntity)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                          mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());
                        cancel();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }


    public void getTemplateFromUserProfile(int id,
                                           final DatabaseCallback<UserProfileAndTemplates> callback) {
        Disposable d = db.soleDao()
                .getTemplateForUserProfile(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedList);

        compositeDisposable.add(d);
    }

    public void deleteTemplate(final int templateParentUid, final String templateName,
                               final DatabaseCallback<TemplateEntity> databaseCallback) {
        Completable
                .fromAction(() -> {
                    TemplateEntity templateEntity = new TemplateEntity();
                    templateEntity.setTemplateParentUid(templateParentUid);
                    templateEntity.setTemplateName(templateName);
                    db.soleDao().deleteTemplate(templateEntity);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        // compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void checkTemplateName(String templateName, int uid,
                                  final DatabaseCallback<Integer> callback) {
        Disposable d = db.soleDao().checkTemplateName(templateName, uid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onCount);

        compositeDisposable.add(d);
    }

    public void checkTemplateSum(int uid, final DatabaseCallback<Integer> callback) {
        Disposable d = db.soleDao().checkTemplateSum(uid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onCount);

        compositeDisposable.add(d);
    }

    /**
     * History
     */
    public void insertHistory(final HistoryEntity historyEntity,
                              final DatabaseCallback<HistoryEntity> databaseCallback) {
        AtomicLong rowId = new AtomicLong();
        Completable
                .fromAction(() -> rowId.set(db.soleDao().insertHistory(historyEntity)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //  compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onAdded(rowId.get());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void getHistoryFromUserProfile(int id,
                                          final DatabaseCallback<UserProfileAndHistory> callback) {
        Disposable d = db.soleDao()
                .getHistoryForUserProfile(id)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedList);

        compositeDisposable.add(d);
    }

    public void getHistoryList(int historyParentUid,
                               final DatabaseCallback<HistoryEntity> callback) {
        Disposable d = db.soleDao()
                .getHistoryList(historyParentUid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedList);

        compositeDisposable.add(d);
    }

    public void deleteHistory(final int historyParentUid, final int historyId,
                              final DatabaseCallback<HistoryEntity> databaseCallback) {
        Completable
                .fromAction(() -> {
                    HistoryEntity historyEntity = new HistoryEntity();
                    historyEntity.setHistoryParentUid(historyParentUid);
                    historyEntity.setUid(historyId);
                    db.soleDao().deleteHistory(historyEntity);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //   compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        databaseCallback.onDeleted();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        databaseCallback.onError(e.getMessage());
                    }
                });
    }

    public void deleteMoreHistory(int parentUid) {
        db.soleDao().deleteMoreHistory(parentUid);
    }


    public void getNotUploadHistoryList(int historyParentUid,
                                        final DatabaseCallback<HistoryEntity> callback) {
        Disposable d = db.soleDao()
                .getNotUploadHistoryList(historyParentUid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onDataLoadedList);

        compositeDisposable.add(d);
    }

    public void updateHistory(final HistoryEntity historyEntity,
                              final DatabaseCallback<HistoryEntity> callback) {
        Completable.fromAction(() -> db.soleDao().updateHistory(historyEntity)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        //   compositeDisposable.add(d);
                    }

                    @Override
                    public void onComplete() {
                        callback.onUpdated();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        callback.onError(e.getMessage());
                    }
                });
    }

    public void checkUserName(String userName, final DatabaseCallback<Integer> callback) {
        Disposable d = db.soleDao().checkUserName(userName)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(callback::onCount);

        compositeDisposable.add(d);
    }

    public void close() {
        db.close();
    }

    public void roomClear() {
        Log.i("WEB_API_ROOM", "ROOM: " + compositeDisposable.size());
        compositeDisposable.clear();
        Log.i("WEB_API_ROOM", "ROOM: " + compositeDisposable.size());
    }


    public void cancel() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
          //  Log.d("UPDATEEEEE", "insertUserProfile insertUserProfile: ");
        }
    }


//    public <T> void addDisposable(Flowable<T> flowable, Consumer<T> consumer) {
//        compositeDisposable.add(flowable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(consumer));
//    }
//
//    public <T> void addDisposable(Completable completable, Action action) {
//        compositeDisposable.add(completable
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(action));
//    }
}
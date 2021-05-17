package com.dyaco.spiritbike.support.room;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.dyaco.spiritbike.support.room.entity.FavoritesEntity;
import com.dyaco.spiritbike.support.room.entity.HistoryEntity;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndFavorites;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndHistory;
import com.dyaco.spiritbike.support.room.entity.UserProfileAndTemplates;

import java.util.List;

import io.reactivex.Maybe;

@Dao
public interface SoleDao {

    @Query("select * from " + UserProfileEntity.USER_PROFILE + " where userType = 1")
    Maybe<List<UserProfileEntity>> getUserProfiles();

    @Query("select * from " + UserProfileEntity.USER_PROFILE + " where userType = 0")
    Maybe<List<UserProfileEntity>> getUserProfilesGuest();

    @Query("select * from " + UserProfileEntity.USER_PROFILE + " where userType = 1 and soleAccountNo not null")
    Maybe<List<UserProfileEntity>> getSyncUserProfiles();

    @Query("SELECT count(*) FROM " + UserProfileEntity.USER_PROFILE + " where soleAccountNo = :accountNo")
    Maybe<Integer> checkSyncAccount(String accountNo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertUserProfile(UserProfileEntity userProfileEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertFavorite(FavoritesEntity favoritesEntity);

    @Query("SELECT * FROM " + UserProfileEntity.USER_PROFILE + " where uid = :id")
    Maybe<UserProfileEntity> getUserProfileById(long id);

    @Update
    void updateUserProfile(UserProfileEntity userProfileEntity);

    @Delete
    void deleteUserProfile(UserProfileEntity userProfileEntity);

    @Transaction
    @Query("SELECT * FROM " + UserProfileEntity.USER_PROFILE + " WHERE uid=:id")
    Maybe<List<UserProfileAndFavorites>> getFavoriteForUserProfile(int id);

    @Delete
    void deleteFavorite(FavoritesEntity favoritesEntity);


    /**
     * Template
     */
    @Transaction
    @Query("SELECT * FROM " + UserProfileEntity.USER_PROFILE + " WHERE uid=:id")
    Maybe<List<UserProfileAndTemplates>> getTemplateForUserProfile(int id);

    @Delete
    void deleteTemplate(TemplateEntity templateEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTemplate(TemplateEntity templateEntity);

    /**
     * History
     */
    @Transaction
    @Query("SELECT * FROM " + UserProfileEntity.USER_PROFILE + " WHERE uid=:id ")
    Maybe<List<UserProfileAndHistory>> getHistoryForUserProfile(int id);

   // @Query("SELECT * FROM " + HistoryEntity.HISTORY + " WHERE historyParentUid=:historyParentUid  ORDER BY updateTime DESC LIMIT 10")
    @Query("SELECT * FROM " + HistoryEntity.HISTORY + " WHERE historyParentUid=:historyParentUid  ORDER BY updateTime DESC")
    Maybe<List<HistoryEntity>> getHistoryList(int historyParentUid);

    @Query("SELECT * FROM " + HistoryEntity.HISTORY + " WHERE historyParentUid=:historyParentUid and uploaded = 0")
    Maybe<List<HistoryEntity>> getNotUploadHistoryList(int historyParentUid);

    @Delete
    void deleteHistory(HistoryEntity historyEntity);

    @Update
    void updateHistory(HistoryEntity historyEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHistory(HistoryEntity historyEntity);


    @Query("SELECT count(*) FROM " + TemplateEntity.TEMPLATE + " where templateName = :templateName and templateParentUid = :uid")
    Maybe<Integer> checkTemplateName(String templateName, int uid);

    @Query("SELECT count(*) FROM " + TemplateEntity.TEMPLATE + " where templateParentUid = :uid")
    Maybe<Integer> checkTemplateSum(int uid);

    @Query("SELECT count(*) FROM " + UserProfileEntity.USER_PROFILE + " where userName = :userName")
    Maybe<Integer> checkUserName(String userName);

    @Query("SELECT uid FROM " + UserProfileEntity.USER_PROFILE + " where userType = 0")
    Maybe<Integer> checkGuest();

    @Query("delete from history where historyParentUid = :parentId and (select count(uid) from history where historyParentUid = :parentId) > 10 and uid in (select uid from history where historyParentUid = :parentId order by updateTime desc limit (select count(uid) from history where historyParentUid = :parentId) offset 10 )")
    void deleteMoreHistory(int parentId);

}

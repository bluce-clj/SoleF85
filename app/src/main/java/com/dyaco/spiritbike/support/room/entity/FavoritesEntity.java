package com.dyaco.spiritbike.support.room.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.dyaco.spiritbike.support.room.UserProfileEntity;

import static androidx.room.ForeignKey.CASCADE;

@Entity(tableName = FavoritesEntity.FAVORITES,
        primaryKeys = {"favoriteParentUid", "favoriteType"},
        indices = {@Index("favoriteParentUid")},
        foreignKeys = @ForeignKey(entity = UserProfileEntity.class,
        parentColumns = "uid",
        childColumns = "favoriteParentUid",
        onDelete = CASCADE))
public class FavoritesEntity {
    public static final String FAVORITES = "favorites";
    public String favoriteName;
    public int favoriteParentUid;
    public int favoriteType;

    public String getFavoriteName() {
        return favoriteName;
    }

    public void setFavoriteName(String favoriteName) {
        this.favoriteName = favoriteName;
    }

    public int getFavoriteParentUid() {
        return favoriteParentUid;
    }

    public void setFavoriteParentUid(int favoriteParentUid) {
        this.favoriteParentUid = favoriteParentUid;
    }

    public int getFavoriteType() {
        return favoriteType;
    }

    public void setFavoriteType(int favoriteType) {
        this.favoriteType = favoriteType;
    }
}

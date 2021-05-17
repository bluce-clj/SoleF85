package com.dyaco.spiritbike.support.room.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.dyaco.spiritbike.support.room.UserProfileEntity;

import java.util.List;

public class UserProfileAndFavorites {

    @Embedded
    public UserProfileEntity userProfileEntity;

    @Relation(parentColumn = "uid",
            entityColumn = "favoriteParentUid",
            entity = FavoritesEntity.class
    )
    public List<FavoritesEntity> favoritesEntityList;
}
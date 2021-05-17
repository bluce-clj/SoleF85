package com.dyaco.spiritbike.support.room.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.dyaco.spiritbike.support.room.UserProfileEntity;

import java.util.List;

public class UserProfileAndHistory {

    @Embedded
    public UserProfileEntity userProfileEntity;

    @Relation(parentColumn = "uid",
            entityColumn = "historyParentUid",
            entity = HistoryEntity.class
    )
    public List<HistoryEntity> historyEntityList;
}
package com.dyaco.spiritbike.support.room.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.dyaco.spiritbike.support.room.UserProfileEntity;

import java.util.List;

public class UserProfileAndTemplates {

    @Embedded
    public UserProfileEntity userProfileEntity;

    @Relation(parentColumn = "uid",
            entityColumn = "templateParentUid",
            entity = TemplateEntity.class
    )
    public List<TemplateEntity> templateEntityList;
}
package com.dyaco.spiritbike.support.room;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.dyaco.spiritbike.support.room.entity.FavoritesEntity;
import com.dyaco.spiritbike.support.room.entity.HistoryEntity;
import com.dyaco.spiritbike.support.room.entity.TemplateEntity;

@Database(entities = {UserProfileEntity.class,
        FavoritesEntity.class,
        TemplateEntity.class,
        HistoryEntity.class},
        version = 3)
@TypeConverters({Converters.class})
public abstract class SoleDatabase extends RoomDatabase {
    public abstract SoleDao soleDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE user_profile ADD COLUMN wattAccumulate REAL NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE user_profile ADD COLUMN wattFrequency INTEGER NOT NULL DEFAULT 0");
            database.execSQL("ALTER TABLE user_profile ADD COLUMN workoutMonth INTEGER NOT NULL DEFAULT 0");
        }
    };

    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            //當添加int 類型數據時，需要添加默認值
            database.execSQL("ALTER TABLE user_profile ADD COLUMN sleepMode INTEGER NOT NULL DEFAULT 0");
        }
    };

//    public static final Migration MIGRATION_2_3 = new Migration(2, 3) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            database.execSQL("ALTER TABLE expense ADD COLUMN itemName TEXT");
//            database.execSQL("ALTER TABLE expense ADD COLUMN itemNo TEXT");
//            database.execSQL("ALTER TABLE expense ADD COLUMN itemQuantity INTEGER NOT NULL DEFAULT 0");
//            //當添加int 類型數據時，需要添加默認值
//            database.execSQL("ALTER TABLE expense ADD COLUMN itemPrice INTEGER NOT NULL DEFAULT 0");
//        }
//    };
//
//    public static final Migration MIGRATION_3_4 = new Migration(3, 4) {
//        @Override
//        public void migrate(@NonNull SupportSQLiteDatabase database) {
//            //當添加int 類型數據時，需要添加默認值
//            database.execSQL("ALTER TABLE expense ADD COLUMN itemTotal INTEGER NOT NULL DEFAULT 0");
//        }
//    };
}

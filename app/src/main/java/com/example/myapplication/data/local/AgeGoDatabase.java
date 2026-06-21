package com.example.myapplication.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {CachedDashboardEntity.class, PendingMutationEntity.class}, version = 1, exportSchema = false)
public abstract class AgeGoDatabase extends RoomDatabase {
    public abstract OfflineDao offlineDao();

    private static volatile AgeGoDatabase INSTANCE;

    public static AgeGoDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AgeGoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AgeGoDatabase.class, "agego_offline.db").build();
                }
            }
        }
        return INSTANCE;
    }
}

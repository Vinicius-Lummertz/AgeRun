package com.example.myapplication.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_dashboards")
public class CachedDashboardEntity {
    @PrimaryKey @NonNull public String userId;
    @NonNull public String payload;
    public long updatedAt;

    public CachedDashboardEntity(@NonNull String userId, @NonNull String payload, long updatedAt) {
        this.userId = userId;
        this.payload = payload;
        this.updatedAt = updatedAt;
    }
}

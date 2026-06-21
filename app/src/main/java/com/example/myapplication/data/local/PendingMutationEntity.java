package com.example.myapplication.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pending_mutations")
public class PendingMutationEntity {
    @PrimaryKey @NonNull public String id;
    @NonNull public String userId;
    @NonNull public String type;
    @NonNull public String payload;
    public long createdAt;
    public int attempts;

    public PendingMutationEntity(@NonNull String id, @NonNull String userId, @NonNull String type,
                                 @NonNull String payload, long createdAt, int attempts) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.payload = payload;
        this.createdAt = createdAt;
        this.attempts = attempts;
    }
}

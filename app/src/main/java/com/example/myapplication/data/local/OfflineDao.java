package com.example.myapplication.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface OfflineDao {
    @Query("SELECT * FROM cached_dashboards WHERE userId = :userId LIMIT 1")
    CachedDashboardEntity dashboard(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveDashboard(CachedDashboardEntity dashboard);

    @Query("DELETE FROM cached_dashboards WHERE userId = :userId")
    void deleteDashboard(String userId);

    @Query("SELECT * FROM pending_mutations WHERE userId = :userId ORDER BY createdAt ASC")
    List<PendingMutationEntity> pending(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void enqueue(PendingMutationEntity mutation);

    @Query("DELETE FROM pending_mutations WHERE id = :id")
    void deleteMutation(String id);

    @Query("UPDATE pending_mutations SET attempts = attempts + 1 WHERE id = :id")
    void incrementAttempts(String id);

    @Query("DELETE FROM pending_mutations WHERE userId = :userId")
    void deleteMutations(String userId);
}

package com.example.petcare.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.petcare.data.entity.CareTask;
import java.util.List;

@Dao
public interface CareTaskDao {
    @Insert
    long insertTask(CareTask task);

    @Update
    void updateTask(CareTask task);

    @Delete
    void deleteTask(CareTask task);

    @Query("SELECT * FROM care_tasks WHERE taskId = :taskId LIMIT 1")
    CareTask getTaskById(int taskId);

    @Query("SELECT * FROM care_tasks WHERE petId = :petId ORDER BY time ASC")
    List<CareTask> getTasksForPet(int petId);

    @Query("SELECT care_tasks.* FROM care_tasks INNER JOIN pets ON care_tasks.petId = pets.petId WHERE pets.userId = :userId ORDER BY care_tasks.time ASC")
    List<CareTask> getTasksForUser(int userId);

    @Query("SELECT COUNT(*) FROM care_tasks WHERE petId = :petId AND completed = 0")
    int getPendingTaskCountForPet(int petId);
}
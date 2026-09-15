package com.example.petcare.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.petcare.data.entity.Location;
import java.util.List;

@Dao
public interface LocationDao {
    @Insert
    long insertLocation(Location location);

    @Update
    void updateLocation(Location location);

    @Delete
    void deleteLocation(Location location);

    @Query("SELECT * FROM locations WHERE locationId = :locationId LIMIT 1")
    Location getLocationById(int locationId);

    @Query("SELECT * FROM locations WHERE userId = :userId ORDER BY name ASC")
    List<Location> getLocationsForUser(int userId);
}
package com.example.petcare.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.petcare.data.entity.Pet;
import java.util.List;

@Dao
public interface PetDao {
    @Insert
    long insertPet(Pet pet);

    @Update
    void updatePet(Pet pet);

    @Delete
    void deletePet(Pet pet);

    @Query("SELECT * FROM pets WHERE petId = :petId LIMIT 1")
    Pet getPetById(int petId);

    @Query("SELECT * FROM pets WHERE userId = :userId ORDER BY name ASC")
    List<Pet> getPetsForUser(int userId);
}
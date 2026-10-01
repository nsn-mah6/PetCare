package com.example.petcare.data.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.petcare.data.dao.UserDao;
import com.example.petcare.data.dao.PetDao;
import com.example.petcare.data.dao.CareTaskDao;
import com.example.petcare.data.dao.LocationDao;
import com.example.petcare.data.entity.User;
import com.example.petcare.data.entity.Pet;
import com.example.petcare.data.entity.CareTask;
import com.example.petcare.data.entity.Location;

@Database(entities = {User.class, Pet.class, CareTask.class, Location.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static volatile AppDatabase instance;

    public abstract UserDao userDao();
    public abstract PetDao petDao();
    public abstract CareTaskDao careTaskDao();
    public abstract LocationDao locationDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "petcare_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
package com.example.petcare.data.repository;

import android.content.Context;
import com.example.petcare.data.database.AppDatabase;
import com.example.petcare.data.dao.UserDao;
import com.example.petcare.data.dao.PetDao;
import com.example.petcare.data.dao.CareTaskDao;
import com.example.petcare.data.dao.LocationDao;
import com.example.petcare.data.entity.User;
import com.example.petcare.data.entity.Pet;
import com.example.petcare.data.entity.CareTask;
import com.example.petcare.data.entity.Location;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PetCareRepository {
    private final UserDao userDao;
    private final PetDao petDao;
    private final CareTaskDao careTaskDao;
    private final LocationDao locationDao;
    private final ExecutorService executor;

    public interface Callback<T> {
        void onResult(T result);
    }

    public PetCareRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        userDao = db.userDao();
        petDao = db.petDao();
        careTaskDao = db.careTaskDao();
        locationDao = db.locationDao();
        executor = Executors.newFixedThreadPool(4);
    }

    // User operations
    public void insertUser(User user, Callback<Long> callback) {
        executor.execute(() -> {
            long id = userDao.insertUser(user);
            if (callback != null) callback.onResult(id);
        });
    }

    public void getUserByEmail(String email, Callback<User> callback) {
        executor.execute(() -> {
            User user = userDao.getUserByEmail(email);
            if (callback != null) callback.onResult(user);
        });
    }

    public void login(String email, String password, Callback<User> callback) {
        executor.execute(() -> {
            User user = userDao.login(email, password);
            if (callback != null) callback.onResult(user);
        });
    }

    public void saveUserToFirebaseDatabase(String firebaseUid, String fullName, String email) {
        executor.execute(() -> {
            try {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(firebaseUid);
                Map<String, Object> userData = new HashMap<>();
                userData.put("uid", firebaseUid);
                userData.put("fullName", fullName);
                userData.put("email", email);
                ref.setValue(userData);
            } catch (Exception ignored) {}
        });
    }

    public void syncFirebaseUser(String firebaseUid, String fullName, String email, Callback<User> callback) {
        executor.execute(() -> {
            User user = userDao.getUserByFirebaseUid(firebaseUid);
            if (user == null) {
                user = userDao.getUserByEmail(email);
            }

            if (user != null) {
                user.setFirebaseUid(firebaseUid);
                if (fullName != null && !fullName.isEmpty()) {
                    user.setFullName(fullName);
                }
                userDao.updateUser(user);
            } else {
                user = new User(firebaseUid, (fullName != null && !fullName.isEmpty()) ? fullName : email.split("@")[0], email, "");
                long id = userDao.insertUser(user);
                user.setUserId((int) id);
            }

            if (callback != null) callback.onResult(user);
        });
    }

    // Pet operations
    public void insertPet(Pet pet, Callback<Long> callback) {
        executor.execute(() -> {
            long id = petDao.insertPet(pet);
            if (callback != null) callback.onResult(id);
        });
    }

    public void updatePet(Pet pet, Runnable callback) {
        executor.execute(() -> {
            petDao.updatePet(pet);
            if (callback != null) callback.run();
        });
    }

    public void deletePet(Pet pet, Runnable callback) {
        executor.execute(() -> {
            petDao.deletePet(pet);
            if (callback != null) callback.run();
        });
    }

    public void getPetById(int petId, Callback<Pet> callback) {
        executor.execute(() -> {
            Pet pet = petDao.getPetById(petId);
            if (callback != null) callback.onResult(pet);
        });
    }

    public void getPetsForUser(int userId, Callback<List<Pet>> callback) {
        executor.execute(() -> {
            List<Pet> pets = petDao.getPetsForUser(userId);
            if (callback != null) callback.onResult(pets);
        });
    }

    // CareTask operations
    public void insertTask(CareTask task, Callback<Long> callback) {
        executor.execute(() -> {
            long id = careTaskDao.insertTask(task);
            if (callback != null) callback.onResult(id);
        });
    }

    public void updateTask(CareTask task, Runnable callback) {
        executor.execute(() -> {
            careTaskDao.updateTask(task);
            if (callback != null) callback.run();
        });
    }

    public void deleteTask(CareTask task, Runnable callback) {
        executor.execute(() -> {
            careTaskDao.deleteTask(task);
            if (callback != null) callback.run();
        });
    }

    public void getTaskById(int taskId, Callback<CareTask> callback) {
        executor.execute(() -> {
            CareTask task = careTaskDao.getTaskById(taskId);
            if (callback != null) callback.onResult(task);
        });
    }

    public void getTasksForPet(int petId, Callback<List<CareTask>> callback) {
        executor.execute(() -> {
            List<CareTask> tasks = careTaskDao.getTasksForPet(petId);
            if (callback != null) callback.onResult(tasks);
        });
    }

    public void getTasksForUser(int userId, Callback<List<CareTask>> callback) {
        executor.execute(() -> {
            List<CareTask> tasks = careTaskDao.getTasksForUser(userId);
            if (callback != null) callback.onResult(tasks);
        });
    }

    public void getPendingTaskCountForPet(int petId, Callback<Integer> callback) {
        executor.execute(() -> {
            int count = careTaskDao.getPendingTaskCountForPet(petId);
            if (callback != null) callback.onResult(count);
        });
    }

    // Location operations
    public void insertLocation(Location location, Callback<Long> callback) {
        executor.execute(() -> {
            long id = locationDao.insertLocation(location);
            if (callback != null) callback.onResult(id);
        });
    }

    public void updateLocation(Location location, Runnable callback) {
        executor.execute(() -> {
            locationDao.updateLocation(location);
            if (callback != null) callback.run();
        });
    }

    public void deleteLocation(Location location, Runnable callback) {
        executor.execute(() -> {
            locationDao.deleteLocation(location);
            if (callback != null) callback.run();
        });
    }

    public void getLocationById(int locationId, Callback<Location> callback) {
        executor.execute(() -> {
            Location location = locationDao.getLocationById(locationId);
            if (callback != null) callback.onResult(location);
        });
    }

    public void getLocationsForUser(int userId, Callback<List<Location>> callback) {
        executor.execute(() -> {
            List<Location> locations = locationDao.getLocationsForUser(userId);
            if (callback != null) callback.onResult(locations);
        });
    }
}
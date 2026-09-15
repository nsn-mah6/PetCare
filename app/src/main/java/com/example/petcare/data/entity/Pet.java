package com.example.petcare.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pets")
public class Pet {
    @PrimaryKey(autoGenerate = true)
    private int petId;
    private int userId;
    private String name;
    private String species;
    private String breed;
    private int age;
    private double weight;
    private String dietaryPreferences;
    private String allergies;
    private String favouriteToys;
    private String vaccinationHistory;
    private String imageUri;

    public Pet(int userId, String name, String species, String breed, int age, double weight,
               String dietaryPreferences, String allergies, String favouriteToys,
               String vaccinationHistory, String imageUri) {
        this.userId = userId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.age = age;
        this.weight = weight;
        this.dietaryPreferences = dietaryPreferences;
        this.allergies = allergies;
        this.favouriteToys = favouriteToys;
        this.vaccinationHistory = vaccinationHistory;
        this.imageUri = imageUri;
    }

    // Getters and Setters
    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
    public String getDietaryPreferences() { return dietaryPreferences; }
    public void setDietaryPreferences(String dietaryPreferences) { this.dietaryPreferences = dietaryPreferences; }
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    public String getFavouriteToys() { return favouriteToys; }
    public void setFavouriteToys(String favouriteToys) { this.favouriteToys = favouriteToys; }
    public String getVaccinationHistory() { return vaccinationHistory; }
    public void setVaccinationHistory(String vaccinationHistory) { this.vaccinationHistory = vaccinationHistory; }
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
}
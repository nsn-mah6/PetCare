package com.example.petcare.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "care_tasks")
public class CareTask {
    @PrimaryKey(autoGenerate = true)
    private int taskId;
    private int petId;
    private String taskName;
    private String category;
    private String frequency;
    private String time;
    private String requiredSupplies;
    private String instructions;
    private String notes;
    private String imageUri;
    private boolean completed;
    private long createdDate;

    public CareTask(int petId, String taskName, String category, String frequency, String time,
                    String requiredSupplies, String instructions, String notes, String imageUri,
                    boolean completed, long createdDate) {
        this.petId = petId;
        this.taskName = taskName;
        this.category = category;
        this.frequency = frequency;
        this.time = time;
        this.requiredSupplies = requiredSupplies;
        this.instructions = instructions;
        this.notes = notes;
        this.imageUri = imageUri;
        this.completed = completed;
        this.createdDate = createdDate;
    }

    // Getters and Setters
    public int getTaskId() { return taskId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public String getRequiredSupplies() { return requiredSupplies; }
    public void setRequiredSupplies(String requiredSupplies) { this.requiredSupplies = requiredSupplies; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getImageUri() { return imageUri; }
    public void setImageUri(String imageUri) { this.imageUri = imageUri; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public long getCreatedDate() { return createdDate; }
    public void setCreatedDate(long createdDate) { this.createdDate = createdDate; }
}
package com.example.petcare.data.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "locations")
public class Location {
    @PrimaryKey(autoGenerate = true)
    private int locationId;
    private int userId;
    private String name;
    private String type;
    private String address;
    private double latitude;
    private double longitude;

    public Location(int userId, String name, String type, String address, double latitude, double longitude) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and Setters
    public int getLocationId() { return locationId; }
    public void setLocationId(int locationId) { this.locationId = locationId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
}
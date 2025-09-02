package com.travel.journal.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class LocationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    //Geo
    @Column(nullable = false)
    private double latitude;
    @Column(nullable = false)
    private double longitude;

    @Column(nullable = true, name="place_name", unique = true)
    private String placeName; // The Old Well
    private String city; // Chapel Hill
    private String region; // North Carolina
    private String country; // France


    @ManyToMany(mappedBy = "locations")
    private List<TravelMemoEntity> memos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<TravelMemoEntity> getMemos() {
        return memos;
    }

    public void setMemos(List<TravelMemoEntity> memos) {
        this.memos = memos;
    }
}

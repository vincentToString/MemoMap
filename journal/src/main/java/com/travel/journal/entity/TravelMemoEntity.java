package com.travel.journal.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
public class TravelMemoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Memo
    private String imageurl;

    @Column(length=100)
    private String title;
    @Column(columnDefinition="TEXT")
    private String content;

    //Geo
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="memo_locations",
            joinColumns = @JoinColumn(name="memo_id"),
            inverseJoinColumns = @JoinColumn(name="location_id")
    )
    private Set<LocationEntity> locations;

    // weather
    private String historicalWeather;

    // review
    private Double rating;
    private String moodIcon;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name="memo_tags",
            joinColumns = @JoinColumn(name="memo_id"),
            inverseJoinColumns = @JoinColumn(name="tag_id")
    )
    private Set<TagEntity> tags;

    //time
    private LocalDateTime date;

    // metadata
    @CreationTimestamp
    private LocalDateTime createdAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private UserEntity user;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Set<LocationEntity> getLocations() {
        return locations;
    }

    public void setLocations(Set<LocationEntity> locations) {
        this.locations = locations;
    }


    public String getHistoricalWeather() {
        return historicalWeather;
    }

    public void setHistoricalWeather(String historicalWeather) {
        this.historicalWeather = historicalWeather;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getMoodIcon() {
        return moodIcon;
    }

    public void setMoodIcon(String moodIcon) {
        this.moodIcon = moodIcon;
    }

    public Set<TagEntity> getTags() {
        return tags;
    }

    public void setTags(Set<TagEntity> tags) {
        this.tags = tags;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
}

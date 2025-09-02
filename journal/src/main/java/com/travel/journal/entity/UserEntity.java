package com.travel.journal.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name ="name", nullable = false)
    private String displayName;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="joined_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime joinedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<TravelMemoEntity> travelMemos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public List<TravelMemoEntity> getTravelMemos() {
        return travelMemos;
    }

    public void setTravelMemos(List<TravelMemoEntity> travelMemos) {
        this.travelMemos = travelMemos;
    }
}

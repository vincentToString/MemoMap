package com.travel.journal.dto;


import com.travel.journal.entity.UserEntity;

import java.time.LocalDateTime;

public class UserDto {
    private int id;
    private String displayName;
    private String email;
    private LocalDateTime joinedAt;

    public UserDto(UserEntity userEntity){
        this.id = userEntity.getId();
        this.displayName = userEntity.getDisplayName();
        this.email = userEntity.getEmail();
        this.joinedAt = userEntity.getJoinedAt();
    }

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
}


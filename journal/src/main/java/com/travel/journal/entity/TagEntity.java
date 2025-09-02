package com.travel.journal.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class TagEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(unique=true)
    private String tag;

    private String description;

    @ManyToMany(mappedBy="tags")
    private List<TravelMemoEntity> memos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        description = description;
    }

    public List<TravelMemoEntity> getMemos() {
        return memos;
    }

    public void setMemos(List<TravelMemoEntity> memos) {
        this.memos = memos;
    }
}



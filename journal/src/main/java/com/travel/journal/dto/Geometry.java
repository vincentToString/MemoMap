package com.travel.journal.dto;

import java.util.List;

public class Geometry {
    private List<Double> coordinates; // [lon, lat]

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }
}

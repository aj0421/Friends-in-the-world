package com.example.friends_in_the_world.Classes;

import com.google.android.gms.maps.model.LatLng;

public class Member {
    private String name;
    private LatLng coordinates;

    public Member(String name) {
        this(name, null);
    }

    public Member(String name, LatLng coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }
}
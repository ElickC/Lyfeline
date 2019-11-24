package com.example.lyfeline;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;



public class EmtLocation {

    private GeoPoint geo_point;
    private @ServerTimestamp Timestamp timestamp;
    private EmtUser emtUser;
    private String user_id;

    public EmtLocation(GeoPoint geo_point, Timestamp timestamp, EmtUser emtUser, String user_id) {
        this.geo_point = geo_point;
        this.timestamp = timestamp;
        this.emtUser = emtUser;
        this.user_id = user_id;
    }

    public EmtLocation() {

    }

    public GeoPoint getGeo_point() {
        return geo_point;
    }

    public void setGeo_point(GeoPoint geo_point) {
        this.geo_point = geo_point;
    }

    public @ServerTimestamp Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public EmtUser getEmtUser(){
        return emtUser;
    }

    public String getUser_id() { return user_id; }

    public void setUser_id(String user_id) { this.user_id = user_id; }

    public void setEmtUser(EmtUser emtUser){
        this.emtUser = emtUser;
    }

    @Override
    public String toString() {
        return "EmtLocation{" +
                "geo_point=" + geo_point +
                ", timestamp=" + timestamp +
                ", user_id=" + user_id +
                ", emtUser=" + emtUser +
                '}';
    }
}

package com.example.lyfeline;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

public class VicLocation {
    private GeoPoint geo_point;
    private @ServerTimestamp
    Timestamp timestamp;
    private VictimUser vicUser;

    public VicLocation(GeoPoint geo_point, Timestamp timestamp, VictimUser vicUser) {
        this.geo_point = geo_point;
        this.timestamp = timestamp;
        this.vicUser = vicUser;
    }

    public VicLocation() {

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

    public VictimUser getVictimUser(){
        return vicUser;
    }

    public void setVictimUser(VictimUser vicUser){
        this.vicUser = vicUser;
    }


    @Override
    public String toString() {
        return "EmtLocation{" +
                "geo_point=" + geo_point +
                ", timestamp=" + timestamp +
                ", vicUser=" + vicUser +
                '}';
    }
}

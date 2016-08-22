package com.fabian.mylocationsideaware.Utils;

import com.google.android.gms.ads.internal.request.StringParcel;
import com.orm.SugarRecord;

/**
 * Created by fabian on 8/21/2016.
 */
public class PlaceDB extends SugarRecord {
    String name;
    String city;
    double lat;
    double longitud;
    String imgpath;

    public String getImgpath() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath = imgpath;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNamePlace() {
        return name;
    }

    public void setNamePlace(String name) {
        this.name = name;
    }

    public PlaceDB(){}

    public PlaceDB(String name, String city, double lat, double longitud, String imgpath){
        this.name = name;
        this.city = city;
        this.lat = lat;
        this.longitud = longitud;
        this.imgpath = imgpath;
    }
}

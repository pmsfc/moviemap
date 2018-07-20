/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.service;

/**
 *
 * @author AW17
 */
class LatLng {

    private String Latitude;
    private String longitude;

    public LatLng(String Latitude, String longitude) {
        this.Latitude = Latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String Latitude) {
        this.Latitude = Latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.utils;

/**
 *
 * @author PedroCaldeira
 */
public enum MovieGetterUtils {
    INSTANCE;

    private boolean movieGetterRuning;
    private boolean locationGetterRuning;

    public boolean isLocationGetterRuning() {
        return locationGetterRuning;
    }

    public void setLocationGetterRuning(boolean locationGetterRuning) {
        this.locationGetterRuning = locationGetterRuning;
    }

    public boolean isMovieGetterRuning() {
        return movieGetterRuning;
    }

    public void setMovieGetterRuning(boolean movieGetterRuning) {
        this.movieGetterRuning = movieGetterRuning;
    }

}

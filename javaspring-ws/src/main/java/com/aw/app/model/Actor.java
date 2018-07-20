/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Actor implements Serializable {
    
    @NotNull
    @Size(min = 1, max = 25)
    private String name;
    private long id;
    private String bio;
    private String birthday;
    private URI homepage;
    private String imdbID;
    private String popularity;
    private List<String> movie;

    public Actor(String name, long id, String bio, String birthday, 
            URI homepage, String imdbID, String popularity) {
        this.name = name;
        this.id = id;
        this.bio = bio;
        this.birthday = birthday;
        this.homepage = homepage;
        this.imdbID = imdbID;
        this.popularity = popularity;
    }

    public List<String> getMovie() {
        return movie;
    }

    public void setMovie(List<String> movie) {
        this.movie = movie;
    }
    
    
    
    public Actor(){
        
    }

 
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }


    public URI getHomepage() {
        return homepage;
    }

    public void setHomepage(URI homepage) {
        this.homepage = homepage;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }
    
    
    
    


}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.model;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Crew implements Serializable {
    
    @NotNull
    @Size(min = 1, max = 25)
    private String name;
    private long id;
    private String job;
    private List<String> movie;

    public Crew(String name, long id, String job) {
        this.name = name;
        this.id = id;
        this.job = job;
    }
    
    public Crew(){
        
    }

    public List<String> getMovie() {
        return movie;
    }

    public void setMovie(List<String> movie) {
        this.movie = movie;
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

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
    
    
    
    
}

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
public class Keyword implements Serializable {
    
    @NotNull
    @Size(min = 1, max = 25)
    private String word;
    private long id;
    private List<String> movie;

    public Keyword(String word, long id) {
        this.word = word;
        this.id = id;
    }

    public Keyword() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getMovie() {
        return movie;
    }

    public void setMovie(List<String> movie) {
        this.movie = movie;
    }

 
    
    
    
}

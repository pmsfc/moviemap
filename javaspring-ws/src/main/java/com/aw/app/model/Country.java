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
public class Country implements Serializable {

    @NotNull
    @Size(min = 1, max = 25)
    private String name;
    private String isoCode;
    private List<String> movie;

    public Country(String name, String isoCode) {
        this.name = name;
        this.isoCode = isoCode;

    }

    public Country() {

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

    public String getIsoCode() {
        return isoCode;
    }

    public void setIsoCode(String isoCode) {
        this.isoCode = isoCode;
    }

}

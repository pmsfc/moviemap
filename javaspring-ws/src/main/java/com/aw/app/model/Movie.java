package com.aw.app.model;

import java.io.Serializable;
import java.net.URI;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Simple POJO of our domain
 *
 */
@XmlRootElement
public class Movie implements Serializable {

    @NotNull
    @Size(min = 1, max = 25)
    private String name;
    private long id;
    private String overview;
    private URI page;
    private String picture;
    private List<String> actors;
    private String date;
    private List<String> category;
    private long duration;
    private List<String> crew;
    private List<String> keyword;
    private String rating;
    private List<String> country;
    private List<String> location;
    private String popularity;
    private String imdbID;

    public Movie(String name, long id, String overview, URI page,
            String picture, List<String> actors, String date,
            List<String> category, long duration, List<String> crew,
            List<String> keyword, String rating, List<String> country,
            String popularity, String imdbID, List<String> location) {
        this.name = name;
        this.id = id;
        this.overview = overview;
        this.page = page;
        this.picture = picture;
        this.actors = actors;
        this.date = date;
        this.category = category;
        this.duration = duration;
        this.crew = crew;
        this.keyword = keyword;
        this.rating = rating;
        this.country = country;
        this.popularity = popularity;
        this.imdbID = imdbID;
        this.location = location;
    }

    public Movie() {
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
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

    public String getPicture() {
        return picture;
    }

    public void setPictures(String picture) {
        this.picture = picture;
    }

    public URI getPage() {
        return page;
    }

    public void setPage(URI page) {
        this.page = page;
    }

    public List<String> getActors() {
        return actors;
    }

    public void setActor(List<String> actors) {
        this.actors = actors;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public List<String> getCrew() {
        return crew;
    }

    public void setCrew(List<String> crew) {
        this.crew = crew;
    }

    public List<String> getKeyword() {
        return keyword;
    }

    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<String> getCountry() {
        return country;
    }

    public void setCountry(List<String> country) {
        this.country = country;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getImdbID() {
        return imdbID;
    }

    public void setImdbID(String imdbID) {
        this.imdbID = imdbID;
    }

}

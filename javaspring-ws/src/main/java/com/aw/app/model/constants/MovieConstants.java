package com.aw.app.model.constants;

import java.net.URI;
import java.net.URISyntaxException;

public enum MovieConstants {

    ID("http://www.aw17rdf.com/movie/id"),
    BASE("http://www.aw17rdf.com/movie/"),
    NAME("http://www.aw17rdf.com/movie/name"),
    DATE("http://www.aw17rdf.com/movie/date"),
    DURATION("http://www.aw17rdf.com/movie/duration"),
    OVERVIEW("http://www.aw17rdf.com/movie/overview"),
    CATEGORY("http://xmlns.com/foaf/0.1/category"),
    PAGE("http://www.aw17rdf.com/movie/page"),
    ACTOR("http://xmlns.com/foaf/0.1/actor"),
    POSTER("http://www.aw17rdf.com/movie/poster"),
    CREW("http://xmlns.com/foaf/0.1/crew"),
    KEYWORD("http://xmlns.com/foaf/0.1/keyword"),
    RATING("http://www.aw17rdf.com/movie/rating"),
    COUNTRY("http://xmlns.com/foaf/0.1/country"),
    POPULARITY("http://www.aw17rdf.com/movie/popularity"),
    LOCATION("http://www.aw17rdf.com/movie/location"),
    IMDBID("http://www.aw17rdf.com/movie/imdbid");

    private String value;

    MovieConstants(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }

    public URI asURI() {
        URI uri = null;
        try {
            uri = new URI(value);
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return uri;
    }

}

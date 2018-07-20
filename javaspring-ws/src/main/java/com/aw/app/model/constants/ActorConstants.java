package com.aw.app.model.constants;

import java.net.URI;
import java.net.URISyntaxException;

public enum ActorConstants {

    BASE("http://www.aw17rdf.com/actor/"),
    NAME("http://www.aw17rdf.com/actor/name"),
    ID("http://www.aw17rdf.com/actor/id"),
    BIO("http://www.aw17rdf.com/actor/bio"),
    BIRTHDAY("http://www.aw17rdf.com/actor/birthday"),
    HOMEPAGE("http://www.aw17rdf.com/actor/homepage"),
    IMDBID("http://www.aw17rdf.com/actor/imdbid"),
    POPULARITY("http://www.aw17rdf.com/actor/popularity"),
    MOVIE("http://xmlns.com/foaf/0.1/movie");

    private String value;

    ActorConstants(String value) {
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

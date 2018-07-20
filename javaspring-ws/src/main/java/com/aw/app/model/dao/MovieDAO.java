package com.aw.app.model.dao;

import com.aw.app.model.Movie;
import com.aw.app.model.constants.MovieConstants;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Remover;
import com.complexible.stardog.ext.spring.GetterCallback;
import com.complexible.stardog.ext.spring.RemoverCallback;
import com.complexible.stardog.ext.spring.RowMapper;
import com.complexible.stardog.ext.spring.SnarlTemplate;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MovieDAO {

    private static final Logger logger = LoggerFactory.getLogger(MovieDAO.class);

    @Autowired
    private SnarlTemplate snarlTemplate;

    public List<Movie> list(int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?date ?poster ?duration ?overview ?page ?rating ?popularity ?imdbid"
                + "(GROUP_CONCAT( DISTINCT ?category;separator=\",\") as ?categories) "
                + "(GROUP_CONCAT( DISTINCT ?country;separator=\",\") as ?countrys) "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.CATEGORY.toString() + "> ?category  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?country } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid}} "
                + "group by ?name ?id ?poster ?date ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "ORDER BY ASC(?name)"
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println("Query:");
        System.out.println(query);

        List<Movie> results = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                try {

                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String categories = bindingSet.getValue("categories").stringValue();
                    List<String> cats = Arrays.asList(categories.split("\\s*,\\s*"));
                    d.setCategory(cats);

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results;
    }

    public List<Movie> getByCategory(List<String> categories, int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?date ?poster ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "(GROUP_CONCAT( DISTINCT ?country;separator=\",\") as ?countrys) "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.CATEGORY.toString() + "> ?category  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?country } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid} . "
                + "	  FILTER (?category in " + convertToSPARQLList(categories) + ")} "
                + "group by ?name ?id ?poster ?date ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "ORDER BY ASC(?name) "
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println("Query getByCategory:");
        System.out.println(query);

        List<Movie> results = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                try {

                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String countrys = bindingSet.getValue("countrys").stringValue();
                    List<String> countrs = Arrays.asList(countrys.split("\\s*,\\s*"));
                    d.setCountry(countrs);

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results;
    }

    public List<Movie> getRegexByCategory(String name, List<String> categories, int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?date ?poster ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "(GROUP_CONCAT( DISTINCT ?country;separator=\",\") as ?countrys) "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.CATEGORY.toString() + "> ?category  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?country } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid} . "
                + "       FILTER regex(?name, \"" + name + "\", \"i\"). "
                + "	  FILTER (?category in " + convertToSPARQLList(categories) + ")} "
                + "group by ?name ?id ?poster ?date ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "ORDER BY ASC(?name) "
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println("Query getByCategory:");
        System.out.println(query);

        List<Movie> results = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                try {

                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String countrys = bindingSet.getValue("countrys").stringValue();
                    List<String> countrs = Arrays.asList(countrys.split("\\s*,\\s*"));
                    d.setCountry(countrs);

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results;
    }

    public List<Movie> getActorRegexAndCategory(String name, List<String> categories, int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?date ?poster ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "(GROUP_CONCAT( DISTINCT ?country;separator=\",\") as ?countrys) "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.CATEGORY.toString() + "> ?category  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.ACTOR.toString() + "> ?actorName  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?country } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid} . "
                + "       FILTER regex(?actorName, \"" + name + "\", \"i\"). "
                + "	  FILTER (?category in " + convertToSPARQLList(categories) + ")} "
                + "group by ?name ?id ?poster ?date ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "ORDER BY ASC(?name) "
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println("Query getByCategory:");
        System.out.println(query);

        List<Movie> results = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                try {

                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String countrys = bindingSet.getValue("countrys").stringValue();
                    List<String> countrs = Arrays.asList(countrys.split("\\s*,\\s*"));
                    d.setCountry(countrs);

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results;
    }

    public List<Movie> getCountryRegexAndCategory(String name, List<String> categories, int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?date ?poster ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "(GROUP_CONCAT( DISTINCT ?country;separator=\",\") as ?countrys) "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.CATEGORY.toString() + "> ?category  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.ACTOR.toString() + "> ?actorName  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?countryName } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid} . "
                + "       FILTER regex(?countryName, \"" + name + "\", \"i\"). "
                + "	  FILTER (?category in " + convertToSPARQLList(categories) + ")} "
                + "group by ?name ?id ?poster ?date ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "ORDER BY ASC(?name) "
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println("Query getByCategory:");
        System.out.println(query);

        List<Movie> results = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                try {

                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String countrys = bindingSet.getValue("countrys").stringValue();
                    List<String> countrs = Arrays.asList(countrys.split("\\s*,\\s*"));
                    d.setCountry(countrs);

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results;
    }

    String convertToSPARQLList(List<String> list) {
        StringBuffer sb = new StringBuffer();
        sb.append("(");
        for (String item : list) {
            sb.append("'" + item + "'");
            sb.append(", ");
        }
        sb.setLength(sb.length() - 2); // remove last comma and whitespace
        sb.append(")");
        return sb.toString();

    }

    public List<Movie> getByKeyword(String keyword, int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?date ?poster ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "(GROUP_CONCAT( DISTINCT ?country;separator=\",\") as ?countrys) "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.KEYWORD.toString() + "> ?keyword  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?country } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid} . "
                + "	  FILTER (?keyword = \"" + keyword + "\")}  "
                + "group by ?name ?id ?poster ?date ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "ORDER BY ASC(?name)"
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println("Query getByCategory:");
        System.out.println(query);

        List<Movie> results = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                try {

                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String countrys = bindingSet.getValue("countrys").stringValue();
                    List<String> countrs = Arrays.asList(countrys.split("\\s*,\\s*"));
                    d.setCountry(countrs);

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results;
    }

    public List<Movie> getByActor(String actorName, int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?date ?poster ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "(GROUP_CONCAT( DISTINCT ?country;separator=\",\") as ?countrys) "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.ACTOR.toString() + "> ?actorName  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?country } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid} . "
                + "       FILTER regex(?actorName,\"" + actorName + "\",\"i\")}  "
                + "group by ?name ?id ?poster ?date ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "ORDER BY ASC(?name)"
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println("Query getByCategory:");
        System.out.println(query);

        List<Movie> results = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                try {

                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String countrys = bindingSet.getValue("countrys").stringValue();
                    List<String> countrs = Arrays.asList(countrys.split("\\s*,\\s*"));
                    d.setCountry(countrs);

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results;
    }

    public List<Movie> getByCrew(String crewName, int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?date ?poster ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "(GROUP_CONCAT( DISTINCT ?country;separator=\",\") as ?countrys) "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.CREW.toString() + "> ?crewName  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?country } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid} . "
                + "       FILTER regex(?crewName,\"" + crewName + "\",\"i\")}  "
                + "group by ?name ?id ?poster ?date ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "ORDER BY ASC(?name)"
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println("Query getByCategory:");
        System.out.println(query);

        List<Movie> results = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                try {

                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String countrys = bindingSet.getValue("countrys").stringValue();
                    List<String> countrs = Arrays.asList(countrys.split("\\s*,\\s*"));
                    d.setCountry(countrs);

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results;
    }

    public List<Movie> getByCountry(String countryName, int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?date ?poster ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?countryName  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid} . "
                + "       FILTER regex(?countryName,\"" + countryName + "\",\"i\")}  "
                + "group by ?name ?id ?poster ?date ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "ORDER BY ASC(?name)"
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println("Query getByCategory:");
        System.out.println(query);

        List<Movie> results = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                try {

                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results;
    }

    public List<Movie> getByName(final String name, int limit, int offset) {
        String query = "SELECT DISTINCT ?name ?id ?date ?duration ?poster ?overview ?page ?rating ?popularity ?imdbid "
                + "(GROUP_CONCAT( DISTINCT ?category;separator=\",\") as ?categories) "
                + "(GROUP_CONCAT( DISTINCT ?country;separator=\",\") as ?countrys) "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + " FILTER regex(?name, \"" + name + "\", \"i\")."
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.CATEGORY.toString() + "> ?category  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating .} "
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?country } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid}}"
                + "group by ?name ?id ?date ?poster ?duration ?overview ?page ?rating ?popularity ?imdbid "
                + "ORDER BY ASC(?name) "
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println("Query:");
        System.out.println(query);
        List<Movie> results = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                try {
                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }

                    //System.out.println(bindingSet.getValue("posters").stringValue());
                    Movie d = new Movie();
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String categories = bindingSet.getValue("categories").stringValue();
                    List<String> cats = Arrays.asList(categories.split("\\s*,\\s*"));
                    d.setCategory(cats);

                    String countrys = bindingSet.getValue("countrys").stringValue();
                    List<String> countrs = Arrays.asList(countrys.split("\\s*,\\s*"));
                    d.setCountry(countrs);

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results;

    }

    public Movie getByID(final Integer id) {
        String query = "SELECT DISTINCT ?name ?id ?date ?duration  ?overview ?poster ?page ?rating ?popularity ?imdbid "
                + "(GROUP_CONCAT( DISTINCT ?category;separator=\",\") as ?categories) "
                + "(GROUP_CONCAT( DISTINCT ?country;separator=\",\") as ?countrys) "
                + "(GROUP_CONCAT( DISTINCT ?location;separator=\"_\") as ?locations) "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + " FILTER (?id = \"" + id + "\"). "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.CATEGORY.toString() + "> ?category  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating .} "
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?country } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid}}"
                + "group by ?name ?id ?date ?duration ?overview ?page ?poster ?rating ?popularity ?imdbid";

        System.out.println("Query:");
        System.out.println(query);
        List<Movie> movies = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                if (!bindingSet.hasBinding("name")) {
                    System.out.println("returning null movie");
                    return null;
                }

                try {
                    //System.out.println(bindingSet.getValue("posters").stringValue());
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    String categories = bindingSet.getValue("categories").stringValue();
                    List<String> cats = Arrays.asList(categories.split("\\s*,\\s*"));
                    d.setCategory(cats);

                    String countrys = bindingSet.getValue("countrys").stringValue();
                    List<String> countrs = Arrays.asList(countrys.split("\\s*,\\s*"));
                    d.setCountry(countrs);

                    String locations = bindingSet.getValue("locations").stringValue();
                    List<String> locals = Arrays.asList(locations.split("\\s*_\\s*"));
                    d.setLocation(locals);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        if (movies.get(0) == null) {
            return null;
        }

        Movie ret = movies.get(0);

        String query2 = "SELECT DISTINCT ?actor "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + "       FILTER (?id = \"" + id + "\"). "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.ACTOR.toString() + "> ?actor  } }   "
                + "group by ?actor order by ?actor";
        System.out.println(query2);
        List<String> actors = snarlTemplate.query(query2, new RowMapper<String>() {
            public String mapRow(BindingSet bindingSet) {
                if (!bindingSet.hasBinding("actor")) {
                    return "empty";
                } else {
                    return bindingSet.getValue("actor").stringValue();
                }
            }
        });

        String query4 = "SELECT DISTINCT ?keys "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name .  "
                + "       FILTER (?id = \"" + id + "\"). "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.KEYWORD.toString() + "> ?keys  } }   "
                + "group by ?keys order by ?keys ";
        System.out.println(query4);
        List<String> keys = snarlTemplate.query(query4, new RowMapper<String>() {
            public String mapRow(BindingSet bindingSet) {
                if (!bindingSet.hasBinding("keys")) {
                    return "empty";
                } else {
                    return bindingSet.getValue("keys").stringValue();
                }
            }
        });

        ret.setActor(actors);
        ret.setKeyword(keys);

        return ret;
    }

    public Movie getByIDSmall(final Integer id) {
        String query = "SELECT DISTINCT ?name ?id ?date ?duration  ?overview ?poster ?page ?rating ?popularity ?imdbid "
                + "WHERE { ?subject <" + MovieConstants.NAME.toString() + "> ?name . "
                + " FILTER (?id = \"" + id + "\"). "
                + "       OPTIONAL { ?subject <" + MovieConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DATE.toString() + "> ?date  } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.DURATION.toString() + "> ?duration  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.OVERVIEW.toString() + "> ?overview  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.CATEGORY.toString() + "> ?category  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.PAGE.toString() + "> ?page  } .  "
                + "       OPTIONAL { ?subject <" + MovieConstants.POSTER.toString() + "> ?poster } . "
                + "       OPTIONAL { ?subject <" + MovieConstants.RATING.toString() + "> ?rating .} "
                + "       OPTIONAL { ?subject <" + MovieConstants.COUNTRY.toString() + "> ?country } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.LOCATION.toString() + "> ?location } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.POPULARITY.toString() + "> ?popularity } ."
                + "       OPTIONAL { ?subject <" + MovieConstants.IMDBID.toString() + "> ?imdbid}}"
                + "group by ?name ?id ?date ?duration ?overview ?page ?poster ?rating ?popularity ?imdbid";

        System.out.println("Query:");
        System.out.println(query);
        List<Movie> movies = snarlTemplate.query(query, new RowMapper<Movie>() {

            public Movie mapRow(BindingSet bindingSet) {

                if (!bindingSet.hasBinding("name")) {
                    System.out.println("returning null movie");
                    return null;
                }

                try {
                    //System.out.println(bindingSet.getValue("posters").stringValue());
                    Movie d = new Movie();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setDate(bindingSet.getValue("date").stringValue());
                    d.setDuration(Long.parseLong(bindingSet.getValue("duration").stringValue()));
                    d.setOverview(bindingSet.getValue("overview").stringValue());
                    d.setPage(new URI(bindingSet.getValue("page").stringValue()));
                    d.setRating(bindingSet.getValue("rating").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPictures(bindingSet.getValue("poster").stringValue());

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        if (movies.get(0) == null) {
            return null;
        }

        return movies.get(0);
    }

    /**
     * <code>exists</code>
     *
     * @param movie to check if it exists in Stardog
     * @return boolean
     */
    public boolean exists(String movie) {
        List<String> results = snarlTemplate.doWithGetter(createNewSubject(movie), null, new GetterCallback<String>() {
            public String processStatement(Statement statement) {
                return statement.getObject().stringValue();
            }
        });
        if (results.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * <code>add</code> Adds a Movie POJO to stardog
     *
     * @param movie instance
     */
    public void add(Movie movie) {

        try {
            // TODO / Note, real implementation would have connection call back and
            // wrapped in tx here, perhaps spring mangaed tx
            URI subject = new URI(createNewSubject(movie.getName().replace(' ', '_')));
            snarlTemplate.add(subject, MovieConstants.NAME.asURI(), movie.getName());
            snarlTemplate.add(subject, MovieConstants.ID.asURI(), movie.getId() + "");
            snarlTemplate.add(subject, MovieConstants.PAGE.asURI(), movie.getPage());
            snarlTemplate.add(subject, MovieConstants.DATE.asURI(), movie.getDate().toString());
            snarlTemplate.add(subject, MovieConstants.DURATION.asURI(), movie.getDuration() + "");
            snarlTemplate.add(subject, MovieConstants.OVERVIEW.asURI(), movie.getOverview());
            snarlTemplate.add(subject, MovieConstants.RATING.asURI(), movie.getRating());
            snarlTemplate.add(subject, MovieConstants.POPULARITY.asURI(), movie.getPopularity());
            snarlTemplate.add(subject, MovieConstants.IMDBID.asURI(), movie.getImdbID());
            snarlTemplate.add(subject, MovieConstants.POSTER.asURI(), new URI(movie.getPicture()));

            for (String localr : movie.getLocation()) {
                snarlTemplate.add(subject, MovieConstants.LOCATION.asURI(), localr);
            }

            for (String aur : movie.getActors()) {
                snarlTemplate.add(subject, MovieConstants.ACTOR.asURI(), new URI(aur));
            }

            for (String cur : movie.getCategory()) {
                snarlTemplate.add(subject, MovieConstants.CATEGORY.asURI(), new URI(cur));
            }

            for (String creur : movie.getCrew()) {
                snarlTemplate.add(subject, MovieConstants.CREW.asURI(), new URI(creur));
            }

            for (String keyur : movie.getKeyword()) {
                snarlTemplate.add(subject, MovieConstants.KEYWORD.asURI(), new URI(keyur));
            }

            for (String countrur : movie.getCountry()) {
                snarlTemplate.add(subject, MovieConstants.COUNTRY.asURI(), new URI(countrur));
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void addLocationToMovie(String movie, String location) {

        try {
            URI subject = new URI(createNewSubject(movie));
            snarlTemplate.add(subject, MovieConstants.LOCATION.asURI(), location);
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(ActorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * <code>remove</code>
     *
     * @param movie instance to remove
     */
    public void remove(final Movie movie) {
        snarlTemplate.doWithRemover(new RemoverCallback<Boolean>() {
            public Boolean remove(Remover remover) throws StardogException {
                remover.statements(new URIImpl(createNewSubject(movie.getName())), null, null);
                //	remover.statements(new URIImpl(createNewSubject(movie.getName())), new URIImpl(Constants.WIKI.toString()), null);
                //	remover.statements(new URIImpl(createNewSubject(movie.getName())), new URIImpl(Constants.IMAGE.toString()), null);
                return true;
            }
        });
    }

    /**
     * <code>update</code> updates a movie POJO in Stardog
     *
     * @param movie instance
     */
    public void update(Movie movie) {
        remove(movie);
        System.out.println("removed");
        add(movie);
        System.out.println("added");
    }

    /**
     * <code>createNewSubject</code> Creates a URI for the movie based on the
     * name
     *
     * @param dogName
     * @return URI
     */
    private String createNewSubject(String dogName) {
        return MovieConstants.BASE + "#" + dogName.toLowerCase();
    }

    public SnarlTemplate getSnarlTemplate() {
        return snarlTemplate;
    }

    public void setSnarlTemplate(SnarlTemplate snarlTemplate) {
        this.snarlTemplate = snarlTemplate;
    }

}

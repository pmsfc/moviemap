/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.model.dao;

import com.aw.app.model.Country;
import com.aw.app.model.constants.CountryConstants;
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
import org.openrdf.model.Statement;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.BindingSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CountryDAO {

    private static final Logger logger = LoggerFactory.getLogger(CountryDAO.class);

    @Autowired
    private SnarlTemplate snarlTemplate;

    /**
     * <code>list</code> Lists all of the dogs in the store in no particular
     * order
     *
     * @return List<Actor>
     */
    public List<Country> list(int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?isocode ?lat ?lng "
                + "WHERE { ?subject <" + CountryConstants.NAME.toString() + "> ?name . "
                + " OPTIONAL { ?subject <" + CountryConstants.LONGITUDE.toString() + "> ?lng  } .  "
                + " OPTIONAL { ?subject <" + CountryConstants.LATITUDE.toString() + "> ?lat  } .  "
                + "  OPTIONAL { ?subject <" + CountryConstants.ISOCODE.toString() + "> ?isocode  }  } "
                + "group by ?name ?isocode ?lat ?lng "
                + "LIMIT" + limit + " OFFSET " + offset + "";

        System.out.println(query);

        List<Country> results = snarlTemplate.query(query, new RowMapper<Country>() {

            public Country mapRow(BindingSet bindingSet) {

                Country d = new Country();
                d.setName(bindingSet.getValue("name").stringValue());
                d.setIsoCode(bindingSet.getValue("isocode").stringValue());

                return d;
            }
        }
        );

        return results;
    }

    /**
     * <code>get</code>
     *
     * @param name of the movie, not a fully qualified URI
     * @return Movie instance
     */
    public List<Country> getByName(String name) {

        String query = "SELECT DISTINCT ?name ?isocode ?lat ?lng (GROUP_CONCAT( DISTINCT ?movi;separator=\",\") as ?movies) "
                + "WHERE { ?subject <" + CountryConstants.NAME.toString() + "> ?name . "
                + " FILTER regex(?name, \"" + name + "\", \"i\"). "
                + " OPTIONAL { ?subject <" + CountryConstants.MOVIE.toString() + "> ?movi  } .  "
                + " OPTIONAL { ?subject <" + CountryConstants.ISOCODE.toString() + "> ?isocode  } } "
                + "group by ?name ?isocode ?lat ?lng "
                + " LIMIT 15";

        System.out.println(query);

        List<Country> results = snarlTemplate.query(query, new RowMapper<Country>() {

            public Country mapRow(BindingSet bindingSet) {

                if (!bindingSet.hasBinding("name")) {
                    return null;
                }

                Country d = new Country();
                d.setName(bindingSet.getValue("name").stringValue());
                d.setIsoCode(bindingSet.getValue("isocode").stringValue());
                String movies = bindingSet.getValue("movies").stringValue();
                List<String> movis = Arrays.asList(movies.split("\\s*,\\s*"));
                d.setMovie(movis);

                return d;

            }
        });
        return results;
    }

    public Country getByISO(String iso) {

        String query = "SELECT DISTINCT ?name ?isocode ?lat ?lng (GROUP_CONCAT( DISTINCT ?movi;separator=\",\") as ?movies) "
                + "WHERE { ?subject <" + CountryConstants.NAME.toString() + "> ?name . "
                + " FILTER (?isocode = \"" + iso + "\"). "
                + " OPTIONAL { ?subject <" + CountryConstants.MOVIE.toString() + "> ?movi  } .  "
                + " OPTIONAL { ?subject <" + CountryConstants.ISOCODE.toString() + "> ?isocode  }} "
                + "group by ?name ?isocode ?lat ?lng ";

        System.out.println(query);

        List<Country> results = snarlTemplate.query(query, new RowMapper<Country>() {

            public Country mapRow(BindingSet bs) {

                if (!bs.hasBinding("name")) {
                    return null;
                }

                Country c = new Country();
                c.setName(bs.getValue("name").stringValue());
                c.setIsoCode(bs.getValue("isocode").stringValue());
                String movies = bs.getValue("movies").stringValue();
                List<String> movis = Arrays.asList(movies.split("\\s*,\\s*"));
                c.setMovie(movis);

                return c;
            }
        });
        return results.get(0);
    }

    /**
     * <code>exists</code>
     *
     * @param movie to check if it exists in Stardog
     * @return boolean
     */
    public boolean exists(String country) {
        List<String> results = snarlTemplate.doWithGetter(createNewSubject(country),
                null, new GetterCallback<String>() {
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
    public void add(Country country) {

        try {
            // TODO / Note, real implementation would have connection call back and
            // wrapped in tx here, perhaps spring mangaed tx
            URI subject = new URI(createNewSubject(country.getName()));
            snarlTemplate.add(subject, CountryConstants.NAME.asURI(), country.getName());
            snarlTemplate.add(subject, CountryConstants.ISOCODE.asURI(), country.getIsoCode());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void addReferenceToMovie(String country, String movieName) {

        try {
            // TODO / Note, real implementation would have connection call back and
            // wrapped in tx here, perhaps spring mangaed tx
            URI subject = new URI(createNewSubject(country));
            snarlTemplate.add(subject, CountryConstants.MOVIE.asURI(), movieName);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * <code>remove</code>
     *
     * @param movie instance to remove
     */
    public void remove(final Country country) {
        snarlTemplate.doWithRemover(new RemoverCallback<Boolean>() {
            public Boolean remove(Remover remover) throws StardogException {
                remover.statements(new URIImpl(createNewSubject(country.getName())), null, null);

                return true;
            }
        });
    }

    /**
     * <code>update</code> updates a movie POJO in Stardog
     *
     * @param movie instance
     */
    public void update(Country country) {
        remove(country);
        add(country);
    }

    /**
     * <code>createNewSubject</code> Creates a URI for the movie based on the
     * name
     *
     * @param dogName
     * @return URI
     */
    private String createNewSubject(String dogName) {
        return CountryConstants.BASE + "#" + dogName.toLowerCase();
    }

    public SnarlTemplate getSnarlTemplate() {
        return snarlTemplate;
    }

    public void setSnarlTemplate(SnarlTemplate snarlTemplate) {
        this.snarlTemplate = snarlTemplate;
    }

}

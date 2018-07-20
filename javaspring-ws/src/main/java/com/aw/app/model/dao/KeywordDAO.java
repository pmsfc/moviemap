/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.model.dao;

import com.aw.app.model.Keyword;
import com.aw.app.model.constants.KeywordConstants;
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
public class KeywordDAO {

    private static final Logger logger = LoggerFactory.getLogger(KeywordDAO.class);

    @Autowired
    private SnarlTemplate snarlTemplate;

    /**
     * <code>list</code> Lists all of the dogs in the store in no particular
     * order
     *
     * @return List<Actor>
     */
    public List<Keyword> list(int limit, int offset) {

        String query = "SELECT DISTINCT ?word ?id "
                + "WHERE { ?subject <" + KeywordConstants.WORD.toString() + "> ?word . "
                + "       OPTIONAL { ?subject <" + KeywordConstants.ID.toString() + "> ?id  }} "
                + "group by ?word ?id "
                + "ORDER BY ASC(?word) "
                + "LIMIT" + limit + " OFFSET " + offset + "";

        System.out.println(query);

        List<Keyword> results = snarlTemplate.query(query, new RowMapper<Keyword>() {

            public Keyword mapRow(BindingSet bindingSet) {

                Keyword d = new Keyword();
                d.setWord(bindingSet.getValue("word").stringValue());
                d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));

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
    public List<Keyword> getByKeyword(String word) {

        String query = "SELECT DISTINCT ?word ?id (GROUP_CONCAT( DISTINCT ?name;separator=\",\") as ?names) "
                + "WHERE { ?subject <" + KeywordConstants.WORD.toString() + "> ?word . "
                + "OPTIONAL { ?subject <" + KeywordConstants.MOVIE.toString() + "> ?name  }."
                + " FILTER regex(?word, \"" + word + "\", \"i\")."
                + "       OPTIONAL { ?subject <" + KeywordConstants.ID.toString() + "> ?id  }} "
                + "group by ?word ?id "
                + " LIMIT 15";
        System.out.println(query);

        List<Keyword> results = snarlTemplate.query(query, new RowMapper<Keyword>() {

            public Keyword mapRow(BindingSet bindingSet) {

                if (!bindingSet.hasBinding("word")) {
                    return null;
                }

                Keyword d = new Keyword();
                d.setWord(bindingSet.getValue("word").stringValue());
                d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));

                String movies = bindingSet.getValue("names").stringValue();
                List<String> movis = Arrays.asList(movies.split("\\s*,\\s*"));
                d.setMovie(movis);

                return d;

            }
        });
        return results;
    }

    public Keyword getByID(int id) {

        String query = "SELECT DISTINCT ?word ?id "
                + "WHERE { ?subject <" + KeywordConstants.WORD.toString() + "> ?word . "
                + " FILTER regex(?id, \"" + id + "\", \"i\")."
                + "       OPTIONAL { ?subject <" + KeywordConstants.ID.toString() + "> ?id  }} "
                + "group by ?word ?id";

        System.out.println(query);

        List<Keyword> results = snarlTemplate.query(query, new RowMapper<Keyword>() {

            public Keyword mapRow(BindingSet bindingSet) {

                if (!bindingSet.hasBinding("word")) {
                    return null;
                }

                Keyword d = new Keyword();
                d.setWord(bindingSet.getValue("word").stringValue());
                d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));

                return d;

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
    public boolean exists(String keyword) {
        List<String> results = snarlTemplate.doWithGetter(createNewSubject(keyword), null, new GetterCallback<String>() {
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
    public void add(Keyword keyword) {
        try {
            // TODO / Note, real implementation would have connection call back and
            // wrapped in tx here, perhaps spring mangaed tx
            URI subject = new URI(createNewSubject(keyword.getWord()));
            snarlTemplate.add(subject, KeywordConstants.WORD.asURI(), keyword.getWord());
            snarlTemplate.add(subject, KeywordConstants.ID.asURI(), keyword.getId() + "");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * <code>add</code> Adds a Movie POJO to stardog
     *
     * @param movie instance
     */
    public void addReferenceToMovie(String keyword, String movieName) {
        try {
            // TODO / Note, real implementation would have connection call back and
            // wrapped in tx here, perhaps spring mangaed tx
            URI subject = new URI(createNewSubject(keyword));
            snarlTemplate.add(subject, KeywordConstants.MOVIE.asURI(), movieName);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * <code>remove</code>
     *
     * @param movie instance to remove
     */
    public void remove(final Keyword keyword) {
        snarlTemplate.doWithRemover(new RemoverCallback<Boolean>() {
            public Boolean remove(Remover remover) throws StardogException {
                remover.statements(new URIImpl(createNewSubject(keyword.getWord())), null, null);

                return true;
            }
        });
    }

    /**
     * <code>update</code> updates a movie POJO in Stardog
     *
     * @param movie instance
     */
    public void update(Keyword keyword) {
        remove(keyword);
        add(keyword);
    }

    /**
     * <code>createNewSubject</code> Creates a URI for the movie based on the
     * name
     *
     * @param dogName
     * @return URI
     */
    private String createNewSubject(String dogName) {
        return KeywordConstants.BASE + "#" + dogName.toLowerCase();
    }

    public SnarlTemplate getSnarlTemplate() {
        return snarlTemplate;
    }

    public void setSnarlTemplate(SnarlTemplate snarlTemplate) {
        this.snarlTemplate = snarlTemplate;
    }

}

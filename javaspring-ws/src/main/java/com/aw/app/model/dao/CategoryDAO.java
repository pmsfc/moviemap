/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.model.dao;

import com.aw.app.model.Category;
import com.aw.app.model.constants.CategoryConstants;
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
public class CategoryDAO {

    private static final Logger logger = LoggerFactory.getLogger(CategoryDAO.class);

    @Autowired
    private SnarlTemplate snarlTemplate;

    /**
     * <code>list</code> Lists all of the dogs in the store in no particular
     * order
     *
     * @return List<Category>
     */
    public List<Category> list(int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id  "
                + "WHERE { ?subject <" + CategoryConstants.NAME.toString() + "> ?name . "
                + "OPTIONAL { ?subject <" + CategoryConstants.ID.toString() + "> ?id  }} "
                + "group by ?name ?id "
                + "ORDER BY ASC(?name)"
                + "LIMIT" + limit + " OFFSET " + offset + "";
        System.out.println(query);

        List<Category> results = snarlTemplate.query(query, new RowMapper<Category>() {

            public Category mapRow(BindingSet bindingSet) {

                Category d = new Category();
                d.setName(bindingSet.getValue("name").stringValue());
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
    public List<Category> getByName(String name) {

        String query = "SELECT DISTINCT ?name ?id (GROUP_CONCAT( DISTINCT ?movi;separator=\",\") as ?movies) "
                + "WHERE { ?subject <" + CategoryConstants.NAME.toString() + "> ?name . "
                 + "OPTIONAL { ?subject <" + CategoryConstants.MOVIE.toString() + "> ?movi  } .  "
                + " FILTER regex(?name, \"" + name + "\", \"i\")."
                + "       OPTIONAL { ?subject <" + CategoryConstants.ID.toString() + "> ?id  }} "
                + "group by ?name ?id"
                + " LIMIT 15";

        System.out.println(query);

        List<Category> results = snarlTemplate.query(query, new RowMapper<Category>() {

            public Category mapRow(BindingSet bindingSet) {

                if (!bindingSet.hasBinding("name")) {
                    return null;
                }

                Category d = new Category();
                d.setName(bindingSet.getValue("name").stringValue());
                d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));

                String movies = bindingSet.getValue("movies").stringValue();
                List<String> movis = Arrays.asList(movies.split("\\s*,\\s*"));
                d.setMovie(movis);

                return d;

            }
        });
        return results;
    }

    public Category getByID(int id) {

        String query = "SELECT DISTINCT ?name ?id "
                + "WHERE { ?subject <" + CategoryConstants.NAME.toString() + "> ?name . "
                + " FILTER regex(?id, \"" + id + "\", \"i\"). "
                + "       OPTIONAL { ?subject <" + CategoryConstants.ID.toString() + "> ?id  }} "
                + "group by ?name ?id ";

        System.out.println(query);

        List<Category> results = snarlTemplate.query(query, new RowMapper<Category>() {

            public Category mapRow(BindingSet bindingSet) {

                if (!bindingSet.hasBinding("name")) {
                    return null;
                }

                Category d = new Category();
                d.setName(bindingSet.getValue("name").stringValue());
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
    public boolean exists(String category) {
        List<String> results = snarlTemplate.doWithGetter(createNewSubject(category), null, new GetterCallback<String>() {
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
    public void add(Category category) {

        try {
            URI subject = new URI(createNewSubject(category.getName()));
            snarlTemplate.add(subject, CategoryConstants.NAME.asURI(), category.getName());
            snarlTemplate.add(subject, CategoryConstants.ID.asURI(), category.getId() + "");

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void addReferenceToMovie(String category, String movieName) {

        try {
            URI subject = new URI(createNewSubject(category));
            snarlTemplate.add(subject, CategoryConstants.MOVIE.asURI(), movieName);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * <code>remove</code>
     *
     * @param movie instance to remove
     */
    public void remove(final Category category) {
        snarlTemplate.doWithRemover(new RemoverCallback<Boolean>() {
            public Boolean remove(Remover remover) throws StardogException {
                remover.statements(new URIImpl(createNewSubject(category.getName())), null, null);

                return true;
            }
        });
    }

    /**
     * <code>update</code> updates a movie POJO in Stardog
     *
     * @param movie instance
     */
    public void update(Category category) {
        remove(category);
        add(category);
    }

    /**
     * <code>createNewSubject</code> Creates a URI for the movie based on the
     * name
     *
     * @param dogName
     * @return URI
     */
    private String createNewSubject(String dogName) {
        return CategoryConstants.BASE + "#" + dogName.toLowerCase();
    }

    public SnarlTemplate getSnarlTemplate() {
        return snarlTemplate;
    }

    public void setSnarlTemplate(SnarlTemplate snarlTemplate) {
        this.snarlTemplate = snarlTemplate;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.model.dao;

import com.aw.app.model.Crew;
import com.aw.app.model.constants.CrewConstants;
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
public class CrewDAO {

    private static final Logger logger = LoggerFactory.getLogger(CrewDAO.class);

    @Autowired
    private SnarlTemplate snarlTemplate;

    /**
     * <code>list</code> Lists all of the dogs in the store in no particular
     * order
     *
     * @return List<Actor>
     */
    public List<Crew> list(int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?job (GROUP_CONCAT( DISTINCT ?movi;separator=\",\") as ?movies) "
                + "WHERE { ?subject <" + CrewConstants.NAME.toString() + "> ?name . "
                + " OPTIONAL { ?subject <" + CrewConstants.ID.toString() + "> ?id  } . "
                + " OPTIONAL { ?subject <" + CrewConstants.JOB.toString() + "> ?job  }. "
                + " OPTIONAL { ?subject <" + CrewConstants.MOVIE.toString() + "> ?movi  }}"
                + "group by ?name ?id ?job "
                + "ORDER BY ASC(?name) "
                + "LIMIT" + limit + " OFFSET " + offset + "";
        ;

        System.out.println(query);

        List<Crew> results = snarlTemplate.query(query, new RowMapper<Crew>() {

            public Crew mapRow(BindingSet bindingSet) {

                Crew d = new Crew();
                d.setName(bindingSet.getValue("name").stringValue());
                d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                d.setJob(bindingSet.getValue("job").stringValue());

                String movies = bindingSet.getValue("movies").stringValue();
                List<String> movis = Arrays.asList(movies.split("\\s*,\\s*"));
                d.setMovie(movis);

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
    public List<Crew> getByName(String name) {

        String query = "SELECT DISTINCT ?name ?id ?job "
                + "WHERE { ?subject <" + CrewConstants.NAME.toString() + "> ?name . "
                + " FILTER regex(?name, \"" + name + "\", \"i\"). "
                + "       OPTIONAL { ?subject <" + CrewConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + CrewConstants.JOB.toString() + "> ?job  }} "
                + "group by ?name ?id ?job "
                + " LIMIT 15";

        System.out.println(query);

        List<Crew> results = snarlTemplate.query(query, new RowMapper<Crew>() {

            public Crew mapRow(BindingSet bindingSet) {

                if (!bindingSet.hasBinding("name")) {
                    return null;
                }

                Crew d = new Crew();
                d.setName(bindingSet.getValue("name").stringValue());
                d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                d.setJob(bindingSet.getValue("job").stringValue());

                return d;

            }
        });
        return results;
    }

    public Crew getByID(int id) {

        String query = "SELECT DISTINCT ?name ?id ?job "
                + "WHERE { ?subject <" + CrewConstants.NAME.toString() + "> ?name . "
                + " FILTER regex(?id, \"" + id + "\", \"i\"). "
                + "       OPTIONAL { ?subject <" + CrewConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + CrewConstants.JOB.toString() + "> ?job  }} "
                + "group by ?name ?id ?job ";

        System.out.println(query);

        List<Crew> results = snarlTemplate.query(query, new RowMapper<Crew>() {

            public Crew mapRow(BindingSet bindingSet) {

                if (!bindingSet.hasBinding("name")) {
                    return null;
                }

                Crew d = new Crew();
                d.setName(bindingSet.getValue("name").stringValue());
                d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                d.setJob(bindingSet.getValue("job").stringValue());

                return d;

            }
        });
        return results.get(0);
    }

    public Crew getByJob(String job) {

        String query = "SELECT DISTINCT ?name ?id ?job "
                + "WHERE { ?subject <" + CrewConstants.NAME.toString() + "> ?name . "
                + " FILTER regex(?job, \"" + job + "\", \"i\"). "
                + "       OPTIONAL { ?subject <" + CrewConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + CrewConstants.JOB.toString() + "> ?job  }} "
                + "group by ?name ?id ?job ";

        System.out.println(query);

        List<Crew> results = snarlTemplate.query(query, new RowMapper<Crew>() {

            public Crew mapRow(BindingSet bindingSet) {

                if (!bindingSet.hasBinding("name")) {
                    return null;
                }

                Crew d = new Crew();
                d.setName(bindingSet.getValue("name").stringValue());
                d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                d.setJob(bindingSet.getValue("job").stringValue());

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
    public boolean exists(String crew) {
        List<String> results = snarlTemplate.doWithGetter(createNewSubject(crew), null, new GetterCallback<String>() {
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
    public void add(Crew crew) {

        try {
            // TODO / Note, real implementation would have connection call back and
            // wrapped in tx here, perhaps spring mangaed tx
            URI subject = new URI(createNewSubject(crew.getName()));
            snarlTemplate.add(subject, CrewConstants.NAME.asURI(), crew.getName());
            snarlTemplate.add(subject, CrewConstants.ID.asURI(), crew.getId() + "");
            snarlTemplate.add(subject, CrewConstants.JOB.asURI(), crew.getJob());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void addReferenceToMovie(String crew, String movieName) {

        try {
            // TODO / Note, real implementation would have connection call back and
            // wrapped in tx here, perhaps spring mangaed tx
            URI subject = new URI(createNewSubject(crew));
            snarlTemplate.add(subject, CrewConstants.MOVIE.asURI(), movieName);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    /**
     * <code>remove</code>
     *
     * @param movie instance to remove
     */
    public void remove(final Crew crew) {
        snarlTemplate.doWithRemover(new RemoverCallback<Boolean>() {
            public Boolean remove(Remover remover) throws StardogException {
                remover.statements(new URIImpl(createNewSubject(crew.getName())), null, null);

                return true;
            }
        });
    }

    /**
     * <code>update</code> updates a movie POJO in Stardog
     *
     * @param movie instance
     */
    public void update(Crew crew) {
        remove(crew);
        add(crew);
    }

    /**
     * <code>createNewSubject</code> Creates a URI for the movie based on the
     * name
     *
     * @param dogName
     * @return URI
     */
    private String createNewSubject(String dogName) {
        return CrewConstants.BASE + "#" + dogName.toLowerCase();
    }

    public SnarlTemplate getSnarlTemplate() {
        return snarlTemplate;
    }

    public void setSnarlTemplate(SnarlTemplate snarlTemplate) {
        this.snarlTemplate = snarlTemplate;
    }

}

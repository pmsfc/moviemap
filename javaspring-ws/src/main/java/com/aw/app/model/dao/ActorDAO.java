package com.aw.app.model.dao;

import com.aw.app.model.Actor;
import com.aw.app.model.constants.ActorConstants;
import com.aw.app.model.constants.CountryConstants;
import com.complexible.stardog.StardogException;
import com.complexible.stardog.api.Remover;
import com.complexible.stardog.ext.spring.GetterCallback;
import com.complexible.stardog.ext.spring.RemoverCallback;
import com.complexible.stardog.ext.spring.RowMapper;
import com.complexible.stardog.ext.spring.SnarlTemplate;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
public class ActorDAO {

    private static final Logger logger = LoggerFactory.getLogger(ActorDAO.class);

    @Autowired
    private SnarlTemplate snarlTemplate;

    /**
     * <code>list</code> Lists all of the dogs in the store in no particular
     * order
     *
     * @return List<Actor>
     */
    public List<Actor> list(int limit, int offset) {

        String query = "SELECT DISTINCT ?name ?id ?bio ?birthday ?gender ?homepage ?imdbid ?popularity  "
                + "WHERE { ?subject <" + ActorConstants.NAME.toString() + "> ?name . "
                + "       OPTIONAL { ?subject <" + ActorConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + ActorConstants.BIO.toString() + "> ?bio  } . "
                + "       OPTIONAL { ?subject <" + ActorConstants.BIRTHDAY.toString() + "> ?birthday  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.HOMEPAGE.toString() + "> ?homepage  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.IMDBID.toString() + "> ?imdbid  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.POPULARITY.toString() + "> ?popularity}} "
                + "group by ?name ?id ?bio ?birthday ?gender ?homepage ?imdbid ?popularity "
                + "ORDER BY ASC(?name)"
                + "LIMIT " + limit + " OFFSET " + offset + "";

        System.out.println(query);

        List<Actor> results = snarlTemplate.query(query, new RowMapper<Actor>() {

            public Actor mapRow(BindingSet bindingSet) {

                try {
                    Actor d = new Actor();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setBio(bindingSet.getValue("bio").stringValue());
                    d.setBirthday(bindingSet.getValue("birthday").stringValue());
                    d.setHomepage(new URI(bindingSet.getValue("homepage").stringValue()));
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(ActorDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;

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
    public List<Actor> getByName(final String name) {

        String query = "SELECT DISTINCT ?name ?id ?bio ?birthday ?gender ?homepage ?imdbid ?popularity (GROUP_CONCAT( DISTINCT ?movi;separator=\",\") as ?movies) "
                + "WHERE { ?subject <" + ActorConstants.NAME.toString() + "> ?name . "
                + " FILTER regex(?name, \"" + name + "\", \"i\")."
                + "       OPTIONAL { ?subject <" + ActorConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + ActorConstants.BIO.toString() + "> ?bio  } . "
                + "OPTIONAL { ?subject <" + ActorConstants.MOVIE.toString() + "> ?movi  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.BIRTHDAY.toString() + "> ?birthday  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.HOMEPAGE.toString() + "> ?homepage  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.IMDBID.toString() + "> ?imdbid  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.POPULARITY.toString() + "> ?popularity  }} "
                + "group by ?name ?id ?bio ?birthday ?gender ?homepage ?imdbid ?popularity"
                + " LIMIT 15";

        System.out.println(query);

        List<Actor> results = snarlTemplate.query(query, new RowMapper<Actor>() {

            public Actor mapRow(BindingSet bindingSet) {

                try {
                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }

                    Actor d = new Actor();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setBio(bindingSet.getValue("bio").stringValue());
                    d.setBirthday(bindingSet.getValue("birthday").stringValue());
                    d.setHomepage(new URI(bindingSet.getValue("homepage").stringValue()));
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());

                    String movies = bindingSet.getValue("movies").stringValue();
                    List<String> movis = Arrays.asList(movies.split("\\s*,\\s*"));
                    d.setMovie(movis);

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });
        return results;
    }

    public Actor getByID(final Integer id) {
        String query = "SELECT DISTINCT ?name ?id ?bio ?birthday ?gender ?homepage ?imdbid ?popularity "
                + "WHERE { ?subject <" + ActorConstants.NAME.toString() + "> ?name . "
                + " FILTER regex(?id, \"" + id + "\", \"i\")."
                + "       OPTIONAL { ?subject <" + ActorConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + ActorConstants.BIO.toString() + "> ?bio  } . "
                + "       OPTIONAL { ?subject <" + ActorConstants.BIRTHDAY.toString() + "> ?birthday  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.HOMEPAGE.toString() + "> ?homepage  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.IMDBID.toString() + "> ?imdbid  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.POPULARITY.toString() + "> ?popularity  }} "
                + "group by ?name ?id ?bio ?birthday ?gender ?homepage ?imdbid ?popularity";

        System.out.println("Query:");
        System.out.println(query);
        List<Actor> results = snarlTemplate.query(query, new RowMapper<Actor>() {

            public Actor mapRow(BindingSet bindingSet) {

                try {
                    if (!bindingSet.hasBinding("name")) {
                        return null; 
                    }
                    Actor d = new Actor();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setBio(bindingSet.getValue("bio").stringValue());
                    d.setBirthday(bindingSet.getValue("birthday").stringValue());
                    d.setHomepage(new URI(bindingSet.getValue("homepage").stringValue()));
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results.get(0);
    }

    public Actor getByBirthday(final String birthday) {
        String query = "SELECT DISTINCT ?name ?id ?bio ?birthday ?gender ?homepage ?imdbid ?popularity "
                + "WHERE { ?subject <" + ActorConstants.NAME.toString() + "> ?name . "
                + " FILTER regex(?birthday, \"" + birthday + "\", \"i\")."
                + "       OPTIONAL { ?subject <" + ActorConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + ActorConstants.BIO.toString() + "> ?bio  } . "
                + "       OPTIONAL { ?subject <" + ActorConstants.BIRTHDAY.toString() + "> ?birthday  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.HOMEPAGE.toString() + "> ?homepage  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.IMDBID.toString() + "> ?imdbid  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.POPULARITY.toString() + "> ?popularity  }} "
                + "group by ?name ?id ?bio ?birthday ?gender ?homepage ?imdbid ?popularity";

        System.out.println("Query:");
        System.out.println(query);
        List<Actor> results = snarlTemplate.query(query, new RowMapper<Actor>() {

            public Actor mapRow(BindingSet bindingSet) {

                try {
                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Actor d = new Actor();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setBio(bindingSet.getValue("bio").stringValue());
                    d.setBirthday(bindingSet.getValue("birthday").stringValue());
                    d.setHomepage(new URI(bindingSet.getValue("homepage").stringValue()));
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        });

        return results.get(0);
    }

    public Actor getByGender(final String gender) {
        String query = "SELECT DISTINCT ?name ?id ?bio ?birthday ?gender ?homepage ?imdbid ?popularity "
                + "WHERE { ?subject <" + ActorConstants.NAME.toString() + "> ?name . "
                + " FILTER regex(?gender, \"" + gender + "\", \"i\")."
                + "       OPTIONAL { ?subject <" + ActorConstants.ID.toString() + "> ?id  } . "
                + "       OPTIONAL { ?subject <" + ActorConstants.BIO.toString() + "> ?bio  } . "
                + "       OPTIONAL { ?subject <" + ActorConstants.BIRTHDAY.toString() + "> ?birthday  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.HOMEPAGE.toString() + "> ?homepage  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.IMDBID.toString() + "> ?imdbid  } .  "
                + "       OPTIONAL { ?subject <" + ActorConstants.POPULARITY.toString() + "> ?popularity  }} "
                + "group by ?name ?id ?bio ?birthday ?gender ?homepage ?imdbid ?popularity";

        System.out.println("Query:");
        System.out.println(query);
        List<Actor> results = snarlTemplate.query(query, new RowMapper<Actor>() {

            public Actor mapRow(BindingSet bindingSet) {

                try {
                    if (!bindingSet.hasBinding("name")) {
                        return null;
                    }
                    Actor d = new Actor();
                    d.setName(bindingSet.getValue("name").stringValue());
                    d.setId(Long.parseLong(bindingSet.getValue("id").stringValue()));
                    d.setBio(bindingSet.getValue("bio").stringValue());
                    d.setBirthday(bindingSet.getValue("birthday").stringValue());
                    d.setHomepage(new URI(bindingSet.getValue("homepage").stringValue()));
                    d.setImdbID(bindingSet.getValue("imdbid").stringValue());
                    d.setPopularity(bindingSet.getValue("popularity").stringValue());

                    return d;
                } catch (URISyntaxException ex) {
                    java.util.logging.Logger.getLogger(MovieDAO.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
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
    public boolean exists(String actor) {
        List<String> results = snarlTemplate.doWithGetter(createNewSubject(actor), null, new GetterCallback<String>() {
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
    public void add(Actor actor) {

        try {
            // TODO / Note, real implementation would have connection call back and
            // wrapped in tx here, perhaps spring mangaed tx
            URI subject = new URI(createNewSubject(actor.getName()));
            snarlTemplate.add(subject, ActorConstants.NAME.asURI(), actor.getName());
            snarlTemplate.add(subject, ActorConstants.ID.asURI(), actor.getId() + "");
            snarlTemplate.add(subject, ActorConstants.BIO.asURI(), actor.getBio());
            snarlTemplate.add(subject, ActorConstants.BIRTHDAY.asURI(), actor.getBirthday());
            snarlTemplate.add(subject, ActorConstants.HOMEPAGE.asURI(), actor.getHomepage());
            snarlTemplate.add(subject, ActorConstants.IMDBID.asURI(), actor.getImdbID());
            snarlTemplate.add(subject, ActorConstants.POPULARITY.asURI(), actor.getPopularity());

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    public void addReferenceToMovie(String actor, String movieName) {

        try {
            URI subject = new URI(createNewSubject(actor));
            snarlTemplate.add(subject, ActorConstants.MOVIE.asURI(), movieName);
        } catch (URISyntaxException ex) {
            java.util.logging.Logger.getLogger(ActorDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * <code>remove</code>
     *
     * @param movie instance to remove
     */
    public void remove(final Actor actor) {
        snarlTemplate.doWithRemover(new RemoverCallback<Boolean>() {
            public Boolean remove(Remover remover) throws StardogException {
                remover.statements(new URIImpl(createNewSubject(actor.getName())), null, null);

                return true;
            }
        });
    }

    /**
     * <code>update</code> updates a movie POJO in Stardog
     *
     * @param movie instance
     */
    public void update(Actor actor) {
        remove(actor);
        add(actor);
    }

    /**
     * <code>createNewSubject</code> Creates a URI for the movie based on the
     * name
     *
     * @param dogName
     * @return URI
     */
    private String createNewSubject(String dogName) {
        return ActorConstants.BASE + "#" + dogName.toLowerCase();
    }

    public SnarlTemplate getSnarlTemplate() {
        return snarlTemplate;
    }

    public void setSnarlTemplate(SnarlTemplate snarlTemplate) {
        this.snarlTemplate = snarlTemplate;
    }

}

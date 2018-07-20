/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.service;

import com.aw.app.model.Actor;
import com.aw.app.model.Category;
import com.aw.app.model.Country;
import com.aw.app.model.Crew;
import com.aw.app.model.Keyword;
import com.aw.app.model.Movie;
import com.aw.app.model.dao.ActorDAO;
import com.aw.app.model.dao.CategoryDAO;
import com.aw.app.model.dao.CountryDAO;
import com.aw.app.model.dao.CrewDAO;
import com.aw.app.model.dao.KeywordDAO;
import com.aw.app.model.dao.MovieDAO;
import com.aw.app.utils.MovieGetterUtils;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.ProductionCountry;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;
import info.movito.themoviedbapi.model.people.PersonPeople;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    @Autowired
    MovieDAO movieDAO;
    @Autowired
    ActorDAO actorDAO;
    @Autowired
    CategoryDAO categoryDAO;
    @Autowired
    CountryDAO countryDAO;
    @Autowired
    CrewDAO crewDAO;
    @Autowired
    KeywordDAO keywordDAO;

    private StringBuilder sb;

    @Async
    public Future<String> asyncMethodWithReturnType(int start, int end) {
        String newline = "<br>";
        MovieGetterUtils.INSTANCE.setMovieGetterRuning(true);
        if (sb == null) {
            sb = new StringBuilder();
        }
        //clearing sb
        sb.setLength(0);
        sb.append(newline).append("Execute method asynchronously - ").append(Thread.currentThread().getName()).append(newline);

        TmdbMovies movies = new TmdbApi("197722df91971d5a4e7b4176d8f31960").getMovies();
        TmdbPeople people = new TmdbApi("197722df91971d5a4e7b4176d8f31960").getPeople();

        for (int i = start; i < end; i++) {

            sb.append("Looking for movie ID").append(i).append(" : ");

            if (!existsURL("https://api.themoviedb.org/3/movie/" + i + "?api_key=197722df91971d5a4e7b4176d8f31960&language=en-US")) {
                sb.append("Not defined").append(newline);
                continue;
            }

            MovieDb movie = movies.getMovie(i, "en", TmdbMovies.MovieMethod.credits, TmdbMovies.MovieMethod.images,
                    TmdbMovies.MovieMethod.keywords, TmdbMovies.MovieMethod.releases, TmdbMovies.MovieMethod.reviews,
                    TmdbMovies.MovieMethod.videos);
            String movieName = clearStr(movie.getTitle());

            try {
                if (!movieDAO.exists(movieName) && !movie.isAdult()) {
                    sb.append("Processing ").append(movieName).append(" i: ").append(i).append(" date: ").append(new Date().toString()).append(newline);
                    URI page = new URI(movie.getHomepage());
                    List<Artwork> poster = movie.getImages(ArtworkType.POSTER);
                    List<PersonCast> actors = movie.getCast();
                    List<Genre> categories = movie.getGenres();
                    List<ProductionCountry> countries = movie.getProductionCountries();
                    List<PersonCrew> crews = movie.getCrew();
                    List<info.movito.themoviedbapi.model.keywords.Keyword> keys = movie.getKeywords();

                    String artk = "";
                    if (poster.size() > 0) {
                        artk = poster.get(0).getFilePath();
                    }

                    //actor
                    List<String> ator = new ArrayList<>();
                    for (PersonCast actor : actors) {
                        String actorName = clearStr(actor.getName());
                        if (!actorDAO.exists(actorName)) {
                            PersonPeople person = people.getPersonInfo(actor.getId(), "en");
                            actorDAO.add(new Actor(actorName, person.getId(),
                                    person.getBiography(), person.getBirthday(), new URI(person.getHomepage().replace(' ', '_')),
                                    person.getImdbId(), person.getPopularity() + ""));

                        }
                        ator.add(actorName);

                        actorDAO.addReferenceToMovie(actorName, movieName);
                    }

                    //category
                    List<String> cate = new ArrayList<>();
                    for (Genre category : categories) {
                        String categoryName = clearStr(category.getName());
                        if (!categoryDAO.exists(categoryName)) {
                            categoryDAO.add(new Category(categoryName, category.getId()));
                        }
                        cate.add(categoryName);

                        categoryDAO.addReferenceToMovie(categoryName, movieName);
                    }

                    //country
                    List<String> cotry = new ArrayList<>();
                    List<String> location = new ArrayList<>();
                    for (ProductionCountry country : countries) {
                        String countryName = clearStr(country.getName());
                        if (!countryDAO.exists(countryName)) {
                            countryDAO.add(new Country(countryName, country.getIsoCode()));
                        }
                        cotry.add(countryName);
                        countryDAO.addReferenceToMovie(countryName, movieName);
                    }
                    /*
                    List<String> movieLocals = getLocal(movie.getImdbID());

                    for (String name : movieLocals) {
                        LatLng pos = getCoordinates(clearStr(name));
                        if (pos != null) {
                            location.add(pos.getLatitude() + "," + pos.getLongitude());
                        }
                    }*/

                    //crew
                    List<String> crw = new ArrayList<>();
                    for (PersonCrew pcrew : crews) {
                        String crewName = clearStr(pcrew.getName());
                        if (!crewDAO.exists(crewName)) {
                            crewDAO.add(new Crew(crewName, pcrew.getId(), pcrew.getJob()));
                        }
                        crw.add(crewName);

                        crewDAO.addReferenceToMovie(crewName, movieName);
                    }

                    //keywords
                    List<String> kes = new ArrayList<>();
                    for (info.movito.themoviedbapi.model.keywords.Keyword pkey : keys) {
                        String keyName = clearStr(pkey.getName());

                        if (!keywordDAO.exists(keyName)) {
                            keywordDAO.add(new Keyword(keyName, pkey.getId()));
                        }
                        kes.add(keyName);

                        keywordDAO.addReferenceToMovie(keyName, movieName);
                    }

                    Movie m = new Movie(movieName, movie.getId(), movie.getOverview(),
                            page, artk, ator, movie.getReleaseDate(), cate, movie.getRuntime(),
                            crw, kes, movie.getVoteAverage() + "", cotry, movie.getPopularity() + "", movie.getImdbID(), location);

                    movieDAO.add(m);
                } else {
                    sb.append("Already exists or Adult - ").append(movie.getTitle()).append(newline);
                }

            } catch (URISyntaxException ex) {
                Logger.getLogger(MovieService.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        sb.append("Finished! ").append(new Date().toString());
        MovieGetterUtils.INSTANCE.setMovieGetterRuning(false);

        return new AsyncResult<>("Time to process" + (System.nanoTime() - start));
    }

    public static boolean existsURL(String URLName) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con
                    = (HttpURLConnection) new URL(URLName).openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String clearStr(String str) {
        str = str.replaceAll(" ", "_");
        str = str.replaceAll("\"", "");
        str = str.replaceAll(",", "");
        str = str.replaceAll("\n", "");

        return str;

    }

    public String getData() {
        if (sb == null) {
            sb = new StringBuilder();
        }
        return sb.toString();
    }

}

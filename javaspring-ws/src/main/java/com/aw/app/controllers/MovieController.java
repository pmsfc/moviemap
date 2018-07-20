package com.aw.app.controllers;

import com.aw.app.model.Movie;
import com.aw.app.model.dao.MovieDAO;
import java.util.List;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthNone;
import org.jsondoc.core.annotation.ApiVersion;
import org.jsondoc.core.pojo.ApiStage;
import org.jsondoc.core.pojo.ApiVisibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api(name = "Movie", description = "Methods for managing movies", visibility = ApiVisibility.PUBLIC, stage = ApiStage.RC)
@ApiVersion(since = "1.0")
@ApiAuthNone
@Controller
@RequestMapping("/movie")
public class MovieController {

    @Autowired
    MovieDAO movieDAO;

    /**
     * Return 50 movies per page
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Movie>> getMovies(@RequestParam(value = "page",
            required = false, defaultValue = "1") Integer page, @RequestParam(value = "perpage",
            required = false, defaultValue = "10") Integer perPage) {

        List<Movie> movieList;
        if (perPage > 50) {
            perPage = 50;
        }
        if (page <= 1) {
            movieList = movieDAO.list(perPage, 0);
        } else {
            movieList = movieDAO.list((perPage), (perPage * (page - 1)));
        }

        if (movieList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Movie>>(movieList, HttpStatus.OK);

    }

    /**
     * Return 50 movies per page by category
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/category/{someCategory}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Movie>> getMoviesByCategory(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @PathVariable(value = "someCategory") List<String> category,
            @RequestParam(value = "perpage", required = false, defaultValue = "10") Integer perPage) {

        List<Movie> movieList;
        if (perPage > 50) {
            perPage = 50;
        }
        if (page <= 1) {
            movieList = movieDAO.getByCategory(category, perPage, 0);
        } else {
            movieList = movieDAO.getByCategory(category, (perPage * page), (perPage * (page - 1)));
        }

        if (movieList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Movie>>(movieList, HttpStatus.OK);

    }

    /**
     * Return 50 movies per page by category
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/{someName}/category/{someCategory}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Movie>> getMovieRegexByCategory(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @PathVariable(value = "someCategory") List<String> category, @PathVariable(value = "someName") String name,
            @RequestParam(value = "perpage", required = false, defaultValue = "10") Integer perPage) {

        List<Movie> movieList;
        if (perPage > 50) {
            perPage = 50;
        }
        if (page <= 1) {
            movieList = movieDAO.getRegexByCategory(name.replace(' ', '_'), category, perPage, 0);
        } else {
            movieList = movieDAO.getRegexByCategory(name.replace(' ', '_'), category, (perPage * page), (perPage * (page - 1)));
        }

        if (movieList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Movie>>(movieList, HttpStatus.OK);

    }

    /**
     * Return 50 movies per page by actor and category
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/actor/{someName}/category/{someCategory}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Movie>> getMovieByActorRegexAndCategory(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @PathVariable(value = "someCategory") List<String> category, @PathVariable(value = "someName") String name,
            @RequestParam(value = "perpage", required = false, defaultValue = "10") Integer perPage) {

        List<Movie> movieList;
        if (perPage > 50) {
            perPage = 50;
        }
        if (page <= 1) {
            movieList = movieDAO.getActorRegexAndCategory(name.replace(' ', '_'), category, perPage, 0);
        } else {
            movieList = movieDAO.getActorRegexAndCategory(name.replace(' ', '_'), category, (perPage * page), (perPage * (page - 1)));
        }

        if (movieList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Movie>>(movieList, HttpStatus.OK);

    }

    /**
     * Return 50 movies per page by country and category
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/country/{someName}/category/{someCategory}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Movie>> getMovieByCountryRegexAndCategory(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @PathVariable(value = "someCategory") List<String> category, @PathVariable(value = "someName") String name,
            @RequestParam(value = "perpage", required = false, defaultValue = "10") Integer perPage) {

        List<Movie> movieList;
        if (perPage > 50) {
            perPage = 50;
        }
        if (page <= 1) {
            movieList = movieDAO.getCountryRegexAndCategory(name.replace(' ', '_'), category, perPage, 0);
        } else {
            movieList = movieDAO.getCountryRegexAndCategory(name.replace(' ', '_'), category, (perPage * page), (perPage * (page - 1)));
        }

        if (movieList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Movie>>(movieList, HttpStatus.OK);

    }

    /**
     * Return 50 movies per page by keyword
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/keyword/{someKeyword}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Movie>> getMoviesByKeyword(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @PathVariable(value = "someKeyword") String keyword,
            @RequestParam(value = "perpage", required = false, defaultValue = "10") Integer perPage) {

        List<Movie> movieList;
        if (perPage > 50) {
            perPage = 50;
        }
        if (page <= 1) {
            movieList = movieDAO.getByKeyword(keyword, perPage, 0);
        } else {
            movieList = movieDAO.getByKeyword(keyword, (perPage), (perPage * (page - 1)));
        }

        if (movieList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Movie>>(movieList, HttpStatus.OK);

    }

    /**
     * Return 50 movies per page by actor
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/actor/{someActor}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Movie>> getMoviesByActor(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @PathVariable(value = "someActor") String actor,
            @RequestParam(value = "perpage", required = false, defaultValue = "10") Integer perPage) {

        List<Movie> movieList;
        if (perPage > 50) {
            perPage = 50;
        }
        if (page <= 1) {
            movieList = movieDAO.getByActor(actor, perPage, 0);
        } else {
            movieList = movieDAO.getByActor(actor, (perPage), (perPage * (page - 1)));
        }

        if (movieList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Movie>>(movieList, HttpStatus.OK);

    }

    /**
     * Return 50 movies per page by crew
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/crew/{someCrew}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Movie>> getMoviesByCrew(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @PathVariable(value = "someCrew") String crew,
            @RequestParam(value = "perpage", required = false, defaultValue = "10") Integer perPage) {

        List<Movie> movieList;
        if (perPage > 50) {
            perPage = 50;
        }
        if (page <= 1) {
            movieList = movieDAO.getByCrew(crew, perPage, 0);
        } else {
            movieList = movieDAO.getByCrew(crew, (perPage), (perPage * (page - 1)));
        }

        if (movieList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Movie>>(movieList, HttpStatus.OK);

    }

    /**
     * Return 50 movies per page by country
     *
     * @param page
     * @return
     */
    @RequestMapping(value = "/country/{someCountry}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Movie>> getMoviesByCountry(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @PathVariable(value = "someCountry") String country,
            @RequestParam(value = "perpage", required = false, defaultValue = "10") Integer perPage) {

        List<Movie> movieList;
        if (perPage > 50) {
            perPage = 50;
        }
        if (page <= 1) {
            movieList = movieDAO.getByCountry(country, perPage, 0);
        } else {
            movieList = movieDAO.getByCountry(country, (perPage), (perPage * (page - 1)));
        }

        if (movieList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Movie>>(movieList, HttpStatus.OK);

    }

    /**
     * Returns up to 15 movies with regex name
     *
     * @param name
     * @return
     */
    @RequestMapping(value = "/name/{someName}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Movie>> getAttrbyName(@PathVariable(value = "someName") String name,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "perpage", required = false, defaultValue = "10") Integer perPage) {

        List<Movie> movieList;
        if (perPage > 50) {
            perPage = 50;
        }
        if (page <= 1) {
            movieList = movieDAO.getByName(name.replace(' ', '_'), perPage, 0);
        } else {
            movieList = movieDAO.getByName(name.replace(' ', '_'), (perPage), (perPage * (page - 1)));
        }

        if (movieList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Movie>>(movieList, HttpStatus.OK);

    }

    /**
     * Get movie by ID
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{someID}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<Movie> getAttrbyID(@PathVariable(value = "someID") Integer id) {
        Movie m = movieDAO.getByID(id);
        if (m == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<Movie>(m, HttpStatus.OK);
        }

    }

    /**
     * Delete movie by ID
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{someID}", method = RequestMethod.DELETE, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity removeById(@PathVariable(value = "someID") Integer id) {
        Movie m = movieDAO.getByID(id);
        if (m == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            movieDAO.remove(m);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    /**
     * Insert a new Movie
     *
     * @param m
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity insertMovie(@RequestBody Movie m) {
        if (m.getName() == null && m.getId() == 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (movieDAO.exists(m.getName())) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        movieDAO.add(m);

        return new ResponseEntity(HttpStatus.CREATED);

    }

    /**
     * Update a movie
     *
     * @param m
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity updateMovie(@RequestBody Movie m) {
        if (m.getName() == null || m.getId() == 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        movieDAO.update(m);

        return new ResponseEntity(HttpStatus.CREATED);

    }

}

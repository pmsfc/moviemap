/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.controllers;

import com.aw.app.model.Crew;
import com.aw.app.model.dao.CrewDAO;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Api(name = "Crew", description = "Methods for managing crew", visibility = ApiVisibility.PUBLIC, stage = ApiStage.RC)
@ApiVersion(since = "1.0")
@ApiAuthNone
@Controller
@RequestMapping("/crew")
public class CrewController {

    @Autowired
    CrewDAO crewDAO;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Crew>> getCrew(@RequestParam(value = "page",
            required = false, defaultValue = "1") Integer page) {

        List<Crew> crewList;
        int perPage = 50;
        if (page <= 1) {
            crewList = crewDAO.list(perPage, 0);
        } else {
            crewList = crewDAO.list((perPage), (perPage * (page - 1)));
        }

        if (crewList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Crew>>(crewList, HttpStatus.OK);

    }

    //alterar a query
    @RequestMapping(value = "/name/{someName}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Crew>> getAttrbyName(@PathVariable(value = "someName") String name) {
        return new ResponseEntity<List<Crew>>(crewDAO.getByName(name.replace(' ', '_')), HttpStatus.OK);

    }

    //alterar a query
    @RequestMapping(value = "/{someID}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<Crew> getAttrbyID(@PathVariable(value = "someID") Integer id) {
        return new ResponseEntity<Crew>(crewDAO.getByID(id), HttpStatus.OK);

    }

    //alterar a query
    @RequestMapping(value = "/job/{someJob}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<Crew> getAttrbyJob(@PathVariable(value = "someJob") String job) {
        return new ResponseEntity<Crew>(crewDAO.getByJob(job), HttpStatus.OK);

    }

    @RequestMapping(value = "/{someID}", method = RequestMethod.DELETE, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<Crew> removeById(@PathVariable(value = "someID") Integer id) {
        Crew c = crewDAO.getByID(id);
        if (c == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            crewDAO.remove(c);
            return new ResponseEntity<Crew>(HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity insertCrew(@RequestBody Crew c) {

        if (c.getName() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (crewDAO.exists(c.getName())) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        crewDAO.add(c);

        return new ResponseEntity(HttpStatus.CREATED);

    }

    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity updateCrew(@RequestBody Crew c) {

        if (c.getName() == null || c.getId() == 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        crewDAO.update(c);

        return new ResponseEntity(HttpStatus.CREATED);

    }

}

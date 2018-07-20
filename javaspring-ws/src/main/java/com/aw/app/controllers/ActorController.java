/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.controllers;

import com.aw.app.model.Actor;
import com.aw.app.model.dao.ActorDAO;
import java.util.List;
import org.jsondoc.core.annotation.Api;
import org.jsondoc.core.annotation.ApiAuthNone;
import org.jsondoc.core.annotation.ApiMethod;
import org.jsondoc.core.annotation.ApiPathParam;
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

@Api(name = "Actor", description = "Methods for managing actors", visibility = ApiVisibility.PUBLIC, stage = ApiStage.RC)
@ApiVersion(since = "1.0")
@ApiAuthNone
@Controller
@RequestMapping("/actor")
public class ActorController {

    @Autowired
    ActorDAO actorDAO;

    @ApiMethod
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Actor>> getActors(@RequestParam(value = "page",
            required = false, defaultValue = "1") Integer page) {

        List<Actor> actorList;
        int perPage = 50;
        if (page <= 1) {
            actorList = actorDAO.list(perPage, 0);
        } else {
            actorList = actorDAO.list((perPage), (perPage * (page - 1)));
        }

        if (actorList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Actor>>(actorList, HttpStatus.OK);

    }

    @ApiMethod
    @RequestMapping(value = "/name/{someName}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Actor>> getAttrbyName(@ApiPathParam(description = "The name of the actor (regex)") @PathVariable(value = "someName") String name) {
        List<Actor> actorList = actorDAO.getByName(name.replace(' ', '_'));
        if (actorList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
        return new ResponseEntity<List<Actor>>(actorList, HttpStatus.OK);
        }
        
    }

    @ApiMethod
    @RequestMapping(value = "/{someID}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<Actor> getAttrbyID(@ApiPathParam(description = "Actor ID") @PathVariable(value = "someID") Integer id) {
         Actor actor = actorDAO.getByID(id);
        if (actor == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
        return new ResponseEntity<Actor>(actor, HttpStatus.OK);
        }

    }

    @ApiMethod
    //alterar a query de forma a pesquisar apenas pelo ano e nao ser preciso a data completa!!!!!!!!!!
    @RequestMapping(value = "/birthday/{someBirthday}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<Actor> getAttrbyBirthday(@ApiPathParam(description = "Actor birthday") @PathVariable(value = "someBirthday") String date) {
        Actor actor = actorDAO.getByBirthday(date);
        if (actor == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
        return new ResponseEntity<Actor>(actor, HttpStatus.OK);
        }
    }
 
    @ApiMethod
    @RequestMapping(value = "/{someID}", method = RequestMethod.DELETE, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity removeById(@ApiPathParam(description = "Actor ID") @PathVariable(value = "someID") Integer id) {
        Actor a = actorDAO.getByID(id);
        if (a == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            actorDAO.remove(a);
            return new ResponseEntity(HttpStatus.OK);
        }

    }

    @ApiMethod
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity insertActor(@RequestBody Actor c) {

        if (c.getName() == null || c.getId() == 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (actorDAO.exists(c.getName())) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        actorDAO.add(c);

        return new ResponseEntity(HttpStatus.CREATED);

    }

    @ApiMethod
    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity updateActor(@RequestBody Actor c) {

        if (c.getName() == null || c.getId() == 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        actorDAO.update(c);

        return new ResponseEntity(HttpStatus.CREATED);

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.controllers;

import com.aw.app.model.Keyword;
import com.aw.app.model.dao.KeywordDAO;
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

@Api(name = "Keyword", description = "Methods for managing keywords", visibility = ApiVisibility.PUBLIC, stage = ApiStage.RC)
@ApiVersion(since = "1.0")
@ApiAuthNone
@Controller
@RequestMapping("/keyword")
public class KeywordController {

    @Autowired
    KeywordDAO keywordDAO;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Keyword>> getKeywords(@RequestParam(value = "page",
            required = false, defaultValue = "1") Integer page) {

        List<Keyword> keywordList;
        int perPage = 50;
        if (page <= 1) {
            keywordList = keywordDAO.list(perPage, 0);
        } else {
            keywordList = keywordDAO.list((perPage), (perPage * (page - 1)));
        }

        if (keywordList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Keyword>>(keywordList, HttpStatus.OK);

    }

    //alterar a query
    @RequestMapping(value = "/word/{someKeyword}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Keyword>> getAttrbyWord(@PathVariable(value = "someKeyword") String word) {
        return new ResponseEntity<List<Keyword>>(keywordDAO.getByKeyword(word.replace(' ', '_')), HttpStatus.OK);

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
    public ResponseEntity<Keyword> getAttrbyID(@PathVariable(value = "someID") Integer id) {
        Keyword key = keywordDAO.getByID(id);
        if (key == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
        return new ResponseEntity<Keyword>(key, HttpStatus.OK);
        }

    }

    /**
     * Delete a Keyword
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{someID}", method = RequestMethod.DELETE, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity removeById(@PathVariable(value = "someID") Integer id) {
        Keyword k = keywordDAO.getByID(id);
        if (k == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            keywordDAO.remove(k);
            return new ResponseEntity(HttpStatus.OK);
        }

    }

    /**
     * Create a keyword
     *
     * @param k
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.POST, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity insertKeyword(@RequestBody Keyword k) {
        if (k.getWord() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (keywordDAO.exists(k.getWord())) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        keywordDAO.add(k);

        return new ResponseEntity(HttpStatus.CREATED);

    }

    /**
     * Update a keyword
     *
     * @param k
     * @return
     */
    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity updateKeyword(@RequestBody Keyword k) {

        if (k.getWord() == null || k.getId() == 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        keywordDAO.add(k);

        return new ResponseEntity(HttpStatus.CREATED);

    }
}

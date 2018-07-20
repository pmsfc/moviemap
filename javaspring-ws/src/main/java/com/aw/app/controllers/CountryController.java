/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.controllers;

import com.aw.app.model.Country;
import com.aw.app.model.dao.CountryDAO;
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

@Api(name = "Country", description = "Methods for managing countries", visibility = ApiVisibility.PUBLIC, stage = ApiStage.RC)
@ApiVersion(since = "1.0")
@ApiAuthNone
@Controller
@RequestMapping("/country")
public class CountryController {

    @Autowired
    CountryDAO countryDAO;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Country>> getCountries(@RequestParam(value = "page",
            required = false, defaultValue = "1") Integer page) {

        List<Country> countryList;
        int perPage = 50;
        if (page <= 1) {
            countryList = countryDAO.list(perPage, 0);
        } else {
            countryList = countryDAO.list((perPage), (perPage * (page - 1)));
        }

        if (countryList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Country>>(countryList, HttpStatus.OK);

    }

    //alterar a query
    @RequestMapping(value = "/name/{someName}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Country>> getAttrbyName(@PathVariable(value = "someName") String name) {
        return new ResponseEntity<List<Country>>(countryDAO.getByName(name.replace(' ', '_')), HttpStatus.OK);

    }

    //alterar a query
    @RequestMapping(value = "/{someISO}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<Country> getAttrbyISO(@PathVariable(value = "someISO") String iso) {
        Country country = countryDAO.getByISO(iso);
        if (country == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
        return new ResponseEntity<Country>(country, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/{someISO}", method = RequestMethod.DELETE, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity removeByISO(@PathVariable(value = "someISO") String iso) {
        Country c = countryDAO.getByISO(iso);
        if (c == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            countryDAO.remove(c);
            return new ResponseEntity(HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity insertCountry(@RequestBody Country c) {

        if (c.getName() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (countryDAO.exists(c.getName())) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        countryDAO.add(c);

        return new ResponseEntity(HttpStatus.CREATED);

    }

    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity updateCountry(@RequestBody Country c) {

        if (c.getName() == null || c.getIsoCode() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        countryDAO.update(c);

        return new ResponseEntity(HttpStatus.CREATED);

    }
}

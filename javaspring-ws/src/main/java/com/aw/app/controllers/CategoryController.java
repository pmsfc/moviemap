/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.controllers;

import com.aw.app.model.Category;
import com.aw.app.model.dao.CategoryDAO;
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

@Api(name = "Category", description = "Methods for managing categories", visibility = ApiVisibility.PUBLIC, stage = ApiStage.RC)
@ApiVersion(since = "1.0")
@ApiAuthNone
@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryDAO categoryDAO;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Category>> getCategories(@RequestParam(value = "page",
            required = false, defaultValue = "1") Integer page) {

        List<Category> categoryList;
        int perPage = 50;
        if (page <= 1) {
            categoryList = categoryDAO.list(perPage, 0);
        } else {
            categoryList = categoryDAO.list((perPage), (perPage * (page - 1)));
        }

        if (categoryList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<List<Category>>(categoryList, HttpStatus.OK);

    }

    //alterar a query
    @RequestMapping(value = "/name/{someName}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<List<Category>> getAttrbyName(@PathVariable(value = "someName") String name) {
        List<Category> categoryList = categoryDAO.getByName(name.replace(' ', '_'));
        if (categoryList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        } else {
        return new ResponseEntity<List<Category>>(categoryList, HttpStatus.OK);
        }

    }

    //alterar a query
    @RequestMapping(value = "/{someID}", method = RequestMethod.GET, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity<Category> getAttrbyID(@PathVariable(value = "someID") Integer id) {
        Category category = categoryDAO.getByID(id);
        if (category.getId() == 0) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
        return new ResponseEntity<Category>(category, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/{someID}", method = RequestMethod.DELETE, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity removeById(@PathVariable(value = "someID") Integer id) {
        Category c = categoryDAO.getByID(id);
        if (c == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            categoryDAO.remove(c);
            return new ResponseEntity(HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/", method = RequestMethod.POST, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity insertCategory(@RequestBody Category c) {

        if (c.getName() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        if (categoryDAO.exists(c.getName())) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        }

        categoryDAO.add(c);

        return new ResponseEntity(HttpStatus.CREATED);

    }

    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = {
        MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public ResponseEntity updateCategory(@RequestBody Category c) {

        if (c.getName() == null || c.getId() == 0) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        categoryDAO.update(c);

        return new ResponseEntity(HttpStatus.CREATED);

    }

}

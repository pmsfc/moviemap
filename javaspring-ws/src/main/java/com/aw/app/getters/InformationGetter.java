/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.getters;

import com.aw.app.service.MovieService;
import com.aw.app.utils.MovieGetterUtils;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequestMapping("/updatedb")
public class InformationGetter {

    @Autowired
    MovieService movieService;

    @RequestMapping(value = "/movies", method = RequestMethod.GET)
    public String process(@RequestParam(value = "start", required = true) Integer start,
            @RequestParam(value = "end", required = true) Integer end) {

        if (MovieGetterUtils.INSTANCE.isMovieGetterRuning()) {
            return "redirect:" + "/updatedb/movies/status";
        }

        System.out.println("Invoking an asynchronous method. "
                + Thread.currentThread().getName());
        Future<String> future = movieService.asyncMethodWithReturnType(start,end);

        return "redirect:" + "/updatedb/movies/status";
    }

    @RequestMapping(value = "/movies/status", method = RequestMethod.GET)
    @ResponseBody
    public String output(HttpServletRequest request) {
        return movieService.getData() != null ? movieService.getData() : "Starting";
    }

}

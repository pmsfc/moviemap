/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.getters;

import com.aw.app.service.LocationService;
import com.aw.app.utils.MovieGetterUtils;
import java.util.concurrent.Future;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *
 * @author AW17
 */
@Controller
@RequestMapping("/updateLocal")
public class LocationGetter {

    @Autowired
    LocationService locationService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String process(@RequestParam(value = "start", required = true) Integer start,
            @RequestParam(value = "end", required = true) Integer end) {

        if (MovieGetterUtils.INSTANCE.isLocationGetterRuning()) {
            return "redirect:" + "/updateLocal/status";
        }

        System.out.println("Invoking an asynchronous method. "
                + Thread.currentThread().getName());
        Future<String> future = locationService.asyncMethodWithReturnType(start, end);

        return "redirect:" + "/updateLocal/status";
    }

    @RequestMapping(value = "/status", method = RequestMethod.GET)
    @ResponseBody
    public String output(HttpServletRequest request) {
        return locationService.getData() != null ? locationService.getData() : "Starting";
    }

}

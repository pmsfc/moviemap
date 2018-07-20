/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.service;

import com.aw.app.model.Movie;
import com.aw.app.model.dao.MovieDAO;
import static com.aw.app.service.MovieService.existsURL;
import com.aw.app.utils.MovieGetterUtils;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

/**
 *
 * @author AW17
 */
@Service
public class LocationService {

    @Autowired
    MovieDAO movieDAO;

    private StringBuilder sb;

    @Async
    public Future<String> asyncMethodWithReturnType(int start, int end) {
        MovieGetterUtils.INSTANCE.setLocationGetterRuning(true);
        String location = "";
        if (sb == null) {
            sb = new StringBuilder();
        }
        String newline = "<br>";
        sb.setLength(0);
        sb.append(HttpClientBuilder.class.getProtectionDomain().getCodeSource().getLocation());
        sb.append(newline).append("Execute method asynchronously - ").append(Thread.currentThread().getName()).append(newline);

        for (int i = start; i < end; i++) {

            Movie movie = movieDAO.getByIDSmall(i);
            if (movie == null || movie.getImdbID().equals("")) {
                sb.append("No movie found with id: ").append(i).append(newline);
                continue;
            }
            List<String> movieLocals = getLocal(movie.getImdbID());
            if (movieLocals.isEmpty()) {
                sb.append("No Location found for: ").append(movie.getName()).append(newline);
            }
            for (String name : movieLocals) {
                LatLng pos = getCoordinates(clearStr(name));
                if (pos != null) {
                    location = pos.getLatitude() + "," + pos.getLongitude();
                    movieDAO.addLocationToMovie(movie.getName(), location);
                    sb.append("Location updated for: ").append(i).append(" ").append(movie.getName()).append(newline);
                }
            }
        }
        MovieGetterUtils.INSTANCE.setLocationGetterRuning(false);

        return new AsyncResult<>("DONE");

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

    public LatLng getCoordinates(String country) {
        try {

            String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + country + "&key=AIzaSyClxrR9mRWwXaCvzDP9n9OYEAJm2Y7qMV4";
            if (!existsURL(url)) {
                return null;
            }
            String jsonMessage = getUrlContents(url);
            JSONObject obj = new JSONObject(jsonMessage);
            if (!obj.getString("status").equals("OK")) {
                return null;
            }
            JSONObject location = obj.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
            String longitude = location.getString("lng");
            String latitude = location.getString("lat");
            return new LatLng(latitude, longitude);
        } catch (JSONException ex) {
            Logger.getLogger(MovieService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getUrlContents(String theUrl) {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(theUrl);
            URLConnection urlConnection = url.openConnection();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }

    public List<String> getLocal(String id) {
        String searchQuery = id;
        WebClient client = new WebClient();
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        HtmlPage page = null;
        List<String> locations = new ArrayList<String>();
        try {
            String searchUrl = "http://www.imdb.com/title/" + URLEncoder.encode(searchQuery, "UTF-8") + "/locations";
            page = client.getPage(searchUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<DomElement> items = page.getByXPath("//dt/a");
        if (items.isEmpty()) {
            System.out.println("No items found !");
        } else {
            for (DomElement item : items) {
                locations.add(item.getTextContent());
            }
        }
        return locations;
    }
}

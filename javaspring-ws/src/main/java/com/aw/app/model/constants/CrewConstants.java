/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aw.app.model.constants;

import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author ruipo
 */
public enum CrewConstants {
    
    BASE("http://www.aw17rdf.com/crew/"),
    NAME("http://www.aw17rdf.com/crew/name"),
    ID("http://www.aw17rdf.com/crew/id"),
    JOB("http://www.aw17rdf.com/crew/job"),
    MOVIE("http://xmlns.com/foaf/0.1/movie");

    
    private String value;
	
	CrewConstants(String value) {
		this.value = value;
	}
	
	public String toString() {
		return value;
	}
	
	public URI asURI() {
		URI uri = null;
		try {
			 uri =  new URI(value);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return uri;
	}
    
}

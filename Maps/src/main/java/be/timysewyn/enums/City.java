package be.timysewyn.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.timysewyn.integration.MongoDBStoreEndpoint;

public enum City {
	
	GHENT("ghent"),
	ANTWERP("antwerp");

	private static final Log LOG = LogFactory.getLog(MongoDBStoreEndpoint.class);
	private static final Map<String, City> CITIES;
	
	static {
		Map<String, City> cities = new HashMap<String, City>();
		for(City city : City.values()) {
			cities.put(city.name, city);
		}
		CITIES = Collections.unmodifiableMap(cities);
	}
	
	private String name;
	private City(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public static final City fromName(String name) {
		City city = CITIES.get(name);
		
		if(city == null) {
    		LOG.error("No city named '" + name + "' found!");
    	}
		
		return city;
	}
}

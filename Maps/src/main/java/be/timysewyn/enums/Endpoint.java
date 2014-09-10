package be.timysewyn.enums;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import be.timysewyn.integration.MongoDBStoreEndpoint;


public enum Endpoint {

	STAT_SECTOR("stat_sector"),
	SPEEDING("speeding");

	private static final Log LOG = LogFactory.getLog(MongoDBStoreEndpoint.class);
	private static final Map<String, Endpoint> ENDPOINTS;
	
	static {
		Map<String, Endpoint> endpoints = new HashMap<String, Endpoint>();
		for(Endpoint endpoint : Endpoint.values()) {
			endpoints.put(endpoint.path, endpoint);
		}
		ENDPOINTS = Collections.unmodifiableMap(endpoints);
	}
	
	private String path;
	private Endpoint(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}

	public static final Endpoint fromPath(String path) {
		Endpoint endpoint = ENDPOINTS.get(path);
		
		if(endpoint == null) {
    		LOG.error("No endpoint found for path '" + path + "'!");
    	}
		
		return endpoint;
	}
	
}

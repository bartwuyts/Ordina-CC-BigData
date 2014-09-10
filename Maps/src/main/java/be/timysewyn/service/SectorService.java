package be.timysewyn.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import be.timysewyn.backend.MongoDBConnection;
import be.timysewyn.enums.City;
import be.timysewyn.enums.Endpoint;

import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Component
public class SectorService {
	
	@Autowired
	private MongoDBConnection mongoDbConnection;
	
	public List<String> getSectors() {
		return getSectors(null);
	}
	
	public List<String> getSectors(String city) {
		DBCollection collection = mongoDbConnection.getApplicationDb().getCollection(Endpoint.STAT_SECTOR.getPath());
		
		List<String> sectors = new ArrayList<String>();
		
		BasicDBObjectBuilder queryBuilder = BasicDBObjectBuilder.start();
		if(city != null && City.fromName(city) != null) {
			queryBuilder.add("city", city);
		}
		DBObject query = queryBuilder.get();
		DBObject fields = BasicDBObjectBuilder.start().add("_id", 0).get();
		
		DBCursor cursor = collection.find(query, fields);
		while(cursor.hasNext()) {
			sectors.add(cursor.next().toString());
		}
		
		return sectors;
	}

}

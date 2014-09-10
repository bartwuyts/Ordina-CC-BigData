package be.timysewyn.integration;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

import be.timysewyn.backend.MongoDBConnection;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.BulkWriteOperation;
import com.mongodb.DBCollection;

@MessageEndpoint
public class MongoDBStoreEndpoint {

	private static final Log LOG = LogFactory.getLog(MongoDBStoreEndpoint.class);
	
	@Autowired
	private MongoDBConnection mongoDbConnection;
    
    @ServiceActivator(inputChannel = "storeDataChannel")
	public void storeData(Message<DataImportPayload> message) {
    	DataImportPayload dataImportPayload = message.getPayload();

		LOG.debug("Storing data..");
    	
		String collectionName = dataImportPayload.getEndpoint().getPath();
		
    	switch (dataImportPayload.getEndpoint()) {
			case STAT_SECTOR:
				saveStatSect(collectionName, dataImportPayload);
				break;
				
			case SPEEDING:
				saveSpeeding(collectionName, dataImportPayload);
				break;
		
			default:
				LOG.error("Data not stored! Unknown endpoint!");
				return;
		}

		LOG.debug("Data stored!");
	}
    
    private void saveStatSect(String collectionName, DataImportPayload dataImportPayload) {
    	DBCollection collection = mongoDbConnection.getApplicationDb().getCollection(collectionName);

		ObjectMapper objectMapper = new ObjectMapper();
		JSONArray sectors = dataImportPayload.getUrlContent().optJSONArray("sectors");
		
		if(sectors != null) {
			BulkWriteOperation ops = collection.initializeOrderedBulkOperation();
			for(int i = 0 ; i < sectors.length() ; ++i) {
				JSONObject sector = sectors.getJSONObject(i);
				String sectorString = sector.toString();
				try {
					ops.find(BasicDBObjectBuilder.start()
								.add("code", sector.getString("code"))
								.add("city", dataImportPayload.getCity().getName())
								.get())
						.upsert()
						.replaceOne(BasicDBObjectBuilder.start(objectMapper.readValue(sectorString, HashMap.class))
								.add("city", dataImportPayload.getCity().getName())
								.get());
				} catch (IOException e) {
					LOG.error("Could not save sector! " + sectorString);
				}
			}
			ops.execute();
		}
    }
    
    private void saveSpeeding(String collectionName, DataImportPayload dataImportPayload) {
    	DBCollection collection = mongoDbConnection.getApplicationDb().getCollection(collectionName);

		ObjectMapper objectMapper = new ObjectMapper();
		JSONArray speedings = dataImportPayload.getUrlContent().optJSONArray("speedings");
		
		if(speedings != null) {
			BulkWriteOperation ops = collection.initializeOrderedBulkOperation();
			for(int i = 0 ; i < speedings.length() ; ++i) {
				JSONObject speeding = speedings.getJSONObject(i);
				String speedingString = speeding.toString();
				try {
					ops.find(BasicDBObjectBuilder.start()
								.add("street_name", speeding.getString("street_name"))
								.add("city", dataImportPayload.getCity().getName())
								.get())
						.upsert()
						.replaceOne(BasicDBObjectBuilder.start(objectMapper.readValue(speedingString, HashMap.class))
								.add("city", dataImportPayload.getCity().getName())
								.get());
				} catch (IOException e) {
					LOG.error("Could not save speeding! " + speedingString);
				}
			}
			ops.execute();
		}
    }
}

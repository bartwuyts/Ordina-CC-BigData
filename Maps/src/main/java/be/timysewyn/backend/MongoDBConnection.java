package be.timysewyn.backend;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import be.timysewyn.configuration.settings.MongoDBSettings;
import be.timysewyn.configuration.settings.MongoDBSettings.Server;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.ServerAddress;

@Component
public class MongoDBConnection implements InitializingBean {
	
	private static final Log LOG = LogFactory.getLog(MongoDBConnection.class);

	@Autowired
	private MongoDBSettings mongoDbSettings;
	
	private MongoClient mongoClient;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		MongoClientOptions options = new MongoClientOptions.Builder()
		// Options, for later use
		.build();
		
		this.mongoClient = new MongoClient(getSeeds(), options);
	}

	private List<ServerAddress> getSeeds() {
		List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
		for(Server server : mongoDbSettings.getServers()) {
			String host = server.getHost();
			int port = server.getPort();
			
			LOG.debug("MongoDB seed: " + host + ":" + port);
			try {
				serverAddresses.add(new ServerAddress(host, port));
			} catch (UnknownHostException e) {
				LOG.error("Unknown seed: " + host + ":" + port + "!");
			}
		}
		
		Assert.notEmpty(serverAddresses);
		
		return serverAddresses;
	}
	
	public MongoClient getClient() {
		return mongoClient;
	}

	public DB getApplicationDb() {
		return mongoClient.getDB(mongoDbSettings.getDb());
	}
	
}

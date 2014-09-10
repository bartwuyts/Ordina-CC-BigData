package be.timysewyn.integration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

@MessageEndpoint
public class JsonDataDownloader {
	
	private static final Log LOG = LogFactory.getLog(JsonDataDownloader.class);
	
	@ServiceActivator(inputChannel="downloadJsonChannel")
	public Message<JSONObject> downloadJson(Message<DataImportPayload> message) {
		DataImportPayload dataImportPayload = message.getPayload();
		String urlString = dataImportPayload.getUrl();
		LOG.debug("Downloading data..");
		try {
			URL url = new URL(urlString);
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(url.openStream()));
				StringBuilder input = new StringBuilder();

			    String inputLine;
			    while ((inputLine = in.readLine()) != null) {
			        input.append(inputLine);
			    }
			    
				LOG.debug("Data downloaded!");
				return new GenericMessage<JSONObject>(new JSONObject(input.toString()));
			} catch (IOException e) {
				LOG.error("An error occurred while downloading data!");
			} finally {
				if(in != null) {
			        try {
						in.close();
					} catch (IOException e) {}
				}
			}
		} catch (MalformedURLException e) {
			LOG.error("HTTP request execution failed for URI [" + urlString + "]");
		}
		
		return null;
	}

}

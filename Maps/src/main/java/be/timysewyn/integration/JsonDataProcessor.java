package be.timysewyn.integration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;

import be.timysewyn.integration.transformer.ITransformer;

@MessageEndpoint
public class JsonDataProcessor {
	
	private static final Log LOG = LogFactory.getLog(JsonDataProcessor.class);
	
	@Autowired
	private ApplicationContext applicationContext;
	
	@ServiceActivator(inputChannel = "processJsonChannel", outputChannel = "storeDataChannel")
	public Message<DataImportPayload> process(Message<DataImportPayload> message) {
		DataImportPayload dataImportPayload = message.getPayload();
		
		JSONObject result = transform(dataImportPayload);
		
		if(result == null) {
			return null;
		}
		
		message.getPayload().setUrlContent(result);
		
		return message;
	}

	private JSONObject transform(DataImportPayload dataImportPayload) {
		ITransformer transformer = null;
		
		try {
			transformer = applicationContext.getBean(dataImportPayload.getCity().getName() + "Transformer", ITransformer.class);
		} catch(BeansException e) {}
		
		if(transformer == null) {
			LOG.error("Could not find a transformer for city '" + dataImportPayload.getCity().getName() + "'!");
			return null;
		}
		
		LOG.debug("Transforming input...");
		
		JSONObject output = null;
		switch (dataImportPayload.getEndpoint()) {
			case STAT_SECTOR:
				output = transformer.transformStatSect(dataImportPayload.getUrlContent());
				break;
			case SPEEDING:
				output = transformer.transformSpeeding(dataImportPayload.getUrlContent());
				break;
		}
		
		if(output != null) {
			LOG.debug("Input transformed!");
		} else {
			LOG.error("Got null!");
		}
		
		return output;
	}

}

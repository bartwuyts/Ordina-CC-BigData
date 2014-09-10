package be.timysewyn.integration;

import java.util.Arrays;
import java.util.concurrent.Executors;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.NullChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.integration.router.AbstractMessageRouter;
import org.springframework.integration.router.RecipientListRouter;
import org.springframework.integration.router.RecipientListRouter.Recipient;
import org.springframework.messaging.MessageChannel;
import org.springframework.util.StringUtils;

@Configuration
public class IntegrationConfiguration {
	
	private final static SpelExpressionParser PARSER = new SpelExpressionParser();
	
	
	/**************************
	 * 		  CHANNELS		  *
	 **************************/
	
	
	@Bean
	public MessageChannel requestImportChannel() {
		return MessageChannels.executor(Executors.newCachedThreadPool()).get();
	}
	
	@Bean
	public MessageChannel downloadJsonChannel() {
		return MessageChannels.direct().get();
	}
	
	@Bean
	public MessageChannel processJsonChannel() {
		return MessageChannels.direct().get();
	}
	
	@Bean
	public MessageChannel storeDataChannel() {
		return MessageChannels.direct().get();
	}
	
	@Bean
	public MessageChannel rejectedChannel() {
		// This channel will just consume the message so it will no longer exist
		return new NullChannel();
	}
	

	
	/**************************
	 * 		  	FLOWS		  *
	 **************************/
	
	
	@Bean
	public IntegrationFlow processImportRequestFlow() {
		return IntegrationFlows
				// Start with the 'requestImportChannel' channel
				.from(requestImportChannel())
				
				// The URL can't be empty
				.filter((DataImportPayload d) -> !StringUtils.isEmpty(d.getUrl()),
							f -> f.discardChannel(rejectedChannel()))
						
				// We should recognize the endpoint
				.filter((DataImportPayload d) -> d.getEndpoint() != null,
							f -> f.discardChannel(rejectedChannel()))
						
				// We should recognize the city
				.filter((DataImportPayload d) -> d.getCity() != null,
							f -> f.discardChannel(rejectedChannel()))
						
				// The given url should be valid
				.filter((DataImportPayload d) -> (new UrlValidator()).isValid(d.getUrl()),
							f -> f.discardChannel(rejectedChannel()))
						
				// Download the url content and add it to the DataImportPayload object
				.enrich(e -> e
								// The header expressions can not be empty, so we use this expression to copy all the headers
								//
								// This is currently a bug in the spring-integration-java-dsl package
								// A pull request has already been accepted & merged ;)
								// https://github.com/spring-projects/spring-integration-extensions/pull/102
								.headerExpression("headers.urlContent", "headers")
								
								// Set the urlContent property with the received payload (JSONObject)
								.propertyExpression("urlContent", "payload")
								
								// Send the message to the "downloadJsonChannel" channel to get the content behind the URL back
								.requestChannel(downloadJsonChannel())
								
								.get())
				
				// The data should have been downloaded
				.filter((DataImportPayload d) -> d.getUrlContent() != null,
							f -> f.discardChannel(rejectedChannel()))
							
				// Route the request using the "processJsonChannelRouter" router
				.route(processJsonChannelRouter())
				
				.get();
	}
	

	
	/**************************
	 *	   FLOW COMPONENTS	  *
	 **************************/
	
	
	@Bean
	public HttpRequestHandlingMessagingGateway httpGateway() {
		HttpRequestHandlingMessagingGateway gateway = new HttpRequestHandlingMessagingGateway(false);
		
		RequestMapping mapping = new RequestMapping();
		mapping.setMethods(HttpMethod.POST);
		mapping.setPathPatterns("/data/import/{endpoint}/{city}");
		
		gateway.setRequestMapping(mapping);
		
		// Construct a payload to send to the "requestImportChannel" channel using SpEL
		gateway.setPayloadExpression(PARSER.parseExpression("new be.timysewyn.integration.DataImportPayload("
																+ " T(be.timysewyn.enums.Endpoint).fromPath(#pathVariables.endpoint),"
																+ " T(be.timysewyn.enums.City).fromName(#pathVariables.city),"
																+ " (#requestParams['url'] != null && !(#requestParams['url'].isEmpty())) ? #requestParams['url'][0] : \"\")"
															));
		
		gateway.setRequestChannel(requestImportChannel());
		
		return gateway;
	}
	
	@Bean
	public AbstractMessageRouter processJsonChannelRouter() {
		RecipientListRouter router = new RecipientListRouter();
		
		// Route the message to the "processJsonChannel" channel
		router.setRecipients(Arrays.asList(new Recipient(processJsonChannel())));
		
		return router;
	}
	
}

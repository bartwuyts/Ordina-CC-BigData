package be.timysewyn.integration;

import org.json.JSONObject;

import be.timysewyn.enums.City;
import be.timysewyn.enums.Endpoint;

public class DataImportPayload {
	
	private final Endpoint endpoint;
	private final City city;
	private final String url;
	private JSONObject urlContent;
	
	public DataImportPayload(Endpoint endpoint, City city, String url) {
		super();
		this.endpoint = endpoint;
		this.city = city;
		this.url = url;
	}

	public Endpoint getEndpoint() {
		return endpoint;
	}

	public City getCity() {
		return city;
	}

	public String getUrl() {
		return url;
	}
	
	public JSONObject getUrlContent() {
		return urlContent;
	}

	public void setUrlContent(JSONObject urlContent) {
		this.urlContent = urlContent;
	}

	@Override
	public String toString() {
		return "DataImportPayload [endpoint=" + endpoint + ", city=" + city
				+ ", url=" + url + ", urlContent=" + urlContent + "]";
	}
	
}

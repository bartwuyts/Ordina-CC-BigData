package be.timysewyn.integration.transformer;

import org.json.JSONObject;
import org.springframework.integration.annotation.MessageEndpoint;

@MessageEndpoint
public interface ITransformer {
	
	/**
	 * OUTPUT
	 * 	"sectors" : [{
	 * 		"code": String
	 * 		"name": String
	 * 		"geometry" : String, based on GeoJSON
	 * 	}]
	 * 
	 * 	Geometry tag is based on GeoJSON
	 * 	"geometry": "{ \"type\": \"Polygon\",\"coordinates\": [[ [x, y], [x, y] ]] }
	 * 	"geometry": "{ \"type\": \"MultiPolygon\",\"coordinates\": [[[[x, y], [x, y]]], [[[x, y], [x, y]]]] }
	 * 
	 * @param input The JSON that needs to be transformed to the structure, as specified above
	 * @return JSONObject The transformed JSON
	 */
	default JSONObject transformStatSect(JSONObject input) {
		return null;
	}
	
	/**
	 * OUTPUT
	 * 	"speedings" : [{
	 * 		"street_name": String
	 * 		"speed": int, 0 for unknown
	 * 		"geometry" : String, based on GeoJSON
	 * 	}]
	 * 
	 * 	Geometry tag is based on GeoJSON
	 * 	"geometry": "{ \"type\": \"Polyline\",\"coordinates\": [[ [x, y], [x, y] ]] }
	 * 
	 * @param input The JSON that needs to be transformed to the structure, as specified above
	 * @return JSONObject The transformed JSON
	 */
	default JSONObject transformSpeeding(JSONObject input) {
		return null;
	}
	
}

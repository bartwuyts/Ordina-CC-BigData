package be.timysewyn.integration.transformer;

import org.json.JSONArray;
import org.json.JSONObject;

public class AntwerpTransformer implements ITransformer {

	/**
	 * INPUT
	 * 	"statistischesector" : [
	 * 		{
	 * 			"seccode": String,
	 * 			"secnaam": String,
	 * 			"nisgemeente": String,
	 * 			"nisdeelgemeente": String,
	 * 			"niscode": String,
	 * 			"oppervlakte": String,
	 * 			"geometry": String (based on GeoJSON, see below),
	 * 			"shape_length": String,
	 * 			"shape_area": String
	 * 		}
	 * 	]
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject transformStatSect(JSONObject input) {
		JSONArray stat_sect = input.optJSONArray("statistischesector");
		
		if(stat_sect == null) {
			return null;
		}

		JSONObject output = new JSONObject();
		
		for(int i = 0 ; i < stat_sect.length(); ++i) {
			JSONObject sector = stat_sect.getJSONObject(i);
			
			JSONObject entry = new JSONObject();
			entry.put("code", sector.getString("seccode"));
			entry.put("name", sector.getString("secnaam"));
			entry.put("geometry", new JSONObject(sector.getString("geometry")));
			
			output.append("sectors", entry);
		}

		return output;
	}

	/**
	 * INPUT
	 * 	"snelheid" : [
	 * 		{
	 * 			"straatcode": String,
	 * 			"straatnaam": String,
	 * 			"postcode_rechts": String,
	 * 			"postcode_links": String,
	 * 			"district": String,
	 * 			"object_sleutel": String,
	 * 			"wegnummer": String,
	 * 			"wegbevoegdheid": String,
	 * 			"snelheid": String,
	 * 			"geometry": String (based on GeoJSON, see below),
	 * 			"objectid": String,
	 * 			"shape_length": String
	 * 		}
	 * 	]
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject transformSpeeding(JSONObject input) {
		JSONArray speedings = input.optJSONArray("snelheid");
		
		if(speedings == null) {
			return null;
		}
		
		JSONObject output = new JSONObject();
		for(int i = 0 ; i < speedings.length(); ++i) {
			JSONObject street = speedings.getJSONObject(i);
			
			JSONObject entry = new JSONObject();
			entry.put("street_name", street.getString("straatnaam"));
			int allowedVelocity = 0;
			
			try {
				allowedVelocity = Integer.parseInt(street.getString("snelheid"));
			} catch (NumberFormatException e) {}
			
			entry.put("allowed_velocity", allowedVelocity);
			entry.put("geometry", new JSONObject(street.getString("geometry")));
			
			output.append("speedings", entry);
		}
		
		return output;
	}
	
}

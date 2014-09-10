package be.timysewyn.integration.transformer;

import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

public class GhentTransformer implements ITransformer {

	/**
	 * INPUT
	 * 	"StatistischeSectoren" : [
	 * 		{
	 * 			"coords": String, based on KML: "x,y,z x,y,z x,y,z |x,y,z x,y,z",
	 * 			"id": String,
	 * 			"fid": String,
	 * 			"statsec_id": String,
	 * 			"sectorcode": String,
	 * 			"x_coord": String,
	 * 			"y_coord": String,
	 * 			"dienstence": String,
	 * 			"sectornaam": String,
	 * 			"stadcode": String,
	 * 			"wijknr": String,
	 * 			"objectid": String,
	 * 			"area": String,
	 * 			"len": String
	 * 		}
	 * 	]
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public JSONObject transformStatSect(JSONObject input) {
		JSONArray stat_sect = input.optJSONArray("StatistischeSectoren");
		
		if(stat_sect == null) {
			return null;
		}
		
		JSONObject output = new JSONObject();
		
		for(int i = 0 ; i < stat_sect.length(); ++i) {
			JSONObject sector = stat_sect.getJSONObject(i);
			
			JSONObject stat_sect_entry = new JSONObject();
			stat_sect_entry.put("code", sector.getString("sectorcode"));
			stat_sect_entry.put("name", sector.getString("sectornaam"));
			
			JSONObject geometry = new JSONObject();
			if(sector.getString("coords").contains("|")) {
				geometry.put("type", "MultiPolygon");
				geometry.append("coordinates", getMultiPolygon(sector.getString("coords")));
			} else {
				geometry.put("type", "Polygon");
				geometry.append("coordinates", getPolygon(sector.getString("coords")));
			}

			stat_sect_entry.put("geometry", geometry);
			
			output.append("sectors", stat_sect_entry);
		}

		return output;
	}
	
	private JSONArray getMultiPolygon(String multiCoordsString) {
		JSONArray multiPolygon = new JSONArray();
		
		for(String coordsString : multiCoordsString.split("\\|")) {
			multiPolygon.put(getPolygon(coordsString));
		}
		
		return multiPolygon;
	}
	
	private JSONArray getPolygon(String coordsString) {
		JSONArray polygon = new JSONArray();
		
		for(String coordString : coordsString.split("\\s+")) {
			String[] splitCoordString = coordString.split(",");
			double x = Double.parseDouble(splitCoordString[0]);
			double y = Double.parseDouble(splitCoordString[1]);
			polygon.put(new JSONArray(Arrays.asList(x, y)));
		}
		
		return polygon;
	}
    
}

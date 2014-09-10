package be.timysewyn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import be.timysewyn.service.SectorService;
import be.timysewyn.service.SpeedingService;

@RestController
@RequestMapping("/map")
public class MapController {
	
	@Autowired
	private SectorService sectorService;
	
	@Autowired
	private SpeedingService speedingService;

    @RequestMapping(value = "/sectors", method = RequestMethod.GET)
    public String getSectors() {
    	return sectorService.getSectors().toString();
    }

    @RequestMapping(value = "/sectors/{city}", method = RequestMethod.GET)
    public String getSectors(@PathVariable String city) {
    	return sectorService.getSectors(city).toString();
    }

    @RequestMapping(value = "/speedings", method = RequestMethod.GET)
    public String getSpeedings() {
    	return speedingService.getSpeedings().toString();
    }

    @RequestMapping(value = "/speedings/{city}", method = RequestMethod.GET)
    public String getSpeedings(@PathVariable String city) {
    	return speedingService.getSpeedings(city).toString();
    }
}
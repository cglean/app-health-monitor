package com.nbcu.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nbcu.service.ApplicationHealthService;
import com.nbcu.vo.CFApplication;

@RestController
public class MonitorAppHealthController {

	// "http://localhost:8080/monitor/getResourceDetailsSummary";
	// TODO :: Need to fetch this from Eureka Server Client Id by just giving
	// application name
	// private static final String INSTANCE_METRICS_URL =
	// "http://chargeback-api.cfapps.io/metrics/getInstanceMetrics";
	// private static final String FREERESOURRCE_URL =
	// "http://chargeback-api.cfapps.io/metrics/getFreeResource";
	// private static final String SPACELIST_URL =
	// "http://chargeback-api.cfapps.io/metrics/getSpaceList";

	@Autowired
	private ApplicationHealthService applicationHealthService;

	@RequestMapping(value = "/getFoundationsList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getFoundationNames() {
		return applicationHealthService.getFoundations();
	}

	@RequestMapping(value = "/getOrgList/{foundationName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getOrganizationNames(@PathVariable String foundationName) {
		return applicationHealthService.getOrganizationsForFoundation(foundationName);
	}

	@RequestMapping(value = "/getSpaceList/{foundationName:.+}/{orgName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getApplicationSpaceByOrg(@PathVariable String foundationName, @PathVariable String orgName) {
		return applicationHealthService.getSpacesByOrg(foundationName, orgName);
	}

	@RequestMapping(value = "/getApplicationListByOrgSpace/{foundationName:.+}/{orgName:.+}/{spaceName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getApplicationListByOrgSpace(@PathVariable String foundationName, @PathVariable String orgName,
			@PathVariable String spaceName) {
		return applicationHealthService.getApplicationsByOrgSpace(foundationName, orgName, spaceName);
	}

	@RequestMapping(value = "/getApplicationDetails/{foundationName:.+}/{orgName:.+}/{spaceName:.+}/{appName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public CFApplication getApplicationDetails(@PathVariable String foundationName, @PathVariable String orgName,
			@PathVariable String spaceName, @PathVariable String appName) {
		return applicationHealthService.getApplicationDetails(foundationName, orgName, spaceName, appName);
	}

	@RequestMapping(value = "/downloadInventory/{foundationName:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String downloadInventory(HttpServletResponse response, @PathVariable String foundationName) {
	  String responseString = applicationHealthService.downloadInventory(response, foundationName);
	  return responseString;
	}
}

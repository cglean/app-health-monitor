package com.nbcu.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.nbcu.vo.CFApplication;

public interface ApplicationHealthService {
	public List<String> getOrganizationsForFoundation(String foundationName);
	public List<String> getSpacesByOrg(String foundationName,String orgName);
	public CFApplication getApplicationDetails(String foundationName,String orgName, String spaceName, String appName);
	public List<String> getFoundations();
	public List<String> getApplicationsByOrgSpace(String foundationName, String orgName, String spaceName);
	public String downloadInventory(HttpServletResponse response,String foundationName);
}

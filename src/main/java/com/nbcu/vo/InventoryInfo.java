package com.nbcu.vo;

public class InventoryInfo {
	private String organization;
	private String space;
	private String application;
	private String buildpack;
	private int noOfInstances;
	private String state;
	
	public InventoryInfo(String organization, String space, String application, String buildpack, int noOfInstances, String state) {
		super();
		this.organization = organization;
		this.space = space;
		this.application = application;
		this.buildpack = buildpack;
		this.noOfInstances = noOfInstances;
		this.state = state;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getSpace() {
		return space;
	}
	public void setSpace(String space) {
		this.space = space;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getBuildpack() {
		return buildpack;
	}
	public void setBuildpack(String buildpack) {
		this.buildpack = buildpack;
	}
	public int getNoOfInstances() {
		return noOfInstances;
	}
	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

}

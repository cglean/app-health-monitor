package com.nbcu.vo;

import java.util.List;

public class CFApplication {
	private String appName;
	private int noOfInstances;
	private int noOfRunningInstances;
	private String buildpackName;
	private String appState;
	private List<AppInstance> appInstances;

	public CFApplication(String appName, String appState, int noOfInstances, int noOfRunningInstances, String buildpackName, List<AppInstance> appInstances) {
		super();
		this.appName = appName;
		this.noOfInstances = noOfInstances;
		this.noOfRunningInstances = noOfRunningInstances;
		this.buildpackName = buildpackName;
		this.appState = appState;
		this.appInstances = appInstances;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppState() {
		return appState;
	}

	public void setAppState(String appState) {
		this.appState = appState;
	}

	public int getNoOfInstances() {
		return noOfInstances;
	}

	public int getNoOfRunningInstances() {
		return noOfRunningInstances;
	}

	public void setNoOfRunningInstances(int noOfRunningInstances) {
		this.noOfRunningInstances = noOfRunningInstances;
	}

	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}

	public String getBuildpackName() {
		return buildpackName;
	}

	public void setBuildpackName(String buildpackName) {
		this.buildpackName = buildpackName;
	}

	public List<AppInstance> getAppInstances() {
		return appInstances;
	}

	public void setAppInstances(List<AppInstance> appInstances) {
		this.appInstances = appInstances;
	}
}

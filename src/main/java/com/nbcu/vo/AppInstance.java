package com.nbcu.vo;

public class AppInstance {
	private String instanceId;
	private String state;
	private String cpu;
	private String disk;
	private String memory;
	private String upTime;

	public AppInstance(String instanceId, String state, String cpu, String disk, String memory, String upTime) {
		super();
		this.instanceId = instanceId;
		this.disk = disk;
		this.memory = memory;
		this.state = state;
		this.upTime = upTime;
		this.cpu = cpu;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getDisk() {
		return disk;
	}

	public void setDisk(String disk) {
		this.disk = disk;
	}

	public String getMemory() {
		return memory;
	}

	public void setMemory(String memory) {
		this.memory = memory;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getUpTime() {
		return upTime;
	}

	public void setUpTime(String upTime) {
		this.upTime = upTime;
	}
}

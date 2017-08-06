package com.nbcu.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
public class FoundationAPI {
	
	private Map<String, List<String>> foundationAPI = new HashMap<>();
	private String proxyHost;
	private int proxyPort;
	private String clientId;
	public String getProxyHost() {
		return proxyHost;
	}

	public void setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public void setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	private String secret;
	
	public Map<String, List<String>> getFoundationAPI() {
		return foundationAPI;
	}

	public void setFoundationAPI(Map<String, List<String>> foundationAPI) {
		this.foundationAPI = foundationAPI;
	}
}

package com.inbcu.vo;

public class APIInfo {
private String apiURL;
private String userName;
public APIInfo(String apiURL, String userName, String password) {
	super();
	this.apiURL = apiURL;
	this.userName = userName;
	this.password = password;
}
public String getApiURL() {
	return apiURL;
}
public void setApiURL(String apiURL) {
	this.apiURL = apiURL;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public String getPassword() {
	return password;
}
public void setPassword(String password) {
	this.password = password;
}
private String password;
}

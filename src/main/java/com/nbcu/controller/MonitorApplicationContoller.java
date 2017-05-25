package com.nbcu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MonitorApplicationContoller {

	/**
	 * This method returns the main HTML page
	 * 
	 * @return This returns the view name
	 */
	@RequestMapping("/")
	public String viewHomePage() {
		return "homePage.html";
	}
}

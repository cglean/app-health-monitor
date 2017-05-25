package com.nbcu.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.InstanceStats;
import org.springframework.stereotype.Service;

import com.inbcu.vo.APIInfo;
import com.inbcu.vo.AppInstance;
import com.inbcu.vo.CFApplication;
import com.inbcu.vo.InventoryInfo;
import com.nbcu.service.ApplicationHealthService;

@Service
public class ApplicationHealthServiceImpl implements ApplicationHealthService {
	private CloudFoundryClient client;
	private CloudCredentials credentials;
	private Map<String, APIInfo> foundationInfoMap = new HashMap<String, APIInfo>();
	private APIInfo apiInfo = null;

	public ApplicationHealthServiceImpl() {
		foundationInfoMap.put("DEVAOA", new APIInfo("http://api.devsysaoa.inbcu.com", "admin", "a3c3eceb3bacc873552c"));

		// foundationInfoMap.put("DEVAOA", new
		// APIInfo("http://api.devsysaoa.inbcu.com", "appmonitor",
		// "appmonitor"));

		foundationInfoMap.put("AOA", new APIInfo("http://api.sysaoa.inbcu.com", "admin", "bf245752689e7396e530"));
		foundationInfoMap.put("DEVASH", new APIInfo("http://api.devsysash.inbcu.com", "admin", "d274aa9301df4eaee54b"));
		foundationInfoMap.put("ASH", new APIInfo("http://api.sysash.inbcu.com", "admin", "e6225e5dd57e3cf06a51"));
		foundationInfoMap.put("DEVUSH",
				new APIInfo("http://api.devsysush.inbcu.com", "admin", "vOscyvHHE3dkx-XWL4HNLsv1GoI9cKCm"));
		foundationInfoMap.put("USH",
				new APIInfo("http://api.sysush.inbcu.com", "admin", "NN7awZXNF2-HGmztvk1Kqw_IXvVLpFnn"));
		/*
		 * foundationInfoMap.put("DEVUSZ", new
		 * APIInfo("http://api.devsysusz.nbcuni.com", "admin",
		 * "a86b7a7e88a84599b75d")); foundationInfoMap.put("USZ", new
		 * APIInfo("http://api.sysusz.nbcuni.com", "admin",
		 * "a467f90a3e44969dc703")); foundationInfoMap.put("DEVASZ", new
		 * APIInfo("http://api.devsysasz.nbcuni.com", "admin",
		 * "X9DKRYOJZV47MjIn5kRccj1m_1K6tUdc")); foundationInfoMap.put("ASZ",
		 * new APIInfo("http://api.sysasz.nbcuni.com", "admin",
		 * "tVqVUbMvPNdPwS-lvjQObApRPNXgGR4g"));
		 */
	}

	@Override
	public List<String> getFoundations() {
		return new ArrayList<>(foundationInfoMap.keySet());
	}

	@Override
	public List<String> getOrganizationsForFoundation(String foundationName) {
		client = loginCloudFoundry(foundationName);
		client.login();
		return client.getOrganizations().stream().map(org -> org.getName()).collect(Collectors.toList());
	}

	@Override
	public List<String> getSpacesByOrg(String foundation, String orgName) {

		client = loginCloudFoundry(foundation);
		client.login();
		return client.getSpaces().stream().filter(cloudspace -> cloudspace.getOrganization().getName().equals(orgName))
				.map(cloudspace -> cloudspace.getName()).collect(Collectors.toList());
	}

	@Override
	public List<String> getApplicationsByOrgSpace(String foundationName, String orgName, String spaceName) {
		client = loginCloudFoundrywithOrgAndSpace(foundationName, orgName, spaceName);
		client.login();
		return client.getApplications().stream().map(cloudspace -> cloudspace.getName()).collect(Collectors.toList());
	}

	@Override
	public CFApplication getApplicationDetails(String foundationName, String orgName, String spaceName,
			String appName) {
		client = loginCloudFoundrywithOrgAndSpace(foundationName, orgName, spaceName);
		client.login();
		List<AppInstance> appInstancesList = new ArrayList<AppInstance>();
		AppInstance appInstance;
		CloudApplication cloudApp = client.getApplication(appName);
		List<InstanceStats> instanceStatsList = client.getApplicationStats(appName).getRecords();
		int days;
		int hours;
		int minutes;
		for (InstanceStats instanceStats : instanceStatsList) {
			Double upTime = instanceStats.getUptime();
			days = (int) (upTime / 86400);
			hours = (int) ((upTime - (days * 86400)) / 3600);
			minutes = (int) ((upTime - (days * 86400) - (hours * 3600)) / 60);
			//System.out.println("Memory for " + appName + " is " + instanceStats.getUsage().getMem());
			float memory = ((float) (instanceStats.getUsage().getMem()) / (1024 * 1024)) >= 1024
					? ((float) (instanceStats.getUsage().getMem())) / (1024 * 1024 * 1024)
					: ((float) (instanceStats.getUsage().getMem())) / (1024 * 1024);
			appInstance = new AppInstance(instanceStats.getId(), instanceStats.getState().name(),
					String.valueOf((int) instanceStats.getUsage().getCpu()) + "%",
					String.valueOf(((instanceStats.getUsage().getDisk())) / (1024 * 1024)) + "MB",
					String.valueOf(round(memory, 2)) + "GB",
					days + " Days " + hours + " Hours " + minutes + " Minutes");
			appInstancesList.add(appInstance);
			appInstance = null;
		}
		CFApplication cfApp = new CFApplication(appName, cloudApp.getState().name(), cloudApp.getInstances(),
				cloudApp.getRunningInstances(), cloudApp.getStaging().getBuildpackUrl(), appInstancesList);
		return cfApp;
	}

	private CloudFoundryClient loginCloudFoundry(String foundationName) {
		apiInfo = foundationInfoMap.get(foundationName);
		credentials = new CloudCredentials(apiInfo.getUserName(), apiInfo.getPassword());
		/*
		 * if (foundationName.contains("ASZ")) { HttpProxyConfiguration
		 * httpProxyConfiguration = new
		 * HttpProxyConfiguration("http://10.40.102.22", 80, false, null, null);
		 * client = new CloudFoundryClient(credentials,
		 * getTargetURL(apiInfo.getApiURL()), null, httpProxyConfiguration,
		 * true); return client; }
		 */

		client = new CloudFoundryClient(credentials, getTargetURL(apiInfo.getApiURL()), null,
				(HttpProxyConfiguration) null, true);
		return client;
	}

	private CloudFoundryClient loginCloudFoundrywithOrgAndSpace(String foundationName, String orgName,
			String spaceName) {
		credentials = new CloudCredentials(apiInfo.getUserName(), apiInfo.getPassword());
		client = new CloudFoundryClient(credentials, getTargetURL(apiInfo.getApiURL()), orgName, spaceName, true);
		return client;
	}

	private URL getTargetURL(String target) {
		try {
			URL apiEndpoint = URI.create(target).toURL();
			return apiEndpoint;
		} catch (MalformedURLException e) {
			throw new RuntimeException("The target URL is not valid: " + e.getMessage());
		}
	}

	private float round(float d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(Float.toString(d));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.floatValue();
	}

	@Override
	public String downloadInventory(HttpServletResponse response, String foundationName) {
		List<String> organizationsList = getOrganizationsForFoundation(foundationName);
		List<String> spacesList;
		List<String> appsList;
		CFApplication appDetails;
		InventoryInfo inventoryInfo;
		List<InventoryInfo> inventoryInfoList = new ArrayList<InventoryInfo>();
		for (String organization : organizationsList) {
			//System.out.println("Gathering info for " + organization);
			spacesList = getSpacesByOrg(foundationName, organization);
			for (String space : spacesList) {
				appsList = getApplicationsByOrgSpace(foundationName, organization, space);
				for (String app : appsList) {
					//System.out.println("Gathering info for " +app);
					appDetails = getApplicationDetails(foundationName, organization, space, app);
					if(appDetails.getNoOfRunningInstances() !=0){
					inventoryInfo = new InventoryInfo(organization, space, app, appDetails.getBuildpackName(),
							appDetails.getNoOfRunningInstances(), "RUNNING");
					inventoryInfoList.add(inventoryInfo);
					}
					if (appDetails.getNoOfInstances() > appDetails.getNoOfRunningInstances()) {
						inventoryInfo = new InventoryInfo(organization, space, app, appDetails.getBuildpackName(),
								(appDetails.getNoOfInstances() - appDetails.getNoOfRunningInstances()), "STOPPED");
						inventoryInfoList.add(inventoryInfo);
					}
				}
			}
		}
		return downloadInventoyInfoAsExcel(foundationName, inventoryInfoList, response);
	}

	private String downloadInventoyInfoAsExcel(String foundationName, List<InventoryInfo> inventoryInfoList,
			HttpServletResponse response) {
		try {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("InventoryInfo_" + foundationName);
			HSSFRow rowhead = sheet.createRow(0);
			rowhead.createCell(0).setCellValue("Organization");
			rowhead.createCell(1).setCellValue("Space");
			rowhead.createCell(2).setCellValue("Application");
			rowhead.createCell(3).setCellValue("Buildpack");
			rowhead.createCell(4).setCellValue("State");
			rowhead.createCell(5).setCellValue("# Of Instances");

			HSSFRow row;
			int rowNumber = 1;
			for (InventoryInfo inventoryInfo : inventoryInfoList) {
				row = sheet.createRow(rowNumber);
				rowNumber++;
				row.createCell(0).setCellValue(inventoryInfo.getOrganization());
				row.createCell(1).setCellValue(inventoryInfo.getSpace());
				row.createCell(2).setCellValue(inventoryInfo.getApplication());
				row.createCell(3).setCellValue(inventoryInfo.getBuildpack());
				row.createCell(4).setCellValue(inventoryInfo.getState());
				row.createCell(5).setCellValue(inventoryInfo.getNoOfInstances());
			}
			System.out.println(sheet.getLastRowNum() + "\n writing to file");
			// Write to a file and download
			String fileName = "C:/" + "InventoryInfo_" + foundationName + ".xls";
			FileOutputStream fileOut = new FileOutputStream(fileName);
			workbook.write(fileOut);
			workbook.close();

			fileOut.close();
			System.out.println("Your excel file has been generated!");

			// Code to download
			File fileToDownload = new File(fileName);
			InputStream in = new FileInputStream(fileToDownload);

			// Gets MIME type of the file
			String mimeType = new MimetypesFileTypeMap().getContentType(fileName);

			if (mimeType == null) {
				// Set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}
			System.out.println("MIME type: " + mimeType);

			// Modifies response
			response.setContentType(mimeType);
			response.setContentLength((int) fileToDownload.length());

			// Forces download
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", fileToDownload.getName());
			response.setHeader(headerKey, headerValue);
			response.setStatus(200);

			// obtains response's output stream
			OutputStream outStream = response.getOutputStream();

			byte[] buffer = new byte[4096];
			int bytesRead = -1;

			while ((bytesRead = in.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}
			in.close();
			outStream.close();

			System.out.println("File downloaded successfully");
			return "Success";
		} catch (Exception ex) {
			return "null";
		}

	}
}

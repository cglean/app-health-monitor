package com.nbcu.service.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.cloudfoundry.client.lib.CloudCredentials;
import org.cloudfoundry.client.lib.CloudFoundryClient;
import org.cloudfoundry.client.lib.HttpProxyConfiguration;
import org.cloudfoundry.client.lib.domain.CloudApplication;
import org.cloudfoundry.client.lib.domain.InstanceStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nbcu.service.ApplicationHealthService;
import com.nbcu.vo.AppInstance;
import com.nbcu.vo.CFApplication;
import com.nbcu.vo.FoundationAPI;
import com.nbcu.vo.InventoryInfo;

@Service
public class ApplicationHealthServiceImpl implements ApplicationHealthService {
	private CloudFoundryClient client;

	@Autowired
	private FoundationAPI foundationAPI;
	CFApplication appDetails;
	InventoryInfo inventoryInfo;
	List<String> spacesList;
	List<String> appsList;
	HttpProxyConfiguration httpProxyConfiguration;
	CloudCredentials credentials;
	List<String> foundationproperties;

	@Override
	public List<String> getFoundations() {
		httpProxyConfiguration = new HttpProxyConfiguration(foundationAPI.getProxyHost(), foundationAPI.getProxyPort());
		return new ArrayList<>(foundationAPI.getFoundationAPI().keySet());
	}

	@Override
	public List<String> getOrganizationsForFoundation(String foundationName) {
		System.out.println(foundationAPI.getFoundationAPI().size());
		foundationproperties = foundationAPI.getFoundationAPI().get(foundationName);
		credentials = new CloudCredentials(foundationproperties.get(1), foundationproperties.get(2));
		client = loginCloudFoundry(foundationproperties.get(0));
		client.login();
		return client.getOrganizations().stream().map(org -> org.getName()).collect(Collectors.toList());
	}

	@Override
	public List<String> getSpacesByOrg(String foundation, String orgName) {
		return client.getSpaces().stream().filter(cloudspace -> cloudspace.getOrganization().getName().equals(orgName))
				.map(cloudspace -> cloudspace.getName()).collect(Collectors.toList());
	}

	@Override
	public List<String> getApplicationsByOrgSpace(String foundationName, String orgName, String spaceName) {
		client = loginCloudFoundrywithOrgAndSpace(foundationproperties.get(0), orgName, spaceName);
		client.login();
		return client.getApplications().stream().map(cloudspace -> cloudspace.getName()).collect(Collectors.toList());
	}

	@Override
	public CFApplication getApplicationDetails(String foundationName, String orgName, String spaceName,
			String appName) {
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
			String memory = ((float) (instanceStats.getUsage().getMem()) / 1024) > 1024
					? (((float) (instanceStats.getUsage().getMem()) / (1024 * 1024)) >= 1024 ? String.valueOf(
							round(((float) (instanceStats.getUsage().getMem())) / (1024 * 1024 * 1024), 2) + " GB")
							: String.valueOf(
									round(((float) (instanceStats.getUsage().getMem())) / (1024 * 1024), 2) + " MB"))
					: String.valueOf(round(((float) (instanceStats.getUsage().getMem())) / (1024), 2) + " MB");
			memory = instanceStats.getUsage().getMem() == 0 ? "0 Bytes" : memory;
			appInstance = new AppInstance(instanceStats.getId(), instanceStats.getState().name(), String.valueOf(
					(int) (((instanceStats.getUsage().getCpu() * 1024 * 1024 * 1024) / instanceStats.getDiskQuota())
							* 100))+ "%",
					instanceStats.getUsage().getDisk() == 0 ? "0 Bytes"
							: String.valueOf(round(((float) (instanceStats.getUsage().getDisk())) / (1024 * 1024), 2))+ "MB",
					memory, days + " Days " + hours + " Hours " + minutes + " Minutes");
			appInstancesList.add(appInstance);
			appInstance = null;
		}
		CFApplication cfApp = new CFApplication(appName, cloudApp.getState().name(), cloudApp.getInstances(),
				cloudApp.getRunningInstances(), cloudApp.getStaging().getBuildpackUrl(), appInstancesList);
		return cfApp;
	}

	private CloudFoundryClient loginCloudFoundry(String foundationURL) {
		// Login to USZ/ASZ foundations
		if (foundationURL.contains("usz") || foundationURL.contains("asz")) {
			client = new CloudFoundryClient(credentials, getTargetURL(foundationURL), httpProxyConfiguration);
			return client;
		}
		client = new CloudFoundryClient(credentials, getTargetURL(foundationURL), true);
		return client;
	}

	private CloudFoundryClient loginCloudFoundrywithOrgAndSpace(String foundationURL, String orgName,
			String spaceName) {
		if (foundationURL.contains("usz") || foundationURL.contains("asz")) {
			client = new CloudFoundryClient(credentials, getTargetURL(foundationURL), orgName, spaceName,
					httpProxyConfiguration);
			return client;
		}
		client = new CloudFoundryClient(credentials, getTargetURL(foundationURL), orgName, spaceName, true);
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
		List<InventoryInfo> inventoryInfoList = new ArrayList<InventoryInfo>();
		organizationsList.forEach(organization -> {
			spacesList = getSpacesByOrg(foundationName, organization);
			getSpacesByOrg(foundationName, organization).forEach(space -> {
				appsList = getApplicationsByOrgSpace(foundationName, organization, space);
				getApplicationsByOrgSpace(foundationName, organization, space).forEach(app -> {
					appDetails = getApplicationDetails(foundationName, organization, space, app);
					if (appDetails.getNoOfRunningInstances() != 0) {
						inventoryInfo = new InventoryInfo(organization, space, app, appDetails.getBuildpackName(),
								appDetails.getNoOfRunningInstances(), "RUNNING");
						inventoryInfoList.add(inventoryInfo);
					}
					if (appDetails.getNoOfInstances() > appDetails.getNoOfRunningInstances()) {
						inventoryInfo = new InventoryInfo(organization, space, app, appDetails.getBuildpackName(),
								(appDetails.getNoOfInstances() - appDetails.getNoOfRunningInstances()), "STOPPED");
						inventoryInfoList.add(inventoryInfo);
					}
				});
			});
		});
		 return downloadInventoyInfoAsExcel(foundationName, inventoryInfoList, response);
	}

	private String downloadInventoyInfoAsExcel(String foundationName, List<InventoryInfo> inventoryInfoList,
			HttpServletResponse response) {
		try {
			HSSFWorkbook  workbook = new HSSFWorkbook ();
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
			String fileName = "InventoryInfo_" + foundationName
					+ ".xls";
			//FileOutputStream fileOut = new FileOutputStream(fileName);
			//workbook.write(fileOut);
			//fileOut.close();
			//((Closeable) workbook).close();

			//fileOut.close();
			System.out.println("Your excel file has been generated!");

			// Code to download
			//File fileToDownload = new File(fileName);
			//InputStream in = new FileInputStream(fileToDownload);

			// Gets MIME type of the file
			/*String mimeType = new MimetypesFileTypeMap().getContentType(fileName);

			if (mimeType == null) {
				// Set to binary type if MIME mapping not found
				mimeType = "application/octet-stream";
			}
			System.out.println("MIME type: " + mimeType);*/
			ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
			workbook.write(outByteStream);
			byte [] outArray = outByteStream.toByteArray();
			// Modifies response
			//response.setContentType(mimeType);
			response.setContentLength(outArray.length);

			// Forces download
			/*String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", fileName);
			response.setHeader(headerKey, headerValue);*/
			response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", fileName));
			response.setStatus(200);
			response.setContentType("application/vnd.ms-excel");

			// obtains response's output stream
			//ServletOutputStream outStream = response.getOutputStream();

			/*byte[] buffer = new byte[4096];
			int bytesRead = -1;*/
			workbook.write(response.getOutputStream());

			/*while ((bytesRead = in.read(buffer)) != -1) {
				outStream.write(buffer, 0, bytesRead);
			}*/
			//in.close();
			//outStream.flush();
			//outStream.close();
			workbook.close();

			System.out.println("File downloaded successfully");
			return "Success";
		} catch (Exception ex) {
			return null;
		}

	}
}

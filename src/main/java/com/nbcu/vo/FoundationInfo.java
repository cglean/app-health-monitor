package com.nbcu.vo;

import java.util.HashMap;
import java.util.Map;

public class FoundationInfo {

	private Map<String, APIInfo> foundationInfoMap = new HashMap<String, APIInfo>();
	public FoundationInfo() {
		
		foundationInfoMap.put("DEVAOA", new APIInfo("http://api.devsysaoa.inbcu.com", "admin", "a3c3eceb3bacc873552c"));
		foundationInfoMap.put("AOA", new APIInfo("http://api.sysaoa.inbcu.com", "admin", "bf245752689e7396e530"));
		foundationInfoMap.put("DEVASH", new APIInfo("http://api.devsysash.inbcu.com", "admin", "d274aa9301df4eaee54b"));
		foundationInfoMap.put("ASH", new APIInfo("http://api.sysash.inbcu.com", "admin", "e6225e5dd57e3cf06a51"));
		foundationInfoMap.put("DEVUSH",
				new APIInfo("http://api.devsysush.inbcu.com", "admin", "vOscyvHHE3dkx-XWL4HNLsv1GoI9cKCm"));
		foundationInfoMap.put("USH",
				new APIInfo("http://api.sysush.inbcu.com", "admin", "NN7awZXNF2-HGmztvk1Kqw_IXvVLpFnn"));
		//Lab ASH Environment
		foundationInfoMap.put("LABASH",
						new APIInfo("http://api.labsysash.inbcu.com", "admin", "OFyeLJ5ab4xLrRnb810fvW-ULaJ4T62o"));
		//DMZ Environments
		foundationInfoMap.put("DEVUSZ", new APIInfo("https://api.devsysusz.nbcuni.com", "admin", "a86b7a7e88a84599b75d"));
		foundationInfoMap.put("USZ", new APIInfo("https://api.sysusz.nbcuni.com", "admin", "a467f90a3e44969dc703"));
		foundationInfoMap.put("DEVASZ",
				new APIInfo("https://api.devsysasz.dmz.tfayd.com", "admin", "X9DKRYOJZV47MjIn5kRccj1m_1K6tUdc"));
		foundationInfoMap.put("ASZ",
				new APIInfo("https://api.sysasz.dmz.tfayd.com", "admin", "tVqVUbMvPNdPwS-lvjQObApRPNXgGR4g"));

	}

	public Map<String, APIInfo> getFoundationInfoMap() {
		return foundationInfoMap;
	}

	public void setFoundationInfoMap(Map<String, APIInfo> foundationInfoMap) {
		this.foundationInfoMap = foundationInfoMap;
	}
}

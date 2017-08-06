/**
 * 
 */
var getFoundationsList = function() {
	//$("body").fadeOut();
	$("#ajaxLoader").css("display", "block");
	$("#overlay").show();
	$.ajax({
		url : "getFoundationsList",
		success : function(data) {
			if (data.length === 0) {
				$("#FoundationSelect").append(
						"<option>" + "No Foundations Available" + "</option>");
				$("#overlay").hide();
				$("#ajaxLoader").css("display", "none");
				$("#overlay").css("display", "none");;				
			} else {
				populateFoundationDropDown(data);
			}
		}
	});
}

var populateFoundationDropDown = function(vals) {
	$("#FoundationSelect").empty();
	$.each(vals, function(index, value) {
		$("#FoundationSelect").append("<option>" + value + "</option>");
	});
	$("#FoundationSelect").unbind().change(function() {
						$("#ajaxLoader").css("display", "block");
						$.ajax({url : "getOrgList/"+ $("#FoundationSelect option:selected").text(), success : function(data) {
										if (data.length === 0) {
											resetFields();
											resetApplicationInfo();
											$("#ajaxLoader").css("display","none");
											$("#overlay").css("display", "none");;
										} else {
											populateOrgDropDown(data);
										}
									}
								});
					});
	if ($("#FoundationSelect option:selected").text() === ""
			|| $("#FoundationSelect option:selected").text() === undefined
			|| $("#FoundationSelect option:selected").text() === null
			|| $("#FoundationSelect option:selected").text() === "No Foundations Available") {
				resetFields();
				resetApplicationInfo();
				return;
	} else {
		$("#overlay").show();
		$("#ajaxLoader").css("display", "block");
		$
				.ajax({
					url : "getOrgList/"
							+ $("#FoundationSelect option:selected").text(),
					success : function(data) {
						if (data.length === 0) {
							resetFields();
							resetApplicationInfo();
							$("#ajaxLoader").css("display", "none");
							$("#overlay").css("display", "none");;
						} else {
							populateOrgDropDown(data);
						}
					}
				});
	}
}

var getOrganisations = function() {
	$.ajax({
		url : "getOrgList",
		success : function(data) {
			if (data.length === 0) {
				resetFields();
				resetApplicationInfo();
				$("#ajaxLoader").css("display", "none");
				$("#overlay").css("display", "none");;
			} else {
				populateOrgDropDown(data);
			}
		}
	});
}
var populateOrgDropDown = function(vals) {
	$("#OrgSelect").empty();
	$("#OrgSelect").append("<option>All</option>");
	$.each(vals, function(index, value) {
		$("#OrgSelect").append("<option>" + value + "</option>");
	});
	$("#OrgSelect")
			.unbind()
			.change(
					function() {
						if ($("#OrgSelect option:selected").text() === "All") {
							$("#OrgSpace").empty();
							$("#OrgSpace").append("<option>"+ "Select a Space" + "</option>");
							$("#Application").empty();
							$("#Application").append("<option>"	+ "Select an Application"	+ "</option>");
							resetApplicationInfo();
							$('#applicationContainerHealthTable tbody').empty();
							$("#downloadInventory").css("display", "block");
							$("#overlay").css("display", "none");;
							$("#ajaxLoader").css("display", "none");
							return;
						} else {
							$("#downloadInventory").css("display", "none");
							$("#overlay").show();
							$("#ajaxLoader").css("display", "block");
							$.ajax({url : "getSpaceList/"+ $("#FoundationSelect option:selected").text()+ "/"+ $("#OrgSelect option:selected").text(),
										success : function(data) {
											if (data.length === 0) {
												$("#OrgSpace").empty();
												$("#OrgSpace").append("<option>"+ "No Spaces Available"+ "</option>");
												$("#Application").empty();
												$("#Application").append("<option>"	+ "No Applications Available"	+ "</option>");
												resetApplicationInfo();
												$('#applicationContainerHealthTable tbody').empty();
												$("#ajaxLoader").css("display",
														"none");
											} else {
												populateSpaceDropDown(data);
											}
										}
									});
						}
					});
	if ($("#OrgSelect option:selected").text() === ""
			|| $("#OrgSelect option:selected").text() === undefined
			|| $("#OrgSelect option:selected").text() === null
			|| $("#OrgSelect option:selected").text() === "No Orgs Available") {
		$("#OrgSpace").empty();
		$("#OrgSpace").append("<option>"+ "No Spaces Available" + "</option>");
		$("#Application").empty();
		$("#Application").append("<option>"	+ "No Applications Available"	+ "</option>");
		resetApplicationInfo();
		$('#applicationContainerHealthTable tbody').empty();
		return;
	} else if ($("#OrgSelect option:selected").text() === "All") {
		$('#applicationContainerHealthTable tbody').empty();
		$("#OrgSpace").empty();
		$("#OrgSpace").append("<option>" + "Select a Space" + "</option>");
		$("#Application").empty();
		resetApplicationInfo();
		$("#Application").append("<option>" + "Select an Application" + "</option>");
		$("#downloadInventory").css("display", "block");
		$("#overlay").hide();
		$("#ajaxLoader").css("display", "none");
		return;
	} else {
		$("#downloadInventory").css("display", "none");
		$("#overlay").show();
		$("#ajaxLoader").css("display", "block");
		$.ajax({
			url : "getSpaceList/"+ $("#FoundationSelect option:selected").text() + "/"+ $("#OrgSelect option:selected").text(),
			success : function(data) {
				if (data.length === 0) {
					$("#OrgSpace").empty();
					$("#OrgSpace").append("<option>" + "No Spaces Available"+ "</option>");
					$("#Application").empty();
					$("#Application").append("<option>"	+ "No Applications Available"	+ "</option>");
					resetApplicationInfo();
					$('#applicationContainerHealthTable tbody').empty();
					$("#overlay").hide();
					$("#ajaxLoader").css("display", "none");
				} else {
					populateSpaceDropDown(data);
				}
			}
		});
	}
}
var populateSpaceDropDown = function(vals) {
	$("#OrgSpace").empty();
	$.each(vals, function(index, value) {
		$("#OrgSpace").append("<option>" + value + "</option>");
	});
	$("#OrgSpace")
			.unbind()
			.change(
					function() {
						$("#ajaxLoader").css("display", "block");
						$("#overlay").show();
						$.ajax({url : "getApplicationListByOrgSpace/"+ $("#FoundationSelect option:selected").text()+ "/"+ 
							$("#OrgSelect option:selected").text()+ "/"+ $("#OrgSpace option:selected").text(),
									success : function(data) {
										if (data.length === 0) {
											$("#Application").empty();
											$("#Application").append("<option>"+ "No Applications Available"+ "</option>");
											resetApplicationInfo();
											$('#applicationContainerHealthTable tbody').empty();
											$("#ajaxLoader").css("display",	"none");
											$("#overlay").hide();
										} else {
											populateApplicationDropDown(data);
										}
									}
								});
					});
	if ($("#OrgSpace option:selected").text() === ""
			|| $("#OrgSpace option:selected").text() === undefined
			|| $("#OrgSpace option:selected").text() === null
			|| $("#OrgSpace option:selected").text() === "No Spaces Available") {
			$("#Application").empty();
			$("#Application").append("<option>"+ "No Applications Available"+ "</option>");
			resetApplicationInfo();
			$('#applicationContainerHealthTable tbody').empty();
			$("#ajaxLoader").css("display",	"none");
			$("#overlay").hide();
			return;
	} else {
		$("#ajaxLoader").css("display", "block");
		$("#overlay").show();
		$
				.ajax({
					url : "getApplicationListByOrgSpace/"+ $("#FoundationSelect option:selected").text()+ "/" + $("#OrgSelect option:selected").text()
							+ "/" + $("#OrgSpace option:selected").text(),success : function(data) {
						if (data.length === 0) {
							$("#Application").empty();
							$("#Application").append("<option>"+ "No Applications Available"+ "</option>");
							resetApplicationInfo();
							$('#applicationContainerHealthTable tbody').empty();
							$("#ajaxLoader").css("display", "none");
							$("#overlay").hide();

						} else {
							populateApplicationDropDown(data);
						}
					}
				});
	}
}
var populateApplicationDropDown = function(vals) {
	$("#Application").empty();
	$.each(vals, function(index, value) {
		$("#Application").append("<option>" + value + "</option>");
	});

	$("#Application").unbind().change(
			function() {
				$("#ajaxLoader").css("display", "block");
				$("#overlay").show();
				$.ajax({url : "getApplicationDetails/"+ $("#FoundationSelect option:selected").text()+ "/" + $("#OrgSelect option:selected").text()
							+ "/" + $("#OrgSpace option:selected").text() + "/"+ $("#Application option:selected").text(),
					success : function(data) {
						populateApplicationDetails(data);
						$("#ajaxLoader").css("display", "none");
						$("#overlay").hide();
					}
				});
			});
	if ($("#Application option:selected").text() === ""
			|| $("#Application option:selected").text() === undefined
			|| $("#Application option:selected").text() === null
			|| $("#Application option:selected").text() === "No Applications Available") {
		resetApplicationInfo();
		$('#applicationContainerHealthTable tbody').empty();
		return;
	} else {
		$("#ajaxLoader").css("display", "block");
		$("#overlay").show();
		$.ajax({
			url : "getApplicationDetails/"
					+ $("#FoundationSelect option:selected").text() + "/"
					+ $("#OrgSelect option:selected").text() + "/"
					+ $("#OrgSpace option:selected").text() + "/"
					+ $("#Application option:selected").text(),
			success : function(data) {
				populateApplicationDetails(data);
				$("#ajaxLoader").css("display", "none");	
				$("#overlay").hide();
			}
		});
	}
}

var populateApplicationDetails = function(vals) {
	$("#applicationName").html($("#Application option:selected").text());
	$("#applicationState").html(vals.appState);
	if (vals.buildpackName === null) {
		$("#buildpackName").html("Not Available");
	} else {
		$("#buildpackName").html(vals.buildpackName);
	}
	$("#noOfInstances").html(vals.noOfInstances);
	$("#noOfRunningInstances").html(vals.noOfRunningInstances);
	if (vals.appInstances.length === 0) {
		$('#applicationContainerHealthTable tbody').empty();
	} else {
		var i = 0;
		var appInstances = vals.appInstances;

		$('#applicationContainerHealthTable tbody').empty();

		$.each(appInstances, function(index, value) {
			$("#applicationContainerHealthTable tbody").append(
					"<tr><td>" + value.instanceId + "</td><td>" + value.state
							+ "</td><td>" + value.cpu + "</td><td>"
							+ value.disk + "</td><td>" + value.memory
							+ "</td><td>" + value.upTime + "</td></tr>");
			i = i + 1;
		});
	}
}

// Download Inventory
var downloadInventory = function(response) {
	var foundationName = $("#FoundationSelect option:selected").text();
	$("#ajaxLoader").css("display", "block");
	$("#overlay").show();
	$.ajax({
		url : "downloadInventory/"
				+ $("#FoundationSelect option:selected").text(),
		success : function(data) {
			$("#ajaxLoader").css("display", "none");
			$("#overlay").hide();
			if (data === "null") {
				alert("Download Failed. Please try again");
			} else {
				alert("Download Successful");
			}
		}
	});
}

var resetFields = function (){
	$("#OrgSelect").empty();
	$("#OrgSelect").append("<option>"+ "No Orgs Available"+ "</option>");
	$("#OrgSpace").empty();
	$("#OrgSpace").append("<option>"+ "No Spaces Available" + "</option>");
	$("#Application").empty();
	$("#Application").append("<option>"	+ "No Applications Available"	+ "</option>");
	$('#applicationContainerHealthTable tbody').empty();
}
var resetApplicationInfo = function(){
	$("#applicationName").html("");
	$("#applicationState").html("");
	$("#buildpackName").html("");
	$("#noOfInstances").html("");
	$("#noOfRunningInstances").html("");
}

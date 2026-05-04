package org.upyog.adv.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;


import org.upyog.adv.validator.CreateApplicationGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Details of the advertisement booking
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ApplicantDetail   {
	
	private String applicantDetailId;
	
	private String bookingId;
	
	@NotBlank(groups = CreateApplicationGroup.class ,message = "ADV_BLANK_APPLICANT_NAME")
	@Size(max = 100, message = "COMMON_MAX_VALIDATION")
	private String applicantName;
	
	@NotBlank(groups = CreateApplicationGroup.class)
	@Size(min = 10, max = 10)
	private String applicantMobileNo;
	
	private String applicantAlternateMobileNo;
	
	@NotBlank(groups = CreateApplicationGroup.class)
	@Email
	private String applicantEmailId;

	private Long dob;

	@Pattern(regexp = "^[0-9]{12}$", message = "AadhaarNumber should be 12 digit number")
	@JsonProperty("aadhaarNumber")
	private String aadhaarNumber;

	@Size(max = 10)
	@JsonProperty("pan")
	private String panNumber;

	private String gender;
	
    private AuditDetails auditDetails;
    
}


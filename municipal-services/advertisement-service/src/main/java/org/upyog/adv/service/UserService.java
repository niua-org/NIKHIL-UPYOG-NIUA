package org.upyog.adv.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.constants.BookingConstants;
import org.upyog.adv.enums.AddressType;
import org.upyog.adv.repository.ServiceRequestRepository;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.util.UserUtil;
import org.upyog.adv.web.models.Address;
import org.upyog.adv.web.models.AdvertisementSearchCriteria;
import org.upyog.adv.web.models.ApplicantDetail;
import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.user.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private BookingConfiguration bookingConfig;

	/**
	 * Retrieves an existing user or creates a new user if not found.
	 *
	 * @param tenantId         tenant ID.
	 * @param applicantDetail  applicant details.
	 * @param requestInfo      request information.
	 * @return The existing or newly created user.
	 */
	public User fetchExistingUser(String tenantId, ApplicantDetail applicantDetail, RequestInfo requestInfo) {
		// Fetch existing user details
		UserDetailResponse userDetailResponse = getUserDetails(applicantDetail, requestInfo, tenantId);
		List<User> existingUsers = userDetailResponse.getUser();

		if (CollectionUtils.isEmpty(existingUsers)) {
			log.info("No existing user found for mobile number: {}", applicantDetail.getApplicantMobileNo());
			return null;
		}

		return existingUsers.get(0);
	}

	private UserDetailResponse createUser(RequestInfo requestInfo, User user, String tenantId) {

		StringBuilder uri = new StringBuilder(bookingConfig.getUserHost())
				.append(bookingConfig.getUserCreateEndpointV2());
		CreateUserRequest userRequest = CreateUserRequest.builder().requestInfo(requestInfo).user(user).build();
		UserDetailResponse userDetailResponse = userServiceCall(userRequest, uri);

		if (ObjectUtils.isEmpty(userDetailResponse)) {
			throw new CustomException("INVALID USER RESPONSE",
					"The user create has failed for the mobileNumber : " + user.getUserName());
		}
		return userDetailResponse;
	}

	/**
	 * Updates an existing user in the user service.
	 *
	 * @param requestInfo The request information.
	 * @param user        The user object with updated fields.
	 * @return The updated user.
	 */
	public User updateUser(RequestInfo requestInfo, User user) {
		StringBuilder uri = new StringBuilder(bookingConfig.getUserHost())
				.append(bookingConfig.getUserUpdateEndpoint());
		CreateUserRequest userRequest = CreateUserRequest.builder().requestInfo(requestInfo).user(user).build();
		UserDetailResponse userDetailResponse = userServiceCall(userRequest, uri);
		if (ObjectUtils.isEmpty(userDetailResponse) || CollectionUtils.isEmpty(userDetailResponse.getUser())) {
			throw new CustomException("INVALID USER RESPONSE",
					"The user update has failed for uuid : " + user.getUuid());
		}
		log.info("User updated successfully for uuid: {}", user.getUuid());
		return userDetailResponse.getUser().get(0);
	}

	/**
	 * Updates an existing user address via user/_updateAddress API.
	 *
	 * @param requestInfo   The request information.
	 * @param address       The updated AddressV2 object.
	 * @param applicantUuid The UUID of the user whose address is being updated.
	 */
	public void updateUserAddress(RequestInfo requestInfo, AddressV2 address, String applicantUuid) {
		AddressRequestV2 addressRequest = AddressRequestV2.builder()
				.requestInfo(requestInfo)
				.address(address)
				.userUuid(applicantUuid)
				.build();
		StringBuilder uri = new StringBuilder(bookingConfig.getUserHost())
				.append(bookingConfig.getUserUpdateAddressEndpointV2());
		Object response = serviceRequestRepository.fetchResult(uri, addressRequest);
		if (response == null) {
			log.warn("Response from user service is null for address update, uuid: {}", applicantUuid);
		} else {
			log.info("Address updated successfully for uuid: {}", applicantUuid);
		}
	}

	/**
	 * Checks if applicant profile fields (name, email, altMobile) have changed.
	 *
	 * @param existingUser    The user fetched from user service.
	 * @param applicantDetail The incoming applicant details.
	 * @return true if any profile field has changed.
	 */
	public boolean hasApplicantDataChanged(User existingUser, ApplicantDetail applicantDetail) {
		return !Objects.equals(existingUser.getName(), applicantDetail.getApplicantName())
				|| !Objects.equals(existingUser.getEmailId(), applicantDetail.getApplicantEmailId())
				|| !Objects.equals(existingUser.getAltContactNumber(), applicantDetail.getApplicantAlternateMobileNo());
	}

	/**
	 * Checks if address fields have changed between existing user address and incoming address.
	 *
	 * @param existingUser    The user fetched from user service.
	 * @param incomingAddress The incoming address from the booking request.
	 * @return true if any address field has changed.
	 */
	public boolean hasAddressDataChanged(User existingUser, Address incomingAddress) {
		if (incomingAddress == null || CollectionUtils.isEmpty(existingUser.getAddresses())) {
			return false;
		}
		AddressV2 existingAddress = existingUser.getAddresses().get(0);
		return !Objects.equals(existingAddress.getAddress(), incomingAddress.getAddressLine1())
				|| !Objects.equals(existingAddress.getAddress2(), incomingAddress.getAddressLine2())
				|| !Objects.equals(existingAddress.getCity(), incomingAddress.getCity())
				|| !Objects.equals(existingAddress.getPinCode(), incomingAddress.getPincode())
				|| !Objects.equals(existingAddress.getHouseNumber(), incomingAddress.getHouseNo())
				|| !Objects.equals(existingAddress.getStreetName(), incomingAddress.getStreetName())
				|| !Objects.equals(existingAddress.getLandmark(), incomingAddress.getLandmark())
				|| !Objects.equals(existingAddress.getLocality(), incomingAddress.getLocality());
	}

	private User convertApplicantToUserRequest(ApplicantDetail applicant, Role role, String tenantId, String bookingId) {
		if (applicant == null) {
			return null;
		}

		User userRequest = new User();
		userRequest.setName(applicant.getApplicantName());
		userRequest.setUserName(bookingId);
		userRequest.setMobileNumber(applicant.getApplicantMobileNo());
		userRequest.setAlternatemobilenumber(applicant.getApplicantAlternateMobileNo());
		userRequest.setEmailId(applicant.getApplicantEmailId());
		userRequest.setActive(true);
		userRequest.setTenantId(tenantId);
		userRequest.setAadhaarNumber(applicant.getAadhaarNumber());
		userRequest.setPan(applicant.getPanNumber());
		userRequest.setDob(applicant.getDob());
		userRequest.setGender(applicant.getGender());
		userRequest.setRoles(Collections.singletonList(role));
		userRequest.setType(BookingConstants.CITIZEN);
		return userRequest;
	}

	private Role getCitizenRole() {

		return Role.builder().code(BookingConstants.CITIZEN).name(BookingConstants.CITIZEN_NAME).build();
	}

	/**
	 * Creates a new user and returns the generated user details.
	 *
	 * @param requestInfo     The request information.
	 * @param applicantDetail The applicant details.
	 * @param tenantId        The tenant ID.
	 * @return The created user.
	 */
	public User createUserHandler(RequestInfo requestInfo,  ApplicantDetail applicantDetail, Address address, String tenantId, String bookingId) {
		Role role = getCitizenRole();
		User user = convertApplicantToUserRequest(applicantDetail, role, tenantId, bookingId);
		AddressV2 addressV2 = convertApplicantAddressToUserAddress(address, tenantId, AddressType.PERMANENT);
		user.addAddressItem(addressV2);
		UserDetailResponse userDetailResponse = createUser(requestInfo, user, tenantId);
		String newUuid = userDetailResponse.getUser().get(0).getUuid();
		log.info("New user uuid returned from user service: {}", newUuid);
		return userDetailResponse.getUser().get(0);
	}


	/**
	 * Searches if the applicant is already created in user registry with the mobile
	 * number entered. Search is based on name of owner, uuid and mobileNumber
	 * 
	 * @param applicant       applicant which is to be searched
	 * @param requestInfo RequestInfo from the propertyRequest
	 * @return UserDetailResponse containing the user if present and the
	 *         responseInfo
	 */
	private UserDetailResponse getUserDetails(ApplicantDetail applicant, RequestInfo requestInfo, String tenantId) {

		UserSearchRequest userSearchRequest = getBaseUserSearchRequest(UserUtil.getStateLevelTenant(tenantId), requestInfo);
		userSearchRequest.setMobileNumber(applicant.getApplicantMobileNo());
		userSearchRequest.setUserType(BookingConstants.CITIZEN);
		userSearchRequest.setEmailId(applicant.getApplicantEmailId());
		userSearchRequest.setPan(applicant.getPanNumber());
		userSearchRequest.setAadhaarNumber(applicant.getAadhaarNumber());
		userSearchRequest.setName(applicant.getApplicantName());
		StringBuilder uri = new StringBuilder(bookingConfig.getUserHost())
				.append(bookingConfig.getUserSearchEndpointV2());
		return userServiceCall(userSearchRequest, uri);
	}

	/**
	 * Returns user using user search based on propertyCriteria(owner
	 * name,mobileNumber,userName)
	 * 
	 * @param userSearchRequest
	 * @return serDetailResponse containing the user if present and the responseInfo
	 */
	public UserDetailResponse getUser(UserSearchRequest userSearchRequest) {

		StringBuilder uri = new StringBuilder(bookingConfig.getUserHost())
				.append(bookingConfig.getUserSearchEndpointV2());
		UserDetailResponse userDetailResponse = userServiceCall(userSearchRequest, uri);
		return userDetailResponse;
	}

	/**
	 * Returns UserDetailResponse by calling user service with given uri and object
	 * 
	 * @param userRequest Request object for user service
	 * @param url         The address of the endpoint
	 * @return Response from user service as parsed as userDetailResponse
	 */
	@SuppressWarnings("unchecked")
	private UserDetailResponse userServiceCall(Object userRequest, StringBuilder url) {

		String dobFormat = null;
		if (url.indexOf(bookingConfig.getUserSearchEndpointV2()) != -1)
			dobFormat = "yyyy-MM-dd";
		else if (url.indexOf(bookingConfig.getUserCreateEndpointV2()) != -1)
			dobFormat = "dd/MM/yyyy";
		try {
			Object response = serviceRequestRepository.fetchResult(url, userRequest);

			if (response != null) {
				LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response;
				parseResponse(responseMap, dobFormat);
				UserDetailResponse userDetailResponse = mapper.convertValue(responseMap, UserDetailResponse.class);
				return userDetailResponse;
			} else {
				return new UserDetailResponse();
			}
		}
		// Which Exception to throw?
		catch (IllegalArgumentException e) {
			throw new CustomException("IllegalArgumentException", "ObjectMapper not able to convertValue in userCall");
		}
	}

	/**
	 * Parses date formats to long for all users in responseMap
	 * 
	 * @param responeMap LinkedHashMap got from user api response
	 * @param dobFormat  dob format (required because dob is returned in different
	 *                   format's in search and create response in user service)
	 */
	@SuppressWarnings("unchecked")
	private void parseResponse(LinkedHashMap<String, Object> responeMap, String dobFormat) {

		List<LinkedHashMap<String, Object>> users = (List<LinkedHashMap<String, Object>>) responeMap.get("user");
		String format1 = "dd-MM-yyyy HH:mm:ss";

		if (null != users) {

			users.forEach(map -> {

				map.put("createdDate", BookingUtil.dateTolong((String) map.get("createdDate"), format1));
				if ((String) map.get("lastModifiedDate") != null)
					map.put("lastModifiedDate",
							BookingUtil.dateTolong((String) map.get("lastModifiedDate"), format1));
				if ((String) map.get("dob") != null)
					map.put("dob", BookingUtil.dateTolong((String) map.get("dob"), dobFormat));
				if ((String) map.get("pwdExpiryDate") != null)
					map.put("pwdExpiryDate", BookingUtil.dateTolong((String) map.get("pwdExpiryDate"), format1));
			});
		}
	}

	/**
	 * provides a user search request with basic mandatory parameters
	 * 
	 * @param tenantId
	 * @param requestInfo
	 * @return
	 */
	public UserSearchRequest getBaseUserSearchRequest(String tenantId, RequestInfo requestInfo) {

		return UserSearchRequest.builder().requestInfo(requestInfo).userType(BookingConstants.CITIZEN)
				.tenantId(tenantId).active(true).build();
	}

	/**
	 * Converts a user object to an applicant detail object.
	 *
	 * @param user The user object.
	 * @return The converted applicant detail.
	 */
	public ApplicantDetail convertUserToApplicantDetail(User user, String applicantUuid, String bookingId) {
		if (user == null) {
			return null;
		}
		// Convert User to ApplicantDetail
		return ApplicantDetail.builder()
				.applicantName(user.getName())
				.applicantEmailId(user.getEmailId())
				.applicantMobileNo(user.getMobileNumber())
				.applicantAlternateMobileNo(user.getAltContactNumber())
				.bookingId(bookingId)
				.applicantDetailId(applicantUuid)
				.build();
	}

	/**
	 * Converts a user address to an address detail object.
	 *
	 * @param addressV2 The set of addresses.
	 * @return The converted address detail.
	 */
	public Address convertUserAddressToAddressDetail(AddressV2 addressV2, String applicantUuid) {
		return Address.builder()
				.addressLine1(addressV2.getAddress())
				.addressLine2(addressV2.getAddress2())
				.city(addressV2.getCity())
				.pincode(addressV2.getPinCode())
				.streetName(addressV2.getStreetName())
				.landmark(addressV2.getLandmark())
				.houseNo(addressV2.getHouseNumber())
				.locality(addressV2.getLocality())
				.addressId(addressV2.getId().toString())
				.addressType(addressV2.getType())
				.applicantDetailId(applicantUuid)
				.build();
	}

	/**
	 * Enriches the provided booking list with user details.
	 * - Collects unique applicant UUIDs from bookings.
	 * - Calls user service once with the UUID set.
	 * - Maps returned users by UUID and enriches each booking with ApplicantDetail and Address.
	 *
	 * Minimal, null-safe and avoids reflection or unused flags.
	 *
	 * @param bookingDetails the list of bookings to enrich
	 * @param searchCriteria the advertisement search criteria (used only for null-guard)
	 */
	public void enrichBookingWithUserDetails(List<BookingDetail> bookingDetails,
											 AdvertisementSearchCriteria searchCriteria) {
		if (CollectionUtils.isEmpty(bookingDetails) || searchCriteria == null) return;

		try {
			Set<String> applicantUuids = bookingDetails.stream()
					.map(BookingDetail::getApplicantUuid)
					.filter(Objects::nonNull)
					.collect(Collectors.toSet());

			if (applicantUuids.isEmpty()) return;

			// Search by UUIDs only
			UserSearchRequest userSearchRequest = UserSearchRequest.builder()
					.uuid(applicantUuids)
					.build();

			UserDetailResponse userDetailResponse = getUser(userSearchRequest);
			if (userDetailResponse == null || CollectionUtils.isEmpty(userDetailResponse.getUser())) return;

			Map<String, User> userMap = userDetailResponse.getUser().stream()
					.filter(Objects::nonNull)
					.collect(Collectors.toMap(User::getUuid, Function.identity(), (u1, u2) -> u1));

			for (BookingDetail booking : bookingDetails) {
				String uuid = booking.getApplicantUuid();
				if (uuid == null) continue;

				User user = userMap.get(uuid);
				if (user == null) continue;

				ApplicantDetail applicantDetail = convertUserToApplicantDetail(user, uuid, booking.getBookingId());
				booking.setApplicantDetail(applicantDetail);
				if (applicantDetail != null && booking.getAuditDetails() != null) {
					applicantDetail.setAuditDetails(booking.getAuditDetails());
				}

				String addrId = booking.getAddressDetailId();
				Address selectedAddress = null;

				if (addrId != null && !CollectionUtils.isEmpty(user.getAddresses())) {
					AddressV2 matchedAddr = user.getAddresses().stream()
							.filter(Objects::nonNull)
							.filter(a -> a.getId() != null && addrId.equals(a.getId().toString()))
							.findFirst()
							.orElse(null);

					if (matchedAddr != null) {
						selectedAddress = convertUserAddressToAddressDetail(matchedAddr, uuid);
					}
				}

				booking.setAddress(selectedAddress);
			}

		} catch (Exception e) {
			log.error("Error while enriching booking list: {}", e.getMessage(), e);
		}
	}

	/**
	 * Converts an applicant address to a User address object to send in user create call with address object.
	 *
	 * @param address The address details.
	 * @param tenantId         The tenant ID.
	 * @return The converted User address object.
	 */
	public static AddressV2 convertApplicantAddressToUserAddress(Address address, String tenantId, AddressType addressType) {
		if (address == null) {
			log.info("The address details are empty or null");
		}
		AddressV2 addressdetails = AddressV2.builder().
				address(address.getAddressLine1()).
				address2(address.getAddressLine2()).
				city(address.getCity()).
				landmark(address.getLandmark()).
				locality(address.getLocality()).
				pinCode(address.getPincode()).
				houseNumber(address.getHouseNo()).
				tenantId(tenantId).
		        type(addressType).
				build();

		return addressdetails;
	}

	/**
	 * Creates a new address for the user UUID provided in the waterTankerRequest.
	 *
	 * This method:
	 * 1. Converts the address details from the application into a user address.
	 * 2. Builds an AddressRequest object with the converted address, user UUID, and request information.
	 * 3. Sends the AddressRequest to the user service to create the new address.
	 * 4. Parses the response to extract and return the first created address, if available.
	 *
	 * If the response is null or an error occurs during processing, appropriate logs are generated
	 * and the method returns null.
	 *
	 * @param address The request object containing the application data and user information.
	 * @return The newly created Address object, or null if creation fails.
	 */
	public AddressV2 createNewAddressV2ByUserUuid(AddressV2 address, @Valid RequestInfo requestInfo, String applicantUuid) {
		AddressRequestV2 addressRequest = AddressRequestV2.builder().requestInfo(requestInfo).address(address).userUuid(applicantUuid).build();

		StringBuilder uri = new StringBuilder(bookingConfig.getUserHost()).append(bookingConfig.getUserCreateAddressEndpointV2());
		Object response = serviceRequestRepository.fetchResult(uri, addressRequest);

		if (response == null) {
			log.warn("Response from user service is null.");
			return null;
		}
		try {
			LinkedHashMap<String, Object> responseMap = (LinkedHashMap<String, Object>) response;
			log.info("Response from user service after address creation: {}", responseMap);
			AddressResponseV2 addressResponse = mapper.convertValue(responseMap, AddressResponseV2.class);
			return Optional.ofNullable(addressResponse).map(AddressResponseV2::getAddress).filter(addresses -> !addresses.isEmpty()).map(addresses -> addresses.get(0)).orElse(null);

		} catch (Exception e) {
			log.error("Error while parsing response from user service: {}", e.getMessage(), e);
			return null;
		}
	}
}

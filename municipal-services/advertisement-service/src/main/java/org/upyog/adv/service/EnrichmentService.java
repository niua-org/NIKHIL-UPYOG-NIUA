package org.upyog.adv.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.enums.AddressType;
import org.upyog.adv.enums.BookingStatusEnum;
import org.upyog.adv.repository.BookingRepository;
import org.upyog.adv.repository.IdGenRepository;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.*;
import org.upyog.adv.web.models.idgen.IdResponse;

import lombok.extern.slf4j.Slf4j;
import org.upyog.adv.web.models.user.AddressV2;
import org.upyog.adv.web.models.user.User;
/**
 * Service class for enriching advertisement booking requests in the Advertisement Booking Service.
 * 
 * Key Responsibilities:
 * - Enriches booking requests with additional details such as booking IDs and audit details.
 * - Generates unique IDs for bookings using the ID generation service.
 * - Updates booking details with metadata like timestamps and user information.
 * - Handles draft applications and ensures proper enrichment before persistence.
 * 
 * Dependencies:
 * - BookingConfiguration: Provides configuration properties for enrichment.
 * - IdGenRepository: Interacts with the ID generation service to generate unique IDs.
 * - BookingRepository: Handles database interactions for booking-related operations.
 * - BookingUtil: Provides utility methods for generating UUIDs and other enrichment tasks.
 * 
 * Annotations:
 * - @Service: Marks this class as a Spring-managed service component.
 * - @Slf4j: Enables logging for debugging and monitoring enrichment processes.
 */
@Service
@Slf4j
public class EnrichmentService {

	@Autowired
	private BookingConfiguration config;

	@Autowired
	private IdGenRepository idGenRepository;
	
	@Autowired
	@Lazy
	private BookingRepository bookingRepository;

	@Autowired
	private UserService userService;

	public void enrichCreateBookingRequest(BookingRequest bookingRequest) {
		String bookingId = BookingUtil.getRandonUUID();
		log.info("Enriching booking request for booking id :" + bookingId);
		
		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		AuditDetails auditDetails = BookingUtil.getAuditDetails(
			    String.valueOf(bookingRequest.getRequestInfo().getUserInfo().getUuid()), 
			    true
			);
		
		bookingDetail.setAuditDetails(auditDetails);
		bookingDetail.setBookingId(bookingId);
		bookingDetail.setApplicationDate(auditDetails.getCreatedTime());
		bookingDetail.setBookingStatus(BookingStatusEnum.valueOf(bookingDetail.getBookingStatus()).toString());
		enrichUserDetails(bookingRequest);
		
		
		//Updating id and status for cart details
		bookingDetail.getCartDetails().stream().forEach(cart -> {
			cart.setBookingId(bookingId);
			
			cart.setCartId(BookingUtil.getRandonUUID());
			//Check cart staus before setting TODO: booking_created
			cart.setStatus(BookingStatusEnum.valueOf(cart.getStatus()).toString());
			cart.setAuditDetails(auditDetails);
			
			
		});
		
		//Updating id booking in documents
		bookingDetail.getUploadedDocumentDetails().stream().forEach(document -> {
			document.setBookingId(bookingId);
			document.setDocumentDetailId(BookingUtil.getRandonUUID());
			document.setAuditDetails(auditDetails);
			
		});


		bookingDetail.getApplicantDetail().setBookingId(bookingId);
		bookingDetail.getApplicantDetail().setApplicantDetailId(BookingUtil.getRandonUUID());
		bookingDetail.getApplicantDetail().setAuditDetails(auditDetails);
	
		
		bookingDetail.getAddress().setAddressId(BookingUtil.getRandonUUID());

		List<String> customIds = getIdList(requestInfo, bookingDetail.getTenantId(),
				config.getAdvertisementBookingIdKey(), config.getAdvertisementBookingIdFromat(), 1);
		
		log.info("Enriched booking request for booking no :" + customIds.get(0));

		bookingDetail.setBookingNo(customIds.get(0));

	}

	/**
	 * Enriches the applicant and address detail IDs in the given application detail.
	 * <p>
	 * If the applicantDetailId is present, it attempts to fetch an existing user based on the request.
	 * - If an existing user is found, sets the applicantDetailId accordingly.
	 * - If not found, it creates a new user and sets both applicantDetailId and addressDetailId
	 *   from the newly created user and their associated address.
	 * </p>
	 *
	 * @param bookingRequest The full application request containing applicant and address info.
	 */
	private void enrichUserDetails(BookingRequest bookingRequest) {
		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		ApplicantDetail applicantDetail = bookingDetail.getApplicantDetail();
		String tenantId = bookingDetail.getTenantId();
		User existingUser = userService.fetchExistingUser(tenantId, applicantDetail, requestInfo);

		if (existingUser != null) {
			bookingDetail.setApplicantUuid(existingUser.getUuid());
			log.info("Existing user found with ID: {}", existingUser.getUuid());

			boolean applicantChanged = userService.hasApplicantDataChanged(existingUser, applicantDetail);
			boolean addressChanged = userService.hasAddressDataChanged(existingUser, bookingDetail.getAddress());

			// Case 1: only applicant details changed -> call CreateUser only
			// Case 2: both changed -> call updateUser + updateUserAddress
			// Case 3: only address changed -> call updateUserAddress only
			if (applicantChanged) {
				User newUser = userService.createUserHandler(requestInfo, applicantDetail,
						bookingDetail.getAddress(), tenantId, bookingDetail.getBookingId());
				log.info("New user created with ID: {}", newUser.getUuid());
				bookingDetail.setApplicantUuid(newUser.getUuid());

				if (newUser.getAddresses() != null) {
					newUser.getAddresses().stream()
							.filter(addr -> addr.getId() != null)
							.findFirst()
							.ifPresent(addr -> bookingDetail.setAddressDetailId(String.valueOf(addr.getId())));
				}
			}

			if (addressChanged && !applicantChanged) {
//				log.info("Address data changed for uuid: {}, calling updateUserAddress", existingUser.getUuid());
//				AddressV2 updatedAddress = UserService.convertApplicantAddressToUserAddress(
//						bookingDetail.getAddress(), BookingUtil.extractTenantId(tenantId));
//				if (!CollectionUtils.isEmpty(existingUser.getAddresses()) && existingUser.getAddresses().get(0).getId() != null) {
//					updatedAddress.setId(existingUser.getAddresses().get(0).getId());
//				}
//				userService.updateUserAddress(requestInfo, updatedAddress, existingUser.getUuid());
//				bookingDetail.setAddressDetailId(updatedAddress.getId().toString());
				enrichAddressDetails(bookingRequest, bookingDetail);

			}

			if (!applicantChanged && !addressChanged) {
				log.info("No data change for uuid: {}, skipping update", existingUser.getUuid());
			}
			return;
		}

		// No existing user found — create new user with address
		User newUser = userService.createUserHandler(requestInfo, applicantDetail,
				bookingDetail.getAddress(), tenantId, bookingDetail.getBookingId());
		log.info("New user created with ID: {}", newUser.getUuid());
		bookingDetail.setApplicantUuid(newUser.getUuid());

		if (newUser.getAddresses() != null) {
			newUser.getAddresses().stream()
					.filter(addr -> addr.getId() != null)
					.findFirst()
					.ifPresent(addr -> bookingDetail.setAddressDetailId(String.valueOf(addr.getId())));
		}
	}


	/**
	 * Enriches the address details in the given WaterTankerBookingDetail object by creating a new address
	 * based on the user UUID provided in the WaterTankerBookingRequest object. If the new address is created
	 * successfully, the addressDetailId in the WaterTankerBookingDetail object is updated.
	 *
	 * @param bookingRequest The request object containing necessary data for address creation.
	 * @param bookingDetail The application details object to be enriched with the new address ID.
	 */
	private void enrichAddressDetails(BookingRequest bookingRequest, BookingDetail bookingDetail) {

		// If applicant UUID is null or blank, throw custom exception
		if (StringUtils.isBlank(bookingRequest.getBookingApplication().getApplicantUuid())) {
			throw new CustomException("APPLICANT_UUID_NULL", "Applicant UUID is blank");
		}

		// Fetch the new address associated with the user's UUID
		AddressV2 addressDetails = UserService.convertApplicantAddressToUserAddress(bookingRequest.getBookingApplication().getAddress(), BookingUtil.extractTenantId(bookingRequest.getBookingApplication().getTenantId()), AddressType.OTHER);
		AddressV2 address = userService.createNewAddressV2ByUserUuid(addressDetails, bookingRequest.getRequestInfo(), bookingRequest.getBookingApplication().getApplicantUuid());

		if (address != null) {
			// Set the address detail ID in the booking detail
			bookingDetail.setAddressDetailId(String.valueOf(address.getId()));
			log.info("Address successfully enriched with ID: {}", address.getId());
		} else {
			throw new CustomException("ADDRESS_CREATION_FAILED", "Failed to create address for the given applicant UUID");
		}
	}

	/**
	 * Returns a list of numbers generated from idgen
	 *
	 * @param requestInfo RequestInfo from the request
	 * @param tenantId    tenantId of the city
	 * @param idKey       code of the field defined in application properties for
	 *                    which ids are generated for
	 * @param idformat    format in which ids are to be generated
	 * @param count       Number of ids to be generated
	 * @return List of ids generated using idGen service
	 */
	private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
		 List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count)
				.getIdResponses();
		

		if (CollectionUtils.isEmpty(idResponses))
			throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

		return idResponses.stream().map(IdResponse::getId).collect(Collectors.toList());
	
	}

	//This enriches the booking request, if status is not null then it updates the booking status in booking detail and cart detail, also updates the payment date and audit details
	public void enrichUpdateBookingRequest(BookingRequest bookingRequest, BookingStatusEnum statusEnum) {
		AuditDetails auditDetails = BookingUtil.getAuditDetails(
			    String.valueOf(bookingRequest.getRequestInfo().getUserInfo().getUuid()), 
			    Boolean.FALSE
			);

		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		if(statusEnum != null) {
			bookingDetail.setBookingStatus(statusEnum.toString());
			bookingDetail.getCartDetails().stream().forEach(cart -> {
				cart.setStatus(statusEnum.toString());
			});
		}
		bookingRequest.getBookingApplication().setPaymentDate(auditDetails.getLastModifiedTime());
		bookingRequest.getBookingApplication().setAuditDetails(auditDetails);
		
	}
	
	public void enrichCreateAdvertisementDraftApplicationRequest(BookingRequest bookingRequest) {
	 
	    List<AdvertisementDraftDetail> draftData = bookingRepository
	            .getDraftData(bookingRequest.getRequestInfo().getUserInfo().getUuid());

	    if (draftData != null && !draftData.isEmpty()) {	      
	        String draftId = draftData.get(0).getDraftId(); 
	        log.info("Enriching create draft street vending application with draft id: " + draftId);

	        BookingDetail bookingDetail = bookingRequest.getBookingApplication();
	        RequestInfo requestInfo = bookingRequest.getRequestInfo();
	        AuditDetails auditDetails = BookingUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), true);

	        bookingDetail.setDraftId(draftId);
	        bookingDetail.setAuditDetails(auditDetails);
	    } else {
	        log.warn("No draft data found for UUID: " + bookingRequest.getRequestInfo().getUserInfo().getUuid());
	    }
	}

	
	public void enrichUpdateAdvertisementDraftApplicationRequest(BookingRequest bookingRequest) {
		BookingDetail bookingDetail = bookingRequest.getBookingApplication();
		log.info("Enriching update draft street vending application with draft id :" + bookingDetail.getDraftId());
		RequestInfo requestInfo = bookingRequest.getRequestInfo();
		AuditDetails auditDetails = BookingUtil.getAuditDetails(requestInfo.getUserInfo().getUuid(), false);

		bookingDetail.setAuditDetails(auditDetails);
		
	}

}

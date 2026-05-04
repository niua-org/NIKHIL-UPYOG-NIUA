package org.upyog.adv.web.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
@Setter
public class CreateUserRequest {

	@JsonProperty("requestInfo")
	private RequestInfo requestInfo;

	@JsonProperty("user")
	private User user;

}

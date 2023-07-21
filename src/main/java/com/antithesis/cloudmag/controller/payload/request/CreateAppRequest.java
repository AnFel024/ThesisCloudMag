package com.antithesis.cloudmag.controller.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAppRequest {
	@NotBlank
  	private String instance_type;

	@NotBlank
	private String cloud_provider;

	@NotBlank
	private String name;

	private Boolean http_access_allowed;
}

package com.antithesis.cloudmag.controller.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateAppDto {
  	private String instance_type;
	private String cloud_provider;
	private String name;
	private String username;
}

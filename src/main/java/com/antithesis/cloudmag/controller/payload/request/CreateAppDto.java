package com.antithesis.cloudmag.controller.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateAppDto {
  	private String instance_type;
  	private String language;
	private String cloud_provider;
	private String name;
	private String username;
}

package com.antithesis.cloudmag.controller.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateVersionRequest {
	@NotBlank
  	private String project;

	@NotBlank
	private String username;

	private String description;

	private String name;

	@NotBlank
	private String tag;
}

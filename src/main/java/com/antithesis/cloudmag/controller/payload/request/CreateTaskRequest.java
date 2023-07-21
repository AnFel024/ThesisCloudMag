package com.antithesis.cloudmag.controller.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateTaskRequest {
	@NotBlank
  	private String action_url;

	@NotBlank
	private String name;

	@NotBlank
	private String crontab_rule;
}

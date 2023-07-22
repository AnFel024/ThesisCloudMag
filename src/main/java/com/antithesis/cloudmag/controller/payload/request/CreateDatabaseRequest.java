package com.antithesis.cloudmag.controller.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDatabaseRequest {
	@NotBlank
  	private String dbms_type;

	@NotBlank
	private String name;
}

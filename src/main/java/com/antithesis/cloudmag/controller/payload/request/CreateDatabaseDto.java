package com.antithesis.cloudmag.controller.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateDatabaseDto {
	private String dbms_type;
	private String username;
	private String name;
}

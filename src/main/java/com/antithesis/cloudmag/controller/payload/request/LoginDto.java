package com.antithesis.cloudmag.controller.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDto {
	@NotBlank
  private String username;

	@NotBlank
	private String password;
}

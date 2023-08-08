package com.antithesis.cloudmag.controller.payload.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteAppDto {
	private String username;
	private String name;
}

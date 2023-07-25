package com.antithesis.cloudmag.controller.payload.request;

import com.antithesis.cloudmag.client.request.JenkinsRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CreateDatabaseDto extends JenkinsDto {
	private String dbms_type;
	private String username;
	private String name;
}
